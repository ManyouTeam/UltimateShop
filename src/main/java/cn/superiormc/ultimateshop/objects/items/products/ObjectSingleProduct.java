package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectSingleProduct extends AbstractSingleThing {

    public ObjectSingleProduct() {
        super();
    }

    public ObjectSingleProduct(ConfigurationSection singleSection) {
        super(singleSection);
    }

    @Override
    public boolean playerHasEnough(Player player, boolean take, int times) {
        if (singleSection == null) {
            return false;
        }
        return checkHasEnough(player, take, times);
    }

    public ItemStack getDisplayItem() {
        switch (type) {
            case "hook":
                String pluginName = singleSection.getString("hook-plugin", "");
                String itemID = singleSection.getString("hook-item", "");
                if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                    itemID = singleSection.getString("hook-item-type") + ";;" + itemID;
                } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                    itemID = itemID + ";;" + singleSection.getString("hook-item-type");
                }
                return ItemsHook.getHookItem(pluginName, itemID);
            case "vanilla":
                ItemStack itemStack = ItemUtil.buildItemStack(singleSection);
                if (itemStack == null) {
                    return null;
                }
                return itemStack;
            default :
                return null;
        }
    }

}
