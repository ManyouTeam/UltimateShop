package cn.superiormc.ultimateshop.paper.adapts.v1_21_11;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageTypeCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "damage-type";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String damageTypeKey = section.getString("damage-type");
        if (damageTypeKey != null) {
            DamageType damageType = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).get(CommonUtil.parseNamespacedKey(damageTypeKey));
            if (damageType != null) {
                item.setData(DataComponentTypes.DAMAGE_TYPE, damageType);
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.DAMAGE_TYPE)) {
            DamageType damageType = item.getData(DataComponentTypes.DAMAGE_TYPE);
            if (damageType != null) {
                section.set("damage-type", damageType.key().asString());
            }
        }
    }

}
