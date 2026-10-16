package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageResistantCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "damage-resistant";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String damageResistant = section.getString("damage-resistant");
        if (damageResistant != null && CommonUtil.getYearVersion(26, 0, 0)) {
            TagKey<DamageType> damageTypeTag = TagKey.create(RegistryKey.DAMAGE_TYPE, damageResistant);
            RegistryKeySet<DamageType> damageTypes = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.DAMAGE_TYPE)
                    .getTag(damageTypeTag);
            item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(damageTypes));
        } else if (section.isList("damage-resistant")) {
            List<TypedKey<DamageType>> damageTypes = section.getStringList("damage-resistant").stream()
                    .map(key -> TypedKey.create(RegistryKey.DAMAGE_TYPE, key))
                    .toList();
            item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(
                    RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, damageTypes)
            ));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.DAMAGE_RESISTANT) && CommonUtil.getYearVersion(26, 0, 0)) {
            DamageResistant resistant = item.getData(DataComponentTypes.DAMAGE_RESISTANT);
            if (resistant != null) {
                section.set("damage-resistant", serializeDamageTypes(resistant.types()));
            }
        }
    }

    private static Object serializeDamageTypes(RegistryKeySet<DamageType> damageTypes) {
        if (damageTypes instanceof Tag<?> tag) {
            return tag.tagKey().key().asString();
        }
        return damageTypes.values().stream()
                .map(TypedKey::asString)
                .toList();
    }

}
