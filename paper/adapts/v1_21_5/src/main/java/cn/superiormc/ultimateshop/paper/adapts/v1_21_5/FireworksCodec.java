package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FireworksCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "firework";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection fireworkKey = section.getConfigurationSection("firework");
        if (fireworkKey != null) {
            Fireworks.Builder builder = Fireworks.fireworks();
            for (String fws : fireworkKey.getKeys(false)) {
                ConfigurationSection fireworkFwsSection = fireworkKey.getConfigurationSection(fws);
                FireworkEffect.Builder fireworkBuilder = FireworkEffect.builder();
                if (fireworkFwsSection != null) {
                    fireworkBuilder.flicker(fireworkFwsSection.getBoolean("flicker"));
                    fireworkBuilder.trail(fireworkFwsSection.getBoolean("trail"));
                    String fireworkType = fireworkFwsSection.getString("type");
                    if (fireworkType != null) {
                        fireworkBuilder.with(Enums.getIfPresent(FireworkEffect.Type.class, fireworkType.toUpperCase())
                                .or(FireworkEffect.Type.STAR));
                    }
                    ConfigurationSection colorsSection = fireworkFwsSection.getConfigurationSection("colors");
                    if (colorsSection != null) {
                        List<Color> colors = new ArrayList<>();
                        for (String colorStr : colorsSection.getStringList("base")) {
                            colors.add(CommonUtil.parseColor(colorStr));
                        }
                        fireworkBuilder.withColor(colors);

                        colors = new ArrayList<>();
                        for (String colorStr : colorsSection.getStringList("fade")) {
                            colors.add(CommonUtil.parseColor(colorStr));
                        }
                        fireworkBuilder.withFade(colors);
                        builder.addEffect(fireworkBuilder.build());
                    }
                }
                int duration = fireworkKey.getInt("duration");
                if (duration >= 0 && duration <= 255) {
                    builder.flightDuration(duration);
                }
            }
            item.setData(DataComponentTypes.FIREWORKS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.FIREWORKS)) {
            Fireworks fireworks = item.getData(DataComponentTypes.FIREWORKS);
            if (fireworks != null) {
                ConfigurationSection fwSection = section.createSection("firework");
                fwSection.set("duration", fireworks.flightDuration());
                int index = 0;
                for (FireworkEffect effect : fireworks.effects()) {
                    ConfigurationSection map = fwSection.createSection(String.valueOf(index));
                    map.set("flicker", effect.hasFlicker());
                    map.set("trail", effect.hasTrail());
                    map.set("type", effect.getType().name());
                    map.set("colors", effect.getColors().stream().map(CommonUtil::colorToString).toList());
                    map.set("fade", effect.getFadeColors().stream().map(CommonUtil::colorToString).toList());
                    index++;
                }
            }
        }
    }

}
