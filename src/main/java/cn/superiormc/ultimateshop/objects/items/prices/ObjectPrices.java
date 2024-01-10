package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.AbstractThings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.*;

public class ObjectPrices extends AbstractThings {

    public List<ObjectSinglePrice> singlePrices = new ArrayList<>();

    private ObjectItem item = null;

    public ObjectPrices() {
        super();
        this.empty = true;
    }

    public ObjectPrices(ConfigurationSection section, String mode) {
        super(section, mode);
        this.empty = false;
        initSinglePrices();
    }

    public ObjectPrices(ConfigurationSection section, String mode, ObjectItem item) {
        super(section, mode);
        this.item = item;
        this.empty = false;
        initSinglePrices();
    }

    public void initSinglePrices() {
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get prices section in your shop config!!");
                singlePrices.add(new ObjectSinglePrice());
            }
            else {
                singlePrices.add(new ObjectSinglePrice(section.getConfigurationSection(s), item));
            }
        }
    }

    public ObjectSinglePrice getAnyTargetPrice(Player player,
                                               int times) {
        for (ObjectSinglePrice tempVal1 : getPrices(player, times, 1).keySet()) {
            if (tempVal1.getCondition(player)) {
                return tempVal1;
            }
        }
        return new ObjectSinglePrice();
    }

    public List<ObjectSinglePrice> getAnyTargetPrice(Inventory inventory,
                                               Player player,
                                               int times,
                                               int amount) {
        List<ObjectSinglePrice> confirmedResult = new ArrayList<>();
        List<ObjectSinglePrice> maybeResult = new ArrayList<>();
        Map<ObjectSinglePrice, PriceType> priceMap = getPrices(player, times, amount);
        for (ObjectSinglePrice tempVal1 : priceMap.keySet()) {
            BigDecimal cost = getAmount(player, times, amount).get(tempVal1);
            if (tempVal1.getCondition(player)) {
                if (tempVal1.playerHasEnough(inventory, player, false, cost.doubleValue())) {
                    if (priceMap.get(tempVal1) == PriceType.FIRST) {
                        confirmedResult.add(tempVal1);
                    }
                }
                else {
                    if (priceMap.get(tempVal1) == PriceType.FIRST) {
                        maybeResult.add(tempVal1);
                    }
                }
            }
        }
        if (confirmedResult.isEmpty() && maybeResult.isEmpty()) {
            maybeResult.add(new ObjectSinglePrice());
            return maybeResult;
        }
        else if (confirmedResult.isEmpty()) {
            return maybeResult;
        }
        else {
            return confirmedResult;
        }
    }

    private Map<ObjectSinglePrice, PriceType> getPrices(Player player, int times, int amount) {
        Map<ObjectSinglePrice, PriceType> applyThings = new HashMap<>();
        switch (mode) {
            case CLASSIC_ALL:
            case CLASSIC_ANY:
              for (ObjectSinglePrice tempVal1 : singlePrices) {
                  if (!tempVal1.getCondition(player)) {
                      continue;
                  }
                  if (tempVal1.getApplyCostMap().containsKey(times)) {
                      applyThings.put(tempVal1, PriceType.FIRST);
                  } else if (tempVal1.getApplyCostMap().isEmpty() &&
                          times >= tempVal1.getStartApply() &&
                          times <= tempVal1.getEndApply()) {
                      applyThings.put(tempVal1, PriceType.FIRST);
                  }
              }
              break;
            case ALL:
            case ANY:
                Set<Integer> confirmedAmount = new HashSet<>();
                for (int i = 0 ; i < amount ; i ++) {
                    for (ObjectSinglePrice tempVal1 : singlePrices) {
                        if (!tempVal1.getCondition(player)) {
                            continue;
                        }
                        if (tempVal1.getApplyCostMap().containsKey(times + i)
                                || (tempVal1.getApplyCostMap().isEmpty() &&
                                times + i >= tempVal1.getStartApply() &&
                                times + i <= tempVal1.getEndApply())) {
                            if (!confirmedAmount.contains(i)) {
                                applyThings.put(tempVal1, PriceType.FIRST);
                                confirmedAmount.add(i);
                            }
                            else {
                                if (!applyThings.containsKey(tempVal1)) {
                                    applyThings.put(tempVal1, PriceType.NOT_FIRST);
                                    confirmedAmount.add(i);
                                }
                            }
                        }
                    }
                }
                break;
        }
        return applyThings;
    }

    @Override
    public void giveSingleThing(Player player, int times, int amount) {
        if (section == null || singlePrices.isEmpty()) {
            return;
        }
        double cost;
        switch (mode) {
            case UNKNOWN:
                return;
            case ANY:
            case CLASSIC_ANY:
                AbstractSingleThing tempVal5 = getAnyTargetPrice(player, times);
                cost = getAmount(player, times, amount).get(tempVal5).doubleValue();
                tempVal5.playerGive(player, cost);
                return;
            case ALL:
            case CLASSIC_ALL:
                for (AbstractSingleThing tempVal2 : getPrices(player, times, amount).keySet()) {
                    cost = getAmount(player, times, amount).get(tempVal2).doubleValue();
                    tempVal2.playerGive(player, cost);
                }
                return;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return;
        }
    }

    // 作为价格时候使用
    @Override
    public boolean takeSingleThing(Inventory inventory, Player player, boolean take, int times, int amount) {
        if (section == null) {
            return false;
        }
        double cost = 0;
        switch (mode) {
            case UNKNOWN:
                return false;
            case ALL:
                Map<AbstractSingleThing, BigDecimal> tempVal6 = getAmount(player, times, amount);
                for (AbstractSingleThing tempVal1 : tempVal6.keySet()) {
                    if (tempVal1.empty) {
                        continue;
                    }
                    cost = tempVal6.get(tempVal1).doubleValue();
                    if (!tempVal1.playerHasEnough(inventory, player, take, cost)) {
                        return false;
                    }
                }
                return true;
            case CLASSIC_ALL:
                Map<AbstractSingleThing, BigDecimal> tempVal3 = getAmount(player, times, amount);
                for (AbstractSingleThing tempVal1 : tempVal3.keySet()) {
                    if (tempVal1.empty) {
                        return false;
                    }
                    cost = tempVal3.get(tempVal1).doubleValue();
                    if (!tempVal1.playerHasEnough(inventory, player, take, cost)) {
                        return false;
                    }
                }
                return true;
            case ANY:
                List<ObjectSinglePrice> tempVal4 = getAnyTargetPrice(
                        inventory, player, times, amount);
                for (ObjectSinglePrice tempVal11 : tempVal4) {
                    if (Objects.nonNull(getAmount(player, times, amount).get(tempVal11))) {
                        cost = getAmount(player, times, amount).get(tempVal11).doubleValue();
                    }
                    if (tempVal11.playerHasEnough(inventory, player, take, cost)) {
                        continue;
                    }
                    return false;
                }
                return true;
            case CLASSIC_ANY:
                ObjectSinglePrice tempVal11 = getAnyTargetPrice
                        (inventory, player, times, amount).get(0);
                cost = getAmount(player, times, amount).get(tempVal11).doubleValue();
                return tempVal11.playerHasEnough(inventory, player, take, cost);
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return false;
        }
    }

    @Override
    public Map<AbstractSingleThing, BigDecimal> getAmount(Player player, int times, int multi) {
        Map<AbstractSingleThing, BigDecimal> priceMaps = new HashMap<>();
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0 ; i < multi ; i ++) {
                    for (AbstractSingleThing tempVal3 : getPrices(player, times + i, 1).keySet()) {
                        if (priceMaps.containsKey(tempVal3)) {
                            priceMaps.put(tempVal3,
                                    priceMaps.get(tempVal3).add(tempVal3.getAmount(player, times + i)));
                        }
                        else {
                            priceMaps.put(tempVal3, tempVal3.getAmount(player, times + i));
                        }
                    }
                }
                break;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                for (AbstractSingleThing tempVal3 : getPrices(player, times, multi).keySet()) {
                    if (priceMaps.containsKey(tempVal3)) {
                        priceMaps.put(tempVal3,
                                priceMaps.get(tempVal3).add(tempVal3.getAmount(player, times).multiply(new BigDecimal(multi))));
                    }
                    else {
                        priceMaps.put(tempVal3,
                                tempVal3.getAmount(player, times).multiply(new BigDecimal(multi)));
                    }
                }
                break;
        }
        return priceMaps;
    }

    public List<String> getDisplayName(Player player, int times, int multi) {
        Map<AbstractSingleThing, BigDecimal> priceMaps = getAmount(player, times, multi);
        List<String> tempVal1 = new ArrayList<>();
        switch (mode) {
            case ANY: case CLASSIC_ANY:
                for (AbstractSingleThing tempVal3 : getAnyTargetPrice(player.getInventory(), player, times, multi)) {
                    if (priceMaps.get(tempVal3) == null) {
                        continue;
                    }
                    tempVal1.add(tempVal3.getDisplayName(priceMaps.get(tempVal3)));
                }
                break;
            case ALL: case CLASSIC_ALL:
                for (AbstractSingleThing tempVal2 : priceMaps.keySet()) {
                    if (priceMaps.get(tempVal2) == null) {
                        continue;
                    }
                    tempVal1.add(tempVal2.getDisplayName(priceMaps.get(tempVal2)));
                }
                break;
        }
        return tempVal1;
    }

    public String getDisplayNameInGUI(List<String> text) {
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
        return tempVal2.toString();
    }

    public String getDisplayNameInChat(Inventory inventory, Player player, int times, int multi) {
        Map<AbstractSingleThing, BigDecimal> priceMaps = getAmount(player, times, multi);
        List<String> tempVal1 = new ArrayList<>();
        switch (mode) {
            case ANY: case CLASSIC_ANY:
                for (ObjectSinglePrice tempVal3 : getAnyTargetPrice(inventory, player, times, multi)) {
                    tempVal1.add(tempVal3.getDisplayName(priceMaps.get(tempVal3)));
                }
                break;
            case ALL: case CLASSIC_ALL:
                for (AbstractSingleThing tempVal2 : priceMaps.keySet()) {
                    tempVal1.add(tempVal2.getDisplayName(priceMaps.get(tempVal2)));
                }
                break;
        }
        return getDisplayNameInGUI(tempVal1);
    }

}