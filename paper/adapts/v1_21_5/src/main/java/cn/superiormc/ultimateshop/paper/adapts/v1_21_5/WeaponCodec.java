package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeaponCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "weapon";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection weaponKey = section.getConfigurationSection("weapon");
        if (weaponKey != null) {
            Weapon.Builder builder = Weapon.weapon();
            if (weaponKey.contains("damage-per-attack")) {
                builder.itemDamagePerAttack(weaponKey.getInt("damage-per-attack"));
            }
            if (weaponKey.contains("disable-blocking-seconds")) {
                builder.disableBlockingForSeconds((float) weaponKey.getDouble("disable-blocking-seconds"));
            }
            item.setData(DataComponentTypes.WEAPON, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.WEAPON)) {
            Weapon weapon = item.getData(DataComponentTypes.WEAPON);
            if (weapon != null) {
                ConfigurationSection weaponSection = section.createSection("weapon");
                weaponSection.set("damage-per-attack", weapon.itemDamagePerAttack());
                weaponSection.set("disable-blocking-seconds", weapon.disableBlockingForSeconds());
            }
        }
    }

}
