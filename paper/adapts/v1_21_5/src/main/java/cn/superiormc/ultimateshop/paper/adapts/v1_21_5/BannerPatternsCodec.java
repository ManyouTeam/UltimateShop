package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BannerPatternsCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "banner-patterns";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        List<String> bannerPatterns = section.getStringList("banner-patterns");
        if (section.isList("banner-patterns")) {
            BannerPatternLayers.Builder builder = BannerPatternLayers.bannerPatternLayers();
            for (String bannerPattern : bannerPatterns) {
                int separatorIndex = bannerPattern.lastIndexOf(':');
                if (separatorIndex <= 0 || separatorIndex == bannerPattern.length() - 1) {
                    continue;
                }
                String pattern = bannerPattern.substring(0, separatorIndex).trim();
                String bannerColor = bannerPattern.substring(separatorIndex + 1).trim();
                PatternType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).get(CommonUtil.parseNamespacedKey(pattern));
                if (type != null) {
                    DyeColor color = Enums.getIfPresent(DyeColor.class, bannerColor.toUpperCase()).or(DyeColor.WHITE);
                    builder.add(new Pattern(color, type));
                }
            }
            item.setData(DataComponentTypes.BANNER_PATTERNS, builder.build());
        } else {
            // Backwards compatibility for the old "PATTERN: COLOR" map format.
            ConfigurationSection bannerPatternsKey = section.getConfigurationSection("banner-patterns");
            if (bannerPatternsKey != null) {
                BannerPatternLayers.Builder builder = BannerPatternLayers.bannerPatternLayers();
                for (String pattern : bannerPatternsKey.getKeys(false)) {
                    PatternType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).get(CommonUtil.parseNamespacedKey(pattern));
                    String bannerColor = bannerPatternsKey.getString(pattern);
                    if (type != null && bannerColor != null) {
                        DyeColor color = Enums.getIfPresent(DyeColor.class, bannerColor.toUpperCase()).or(DyeColor.WHITE);
                        builder.add(new Pattern(color, type));
                    }
                }
                item.setData(DataComponentTypes.BANNER_PATTERNS, builder.build());
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.BANNER_PATTERNS)) {
            BannerPatternLayers bannerPatterns = item.getData(DataComponentTypes.BANNER_PATTERNS);
            if (bannerPatterns != null) {
                List<String> bannerPatternList = new ArrayList<>();
                bannerPatterns.patterns().forEach(layer -> {
                    NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).
                            getKey(layer.getPattern());
                    if (key != null) {
                        bannerPatternList.add(key.asString() + ": " + layer.getColor().name());
                    }
                });
                section.set("banner-patterns", bannerPatternList);
            }
        }
    }

}
