package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "potion";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection potionKey = section.getConfigurationSection("potion");
        if (potionKey != null) {
            String basePotionType = potionKey.getString("base-effect");
            assert basePotionType != null;
            PotionContents.Builder builder = PotionContents.potionContents();
            String[] singlePotion = basePotionType.replace(" ", "").split(",");
            PotionType potionType = Enums.getIfPresent(PotionType.class, singlePotion[0].toUpperCase()).orNull();
            builder.potion(potionType);

            // Custom Effect
            for (String effects : potionKey.getStringList("effects")) {
                String[] effectParseResult = effects.replace(" ", "").split(",");
                if (effectParseResult.length < 3) {
                    continue;
                }
                PotionEffectType potionEffectType = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(effectParseResult[0]));
                if (potionEffectType != null) {
                    PotionEffect potionEffect = new PotionEffect(potionEffectType,
                            Integer.parseInt(effectParseResult[1]),
                            Integer.parseInt(effectParseResult[2]),
                            effectParseResult.length < 4 || Boolean.parseBoolean(effectParseResult[3]),
                            effectParseResult.length < 5 || Boolean.parseBoolean(effectParseResult[4]),
                            effectParseResult.length < 6 || Boolean.parseBoolean(effectParseResult[5]));
                    builder.addCustomEffect(potionEffect);
                }
            }

            String potionColor = potionKey.getString("color");
            if (potionColor != null) {
                builder.customColor(CommonUtil.parseColor(potionColor));
            }

            String potionName = potionKey.getString("name");
            if (potionName != null) {
                builder.customName(potionName);
            }

            item.setData(DataComponentTypes.POTION_CONTENTS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.POTION_CONTENTS)) {
            PotionContents potionContents = item.getData(DataComponentTypes.POTION_CONTENTS);
            if (potionContents != null) {
                ConfigurationSection potionSection = section.createSection("potion");
                if (potionContents.potion() != null) {
                    potionSection.set("base-effect", potionContents.potion().name());
                }
                if (!potionContents.customEffects().isEmpty()) {
                    List<String> effects = potionContents.customEffects().stream().map(effect -> String.join(",",
                            effect.getType().getKey().getKey(),
                            String.valueOf(effect.getDuration()),
                            String.valueOf(effect.getAmplifier()),
                            String.valueOf(effect.isAmbient()),
                            String.valueOf(effect.hasParticles()),
                            String.valueOf(effect.hasIcon())
                    )).collect(Collectors.toList());
                    potionSection.set("effects", effects);
                }
                if (potionContents.customColor() != null) {
                    potionSection.set("color", CommonUtil.colorToString(potionContents.customColor()));
                }
                if (potionContents.customName() != null) {
                    potionSection.set("name", potionContents.customName());
                }
            }
        }
    }

}
