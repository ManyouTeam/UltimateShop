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

public class BlocksAttacksCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "blocks-attacks";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection blocksAttacksKey = section.getConfigurationSection("blocks-attacks");
        if (blocksAttacksKey != null) {
            BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks();
            if (blocksAttacksKey.contains("block-delay-seconds")) {
                builder.blockDelaySeconds((float) blocksAttacksKey.getDouble("block-delay-seconds"));
            }
            if (blocksAttacksKey.contains("disable-cooldown-scale")) {
                builder.disableCooldownScale((float) blocksAttacksKey.getDouble("disable-cooldown-scale"));
            }
            if (blocksAttacksKey.contains("block-sound")) {
                builder.blockSound(CommonUtil.parseNamespacedKey(blocksAttacksKey.getString("block-sound", "")));
            }
            if (blocksAttacksKey.contains("bypassed-by") && CommonUtil.getYearVersion(26, 0, 0)) {
                String bypassedBy = blocksAttacksKey.getString("bypassed-by");
                if (bypassedBy != null) {
                    TagKey<DamageType> damageTypeTag = TagKey.create(RegistryKey.DAMAGE_TYPE, bypassedBy);
                    builder.bypassedBy(RegistryAccess.registryAccess()
                            .getRegistry(RegistryKey.DAMAGE_TYPE)
                            .getTag(damageTypeTag));
                } else if (blocksAttacksKey.isList("bypassed-by")) {
                    List<TypedKey<DamageType>> damageTypes = blocksAttacksKey.getStringList("bypassed-by").stream()
                            .map(key -> TypedKey.create(RegistryKey.DAMAGE_TYPE, key))
                            .toList();
                    builder.bypassedBy(RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, damageTypes));
                }
            }
            item.setData(DataComponentTypes.BLOCKS_ATTACKS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.BLOCKS_ATTACKS)) {
            BlocksAttacks blocks = item.getData(DataComponentTypes.BLOCKS_ATTACKS);
            if (blocks != null) {
                ConfigurationSection blocksSection = section.createSection("blocks-attacks");
                if (blocks.blockSound() != null) {
                    blocksSection.set("block-delay-seconds", blocks.blockSound().asString());
                }
                blocksSection.set("disable-cooldown-scale", blocks.disableCooldownScale());
                if (blocks.blockSound() != null) {
                    blocksSection.set("block-sound", blocks.blockSound().asString());
                }
                if (blocks.bypassedBy() != null && CommonUtil.getYearVersion(26, 0, 0)) {
                    blocksSection.set("bypassed-by", serializeDamageTypes(blocks.bypassedBy()));
                }
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
