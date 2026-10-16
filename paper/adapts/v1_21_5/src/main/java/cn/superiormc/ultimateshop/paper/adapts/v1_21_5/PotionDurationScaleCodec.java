package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PotionDurationScaleCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "potion-duration-scale";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        if (section.contains("potion-duration-scale")) {
            item.setData(DataComponentTypes.POTION_DURATION_SCALE, (float) section.getDouble("potion-duration-scale"));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.POTION_DURATION_SCALE)) {
            Float durationScale = item.getData(DataComponentTypes.POTION_DURATION_SCALE);
            if (durationScale != null) {
                section.set("potion-duration-scale", durationScale);
            }
        }
    }

}
