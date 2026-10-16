package cn.superiormc.ultimateshop.paper.adapts.v1_21_11;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwingAnimationCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "swing-animation";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection swingAnimationKey = section.getConfigurationSection("swing-animation");
        if (swingAnimationKey != null) {
            SwingAnimation.Builder builder = SwingAnimation.swingAnimation();
            String typeStr = swingAnimationKey.getString("type");
            if (typeStr != null) {
                builder.type(Enums.getIfPresent(SwingAnimation.Animation.class, typeStr).or(SwingAnimation.Animation.NONE));
            }
            int duration = swingAnimationKey.getInt("duration", -1);
            if (duration > 0) {
                builder.duration(duration);
            }
            item.setData(DataComponentTypes.SWING_ANIMATION, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.SWING_ANIMATION)) {
            SwingAnimation sa = item.getData(DataComponentTypes.SWING_ANIMATION);
            if (sa != null) {
                ConfigurationSection saSection = section.createSection("swing-animation");
                saSection.set("type", sa.type().name());
                saSection.set("duration", sa.duration());
            }
        }
    }

}
