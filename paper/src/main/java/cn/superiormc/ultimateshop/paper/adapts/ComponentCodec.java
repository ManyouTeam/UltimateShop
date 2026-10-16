package cn.superiormc.ultimateshop.paper.adapts;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ComponentCodec {

    String getIdentifier();

    void apply(ItemStack item, Player player, ConfigurationSection section, String... args);

    void write(ItemStack item, ConfigurationSection section);

}
