package cn.superiormc.ultimateshop.hooks.items;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemOraxenHook extends AbstractItemHook {

    public ItemOraxenHook() {
        super("Oraxen");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        ItemBuilder itemBuilder = OraxenItems.getItemById(hookItemID);
        if (itemBuilder == null) {
            return returnNullItem(hookItemID);
        }
        return itemBuilder.build();
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        return OraxenItems.getIdByItem(hookItem);
    }
}
