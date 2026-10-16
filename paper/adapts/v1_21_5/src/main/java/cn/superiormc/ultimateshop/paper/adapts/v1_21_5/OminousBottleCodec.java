package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OminousBottleCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "ominous-bottle-amplifier";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        int ominousPowerKey = section.getInt("ominous-bottle-amplifier", -1);
        if (ominousPowerKey >= 0 && ominousPowerKey <= 4) {
            item.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, OminousBottleAmplifier.amplifier(ominousPowerKey));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)) {
            OminousBottleAmplifier amplifier = item.getData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
            if (amplifier != null) {
                section.set("ominous-bottle-amplifier", amplifier.amplifier());
            }
        }
    }

}
