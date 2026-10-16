package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConsumableCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "consumable";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection consumableKey = section.getConfigurationSection("consumable");
        if (consumableKey != null) {
            Consumable.Builder builder = Consumable.consumable();
            if (consumableKey.contains("consume-seconds")) {
                builder.consumeSeconds((float) consumableKey.getDouble("consume-seconds"));
            }
            if (consumableKey.contains("animation")) {
                builder.animation(Enums.getIfPresent(ItemUseAnimation.class, consumableKey.getString("animation", "NONE")).or(ItemUseAnimation.NONE));
            }
            if (consumableKey.contains("sound")) {
                builder.sound(CommonUtil.parseNamespacedKey(consumableKey.getString("sound", "")));
            }
            if (consumableKey.contains("effects")) {
                for (String consumeEffects : consumableKey.getKeys(false)) {
                    switch (consumeEffects) {
                        case "apply-effect":
                            ConfigurationSection effectSection = consumableKey.getConfigurationSection(consumeEffects);
                            if (effectSection == null) {
                                continue;
                            }
                            List<PotionEffect> potionEffectList = new ArrayList<>();
                            for (String effects : effectSection.getStringList("effects")) {
                                String[] effectParseResult = effects.replace(" ", "").split(",");
                                if (effectParseResult.length < 4) {
                                    continue;
                                }
                                PotionEffectType potionEffectType = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(effectParseResult[0]));
                                if (potionEffectType != null) {
                                    PotionEffect potionEffect = new PotionEffect(potionEffectType,
                                            Integer.parseInt(effectParseResult[1]),
                                            Integer.parseInt(effectParseResult[2]),
                                            effectParseResult.length < 5 || Boolean.parseBoolean(effectParseResult[3]),
                                            effectParseResult.length < 6 || Boolean.parseBoolean(effectParseResult[4]),
                                            effectParseResult.length < 7 || Boolean.parseBoolean(effectParseResult[5]));
                                    potionEffectList.add(potionEffect);
                                }
                            }
                            builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffectList, (float) effectSection.getDouble("probability")));

                        case "play-sound":
                            builder.addEffect(ConsumeEffect.playSoundConsumeEffect(CommonUtil.parseNamespacedKey(consumableKey.getString("play-sound", ""))));

                        case "random-teleport":
                            builder.addEffect(ConsumeEffect.teleportRandomlyEffect((float) consumableKey.getDouble("random-teleport")));

                        case "clear-effect":
                            builder.addEffect(ConsumeEffect.clearAllStatusEffects());
                    }

                }
            }
            item.setData(DataComponentTypes.CONSUMABLE, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.CONSUMABLE)) {
            Consumable consumable = item.getData(DataComponentTypes.CONSUMABLE);
            if (consumable != null) {
                ConfigurationSection consumableSection = section.createSection("consumable");
                consumableSection.set("consume-seconds", consumable.consumeSeconds());
                consumableSection.set("animation", consumable.animation().name());
                consumableSection.set("sound", consumable.sound().asString());

                ConfigurationSection effectsSection = consumableSection.createSection("effects");
                int effectIndex = 0;
                for (ConsumeEffect effect : consumable.consumeEffects()) {
                    ConfigurationSection effectSub = effectsSection.createSection(String.valueOf(effectIndex++));
                    if (effect instanceof ConsumeEffect.ApplyStatusEffects potionEffect) {
                        effectSub.set("type", "apply-effect");
                        effectSub.set("probability", potionEffect.probability());
                        List<String> potions = potionEffect.effects().stream()
                                .map(pe -> pe.getType().getKey().getKey() + "," +
                                        pe.getDuration() + "," +
                                        pe.getAmplifier() + "," +
                                        pe.isAmbient() + "," +
                                        pe.hasParticles() + "," +
                                        pe.hasIcon())
                                .toList();
                        effectSub.set("effects", potions);
                    } else if (effect instanceof ConsumeEffect.PlaySound playSoundEffect) {
                        effectSub.set("type", "play-sound");
                        effectSub.set("sound", playSoundEffect.sound().asString());
                    } else if (effect instanceof ConsumeEffect.TeleportRandomly rtEffect) {
                        effectSub.set("type", "random-teleport");
                        effectSub.set("chance", rtEffect.diameter());
                    } else if (effect instanceof ConsumeEffect.RemoveStatusEffects) {
                        effectSub.set("type", "clear-effect");
                    }
                }
            }
        }
    }

}
