package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaxDamageCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "max-damage";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        int maxDamageKey = section.getInt("max-damage", -1);
        if (maxDamageKey > 0) {
            item.setData(DataComponentTypes.MAX_DAMAGE, maxDamageKey);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.MAX_DAMAGE)) {
            section.set("max-damage", item.getData(DataComponentTypes.MAX_DAMAGE));
        }
    }

}
