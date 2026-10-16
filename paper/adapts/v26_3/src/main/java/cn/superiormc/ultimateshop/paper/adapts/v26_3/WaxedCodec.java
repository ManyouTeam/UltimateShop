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

public class WaxedCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "waxed";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        if (section.getBoolean("waxed", false)) {
            item.setData(DataComponentTypes.WAXED);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.WAXED) && item.hasData(DataComponentTypes.WAXED)) {
            section.set("waxed", true);
        }
    }
}
