package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import java.util.List;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SuspiciousStewCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "suspicious-stew-effects";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        List<String> suspiciousStewEffects = section.getStringList("suspicious-stew-effects");
        if (!suspiciousStewEffects.isEmpty()) {
            SuspiciousStewEffects.Builder builder = SuspiciousStewEffects.suspiciousStewEffects();
            for (String effects : suspiciousStewEffects) {
                String[] effectParseResult = effects.replace(" ", "").split(",");
                if (effectParseResult.length < 2) {
                    continue;
                }
                PotionEffectType potionEffectType = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(effectParseResult[0]));
                if (potionEffectType != null) {
                    builder.add(SuspiciousEffectEntry.create(potionEffectType, Integer.parseInt(effectParseResult[1])));
                }
            }
            item.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)) {
            SuspiciousStewEffects stewEffects = item.getData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
            List<String> effectStrings = null;
            if (stewEffects != null) {
                effectStrings = stewEffects.effects().stream()
                        .map(entry -> entry.effect().getKey().getKey() + "," + entry.duration() + ",1")
                        .toList();
            }
            section.set("suspicious-stew-effects", effectStrings);
        }
    }

}
