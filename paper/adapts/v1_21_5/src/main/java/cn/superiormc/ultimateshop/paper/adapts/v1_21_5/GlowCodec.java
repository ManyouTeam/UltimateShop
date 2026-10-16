package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GlowCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "glow";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        if (section.get("glow") != null) {
            item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, section.getBoolean("glow"));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) {
            section.set("glow", item.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE));
        }
    }

}
