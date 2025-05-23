package cn.superiormc.ultimateshop.hooks.items;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemMMOItemsHook extends AbstractItemHook {

    public ItemMMOItemsHook() {
        super("MMOItems");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        ItemStack resultItem = MMOItems.plugin.getItem(hookItemID.split(";;")[0], hookItemID.split(";;")[1]);
        if (resultItem == null) {
            return returnNullItem(hookItemID);
        }
        return resultItem;
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        String tempVal1 = MMOItems.getID(hookItem);
        if (tempVal1 == null || tempVal1.isEmpty()) {
            return null;
        }
        String tempVal2 = MMOItems.getTypeName(hookItem);
        if (tempVal2 == null || tempVal2.isEmpty()) {
            return null;
        }
        else {
            return tempVal2 + ";;" + tempVal1;
        }
    }
}
