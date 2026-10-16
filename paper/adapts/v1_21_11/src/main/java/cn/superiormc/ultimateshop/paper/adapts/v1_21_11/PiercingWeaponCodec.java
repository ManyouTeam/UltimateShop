package cn.superiormc.ultimateshop.paper.adapts.v1_21_11;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PiercingWeaponCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "piercing-weapon";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection piercingWeaponKey = section.getConfigurationSection("piercing-weapon");
        if (piercingWeaponKey != null) {
            PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();
            if (piercingWeaponKey.contains("deals-knockback")) {
                builder.dealsKnockback(piercingWeaponKey.getBoolean("deals-knockback"));
            }
            if (piercingWeaponKey.contains("dismounts")) {
                builder.dismounts(piercingWeaponKey.getBoolean("dismounts"));
            }
            String sound = piercingWeaponKey.getString("sound");
            if (sound != null) {
                builder.sound(CommonUtil.parseNamespacedKey(sound));
            }
            String hitSound = piercingWeaponKey.getString("hit-sound");
            if (hitSound != null) {
                builder.hitSound(CommonUtil.parseNamespacedKey(hitSound));
            }
            item.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.PIERCING_WEAPON)) {
            PiercingWeapon pw = item.getData(DataComponentTypes.PIERCING_WEAPON);
            if (pw != null) {
                ConfigurationSection pwSection = section.createSection("piercing-weapon");
                pwSection.set("deals-knockback", pw.dealsKnockback());
                pwSection.set("dismounts", pw.dismounts());
                if (pw.sound() != null) {
                    pwSection.set("sound", pw.sound().asString());
                }
                if (pw.hitSound() != null) {
                    pwSection.set("hit-sound", pw.hitSound().asString());
                }
            }
        }
    }

}
