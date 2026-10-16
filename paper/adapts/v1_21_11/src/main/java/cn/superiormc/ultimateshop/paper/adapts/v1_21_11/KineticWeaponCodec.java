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

public class KineticWeaponCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "kinetic-weapon";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection kineticWeaponKey = section.getConfigurationSection("kinetic-weapon");
        if (kineticWeaponKey != null) {
            KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();
            int contactCooldown = kineticWeaponKey.getInt("contact-cooldown-ticks", -1);
            if (contactCooldown >= 0) {
                builder.contactCooldownTicks(contactCooldown);
            }
            int delayTicks = kineticWeaponKey.getInt("delay-ticks", -1);
            if (delayTicks >= 0) {
                builder.delayTicks(delayTicks);
            }
            double forwardMovement = kineticWeaponKey.getDouble("forward-movement", -1);
            if (forwardMovement >= 0) {
                builder.forwardMovement((float) forwardMovement);
            }
            double damageMultiplier = kineticWeaponKey.getDouble("damage-multiplier", -1);
            if (damageMultiplier >= 0) {
                builder.damageMultiplier((float) damageMultiplier);
            }
            String sound = kineticWeaponKey.getString("sound");
            if (sound != null) {
                builder.sound(CommonUtil.parseNamespacedKey(sound));
            }
            String hitSound = kineticWeaponKey.getString("hit-sound");
            if (hitSound != null) {
                builder.hitSound(CommonUtil.parseNamespacedKey(hitSound));
            }
            KineticWeapon.Condition damageCondition = parseKineticWeaponCondition(kineticWeaponKey, "damage-conditions");
            if (damageCondition != null) {
                builder.damageConditions(damageCondition);
            }
            KineticWeapon.Condition knockbackCondition = parseKineticWeaponCondition(kineticWeaponKey, "knockback-conditions");
            if (knockbackCondition != null) {
                builder.knockbackConditions(knockbackCondition);
            }
            KineticWeapon.Condition dismountCondition = parseKineticWeaponCondition(kineticWeaponKey, "dismount-conditions");
            if (dismountCondition != null) {
                builder.dismountConditions(dismountCondition);
            }
            item.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.KINETIC_WEAPON)) {
            KineticWeapon kineticWeapon = item.getData(DataComponentTypes.KINETIC_WEAPON);
            if (kineticWeapon != null) {
                ConfigurationSection kwSection = section.createSection("kinetic-weapon");
                kwSection.set("contact-cooldown-ticks", kineticWeapon.contactCooldownTicks());
                kwSection.set("delay-ticks", kineticWeapon.delayTicks());
                kwSection.set("forward-movement", kineticWeapon.forwardMovement());
                kwSection.set("damage-multiplier", kineticWeapon.damageMultiplier());
                if (kineticWeapon.sound() != null) {
                    kwSection.set("sound", kineticWeapon.sound().asString());
                }
                if (kineticWeapon.hitSound() != null) {
                    kwSection.set("hit-sound", kineticWeapon.hitSound().asString());
                }

                // Conditions
                if (kineticWeapon.damageConditions() != null) {
                    serializeKineticWeaponCondition(kineticWeapon.damageConditions(), kwSection, "damage-conditions");
                }
                if (kineticWeapon.knockbackConditions() != null) {
                    serializeKineticWeaponCondition(kineticWeapon.knockbackConditions(), kwSection, "knockback-conditions");
                }
                if (kineticWeapon.dismountConditions() != null) {
                    serializeKineticWeaponCondition(kineticWeapon.dismountConditions(), kwSection, "dismount-conditions");
                }
            }
        }
    }

    private static KineticWeapon.Condition parseKineticWeaponCondition(
            ConfigurationSection parent,
            String key
    ) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            return null;
        }

        int maxDuration = section.getInt("max-duration-ticks", -1);
        double minSpeed = section.getDouble("min-speed", -1);
        double minRelativeSpeed = section.getDouble("min-relative-speed", -1);

        if (maxDuration < 0 || minSpeed < 0 || minRelativeSpeed < 0) {
            return null;
        }

        return KineticWeapon.condition(
                maxDuration,
                (float) minSpeed,
                (float) minRelativeSpeed
        );
    }

    private static void serializeKineticWeaponCondition(
            KineticWeapon.Condition condition,
            ConfigurationSection parent,
            String key
    ) {
        if (condition == null) {
            return;
        }

        ConfigurationSection condSection = parent.createSection(key);
        condSection.set("max-duration-ticks", condition.maxDurationTicks());
        condSection.set("min-speed", condition.minSpeed());
        condSection.set("min-relative-speed", condition.minRelativeSpeed());
    }

}
