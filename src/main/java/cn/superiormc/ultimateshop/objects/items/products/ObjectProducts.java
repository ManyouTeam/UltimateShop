package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.AbstractThings;
import cn.superiormc.ultimateshop.utils.RandomUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectProducts extends AbstractThings {
    public List<ObjectSingleProduct> singleProducts = new ArrayList<>();

    public ObjectItem item;

    public ObjectProducts() {
        super();
    }

    public ObjectProducts(ConfigurationSection section, String mode) {
        super(section, mode);
        initSingleProducts();
    }

    public ObjectProducts(ConfigurationSection section, String mode, ObjectItem item) {
        super(section, mode);
        this.item = item;
        initSingleProducts();
    }

    public void initSingleProducts() {
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get products section in your shop config!!");
                singleProducts.add(new ObjectSingleProduct());
            }
            else {
                singleProducts.add(new ObjectSingleProduct(section.getConfigurationSection(s), item));
            }
        }
    }

    @Override
    public void giveSingleThing(Player player, int times, int amount) {
        if (section == null || singleProducts.isEmpty()) {
            return;
        }
        List<ObjectSingleProduct> tempVal6 = new ArrayList<>();
        double cost;
        switch (mode) {
            case UNKNOWN:
                return;
            case ANY:
            case CLASSIC_ANY:
                for (ObjectSingleProduct tempVal5 : singleProducts) {
                    if (tempVal5.getCondition(player)) {
                        tempVal6.add(tempVal5);
                    }
                }
                if (tempVal6.isEmpty()) {
                    return;
                }
                ObjectSingleProduct tempVal1 = RandomUtil.getRandomElement(tempVal6);
                cost = getAmount(player, times, amount).get(tempVal1).doubleValue();
                tempVal1.playerGive(player, cost);
                return;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSingleProduct tempVal2 : singleProducts) {
                    cost = getAmount(player, times, amount).get(tempVal2).doubleValue();
                    tempVal2.playerGive(player, cost);
                }
                return;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return;
        }
    }

    @Override
    public boolean takeSingleThing(Inventory inventory, Player player, boolean take, int times, int amount) {
        double cost;
        switch (mode) {
            case UNKNOWN:
                return false;
            case ANY:
            case CLASSIC_ANY:
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    cost = getAmount(player, times, amount).get(tempVal1).doubleValue();
                    if (tempVal1.playerHasEnough(inventory, player, take, cost)) {
                        return true;
                    }
                }
                return false;
            case ALL:
            case CLASSIC_ALL:
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    cost = getAmount(player, times, amount).get(tempVal1).doubleValue();
                    if (!tempVal1.playerHasEnough(inventory, player, take, cost)) {
                        return false;
                    }
                }
                return true;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return false;
        }
    }

    @Override
    public Map<AbstractSingleThing, BigDecimal> getAmount(Player player, int times, int multi) {
        Map<AbstractSingleThing, BigDecimal> productMaps = new HashMap<>();
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0 ; i < multi ; i ++) {
                    for (AbstractSingleThing tempVal3 : singleProducts) {
                        if (productMaps.containsKey(tempVal3)) {
                            productMaps.put(tempVal3,
                                    productMaps.get(tempVal3).add(tempVal3.getAmount(player, times + i)));
                        }
                        else {
                            productMaps.put(tempVal3, tempVal3.getAmount(player, times + i));
                        }
                    }
                }
                break;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                for (AbstractSingleThing tempVal3 : singleProducts) {
                    if (productMaps.containsKey(tempVal3)) {
                        productMaps.put(tempVal3,
                                productMaps.get(tempVal3).add(tempVal3.getAmount(player, times).multiply(new BigDecimal(multi))));
                    }
                    else {
                        productMaps.put(tempVal3, tempVal3.getAmount(player, times).multiply(new BigDecimal(multi)));
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
                    double cost = getAmount(player, times, 1).get(tempVal1).doubleValue();
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

    public ItemStack getDisplayItem(ConfigurationSection section,
                                    Player player,
                                    boolean give,
                                    int times,
                                    int classic_multi) {
        for (ObjectSingleProduct tempVal1 : singleProducts) {
            // 商品的 times 是没用的，因为商品没有 apply 选项
            double cost = getAmount(player, times, classic_multi).get(tempVal1).doubleValue();
            ItemStack tempVal2 = tempVal1.getItemThing(section, player, give, cost);
            if (tempVal2 != null) {
                return tempVal2;
            }
        }
        return null;
    }
}
