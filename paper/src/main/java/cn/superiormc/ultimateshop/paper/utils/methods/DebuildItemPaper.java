package cn.superiormc.ultimateshop.paper.utils.methods;

import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.JukeboxSong;
import org.bukkit.MusicInstrument;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DebuildItemPaper {
    public static ConfigurationSection serializeItemStack(ItemStack item) {
        ConfigurationSection section = new MemoryConfiguration();
        // Amount
        section.set("amount", item.getAmount());

        // Custom Name
        if (item.hasData(DataComponentTypes.CUSTOM_NAME)) {
            section.set("name", PaperTextUtil.changeToString(item.getData(DataComponentTypes.CUSTOM_NAME)));
        }

        // Item Name
        if (item.hasData(DataComponentTypes.ITEM_NAME)) {
            section.set("item-name", PaperTextUtil.changeToString(item.getData(DataComponentTypes.ITEM_NAME)));
        }

        // Lore
        if (item.hasData(DataComponentTypes.LORE)) {
            ItemLore lore = item.getData(DataComponentTypes.LORE);
            if (lore != null) {
                List<String> lores = lore.lines().stream()
                        .map(PaperTextUtil::changeToString)
                        .toList();
                section.set("lore", lores);
            }
        }

        // Custom Model Data
        if (item.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            CustomModelData cmd = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
            if (cmd != null) {
                ConfigurationSection cmdSection = section.createSection("custom-model-data");
                cmdSection.set("float", cmd.floats());
                cmdSection.set("flag", cmd.flags());
                cmdSection.set("string", cmd.strings());
                cmdSection.set("color", cmd.colors().stream().map(CommonUtil::colorToString).toList());
            }
        }

        // Max Stack
        if (item.hasData(DataComponentTypes.MAX_STACK_SIZE)) {
            section.set("max-stack", item.getData(DataComponentTypes.MAX_STACK_SIZE));
        }

        // Food
        if (item.hasData(DataComponentTypes.FOOD)) {
            FoodProperties food = item.getData(DataComponentTypes.FOOD);
            if (food != null) {
                ConfigurationSection foodSection = section.createSection("food");
                foodSection.set("can-always-eat", food.canAlwaysEat());
                foodSection.set("nutrition", food.nutrition());
                foodSection.set("saturation", food.saturation());
            }
        }

        // Tool
        if (item.hasData(DataComponentTypes.TOOL)) {
            Tool tool = item.getData(DataComponentTypes.TOOL);
            if (tool != null) {
                ConfigurationSection toolSection = section.createSection("tool");
                toolSection.set("damage-per-block", tool.damagePerBlock());
                toolSection.set("mining-speed", tool.defaultMiningSpeed());
                toolSection.set("destroy-blocks-in-creative", tool.canDestroyBlocksInCreative());
                List<String> rules = new ArrayList<>();
                for (Tool.Rule rule : tool.rules()) {
                    Iterable<TypedKey<BlockType>> blocksIterable = rule.blocks();
                    String blocks = StreamSupport.stream(blocksIterable.spliterator(), false)
                            .map(TypedKey::asString)
                            .collect(Collectors.joining(","));
                    rules.add(blocks + "," + rule.speed() + "," + rule.correctForDrops().toBoolean());
                }
                toolSection.set("rules", rules);
            }
        }

        // Jukebox
        if (item.hasData(DataComponentTypes.JUKEBOX_PLAYABLE)) {
            JukeboxPlayable jukeboxPlayable = item.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
            if (jukeboxPlayable != null) {
                JukeboxSong song = jukeboxPlayable.jukeboxSong();
                section.set("song", song.key().asString());
            }
        }

        // Glow
        if (item.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)) {
            section.set("glow", item.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE));
        }

        // Unbreakable
        if (item.hasData(DataComponentTypes.UNBREAKABLE)) {
            section.set("unbreakable", true);
        }

        // Rarity
        if (item.hasData(DataComponentTypes.RARITY)) {
            ItemRarity rarity = item.getData(DataComponentTypes.RARITY);
            if (rarity != null) {
                section.set("rarity", rarity.name());
            }
        }

        // Flag
        if (item.hasData(DataComponentTypes.TOOLTIP_DISPLAY)) {
            TooltipDisplay tooltipDisplay = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
            if (tooltipDisplay != null) {
                List<String> hiddenComponents = tooltipDisplay.hiddenComponents()
                        .stream()
                        .map(DataComponentType::key)
                        .map(Key::asString)
                        .collect(Collectors.toList());
                section.set("hide-tooltip", hiddenComponents);
            }
        }

        // Enchantments
        if (item.hasData(DataComponentTypes.ENCHANTMENTS)) {
            ItemEnchantments enchants = item.getData(DataComponentTypes.ENCHANTMENTS);
            if (enchants != null) {
                ConfigurationSection enchantsSection = section.createSection("enchants"); // 创建子节
                enchants.enchantments().forEach((enchantment, level) -> {
                    Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getKey(enchantment);
                    if (key != null) {
                        enchantsSection.set(key.asString(), level); // 设置到配置
                    }
                });
            }
        }

        // Attribute Modifiers
        if (item.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers attributes = item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            if (attributes != null) {
                ConfigurationSection attributesSection = section.createSection("attributes");

                for (ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
                    Attribute attribute = entry.attribute();
                    AttributeModifier modifier = entry.modifier();
                    entry.getGroup();
                    EquipmentSlotGroup slot = entry.getGroup();
                    AttributeModifierDisplay display = entry.display();
                    ConfigurationSection attributeSection = attributesSection.createSection(attribute.getKey().getKey());
                    ConfigurationSection modSection = attributeSection.createSection(modifier.getName());
                    modSection.set("name", modifier.getName());
                    modSection.set("amount", modifier.getAmount());
                    modSection.set("operation", modifier.getOperation().name());
                    modSection.set("slot", slot.toString());

                    // Paper 1.21+ 显示模式
                    if (CommonUtil.getMinorVersion(21, 6)) {
                        if (display instanceof AttributeModifierDisplay.Hidden) {
                            modSection.set("display-mode", "HIDDEN");
                        } else if (display instanceof AttributeModifierDisplay.Default) {
                            modSection.set("display-mode", "RESET");
                        } else if (display instanceof AttributeModifierDisplay.OverrideText displayText) {
                            modSection.set("display-mode", "OVERRIDE");
                            modSection.set("display-text", displayText.text());
                        } else {
                            modSection.set("display-mode", "DEFAULT");
                        }
                    }
                }
            }
        }

        // Damage
        if (item.hasData(DataComponentTypes.DAMAGE)) {
            section.set("damage", item.getData(DataComponentTypes.DAMAGE));
        }

        // Stored Enchantments
        if (item.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) {
            ItemEnchantments enchants = item.getData(DataComponentTypes.STORED_ENCHANTMENTS);
            if (enchants != null) {
                ConfigurationSection enchantsSection = section.createSection("stored-enchants"); // 创建子节
                enchants.enchantments().forEach((enchantment, level) -> {
                    Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getKey(enchantment);
                    if (key != null) {
                        enchantsSection.set(key.asString(), level); // 设置到配置
                    }
                });
            }
        }

        // Banner Patterns
        if (item.hasData(DataComponentTypes.BANNER_PATTERNS)) {
            BannerPatternLayers bannerPatterns = item.getData(DataComponentTypes.BANNER_PATTERNS);
            if (bannerPatterns != null) {
                ConfigurationSection bannerSection = section.createSection("banner-patterns");
                bannerPatterns.patterns().forEach(layer -> {
                    org.bukkit.NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).
                            getKey(layer.getPattern());
                    if (key != null) {
                        bannerSection.set(key.asString(), layer.getColor().name());
                    }
                });
            }
        }

        // Potion Contents
        if (item.hasData(DataComponentTypes.POTION_CONTENTS)) {

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

        // Potion Duration Scale
        if (item.hasData(DataComponentTypes.POTION_DURATION_SCALE)) {
            Float durationScale = item.getData(DataComponentTypes.POTION_DURATION_SCALE);
            if (durationScale != null) {
                section.set("potion-duration-scale", durationScale);
            }
        }

        // Charged Projectiles
        if (item.hasData(DataComponentTypes.CHARGED_PROJECTILES)) {
            ChargedProjectiles chargedProjectiles = item.getData(DataComponentTypes.CHARGED_PROJECTILES);
            if (chargedProjectiles != null) {
                ConfigurationSection chargedSection = section.createSection("charged-projectiles");
                int index = 0;
                for (ItemStack subItem : chargedProjectiles.projectiles()) {
                    chargedSection.set(String.valueOf(index++), serializeItemStack(subItem));
                }
            }
        }

        // Armor Trim
        if (item.hasData(DataComponentTypes.TRIM)) {
            ItemArmorTrim armorTrim = item.getData(DataComponentTypes.TRIM);
            ConfigurationSection trimSection = section.createSection("trim");
            if (armorTrim != null) {
                trimSection.set("material", RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)
                        .getKey(armorTrim.armorTrim().getMaterial()));
                trimSection.set("pattern", RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN)
                        .getKey(armorTrim.armorTrim().getPattern()));
            }
        }

        // Leather Armor Color
        if (item.hasData(DataComponentTypes.DYED_COLOR)) {
            DyedItemColor dyedColor = item.getData(DataComponentTypes.DYED_COLOR);
            if (dyedColor != null) {
                section.set("leather-color", CommonUtil.colorToString(dyedColor.color()));
            }
        }

        // Axolotl Variant
        if (item.hasData(DataComponentTypes.AXOLOTL_VARIANT)) {
            Axolotl.Variant axolotlVariant = item.getData(DataComponentTypes.AXOLOTL_VARIANT);
            if (axolotlVariant != null) {
                section.set("axolotl-variant", axolotlVariant.name());
            }
        }

        // Tropical Fish
        if (item.hasData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR)) {
            DyeColor baseColor = item.getData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR);
            if (baseColor != null) {
                section.set("tropical-fish-base-color", baseColor.name());
            }
        }

        if (item.hasData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR)) {
            DyeColor patternColor = item.getData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR);
            if (patternColor != null) {
                section.set("tropical-fish-pattern-color", patternColor.name());
            }
        }

        if (item.hasData(DataComponentTypes.TROPICAL_FISH_PATTERN)) {
            TropicalFish.Pattern fishPattern = item.getData(DataComponentTypes.TROPICAL_FISH_PATTERN);
            if (fishPattern != null) {
                section.set("tropical-fish-pattern", fishPattern.name());
            }
        }

        // Skull
        if (item.hasData(DataComponentTypes.PROFILE)) {
            ResolvableProfile profile = item.getData(DataComponentTypes.PROFILE);
            if (profile != null) {
                profile.properties().stream()
                        .filter(p -> "textures".equalsIgnoreCase(p.getName()))
                        .findFirst()
                        .map(ProfileProperty::getValue).ifPresent(textureValue -> section.set("skull", textureValue));

            }
        }

        // Fireworks
        if (item.hasData(DataComponentTypes.FIREWORKS)) {
            Fireworks fireworks = item.getData(DataComponentTypes.FIREWORKS);
            if (fireworks != null) {
                ConfigurationSection fwSection = section.createSection("firework");
                fwSection.set("duration", fireworks.flightDuration());
                List<Map<String, Object>> effects = new ArrayList<>();
                for (FireworkEffect effect : fireworks.effects()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("flicker", effect.hasFlicker());
                    map.put("trail", effect.hasTrail());
                    map.put("type", effect.getType().name());
                    map.put("colors", effect.getColors().stream().map(CommonUtil::colorToString).toList());
                    map.put("fade", effect.getFadeColors().stream().map(CommonUtil::colorToString).toList());
                    effects.add(map);
                }
                fwSection.set("effects", effects);
            }
        }

        // Suspicious Stew
        if (item.hasData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)) {
            SuspiciousStewEffects stewEffects = item.getData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
            List<String> effectStrings = null;
            if (stewEffects != null) {
                effectStrings = stewEffects.effects().stream()
                        .map(entry -> entry.effect().getKey().getKey() + "," + entry.duration() + ",1")
                        .toList();
            }
            section.set("suspicious-stew-effects", effectStrings);
        }

        // Bundle Contents
        if (item.hasData(DataComponentTypes.BUNDLE_CONTENTS)) {
            BundleContents bundleContents = item.getData(DataComponentTypes.BUNDLE_CONTENTS);
            ConfigurationSection bundleSection = section.createSection("bundle-contents");
            int index = 0;
            if (bundleContents != null) {
                for (ItemStack subItem : bundleContents.contents()) {
                    bundleSection.set(String.valueOf(index++), serializeItemStack(subItem));
                }
            }
        }

        // Ominous Bottle
        if (item.hasData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)) {
            OminousBottleAmplifier amplifier = item.getData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
            if (amplifier != null) {
                section.set("ominous-bottle-amplifier", amplifier.amplifier());
            }
        }

        // Music Instrument
        if (item.hasData(DataComponentTypes.INSTRUMENT)) {
            MusicInstrument instrument = item.getData(DataComponentTypes.INSTRUMENT);
            if (instrument != null) {
                section.set("music", RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT)
                        .getKey(instrument));
            }
        }

        // Repairable
        if (item.hasData(DataComponentTypes.REPAIR_COST)) {
            Integer repairCost = item.getData(DataComponentTypes.REPAIR_COST);
            section.set("repair-cost", repairCost);
        }

        // Enchantable
        if (item.hasData(DataComponentTypes.ENCHANTABLE)) {
            Enchantable enchantable = item.getData(DataComponentTypes.ENCHANTABLE);
            if (enchantable != null) {
                section.set("enchantable", enchantable.value());
            }
        }

        // Glider
        if (item.hasData(DataComponentTypes.GLIDER)) {
            section.set("glider", true);
        }

        // Item Model
        if (item.hasData(DataComponentTypes.ITEM_MODEL)) {
            Key modelKey = item.getData(DataComponentTypes.ITEM_MODEL);
            if (modelKey != null) {
                section.set("item-model", modelKey.asString());
            }
        }

        // Tooltip Style
        if (item.hasData(DataComponentTypes.TOOLTIP_STYLE)) {
            Key tooltipKey = item.getData(DataComponentTypes.TOOLTIP_STYLE);
            if (tooltipKey != null) {
                section.set("tooltip-style", tooltipKey.asString());
            }
        }

        // Use Cooldown
        if (item.hasData(DataComponentTypes.USE_COOLDOWN)) {
            UseCooldown cooldown = item.getData(DataComponentTypes.USE_COOLDOWN);
            ConfigurationSection cooldownSection = section.createSection("use-cooldown");
            if (cooldown != null) {
                cooldownSection.set("cooldown-seconds", cooldown.cooldownGroup());
                if (cooldown.cooldownGroup() != null) {
                    cooldownSection.set("cooldown-group", cooldown.cooldownGroup().asString());
                }
            }
        }

        // Equippable
        if (item.hasData(DataComponentTypes.EQUIPPABLE)) {
            Equippable eq = item.getData(DataComponentTypes.EQUIPPABLE);
            if (eq != null) {
                ConfigurationSection equippableSection = section.createSection("equippable");
                equippableSection.set("slot", eq.slot().name());
                equippableSection.set("equip-sound", eq.equipSound().asString());
                if (eq.assetId() != null) equippableSection.set("asset-id", eq.assetId().asString());
                if (eq.cameraOverlay() != null)
                    equippableSection.set("camera-overlay", eq.cameraOverlay().asString());
                // Equippable allowed entities
                if (eq.allowedEntities() != null && !eq.allowedEntities().values().isEmpty()) {
                    equippableSection.set("allowed-entities",
                            eq.allowedEntities().values().stream()  // <-- 这里用 .values()
                                    .map(TypedKey::key)                // 获取 NamespacedKey
                                    .map(Key::asString)              // 转为 String
                                    .toList()
                    );
                }
                equippableSection.set("dispensable", eq.dispensable());
                equippableSection.set("swappable", eq.swappable());
                equippableSection.set("damage-on-hurt", eq.damageOnHurt());
                equippableSection.set("equip-on-interact", eq.equipOnInteract());
                if (CommonUtil.getMinorVersion(21, 6)) {
                    equippableSection.set("can-be-sheared", eq.canBeSheared());
                    equippableSection.set("shear-sound", eq.shearSound().asString());
                }
            }
        }

