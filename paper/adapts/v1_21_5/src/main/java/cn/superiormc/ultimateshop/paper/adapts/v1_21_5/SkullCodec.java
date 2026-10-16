package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkullCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "skull";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String skullTextureNameKey = section.getString("skull-meta", section.getString("skull"));
        if (skullTextureNameKey != null) {
            skullTextureNameKey = TextUtil.withPAPI(skullTextureNameKey, player);
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
            profile.setProperty(new ProfileProperty("textures", skullTextureNameKey));
            item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(profile));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.PROFILE)) {
            ResolvableProfile profile = item.getData(DataComponentTypes.PROFILE);
            if (profile != null) {
                profile.properties().stream()
                        .filter(p -> "textures".equalsIgnoreCase(p.getName()))
                        .findFirst()
                        .map(ProfileProperty::getValue).ifPresent(textureValue -> section.set("skull", textureValue));

            }
        }
    }

}
