package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnbreakableCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "unbreakable";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        if (section.get("unbreakable") != null && section.getBoolean("unbreakable")) {
            item.setData(DataComponentTypes.UNBREAKABLE);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.UNBREAKABLE)) {
            section.set("unbreakable", true);
        }
    }

}