// Weapon
        if (item.hasData(DataComponentTypes.WEAPON)) {
            Weapon weapon = item.getData(DataComponentTypes.WEAPON);
            if (weapon != null) {
                ConfigurationSection weaponSection = section.createSection("weapon");
                weaponSection.set("damage-per-attack", weapon.itemDamagePerAttack());
                weaponSection.set("disable-blocking-seconds", weapon.disableBlockingForSeconds());
            }
        }


// Damage Resistant
        if (item.hasData(DataComponentTypes.DAMAGE_RESISTANT)) {
            DamageResistant resistant = item.getData(DataComponentTypes.DAMAGE_RESISTANT);
            if (resistant != null) {
            section.set("damage-resistant", resistant.types().key().asString());
            }
        }

// Blocks Attacks
        if (item.hasData(DataComponentTypes.BLOCKS_ATTACKS)) {
            BlocksAttacks blocks = item.getData(DataComponentTypes.BLOCKS_ATTACKS);
            if (blocks != null) {
                ConfigurationSection blocksSection = section.createSection("blocks-attacks");
                if (blocks.blockSound() != null) {
                    blocksSection.set("block-delay-seconds", blocks.blockSound().asString());
                }
                blocksSection.set("disable-cooldown-scale", blocks.disableCooldownScale());
                if (blocks.blockSound() != null) blocksSection.set("block-sound", blocks.blockSound().asString());
                if (blocks.bypassedBy() != null)
                    blocksSection.set("bypassed-by", blocks.bypassedBy().key().asString());
            }
        }

