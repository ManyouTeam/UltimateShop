package cn.superiormc.ultimateshop.methods.Items;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrushableBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.Sound;

import java.lang.reflect.Field;
import java.util.*;

public class DebuildItem {

    public static ConfigurationSection debuildItem(ItemStack itemStack, ConfigurationSection section) {

        String[] tempVal1 = HookManager.hookManager.getHookItemPluginAndID(itemStack);
        if (tempVal1 != null) {
            section.set("hook-plugin", tempVal1[0]);
            section.set("hook-item", tempVal1[1]);
        } else {
            // Material
            section.set("material", itemStack.getType().name());
        }

        // Amount
        section.set("amount", itemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return section;
        }

        // Custom Name
        if (meta.hasDisplayName()) {
            if (UltimateShop.isPaper && ConfigManager.configManager.getBoolean("paper-api.use-component.item")) {
                section.set("name", meta.displayName());
            } else {
                section.set("name", meta.getDisplayName());
            }
        }

        // Item Name
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta.hasItemName()) {
                section.set("item-name", meta.getItemName());
            }
        }

        // Lore
        if (meta.hasLore()) {
            if (UltimateShop.isPaper && ConfigManager.configManager.getBoolean("paper-api.use-component.item")) {
                section.set("lore", meta.lore());
            } else {
                section.set("lore", meta.getLore());
            }
        }

        // Custom Model Data
        if (meta.hasCustomModelData()) {
            section.set("custom-model-data", meta.getCustomModelData());
        }

        // Max Stack
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta.hasMaxStackSize()) {
                section.set("max-stack", meta.getMaxStackSize());
            }
        }

        // Food
        if (CommonUtil.getMinorVersion(20, 5)) {
            FoodComponent foodComponent = meta.getFood();
            if (foodComponent.canAlwaysEat()) {
                section.set("food.can-alawys-eat", true);
            }
            if (foodComponent.getNutrition() > 0) {
                section.set("food.nutrition", foodComponent.getNutrition());
            }
            if (foodComponent.getSaturation() > 0) {
                section.set("food.saturation", foodComponent.getSaturation());
            }
            if (!CommonUtil.getMinorVersion(21, 2)) {
                if (foodComponent.getEatSeconds() != 1.6F) {
                    section.set("food.eat-seconds", foodComponent.getEatSeconds());
                }
                if (CommonUtil.getMajorVersion(21) && foodComponent.getUsingConvertsTo() != null) {
                    debuildItem(foodComponent.getUsingConvertsTo(), section.createSection("food.convert"));
                }
                List<String> effects = new ArrayList<>();
                for (FoodComponent.FoodEffect foodEffect : foodComponent.getEffects()) {
                    if (CommonUtil.getMajorVersion(18)) {
                        effects.add(foodEffect.getEffect().getType().getKey() + ", " + foodEffect.getEffect().getDuration() + ", " +
                                foodEffect.getEffect().getAmplifier() + ", " + foodEffect.getEffect().isAmbient() + ", " +
                                foodEffect.getEffect().hasParticles() + ", " + foodEffect.getEffect().hasIcon() + ", " +
                                foodEffect.getProbability());
                    } else {
                        effects.add(foodEffect.getEffect().getType().getName() + ", " + foodEffect.getEffect().getDuration() + ", " +
                                foodEffect.getEffect().getAmplifier() + ", " + foodEffect.getEffect().isAmbient() + ", " +
                                foodEffect.getEffect().hasParticles() + ", " + foodEffect.getEffect().hasIcon() + ", " +
                                foodEffect.getProbability());
                    }
                }
                if (!effects.isEmpty()) {
                    section.set("effects", effects);
                }
            }
        }

        // Tool
        if (CommonUtil.getMajorVersion(21)) {
            ToolComponent toolComponent = meta.getTool();
            if (toolComponent.getDamagePerBlock() != 0) {
                section.set("tool.damage-per-block", toolComponent.getDamagePerBlock());
            }
            if (toolComponent.getDefaultMiningSpeed() != 1) {
                section.set("tool.mining-speed", toolComponent.getDefaultMiningSpeed());
            }
            List<String> toolRules = new ArrayList<>();
            for (ToolComponent.ToolRule toolRule : toolComponent.getRules()) {
                if (toolRule.getBlocks().isEmpty()) {
                    continue;
                }
                StringBuilder materials = new StringBuilder();
                for (Material material : toolRule.getBlocks()) {
                    if (materials.toString().isEmpty()) {
                        materials.append(material.name());
                    } else {
                        materials.append(", ").append(material.name());
                    }
                }
                materials.append(", ").append(toolRule.getSpeed()).append(", ").append(toolRule.isCorrectForDrops());
                toolRules.add(materials.toString());
            }
            if (!toolRules.isEmpty()) {
                section.set("tool.rules", toolRules);
            }
        }

        // Jukebox Playable
        if (CommonUtil.getMajorVersion(21)) {
            JukeboxPlayableComponent jukeboxPlayableComponent = meta.getJukeboxPlayable();
            if (!jukeboxPlayableComponent.isShowInTooltip()) {
                section.set("show-song", jukeboxPlayableComponent.isShowInTooltip());
            }
            if (!jukeboxPlayableComponent.getSongKey().toString().equals("minecraft:13")) {
                section.set("song", jukeboxPlayableComponent.getSongKey().toString());
            }
        }

        // Fire Resistant
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta.isFireResistant()) {
                section.set("fire-resistant", "true");
            }
        }

        // Hide Tooltip
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta.isHideTooltip()) {
                section.set("hide-tool-tip", "true");
            }
        }

        // Glow
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta.hasEnchantmentGlintOverride()) {
                section.set("hide-tool-tip", "true");
            }
        }

        // Unbreakable
        if (meta.isUnbreakable()) {
            section.set("unbreakable", "true");
        }

        // Rarity
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta.hasRarity()) {
                section.set("rarity", meta.getRarity().name());
            }
        }

        // Item Flag
        Set<ItemFlag> flags = meta.getItemFlags();
        if (!flags.isEmpty()) {
            List<String> flagNames = new ArrayList<>();
            for (ItemFlag flag : flags) {
                flagNames.add(flag.name());
            }
            section.set("flags", flagNames);
        }

        // Enchantments
        for (Map.Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet()) {
            String entry = "enchants." + enchant.getKey().getKey().getKey();
            section.set(entry, enchant.getValue());
        }

        // Attribute
        Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
        if (attributes != null) {
            for (Map.Entry<Attribute, AttributeModifier> attribute : attributes.entries()) {
                String path = "attributes." + attribute.getKey().name() + '.';
                AttributeModifier modifier = attribute.getValue();

                if (!CommonUtil.getMajorVersion(21)) {
                    section.set(path + "id", modifier.getUniqueId().toString());
                }
                section.set(path + "name", modifier.getName());
                section.set(path + "amount", modifier.getAmount());
                section.set(path + "operation", modifier.getOperation().name());

                if (CommonUtil.getMinorVersion(20, 5)) {
                    section.set(path + "slot", modifier.getSlotGroup().toString());
                } else if (modifier.getSlot() != null) {
                    section.set(path + "slot", modifier.getSlot().name());
                }
            }
        }

        // Damage
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            if (damageable.hasDamage()) {
                section.set("damage", damageable.getDamage());
            }
            if (CommonUtil.getMinorVersion(20, 5)) {
                if (damageable.hasMaxDamage()) {
                    section.set("max-damage", damageable.getMaxDamage());
                }
            }
        }

        // Stored Enchantments
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
            for (Map.Entry<Enchantment, Integer> enchant : book.getStoredEnchants().entrySet()) {
                String entry = "stored-enchants." + enchant.getKey().getKey().getKey();
                section.set(entry, enchant.getValue());
            }
        }

        // Banner
        if (meta instanceof BannerMeta) {
            BannerMeta banner = (BannerMeta) meta;
            ConfigurationSection patterns = section.createSection("patterns");
            for (Pattern pattern : banner.getPatterns()) {
                patterns.set(pattern.getPattern().name(), pattern.getColor().name());
            }
        }

        // Potion
        if (meta instanceof PotionMeta) {
            PotionMeta potion = (PotionMeta) meta;
            List<String> effects = new ArrayList<>();
            for (PotionEffect effect : potion.getCustomEffects()) {
                if (CommonUtil.getMajorVersion(18)) {
                    effects.add(effect.getType().getKey() + ", " + effect.getDuration() + ", " + effect.getAmplifier() + ", " +
                            effect.getAmplifier() + ", " + effect.isAmbient() + ", " +
                            effect.hasParticles() + ", " + effect.hasIcon());
                } else {
                    effects.add(effect.getType().getName() + ", " + effect.getDuration() + ", " + effect.getAmplifier() + ", " +
                            effect.getAmplifier() + ", " + effect.isAmbient() + ", " +
                            effect.hasParticles() + ", " + effect.hasIcon());
                }
            }
            if (!effects.isEmpty()) {
                section.set("effects", effects);
            }
            if (CommonUtil.getMinorVersion(20, 5)) {
                PotionType potionType = potion.getBasePotionType();
                if (potionType != null) {
                    section.set("base-effect", potionType.getKey().toString());
                }
            } else {
                PotionData potionData = potion.getBasePotionData();
                if (potionData != null) {
                    section.set("base-effect", potionData.getType().name() + ", " + potionData.isExtended()
                            + ", " + potionData.isUpgraded());
                }
            }
            if (potion.hasColor()) {
                section.set("color", potion.getColor().asRGB());
            }
        }

        // Armor Trim
        if (CommonUtil.getMajorVersion(20)) {
            if (meta instanceof ArmorMeta) {
                ArmorMeta armorMeta = (ArmorMeta) meta;
                if (armorMeta.hasTrim()) {
                    ArmorTrim trim = armorMeta.getTrim();
                    ConfigurationSection trimConfig = section.createSection("trim");
                    trimConfig.set("material", trim.getMaterial().getKey().toString());
                    trimConfig.set("pattern", trim.getPattern().getKey().toString());
                }
            }
        }

        // Leather Armor Color
        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leather = (LeatherArmorMeta) meta;
            if (leather.getColor().asRGB() != 10511680) {
                Color color = leather.getColor();
                section.set("color", color.asRGB());
            }
        }

        // Axolotl Bucket
        if (CommonUtil.getMajorVersion(17)) {
            if (meta instanceof AxolotlBucketMeta) {
                AxolotlBucketMeta bucket = (AxolotlBucketMeta) meta;
                if (bucket.hasVariant()) {
                    section.set("color", bucket.getVariant().toString());
                }
            }
        }

        // Tropical Fish Bucket
        if (meta instanceof TropicalFishBucketMeta) {
            TropicalFishBucketMeta tropical = (TropicalFishBucketMeta) meta;
            if (tropical.hasVariant()) {
                section.set("pattern", tropical.getPattern().name());
                section.set("color", tropical.getBodyColor().name());
                section.set("pattern-color", tropical.getPatternColor().name());
            }
        }

        // Skull
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (skullMeta.hasOwner() && skullMeta.getOwningPlayer().getName() != null) {
                section.set("skull", skullMeta.getOwningPlayer().getName());
            } else {
                try {
                    Field field = skullMeta.getClass().getDeclaredField("profile");
                    field.setAccessible(true);
                    if (UltimateShop.newSkullMethod) {
                        Object playerProfile = field.get(skullMeta);
                        if (playerProfile != null) {
                            Field field2 = playerProfile.getClass().getDeclaredField("f");
                            field2.setAccessible(true);
                            GameProfile gameProfile = (GameProfile) field2.get(playerProfile);
                            if (gameProfile != null) {
                                Property property = gameProfile.getProperties().get("textures").iterator().next();
                                Field field3 = property.getClass().getDeclaredField("value");
                                field3.setAccessible(true);
                                section.set("skull", field3.get(property));
                            }
                        }
                    } else {
                        GameProfile gameProfile = (GameProfile) field.get(skullMeta);
                        if (gameProfile != null) {
                            Property property = gameProfile.getProperties().get("textures").iterator().next();
                            Field field3 = property.getClass().getDeclaredField("value");
                            field3.setAccessible(true);
                            section.set("skull", field3.get(property));
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[ManyouItems] §cError: Can not parse skull texture in a item!");
                }
            }
        }

        // Firework
        if (meta instanceof FireworkMeta) {
            FireworkMeta fireworkMeta = (FireworkMeta) meta;
            section.set("power", fireworkMeta.getPower());
            int i = 1;

            for (FireworkEffect fw : fireworkMeta.getEffects()) {
                section.set("firework." + i + ".type", fw.getType().name());
                section.set("firework." + i + ".flicker", fw.hasFlicker());
                section.set("firework." + i + ".trail", fw.hasTrail());

                List<Integer> baseColors = new ArrayList<>();
                List<Integer> fadeColors = new ArrayList<>();

                ConfigurationSection colors = section.createSection("firework." + i + ".colors");
                for (Color color : fw.getColors()) {
                    baseColors.add(color.asRGB());
                }
                colors.set("base", baseColors);

                for (Color color : fw.getFadeColors()) {
                    fadeColors.add(color.asRGB());
                }
                colors.set("fade", fadeColors);
                i++;
            }
        }

        // Firework Effect
        if (meta instanceof FireworkEffectMeta) {
            FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) meta;
            if (fireworkEffectMeta.hasEffect()) {
                FireworkEffect fireworkEffect = fireworkEffectMeta.getEffect();
                section.set("firework.type", fireworkEffect.getType().name());
                section.set("firework.flicker", fireworkEffect);
                section.set("firework.trail", fireworkEffect.hasTrail());

                List<Integer> baseColors = new ArrayList<>();
                List<Integer> fadeColors = new ArrayList<>();

                ConfigurationSection colors = section.createSection("firework.colors");
                for (Color color : fireworkEffect.getColors()) {
                    baseColors.add(color.asRGB());
                }
                colors.set("base", baseColors);

                for (Color color : fireworkEffect.getFadeColors()) {
                    fadeColors.add(color.asRGB());
                }
                colors.set("fade", fadeColors);
            }
        }

        // Suspicious Stew
        if (meta instanceof SuspiciousStewMeta) {
            SuspiciousStewMeta stew = (SuspiciousStewMeta) meta;
            List<String> effects = new ArrayList<>();

            for (PotionEffect effect : stew.getCustomEffects()) {
                if (CommonUtil.getMajorVersion(18)) {
                    effects.add(effect.getType().getKey() + ", " + effect.getDuration() + ", " + effect.getAmplifier());
                } else {
                    effects.add(effect.getType().getName() + ", " + effect.getDuration() + ", " + effect.getAmplifier());
                }
            }

            section.set("effects", effects);
        }

        // Bundle
        if (CommonUtil.getMajorVersion(17)) {
            if (meta instanceof BundleMeta) {
                BundleMeta bundleMeta = (BundleMeta) meta;

                if (bundleMeta.hasItems()) {
                    ConfigurationSection bundleContentKey = section.createSection("contents");
                    int i = 0;
                    for (ItemStack singleItem : bundleMeta.getItems()) {
                        if (singleItem != null && !singleItem.getType().isAir()) {
                            debuildItem(singleItem, bundleContentKey.createSection(Integer.toString(i)));
                        }
                        i++;
                    }
                }
            }
        }

        // Block
        if (meta instanceof BlockStateMeta) {
            BlockState state = ((BlockStateMeta) meta).getBlockState();

            if (state instanceof CreatureSpawner) {
                CreatureSpawner cs = (CreatureSpawner) state;
                if (cs.getSpawnedType() != null) {
                    section.set("spawner", cs.getSpawnedType().name());
                }
                section.set("min-delay", cs.getMinSpawnDelay());
                section.set("max-delay", cs.getMaxSpawnDelay());
                section.set("max-entities", cs.getMaxNearbyEntities());
                section.set("player-range", cs.getRequiredPlayerRange());
                section.set("spawn-range", cs.getSpawnRange());
            } else if (state instanceof ShulkerBox) {
                ShulkerBox box = (ShulkerBox) state;

                if (!box.getInventory().isEmpty()) {
                    ConfigurationSection shulkerContentKey = section.createSection("contents");
                    int i = 0;
                    for (ItemStack singleItem : box.getInventory().getContents()) {
                        if (singleItem != null && !singleItem.getType().isAir()) {
                            debuildItem(singleItem, shulkerContentKey.createSection(Integer.toString(i)));
                        }
                        i++;
                    }
                }
            } else if (CommonUtil.getMajorVersion(20) && state instanceof BrushableBlock) {
                BrushableBlock brushableBlock = (BrushableBlock) state;

                if (brushableBlock.getItem() != null && !brushableBlock.getItem().getType().isAir()) {
                    debuildItem(brushableBlock.getItem(), section.createSection("content"));
                }
            }
        }

        // Ominous Bottle
        if (CommonUtil.getMinorVersion(20, 5)) {
            if (meta instanceof OminousBottleMeta) {
                OminousBottleMeta ominousBottleMeta = (OminousBottleMeta) meta;
                if (ominousBottleMeta.hasAmplifier()) {
                    section.set("power", ominousBottleMeta.getAmplifier());
                }
            }
        }

        // Music Instrument
        if (CommonUtil.getMinorVersion(19, 3)) {
            if (meta instanceof MusicInstrumentMeta) {
                MusicInstrumentMeta musicInstrumentMeta = (MusicInstrumentMeta) meta;
                if (musicInstrumentMeta.getInstrument() != null) {
                    section.set("music", musicInstrumentMeta.getInstrument().getKey().toString());
                }
            }
        }

        // Repairable
        if (meta instanceof Repairable) {
            Repairable repairableMeta = (Repairable) meta;
            if (repairableMeta.hasRepairCost()) {
                section.set("repair-cost", repairableMeta.getRepairCost());
            }
        }

        if (CommonUtil.getMinorVersion(21, 2)) {
            // Enchantable
            if (meta.hasEnchantable()) {
                section.set("enchantable", meta.getEnchantable());
            }

            // Glider
            if (meta.isGlider()) {
                section.set("glider", meta.isGlider());
            }

            // Item Model
            if (meta.hasItemModel()) {
                section.set("item-model", meta.getItemModel());
            }

            // Tooltip Style
            if (meta.hasTooltipStyle()) {
                section.set("tooltip-style", meta.getTooltipStyle());
            }

            // Item Cooldown
            if (meta.hasUseCooldown()) {
                UseCooldownComponent useCooldownComponent = meta.getUseCooldown();
                section.set("use-cooldown.cooldown-group", useCooldownComponent.getCooldownGroup());
                section.set("use-cooldown.cooldown-seconds", useCooldownComponent.getCooldownSeconds());
            }

            // Equippable
            if (meta.hasEquippable()) {
                EquippableComponent equippableComponent = meta.getEquippable();
                Collection<EntityType> entities = equippableComponent.getAllowedEntities();
                if (entities != null && !entities.isEmpty()) {
                    section.set("equippable.entities", entities);
                }
                if (!equippableComponent.isDamageOnHurt()) {
                    section.set("equippable.damage-on-hurt", equippableComponent.isDamageOnHurt());
                }
                if (!equippableComponent.isDispensable()) {
                    section.set("equippable.dispensable", equippableComponent.isDispensable());
                }
                if (!equippableComponent.isSwappable()) {
                    section.set("equippable.swappable", equippableComponent.isSwappable());
                }
                NamespacedKey cameraOverlay = equippableComponent.getCameraOverlay();
                if (cameraOverlay != null) {
                    section.set("equippable.camera-overlay", cameraOverlay.asString());
                }
                NamespacedKey model = equippableComponent.getModel();
                if (model != null) {
                    section.set("equippable.model", model.asString());
                }
                section.set("equippable.equipment-slot", equippableComponent.getSlot().name());
                Sound equipSound = equippableComponent.getEquipSound();
                if (!equipSound.getKey().asString().equals("minecraft:item.armor.equip_generic")) {
                    section.set("equippable.sound", equipSound.getKey().asString());
                }
            }

            // Damage
            if (meta.hasDamageResistant()) {
                Tag<DamageType> damageTypeTag = meta.getDamageResistant();
                if (damageTypeTag != null) {
                    section.set("damage-resistant", damageTypeTag.getKey().asString());
                }
            }
        }

        return section;
    }
}
