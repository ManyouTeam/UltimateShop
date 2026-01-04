package cn.superiormc.ultimateshop.hooks.items;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemMythicMobsHook extends AbstractItemHook {

    public ItemMythicMobsHook() {
        super("MythicMobs");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        ItemStack mmItem = MythicBukkit.inst().getItemManager().getItemStack(hookItemID);
        if (mmItem == null) {
            return returnNullItem(hookItemID);
        }
        return mmItem;
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(hookItem);
    }
}
