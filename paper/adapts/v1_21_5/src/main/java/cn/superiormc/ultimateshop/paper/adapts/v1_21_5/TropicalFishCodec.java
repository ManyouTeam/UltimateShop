package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;

public class TropicalFishCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "tropical-fish-base-color";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String baseColorKey = section.getString("tropical-fish-base-color");
        if (baseColorKey != null) {
            DyeColor color = Enums.getIfPresent(DyeColor.class, baseColorKey).or(DyeColor.WHITE);
            item.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, color);
        }

        String patternColorKey = section.getString("tropical-fish-pattern-color");
        if (patternColorKey != null) {
            DyeColor patternColor = Enums.getIfPresent(DyeColor.class, patternColorKey).or(DyeColor.WHITE);
            item.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, patternColor);
        }

        String patternKey = section.getString("tropical-fish-pattern");
        if (patternKey != null) {
            TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, patternKey).or(TropicalFish.Pattern.BETTY);
            item.setData(DataComponentTypes.TROPICAL_FISH_PATTERN, pattern);
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.TROPICAL_FISH_BASE_COLOR)) {
            DyeColor baseColor = item.getData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR);
            if (baseColor != null) {
                section.set("tropical-fish-base-color", baseColor.name());
            }
        }

        if (item.isDataOverridden(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR)) {
            DyeColor patternColor = item.getData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR);
            if (patternColor != null) {
                section.set("tropical-fish-pattern-color", patternColor.name());
            }
        }

        if (item.isDataOverridden(DataComponentTypes.TROPICAL_FISH_PATTERN)) {
            TropicalFish.Pattern fishPattern = item.getData(DataComponentTypes.TROPICAL_FISH_PATTERN);
            if (fishPattern != null) {
                section.set("tropical-fish-pattern", fishPattern.name());
            }
        }
    }

}
