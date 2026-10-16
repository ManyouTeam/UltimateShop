package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaxStackCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "max-stack";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        int maxStackKey = section.getInt("max-stack", -1);
        if (maxStackKey > 0 && maxStackKey < 100) {
            item.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackKey);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.MAX_STACK_SIZE)) {
            section.set("max-stack", item.getData(DataComponentTypes.MAX_STACK_SIZE));
        }
    }

}
