package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.configuration.ConfigurationSection;
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
            String bypassedBy = blocksAttacksKey.getString("bypassed-by");
            if (bypassedBy != null) {
                builder.bypassedBy(TagKey.create(RegistryKey.DAMAGE_TYPE, bypassedBy));
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
                blocksSection.set("block-delay-seconds", blocks.blockDelaySeconds());
                blocksSection.set("disable-cooldown-scale", blocks.disableCooldownScale());
                if (blocks.blockSound() != null) {
                    blocksSection.set("block-sound", blocks.blockSound().asString());
                }
                if (blocks.bypassedBy() != null) {
                    blocksSection.set("bypassed-by", blocks.bypassedBy().key().asString());
                }
            }
        }
    }

}
