package cn.superiormc.ultimateshop.paper.adapts.v1_21_11;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UseEffectsCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "use-effects";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection useEffectsKey = section.getConfigurationSection("use-effects");
        if (useEffectsKey != null) {
            UseEffects.Builder builder = UseEffects.useEffects();
            if (useEffectsKey.contains("can-sprint")) {
                builder.canSprint(useEffectsKey.getBoolean("can-sprint"));
            }
            if (useEffectsKey.contains("interact-vibrations")) {
                builder.interactVibrations(useEffectsKey.getBoolean("interact-vibrations"));
            }
            double speedMultiplier = useEffectsKey.getDouble("speed-multiplier", -1);
            if (speedMultiplier >= 0 && speedMultiplier <= 1) {
                builder.speedMultiplier((float) speedMultiplier);
            }
            item.setData(DataComponentTypes.USE_EFFECTS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.USE_EFFECTS)) {
            UseEffects ue = item.getData(DataComponentTypes.USE_EFFECTS);
            if (ue != null) {
                ConfigurationSection ueSection = section.createSection("use-effects");
                ueSection.set("can-sprint", ue.canSprint());
                ueSection.set("interact-vibrations", ue.interactVibrations());
                ueSection.set("speed-multiplier", ue.speedMultiplier());
            }
        }
    }

}
