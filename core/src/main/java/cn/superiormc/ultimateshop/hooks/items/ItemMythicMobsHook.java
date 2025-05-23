package cn.superiormc.ultimateshop.hooks.items;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemMythicMobsHook extends AbstractItemHook {

    public int mythicMobsVersion = 0;

    public ItemMythicMobsHook() {
        super("MythicMobs");
        if (CommonUtil.getClass("io.lumine.mythic.bukkit.MythicBukkit")) {
            mythicMobsVersion = 5;
        } else if (CommonUtil.getClass("io.lumine.xikage.mythicmobs.MythicMobs")) {
            mythicMobsVersion = 4;
        }
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        if (mythicMobsVersion == 5) {
            ItemStack mmItem = MythicBukkit.inst().getItemManager().getItemStack(hookItemID);
            if (mmItem == null) {
                return returnNullItem(hookItemID);
            }
            return mmItem;
        } else if (mythicMobsVersion == 4) {
            ItemStack mmItem = MythicMobs.inst().getItemManager().getItemStack(hookItemID);
            if (mmItem == null) {
                return returnNullItem(hookItemID);
            }
            return mmItem;
        }
        return null;
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        if (mythicMobsVersion == 5) {
            return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(hookItem);
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your MythicMobs is too old, we can not parse the item from " +
                "old version of MythicMobs.");
        return null;
    }
}
