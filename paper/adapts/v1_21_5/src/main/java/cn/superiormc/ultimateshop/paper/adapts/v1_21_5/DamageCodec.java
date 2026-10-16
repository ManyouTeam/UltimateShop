package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "damage";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        int damageKey = section.getInt("damage", -1);
        if (damageKey >= 0) {
            item.setData(DataComponentTypes.DAMAGE, damageKey);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.DAMAGE)) {
            section.set("damage", item.getData(DataComponentTypes.DAMAGE));
        }
    }

}
