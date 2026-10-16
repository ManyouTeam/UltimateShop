package cn.superiormc.ultimateshop.paper.adapts.v1_21_11;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AttackRangeCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "attack-range-key";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection attackRangeKey = section.getConfigurationSection("attack-range-key");
        if (attackRangeKey != null) {
            AttackRange.Builder builder = AttackRange.attackRange();
            double minReach = attackRangeKey.getDouble("min-reach", -1);
            if (minReach >= 0 && minReach <= 64) {
                builder.minReach((float) minReach);
            }
            double maxReach = attackRangeKey.getDouble("max-reach", -1);
            if (maxReach >= 0 && maxReach <= 64) {
                builder.maxReach((float) maxReach);
            }
            double minCreativeReach = attackRangeKey.getDouble("min-creative-reach", -1);
            if (minCreativeReach >= 0 && minCreativeReach <= 64) {
                builder.minCreativeReach((float) minCreativeReach);
            }
            double maxCreativeReach = attackRangeKey.getDouble("max-creative-reach", -1);
            if (maxCreativeReach >= 0 && maxCreativeReach <= 64) {
                builder.maxCreativeReach((float) maxCreativeReach);
            }
            double hitboxMargin = attackRangeKey.getDouble("hitbox-margin", -1);
            if (hitboxMargin >= 0 && hitboxMargin <= 1) {
                builder.hitboxMargin((float) hitboxMargin);
            }
            double mobFactor = attackRangeKey.getDouble("mob-factor", -1);
            if (mobFactor >= 0 && mobFactor <= 2) {
                builder.mobFactor((float) mobFactor);
            }
            item.setData(DataComponentTypes.ATTACK_RANGE, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ATTACK_RANGE)) {
            AttackRange ar = item.getData(DataComponentTypes.ATTACK_RANGE);
            if (ar != null) {
                ConfigurationSection arSection = section.createSection("attack-range-key");
                arSection.set("min-reach", ar.minReach());
                arSection.set("max-reach", ar.maxReach());
                arSection.set("min-creative-reach", ar.minCreativeReach());
                arSection.set("max-creative-reach", ar.maxCreativeReach());
                arSection.set("hitbox-margin", ar.hitboxMargin());
                arSection.set("mob-factor", ar.mobFactor());
            }
        }
    }

}
