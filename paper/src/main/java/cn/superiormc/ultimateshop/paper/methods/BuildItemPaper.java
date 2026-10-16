package cn.superiormc.ultimateshop.paper.methods;

import cn.superiormc.ultimateshop.paper.adapts.PaperComponentAdapter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuildItemPaper {

    public static ItemStack buildItemStack(Player player,
                                           ConfigurationSection section,
                                           int amount,
                                           String... args) {
        ItemStack item = new ItemStack(Material.STONE);
        return editItemStack(item, player, section, amount, args);
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
