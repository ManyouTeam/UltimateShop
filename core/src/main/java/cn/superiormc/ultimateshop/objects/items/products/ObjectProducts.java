package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.*;
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
        boolean allIsStatic = true;
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not get products section in your shop config!!");
                singleProducts.add(new ObjectSingleProduct());
            } else {
                ObjectSingleProduct singleProduct = new ObjectSingleProduct(s, this);
                singleProducts.add(singleProduct);
                if (!singleProduct.isStatic()) {
                    allIsStatic = false;
                }
            }
        }
        if (allIsStatic) {
            if (mode.equals(ThingMode.ANY)) {
                mode = ThingMode.CLASSIC_ANY;
            } else if (mode.equals(ThingMode.ALL)) {
                mode = ThingMode.CLASSIC_ALL;
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
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not get price-mode section in your shop config!!");
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
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not get price-mode section in your shop config!!");
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
                        } else {
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
                    } else {
                        productMaps.put(tempVal3, tempVal3.getAmount(player, 0, buyOrSell).multiply(new BigDecimal(multi)));
                    }
                }
                break;
        }
        return productMaps;
    }

    public MaxSellResult getMaxAbleSellAmount(Inventory inventory, Player player, int times) {
        int maxAmount = -1;
        MaxSellResult sellResult = new MaxSellResult();
        switch (mode) {
            case UNKNOWN:
                return MaxSellResult.empty;
            case ANY:
            case CLASSIC_ANY:
                boolean needTrue = true;
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.getCondition(player)) {
                        continue;
                    }
                    if (!tempVal1.isStatic()) {
                        ErrorManager.errorManager.sendErrorMessage("§6Warning: It seems that one of your product is using dynamic amounts, which results in an error in calculating the maximum sellable quantity for that product in the sell all, as the results of each sale are different and the plugin cannot predict the price for the next sale.");
                    }
                    BigDecimal cost = getAmount(player, times, 1, false).get(tempVal1);
                    int tempVal2 = (int) (tempVal1.playerHasAmount(inventory, player) / cost.doubleValue());
                    if (tempVal2 >= 0) {
                        if (maxAmount < 0) {
                            maxAmount = 0;
                        }
                        maxAmount = tempVal2 + maxAmount;
                        BigDecimal realCost = getAmount(player, times, tempVal2, false).get(tempVal1);
                        if (tempVal1.playerHasEnough(inventory, player, false, realCost.doubleValue())) {
                            sellResult.getTakeResult().addResultMapElement(tempVal1, realCost);
                        } else {
                            needTrue = false;
                        }
                    }
                }
                if (needTrue) {
                    sellResult.getTakeResult().setResultBoolean();
                }
                sellResult.setMaxAmount(maxAmount);
                return sellResult;
            case ALL:
            case CLASSIC_ALL:
                boolean needFalse = false;
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.getCondition(player)) {
                        continue;
                    }
                    if (!tempVal1.isStatic()) {
                        ErrorManager.errorManager.sendErrorMessage("§6Warning: It seems that one of your product is using dynamic amounts, which results in an error in calculating the maximum sellable quantity for that product in the sell all, as the results of each sale are different and the plugin cannot predict the price for the next sale.");
                    }
                    BigDecimal cost = getAmount(player, times, 1, false).get(tempVal1);
                    int tempVal2 = (int) (tempVal1.playerHasAmount(inventory, player) / cost.doubleValue());
                    if (maxAmount == -1 || tempVal2 < maxAmount) {
                        maxAmount = tempVal2;
                    }
                }
                if (maxAmount > 0) {
                    for (ObjectSingleProduct tempVal1 : singleProducts) {
                        if (!tempVal1.getCondition(player)) {
                            continue;
                        }
                        BigDecimal realCost = getAmount(player, times, maxAmount, false).get(tempVal1);
                        sellResult.getTakeResult().addResultMapElement(tempVal1, realCost);

                        if (!tempVal1.playerHasEnough(inventory, player, false, realCost.doubleValue())) {
                            needFalse = true;
                        }
                    }

                    if (!needFalse) {
                        sellResult.getTakeResult().setResultBoolean();
                    }
                }
                sellResult.setMaxAmount(maxAmount);
                return sellResult;
            default:
                ErrorManager.errorManager.sendErrorMessage("§cError: Can not get price-mode section in your shop config!!");
                return MaxSellResult.empty;
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
