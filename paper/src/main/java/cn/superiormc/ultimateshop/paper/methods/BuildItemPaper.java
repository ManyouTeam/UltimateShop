package cn.superiormc.ultimateshop.paper.methods;

import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.paper.adapts.PaperComponentAdapter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuildItemPaper {

    public static ItemStack buildItemStack(Player player,
                                           ConfigurationSection section,
                                           int amount,
                                           String... args) {
        return BuildItem.buildItemStack(player, section, amount > 0 ? amount : section.getInt("amount", 1), args);
    }

    public static ItemStack editItemStack(ItemStack item,
                                          Player player,
                                          ConfigurationSection section,
                                          int amount,
                                          String... args) {
        if (amount > 0) {
            item.setAmount(amount);
        }
        return PaperComponentAdapter.editItemStack(item, player, section, args);
    }

}
