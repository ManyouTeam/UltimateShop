package cn.superiormc.ultimateshop.paper.adapts.v26_3;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;

public class CushionColorCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "cushion-color";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String color = section.getString("cushion-color");
        if (color != null) {
            try {
                item.setData(DataComponentTypes.CUSHION_COLOR, org.bukkit.DyeColor.valueOf(color.toUpperCase(java.util.Locale.ENGLISH)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.CUSHION_COLOR)) {
            org.bukkit.DyeColor color = item.getData(DataComponentTypes.CUSHION_COLOR);
            if (color != null) {
                section.set("cushion-color", color.name());
            }
        }
    }
}
