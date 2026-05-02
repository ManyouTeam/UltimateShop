package cn.superiormc.ultimateshop.hooks.items;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCraftEngineHook extends AbstractItemHook {

    public ItemCraftEngineHook() {
        super("CraftEngine");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        if (hookItemID.split(";;").length != 2) {
            return returnNullItem(hookItemID);
        }
        BukkitItemDefinition customItem = CraftEngineItems.byId(new Key(hookItemID.split(";;")[0], hookItemID.split(";;")[1]));
        if (customItem == null) {
            return null;
        }
        return customItem.buildBukkitItem(player);
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        Key key = CraftEngineItems.getCustomItemId(hookItem);
        if (key == null) {
            return null;
        }
        return key.namespace() + ";;" + key.value();
    }
}
