package cn.superiormc.ultimateshop.hooks.items;

import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoarmor.sets.ArmorUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemEcoArmorHook extends AbstractItemHook {

    public ItemEcoArmorHook() {
        super("EcoArmor");
    }

    @Override
    public ItemStack getHookItemByID(Player player, String hookItemID) {
        ArmorSet armorSet = ArmorSets.getByID(hookItemID.split(";;")[0]);
        if (armorSet == null) {
            return returnNullItem(hookItemID);
        }
        ArmorSlot armorSlot = ArmorSlot.getSlot(hookItemID.split(";;")[1].toUpperCase());
        if (armorSlot == null) {
            return returnNullItem(hookItemID);
        }
        return armorSet.getItemStack(armorSlot);
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        ArmorSet tempVal1 = ArmorUtils.getSetOnItem(hookItem);
        if (tempVal1 == null) {
            return null;
        }
        else {
            String tempVal2 = tempVal1.getId();
            ArmorSlot tempVal3 = ArmorSlot.getSlot(hookItem);
            if (tempVal3 == null) {
                return null;
            }
            return tempVal2 + ";;" + tempVal3;
        }
    }
}
