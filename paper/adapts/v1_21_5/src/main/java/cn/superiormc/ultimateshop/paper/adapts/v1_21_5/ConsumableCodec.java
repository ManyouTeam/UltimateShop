package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Registry;
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
            ConfigurationSection effectsSection = consumableKey.getConfigurationSection("effects");
            if (effectsSection != null) {
                for (String key : effectsSection.getKeys(false)) {
                    ConfigurationSection effectSection = effectsSection.getConfigurationSection(key);
                    if (effectSection != null) {
                        addEffect(builder, effectSection.getString("type", key), effectSection);
                    }
                }
            }
            for (String key : consumableKey.getKeys(false)) {
                if (key.equals("apply-effect")) {
                    ConfigurationSection effectSection = consumableKey.getConfigurationSection(key);
                    if (effectSection != null) {
                        addEffect(builder, key, effectSection);
                    }
                } else if (key.equals("play-sound") || key.equals("random-teleport") || key.equals("clear-effect")) {
                    addEffect(builder, key, consumableKey);
                }
            }
            item.setData(DataComponentTypes.CONSUMABLE, builder.build());
        }
    }

    private void addEffect(Consumable.Builder builder, String type, ConfigurationSection section) {
        switch (type) {
            case "apply-effect":
                List<PotionEffect> potionEffects = new ArrayList<>();
                for (String value : section.getStringList("effects")) {
                    String[] parts = value.replace(" ", "").split(",");
                    if (parts.length < 3) {
                        continue;
                    }
                    PotionEffectType effectType = Registry.EFFECT.get(CommonUtil.parseNamespacedKey(parts[0]));
                    if (effectType != null) {
                        potionEffects.add(new PotionEffect(effectType,
                                Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                                parts.length < 4 || Boolean.parseBoolean(parts[3]),
                                parts.length < 5 || Boolean.parseBoolean(parts[4]),
                                parts.length < 6 || Boolean.parseBoolean(parts[5])));
                    }
                }
                builder.addEffect(ConsumeEffect.applyStatusEffects(potionEffects,
                        (float) section.getDouble("probability", 1.0)));
                break;
            case "play-sound":
                builder.addEffect(ConsumeEffect.playSoundConsumeEffect(CommonUtil.parseNamespacedKey(
                        section.getString("sound", section.getString("play-sound", "minecraft:entity.generic.eat")))));
                break;
            case "random-teleport":
                builder.addEffect(ConsumeEffect.teleportRandomlyEffect((float) section.getDouble("diameter",
                        section.getDouble("chance", section.getDouble("random-teleport", 16)))));
                break;
            case "clear-effect":
                builder.addEffect(ConsumeEffect.clearAllStatusEffects());
                break;
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
                        effectSub.set("diameter", rtEffect.diameter());
                    } else if (effect instanceof ConsumeEffect.ClearAllStatusEffects) {
                        effectSub.set("type", "clear-effect");
                    }
                }
            }
        }
    }

}
