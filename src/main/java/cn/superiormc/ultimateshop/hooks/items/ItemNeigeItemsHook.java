package cn.superiormc.ultimateshop.hooks.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

public class ItemNeigeItemsHook extends AbstractItemHook {

    public ItemNeigeItemsHook() {
        super("NeigeItems");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        return ItemManager.INSTANCE.getItemStack(hookItemID, player);
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        ItemInfo itemInfo = ItemManager.INSTANCE.isNiItem(hookItem);
        if (itemInfo == null) {
            return null;
        }
        return itemInfo.getId();
    }
}
