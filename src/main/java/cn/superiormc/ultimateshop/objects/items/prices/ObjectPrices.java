package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectItemConfig;
import cn.superiormc.ultimateshop.objects.items.*;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectDisplayPlaceholder;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.*;

public class ObjectPrices extends AbstractThings {

    public Collection<ObjectSinglePrice> singlePrices = new TreeSet<>();

    private PriceMode priceMode;

    public ObjectPrices() {
        super();
        this.empty = true;
    }

    public ObjectPrices(ConfigurationSection section, String mode, ObjectItem item, PriceMode priceMode) {
        super(section, mode, item);
        this.priceMode = priceMode;
        initSinglePrices();
    }

    public void initSinglePrices() {
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get prices section in your shop config!!");
                singlePrices.add(new ObjectSinglePrice());
            }
            else {
                singlePrices.add(new ObjectSinglePrice(s, this));
            }
        }
        empty = singlePrices.isEmpty();
    }

    private Map<ObjectSinglePrice, Boolean> getAnyTargetPrice(Inventory inventory,
                                                              Player player,
                                                              int times,
                                                              int amount) {
        Map<ObjectSinglePrice, Boolean> confirmedResult = new HashMap<>();
        Map<ObjectSinglePrice, Boolean> maybeResult = new HashMap<>();
        Map<ObjectSinglePrice, PriceType> priceMap = getPriceType(player, times, amount);
        for (ObjectSinglePrice tempVal1 : priceMap.keySet()) {
            BigDecimal cost = getAmount(player, times, amount).get(tempVal1);
            if (tempVal1.getCondition(player)) {
                if (priceMap.get(tempVal1) == PriceType.FIRST) {
                    if (tempVal1.playerHasEnough(inventory, player, false, cost.doubleValue())) {
                        confirmedResult.put(tempVal1, true);
                    } else {
                        maybeResult.put(tempVal1, false);
                    }
                }
            }
        }
        if (confirmedResult.isEmpty() && maybeResult.isEmpty()) {
            maybeResult.put(new ObjectSinglePrice(), true);
            return maybeResult;
        } else if (confirmedResult.isEmpty()) {
            return maybeResult;
        } else {
            return confirmedResult;
        }
    }

    // 满足条件和apply的
    public Map<ObjectSinglePrice, PriceType> getPriceType(Player player, int times, int amount) {
        Map<ObjectSinglePrice, PriceType> applyThings = new TreeMap<>();
        switch (mode) {
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                for (ObjectSinglePrice tempVal1 : singlePrices) {
                    if (!tempVal1.getCondition(player)) {
                        continue;
                    }
                    if (applyThings.isEmpty()) {
                        applyThings.put(tempVal1, PriceType.FIRST);
                    } else {
                        applyThings.put(tempVal1, PriceType.NOT_FIRST);
                    }
                }
              break;
            case ALL:
            case ANY:
                // 已经决定好价格的次数
                Set<Integer> confirmedAmount = new HashSet<>();
                for (int i = 0 ; i < amount ; i ++) {
                    for (ObjectSinglePrice tempVal1 : singlePrices) {
                        if (!tempVal1.getCondition(player)) {
                            continue;
                        }
                        if (tempVal1.isAlwaysApply() || tempVal1.getApplyCostMap().containsKey(times + i)
                                || (tempVal1.getApplyCostMap().isEmpty() &&
                                times + i >= tempVal1.getStartApply() &&
                                times + i <= tempVal1.getEndApply())) {
                            if (!confirmedAmount.contains(i)) {
                                applyThings.put(tempVal1, PriceType.FIRST);
                                confirmedAmount.add(i);
                            } else if (!applyThings.containsKey(tempVal1)) {
                                applyThings.put(tempVal1, PriceType.NOT_FIRST);
                            }
                        }
                    }
                }
                break;
        }
        return applyThings;
    }

    @Override
    public GiveResult giveSingleThing(Player player, int times, int amount) {
        Map<AbstractSingleThing, BigDecimal> result = new TreeMap<>();
        GiveResult resultObject = new GiveResult(result);
        if (section == null || singlePrices.isEmpty()) {
            return resultObject;
        }
        BigDecimal cost;
        Map<ObjectSinglePrice, BigDecimal> tempVal3 = getAmount(player, times, amount);
        switch (mode) {
            case UNKNOWN:
                return resultObject;
            case ANY:
            case CLASSIC_ANY:
                Map<ObjectSinglePrice, PriceType> priceMap = getPriceType(player, times, amount);
                for (ObjectSinglePrice tempVal1 : priceMap.keySet()) {
                    if (priceMap.get(tempVal1) == PriceType.FIRST) {
                        cost = tempVal3.get(tempVal1);
                        resultObject.addResultMapElement(tempVal1, cost);
                    }
                }
                return resultObject;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSinglePrice tempVal2 : getAmount(player, times, amount).keySet()) {
                    cost = tempVal3.get(tempVal2);
                    resultObject.addResultMapElement(tempVal2, cost);
                }
                return resultObject;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return resultObject;
        }
    }

    // 作为价格时候使用
    @Override
    public TakeResult takeSingleThing(Inventory inventory, Player player, int times, int amount, boolean test) {
        Map<AbstractSingleThing, BigDecimal> result = new TreeMap<>();
        TakeResult resultObject = new TakeResult(result);
        if (section == null) {
            return resultObject;
        }
        BigDecimal cost = BigDecimal.ZERO;
        boolean needFalse = false;
        switch (mode) {
            case UNKNOWN:
                return resultObject;
            case ALL:
            case CLASSIC_ALL:
                Map<ObjectSinglePrice, BigDecimal> tempVal3 = getAmount(player, times, amount);
                for (ObjectSinglePrice tempVal1 : tempVal3.keySet()) {
                    if (tempVal1.empty) {
                        continue;
                    }
                    cost = tempVal3.get(tempVal1);
                    resultObject.addResultMapElement(tempVal1, cost);
                    if (!test && !tempVal1.playerHasEnough(inventory, player, false, cost.doubleValue())) {
                        needFalse = true;
                    }
                }
                if (!needFalse) {
                    resultObject.setResultBoolean();
                }
                return resultObject;
            case ANY:
            case CLASSIC_ANY:
                Map<ObjectSinglePrice, BigDecimal> tempVal4 = getAmount(player, times, amount);
                Map<ObjectSinglePrice, Boolean> tempVal5 = getAnyTargetPrice(inventory, player, times, amount);
                ObjectSinglePrice first = null;
                for (ObjectSinglePrice tempVal11 : tempVal5.keySet()) {
                    if (tempVal11.empty) {
                        continue;
                    }
                    if (Objects.nonNull(tempVal4.get(tempVal11))) {
                        cost = tempVal4.get(tempVal11);
                    }
                    if (first == null) {
                        first = tempVal11;
                    }
                    if (tempVal5.get(tempVal11)) {
                        resultObject.addResultMapElement(tempVal11, cost);
                        resultObject.setResultBoolean();
                        return resultObject;
                    }
                }
                resultObject.addResultMapElement(first, cost);
                return resultObject;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return resultObject;
        }
    }

    public Map<ObjectSinglePrice, BigDecimal> getAmount(Player player, int times, int multi) {
        Map<ObjectSinglePrice, BigDecimal> priceMaps = new TreeMap<>();
        Collection<ObjectSinglePrice> meetConditionSingle = new ArrayList<>();
        for (ObjectSinglePrice tempVal1 : singlePrices) {
            if (tempVal1.getCondition(player)) {
                meetConditionSingle.add(tempVal1);
            }
        }
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0 ; i < multi ; i ++) {
                    for (ObjectSinglePrice tempVal3 : meetConditionSingle) {
                            //getAllPrices(player, times + i, 1).keySet()) {
                        int nowTimes = times + i;
                        if (tempVal3.isAlwaysApply() || tempVal3.getApplyCostMap().containsKey(nowTimes) ||
                                (tempVal3.getApplyCostMap().isEmpty() &&
                                        nowTimes >= tempVal3.getStartApply() &&
                                        nowTimes <= tempVal3.getEndApply())) {
                            if (priceMaps.containsKey(tempVal3)) {
                                priceMaps.put(tempVal3,
                                        priceMaps.get(tempVal3).add(tempVal3.getAmount(player, times + i, i)));
                            }
                            else {
                                priceMaps.put(tempVal3, tempVal3.getAmount(player, times + i, i));
                            }
                        }
                    }
                }
                break;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                for (ObjectSinglePrice tempVal3 : meetConditionSingle) {
                    if (priceMaps.containsKey(tempVal3)) {
                        priceMaps.put(tempVal3,
                                priceMaps.get(tempVal3).add(tempVal3.getAmount(player, times, 0).multiply(new BigDecimal(multi))));
                    }
                    else {
                        priceMaps.put(tempVal3,
                                tempVal3.getAmount(player, times, 0).multiply(new BigDecimal(multi)));
                    }
                }
                break;
        }
        return priceMaps;
    }

    public PriceMode getPriceMode() {
        return priceMode;
    }

    public static List<String> getDisplayName(Player player,
                                              int multi,
                                              Map<AbstractSingleThing, BigDecimal> result,
                                              ThingMode mode,
                                              boolean alwaysStatic) {
        Map<ObjectDisplayPlaceholder, BigDecimal> tempVal1 = new TreeMap<>();
        switch (mode) {
            case ANY: case CLASSIC_ANY:
                for (AbstractSingleThing tempVal3 : result.keySet()) {
                    if (tempVal1.containsKey(tempVal3.getDisplayPlaceholder())) {
                        tempVal1.replace(tempVal3.getDisplayPlaceholder(), tempVal1.get(tempVal3.getDisplayPlaceholder()).add(result.get(tempVal3)));
                        continue;
                    }
                    tempVal1.put(tempVal3.getDisplayPlaceholder(), result.get(tempVal3));
                }
                break;
            case ALL: case CLASSIC_ALL:
                for (AbstractSingleThing tempVal2 : result.keySet()) {
                    if (result.get(tempVal2) == null) {
                        continue;
                    }
                    if (tempVal1.containsKey(tempVal2.getDisplayPlaceholder())) {
                        tempVal1.replace(tempVal2.getDisplayPlaceholder(), tempVal1.get(tempVal2.getDisplayPlaceholder()).add(result.get(tempVal2)));
                        continue;
                    }
                    tempVal1.put(tempVal2.getDisplayPlaceholder(), result.get(tempVal2));
                }
                break;
        }
        List<String> tempVal2 = new ArrayList<>();
        for (ObjectDisplayPlaceholder placeholder : tempVal1.keySet()) {
            tempVal2.add(TextUtil.withPAPI(placeholder.getDisplayName(multi, tempVal1.get(placeholder), alwaysStatic), player));
        }
        return tempVal2;
    }

    public static String getDisplayNameInLine(Player player,
                                              int multi,
                                              Map<AbstractSingleThing, BigDecimal> result,
                                              ThingMode mode,
                                              boolean alwaysStatic) {
        List<String> text = getDisplayName(player, multi, result, mode, alwaysStatic);
        StringBuilder tempVal2 = new StringBuilder();
        switch (mode) {
            case ANY: case CLASSIC_ANY:
                for (int i = 0; i < text.size(); i++) {
                    if (i > 0) {
                        tempVal2.append(ConfigManager.configManager.getString("placeholder.price.split-symbol-any"));
                    }
                    tempVal2.append(text.get(i));
                }
                break;
            case ALL: case CLASSIC_ALL:
                for (int i = 0; i < text.size(); i++) {
                    if (i > 0) {
                        tempVal2.append(ConfigManager.configManager.getString("placeholder.price.split-symbol-all"));
                    }
                    tempVal2.append(text.get(i));
                }
                break;
            default:
                tempVal2 = new StringBuilder(ConfigManager.configManager.getString("placeholder.price.unknown-price-type"));
                break;
        }
        return tempVal2.toString().replace(";;", ConfigManager.configManager.getString("placeholder.price.replace-new-line-symbol"));
    }
}