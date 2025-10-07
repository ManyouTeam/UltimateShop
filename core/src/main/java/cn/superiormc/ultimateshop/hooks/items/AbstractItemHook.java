package cn.superiormc.ultimateshop.hooks.items;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemHook {

    protected String pluginName;

    public AbstractItemHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract ItemStack getHookItemByID(Player player, String itemID);

    public ItemStack returnNullItem(String itemID) {
        ErrorManager.errorManager.sendErrorMessage("§cError: Can not get " + pluginName + " item: " + itemID + ", " +
                "if you firmly believe that the item ID is correct, it is possible that your formatting is incorrect.");
        return null;
    }

    public abstract String getIDByItemStack(ItemStack hookItem);

    public String getPluginName() {
        return pluginName;
    }
}
