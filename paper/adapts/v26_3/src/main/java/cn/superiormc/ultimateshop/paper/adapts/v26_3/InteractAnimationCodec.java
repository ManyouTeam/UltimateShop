package cn.superiormc.ultimateshop.paper.adapts.v26_3;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;

public class InteractAnimationCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "interact-animation";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection animationSection = section.getConfigurationSection("interact-animation");
        if (animationSection == null) {
            return;
        }

        SwingAnimation.Builder builder = SwingAnimation.swingAnimation();
        String type = animationSection.getString("type");
        if (type != null) {
            builder.type(com.google.common.base.Enums.getIfPresent(SwingAnimation.Animation.class,
                    type.toUpperCase(java.util.Locale.ENGLISH)).or(SwingAnimation.Animation.NONE));
        }
        int duration = animationSection.getInt("duration", -1);
        if (duration > 0) {
            builder.duration(duration);
        }
        item.setData(DataComponentTypes.INTERACT_ANIMATION, builder.build());
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (!item.isDataOverridden(DataComponentTypes.INTERACT_ANIMATION)) {
            return;
        }
        SwingAnimation animation = item.getData(DataComponentTypes.INTERACT_ANIMATION);
        if (animation != null) {
            ConfigurationSection animationSection = section.createSection("interact-animation");
            animationSection.set("type", animation.type().name());
            animationSection.set("duration", animation.duration());
        }
    }
}