// Break Sound
        if (item.hasData(DataComponentTypes.BREAK_SOUND)) {
            if (item.getData(DataComponentTypes.BREAK_SOUND) != null) {
                section.set("break-sound", item.getData(DataComponentTypes.BREAK_SOUND).asString());
            }
        }

// Use Reminder
        if (item.hasData(DataComponentTypes.USE_REMAINDER)) {
            UseRemainder reminder = item.getData(DataComponentTypes.USE_REMAINDER);
            if (reminder != null) {
                ConfigurationSection reminderSection = section.createSection("use-reminder");
                reminderSection.set("item", serializeItemStack(reminder.transformInto())); // 递归序列化
            }
        }

// Consumable
        if (item.hasData(DataComponentTypes.CONSUMABLE)) {
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

        if (CommonUtil.getMinorVersion(21, 11)) {
            // Damage Type
            if (item.hasData(DataComponentTypes.DAMAGE_TYPE)) {
                DamageType damageType = item.getData(DataComponentTypes.DAMAGE_TYPE);
                if (damageType != null) {
                    section.set("damage-type", damageType.key().asString());
                }
            }

            // Kinetic Weapon
            if (item.hasData(DataComponentTypes.KINETIC_WEAPON)) {
                KineticWeapon kineticWeapon = item.getData(DataComponentTypes.KINETIC_WEAPON);
                if (kineticWeapon != null) {
                    ConfigurationSection kwSection = section.createSection("kinetic-weapon");
                    kwSection.set("contact-cooldown-ticks", kineticWeapon.contactCooldownTicks());
                    kwSection.set("delay-ticks", kineticWeapon.delayTicks());
                    kwSection.set("forward-movement", kineticWeapon.forwardMovement());
                    kwSection.set("damage-multiplier", kineticWeapon.damageMultiplier());
                    if (kineticWeapon.sound() != null) {
                        kwSection.set("sound", kineticWeapon.sound().asString());
                    }
                    if (kineticWeapon.hitSound() != null) {
                        kwSection.set("hit-sound", kineticWeapon.hitSound().asString());
                    }

                    // Conditions
                    if (kineticWeapon.damageConditions() != null) {
                        serializeKineticWeaponCondition(kineticWeapon.damageConditions(), kwSection, "damage-conditions");
                    }
                    if (kineticWeapon.knockbackConditions() != null) {
                        serializeKineticWeaponCondition(kineticWeapon.knockbackConditions(), kwSection, "knockback-conditions");
                    }
                    if (kineticWeapon.dismountConditions() != null) {
                        serializeKineticWeaponCondition(kineticWeapon.dismountConditions(), kwSection, "dismount-conditions");
                    }
                }
            }

            // Minimum Attack Charge
            if (item.hasData(DataComponentTypes.MINIMUM_ATTACK_CHARGE)) {
                Float minCharge = item.getData(DataComponentTypes.MINIMUM_ATTACK_CHARGE);
                if (minCharge != null) {
                    section.set("minimum-attack-charge", minCharge);
                }
            }

            // Piercing Weapon
            if (item.hasData(DataComponentTypes.PIERCING_WEAPON)) {
                PiercingWeapon pw = item.getData(DataComponentTypes.PIERCING_WEAPON);
                if (pw != null) {
                    ConfigurationSection pwSection = section.createSection("piercing-weapon");
                    pwSection.set("deals-knockback", pw.dealsKnockback());
                    pwSection.set("dismounts", pw.dismounts());
                    if (pw.sound() != null) {
                        pwSection.set("sound", pw.sound().asString());
                    }
                    if (pw.hitSound() != null) {
                        pwSection.set("hit-sound", pw.hitSound().asString());
                    }
                }
            }

            // Swing Animation
            if (item.hasData(DataComponentTypes.SWING_ANIMATION)) {
                SwingAnimation sa = item.getData(DataComponentTypes.SWING_ANIMATION);
                if (sa != null) {
                    ConfigurationSection saSection = section.createSection("swing-animation");
                    saSection.set("type", sa.type().name());
                    saSection.set("duration", sa.duration());
                }
            }

            // Use Effects
            if (item.hasData(DataComponentTypes.USE_EFFECTS)) {
                UseEffects ue = item.getData(DataComponentTypes.USE_EFFECTS);
                if (ue != null) {
                    ConfigurationSection ueSection = section.createSection("use-effects");
                    ueSection.set("can-sprint", ue.canSprint());
                    ueSection.set("interact-vibrations", ue.interactVibrations());
                    ueSection.set("speed-multiplier", ue.speedMultiplier());
                }
            }

            // Attack Range
            if (item.hasData(DataComponentTypes.ATTACK_RANGE)) {
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

        return section;
    }

    private static void serializeKineticWeaponCondition(
            KineticWeapon.Condition condition,
            ConfigurationSection parent,
            String key
    ) {
        if (condition == null) return;

        ConfigurationSection condSection = parent.createSection(key);
        condSection.set("max-duration-ticks", condition.maxDurationTicks());
        condSection.set("min-speed", condition.minSpeed());
        condSection.set("min-relative-speed", condition.minRelativeSpeed());
    }
}
