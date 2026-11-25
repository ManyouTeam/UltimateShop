package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.AbstractThings;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.objects.items.MaxSellResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.utils.RandomUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
    public GiveResult give(Player player, int times, int amount) {
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
                    if (tempVal5.getApplyCondition(player)) {
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
                    if (!tempVal2.getApplyCondition(player)) {
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
    public TakeResult take(ItemStorage storage, Player player, int times, int amount, boolean test) {
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
                    if (!tempVal1.getApplyCondition(player)) {
                        continue;
                    }
                    cost = getAmount(player, times, amount, false).get(tempVal1);
                    resultObject.addResultMapElement(tempVal1, player, times, amount, cost);
                    if (!test && tempVal1.playerHasEnough(storage, player, false, cost.doubleValue())) {
                        resultObject.setResultBoolean();
                        return resultObject;
                    }
                }
                return resultObject;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.getApplyCondition(player)) {
                        continue;
                    }
                    cost = getAmount(player, times, amount, false).get(tempVal1);
                    resultObject.addResultMapElement(tempVal1, player, times, amount, cost);
                    if (!test && !tempVal1.playerHasEnough(storage, player, false, cost.doubleValue())) {
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

    public MaxSellResult getMaxSellAmount(Inventory inventory, Player player, int times, int maxLimit) {
        return getMaxSellAmount(ItemStorage.of(inventory), player, times, maxLimit);
    }

    public MaxSellResult getMaxSellAmount(ItemStorage storage, Player player, int times, int limit) {
        return new MaxSellCalculator(this, storage, player, times).calculate(limit, null, 0, null, false, 1);
    }

    public MaxSellResult getMaxSellAmount(ItemStorage storage, Player player, int times, int serverTimes,
                                          ObjectItem item, ObjectPrices price, boolean ignoreMultiplier, double multiplier) {
        return new MaxSellCalculator(this, storage, player, times)
                .calculate(Integer.MAX_VALUE, item, serverTimes, price, ignoreMultiplier, multiplier);
    }

    public ObjectSingleProduct getTargetProduct(Player player) {
        for (ObjectSingleProduct tempVal1 : singleProducts) {
            if (!tempVal1.getApplyCondition(player)) {
                continue;
            }
            // 商品的 times 是没用的，因为商品没有 apply 选项
            return tempVal1;
        }
        return null;
    }
}
