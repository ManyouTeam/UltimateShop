package cn.superiormc.ultimateshop.hooks.items;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemItemsAdderHook extends AbstractItemHook {

    public ItemItemsAdderHook() {
        super("ItemsAdder");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        CustomStack customStack = CustomStack.getInstance(hookItemID);
        if (customStack == null) {
            return super.returnNullItem(hookItemID);
        }
        return customStack.getItemStack();
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        CustomStack customStack = CustomStack.byItemStack(hookItem);
        if (customStack != null) {
            return customStack.getNamespacedID();
        }
        return null;
    }
}
