package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.AbstractThings;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.utils.RandomUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.*;

public class ObjectProducts extends AbstractThings {
    public Collection<ObjectSingleProduct> singleProducts = new TreeSet<>();

    public ObjectProducts() {
        super();
    }

    public ObjectProducts(ConfigurationSection section, String mode, ObjectItem item) {
        super(section, mode, item);
        initSingleProducts();
    }

    public void initSingleProducts() {
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get products section in your shop config!!");
                singleProducts.add(new ObjectSingleProduct());
            }
            else {
                singleProducts.add(new ObjectSingleProduct(s, this));
            }
        }
        empty = singleProducts.isEmpty();
    }

    @Override
    public GiveResult giveSingleThing(Player player, int times, int amount) {
        Map<AbstractSingleThing, BigDecimal> result = new TreeMap<>();
        GiveResult resultObject = new GiveResult(result);
        if (section == null || singleProducts.isEmpty()) {
            return resultObject;
        }
        List<ObjectSingleProduct> tempVal6 = new ArrayList<>();
        BigDecimal cost;
        switch (mode) {
            case UNKNOWN:
                return resultObject;
            case ANY:
            case CLASSIC_ANY:
                for (ObjectSingleProduct tempVal5 : singleProducts) {
                    if (tempVal5.getCondition(player)) {
                        tempVal6.add(tempVal5);
                    }
                }
                if (tempVal6.isEmpty()) {
                    return resultObject;
                }
                ObjectSingleProduct tempVal1 = RandomUtil.getRandomElement(tempVal6);
                cost = getAmount(player, times, amount, true).get(tempVal1);
                resultObject.addResultMapElement(tempVal1, cost);
                return resultObject;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSingleProduct tempVal2 : singleProducts) {
                    if (!tempVal2.getCondition(player)) {
                        continue;
                    }
                    cost = getAmount(player, times, amount, true).get(tempVal2);
                    resultObject.addResultMapElement(tempVal2, cost);
                }
                return resultObject;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return resultObject;
        }
    }

    @Override
    public TakeResult takeSingleThing(Inventory inventory, Player player, int times, int amount, boolean test) {
        Map<AbstractSingleThing, BigDecimal> result = new TreeMap<>();
        TakeResult resultObject = new TakeResult(result);
        if (section == null) {
            return resultObject;
        }
        BigDecimal cost;
        boolean needFalse = false;
        switch (mode) {
            case UNKNOWN:
                return resultObject;
            case ANY:
            case CLASSIC_ANY:
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.getCondition(player)) {
                        continue;
                    }
                    cost = getAmount(player, times, amount, false).get(tempVal1);
                    if (!test && tempVal1.playerHasEnough(inventory, player, false, cost.doubleValue())) {
                        resultObject.addResultMapElement(tempVal1, cost);
                        resultObject.setResultBoolean();
                        return resultObject;
                    }
                }
                return resultObject;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.getCondition(player)) {
                        continue;
                    }
                    cost = getAmount(player, times, amount, false).get(tempVal1);
                    resultObject.addResultMapElement(tempVal1, cost);
                    if (!test && !tempVal1.playerHasEnough(inventory, player, false, cost.doubleValue())) {
                        needFalse = true;
                    }
                }
                if (!needFalse) {
                    resultObject.setResultBoolean();
                }
                return resultObject;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return resultObject;
        }
    }

    public Map<ObjectSingleProduct, BigDecimal> getAmount(Player player, int times, int multi, boolean buyOrSell) {
        Map<ObjectSingleProduct, BigDecimal> productMaps = new TreeMap<>();
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0 ; i < multi ; i ++) {
                    for (ObjectSingleProduct tempVal3 : singleProducts) {
                        if (productMaps.containsKey(tempVal3)) {
                            productMaps.put(tempVal3,
                                    productMaps.get(tempVal3).add(tempVal3.getAmount(player, i, buyOrSell)));
                        }
                        else {
                            productMaps.put(tempVal3, tempVal3.getAmount(player, i, buyOrSell));
                        }
                    }
                }
                break;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                for (ObjectSingleProduct tempVal3 : singleProducts) {
                    if (productMaps.containsKey(tempVal3)) {
                        productMaps.put(tempVal3,
                                productMaps.get(tempVal3).add(tempVal3.getAmount(player, 0, buyOrSell).multiply(new BigDecimal(multi))));
                    }
                    else {
                        productMaps.put(tempVal3, tempVal3.getAmount(player, 0, buyOrSell).multiply(new BigDecimal(multi)));
                    }
                }
                break;
        }
        return productMaps;
    }

    public int getMaxAbleSellAmount(Inventory inventory, Player player, int times) {
        int maxAmount = -1;
        switch (mode) {
            case UNKNOWN:
                return 0;
            case ANY:
            case CLASSIC_ANY:
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.getCondition(player)) {
                        continue;
                    }
                    double cost = getAmount(player, times, 1, false).get(tempVal1).doubleValue();
                    int tempVal2 = (int) (tempVal1.playerHasAmount(inventory, player) / cost);
                    if (maxAmount == -1 || tempVal2 < maxAmount) {
                        maxAmount = tempVal2;
                    }
                }
                return maxAmount;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return 0;
        }
    }

    public ObjectSingleProduct getTargetProduct(Player player) {
        for (ObjectSingleProduct tempVal1 : singleProducts) {
            if (!tempVal1.getCondition(player)) {
                continue;
            }
            // 商品的 times 是没用的，因为商品没有 apply 选项
            return tempVal1;
        }
        return null;
    }
}
