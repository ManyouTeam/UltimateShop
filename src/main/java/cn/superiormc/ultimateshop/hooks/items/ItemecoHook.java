package cn.superiormc.ultimateshop.hooks.items;

import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemecoHook extends AbstractItemHook {

    public ItemecoHook() {
        super("eco");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        return Items.lookup(hookItemID).getItem();
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        CustomItem item = Items.getCustomItem(hookItem);
        if (item == null) {
            return null;
        }
        return item.getKey().toString();
    }
}
