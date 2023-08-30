package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.items.AbstractThings;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectSinglePrice;
import cn.superiormc.ultimateshop.utils.RandomUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ObjectProducts extends AbstractThings {
    public List<ObjectSingleProduct> singleProducts = new ArrayList<>();

    public ObjectProducts() {
        super();
    }

    public ObjectProducts(ConfigurationSection section, String mode) {
        super(section, mode);
        initSingleProducts();
    }

    public void initSingleProducts() {
        for (String s : section.getKeys(false)) {
            if (section.getConfigurationSection(s) == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get products section in your shop config!!");
                singleProducts.add(new ObjectSingleProduct());
            }
            else {
                singleProducts.add(new ObjectSingleProduct(section.getConfigurationSection(s)));
            }
        }
    }

    @Override
    public void giveThing(Player player, int times) {
        if (section == null || singleProducts.isEmpty()) {
            return;
        }
        if (mode.equals("ANY")) {
            ObjectSingleProduct tempVal1 = RandomUtil.getRandomElement(singleProducts);
            tempVal1.playerGive(player, times);
        } else if (mode.equals("ALL")) {
            for (ObjectSingleProduct tempVal2 : singleProducts) {
                tempVal2.playerGive(player, times);
            }
        } else {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
        }
    }

    @Override
    public boolean takeThing(Player player, boolean take, int times) {
        switch (mode) {
            case "UNKNOWN":
                return false;
            case "ANY":
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (tempVal1.checkHasEnough(player, take, times)) {
                        return true;
                    }
                }
                return false;
            case "ALL":
                for (ObjectSingleProduct tempVal1 : singleProducts) {
                    if (!tempVal1.checkHasEnough(player, take, times)) {
                        return false;
                    }
                }
                return true;
            default:
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get price-mode section in your shop config!!");
                return false;
        }
    }

    public ItemStack getDisplayItem() {
        for (ObjectSingleProduct tempVal1 : singleProducts) {
            if (tempVal1.getDisplayItem() != null) {
                return tempVal1.getDisplayItem();
            }
        }
        return null;
    }
}
