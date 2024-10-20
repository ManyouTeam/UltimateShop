package cn.superiormc.ultimateshop.hooks.items;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import com.willfp.ecoitems.items.ItemUtilsKt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemEcoItemsHook extends AbstractItemHook {

    public ItemEcoItemsHook() {
        super("EcoItems");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        EcoItem ecoItems = EcoItems.INSTANCE.getByID(hookItemID);
        if (ecoItems == null) {
            return returnNullItem(hookItemID);
        }
        return ecoItems.getItemStack();
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        EcoItem tempVal1 = ItemUtilsKt.getEcoItem(hookItem);
        if (tempVal1 == null) {
            return null;
        }
        else {
            return tempVal1.getID();
        }
    }
}
