package cn.superiormc.ultimateshop.hooks.items;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemNexoHook extends AbstractItemHook {

    public ItemNexoHook() {
        super("Nexo");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        ItemBuilder itemBuilder = NexoItems.itemFromId(hookItemID);
        if (itemBuilder == null) {
            return returnNullItem(hookItemID);
        }
        return itemBuilder.build();
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        return NexoItems.idFromItem(hookItem);
    }
}
