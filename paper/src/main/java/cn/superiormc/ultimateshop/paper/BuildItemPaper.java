package cn.superiormc.ultimateshop.paper;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class BuildItemPaper {
    public static ItemStack buildItemStack(Player player,
                                           ConfigurationSection section,
                                           int amount,
                                           String... args) {
        ItemStack item = new ItemStack(Material.STONE);
        return editItemStack(item, player, section, amount, args);
    }

    public static ItemStack editItemStack(ItemStack item,
                                          Player player,
                                          ConfigurationSection section,
                                          int amount,
                                          String... args) {

        // Amount
        if (amount > 0) {
            item.setAmount(amount);
        }

        // Custom Name
        String displayNameKey = section.getString("name", section.getString("display"));
        if (displayNameKey != null) {
            item.setData(DataComponentTypes.CUSTOM_NAME, PaperTextUtil.modernParse(displayNameKey, player));
        }

        // Item Name
        String itemNameKey = section.getString("item-name");
        if (itemNameKey != null) {
            item.setData(DataComponentTypes.ITEM_NAME, PaperTextUtil.modernParse(itemNameKey, player));
        }

        // Lore
        List<String> lores = section.getStringList("lore");
        if (!lores.isEmpty()) {
            ItemLore.Builder builder = ItemLore.lore();
            for (String lore : lores) {
                lore = CommonUtil.modifyString(lore, args);
                for (String singleLore : lore.split("\n")) {
                    builder.addLine(PaperTextUtil.modernParse(singleLore, player));
                }
            }
            item.setData(DataComponentTypes.LORE, builder.build());
        }

        // Custom Model Data Model
        ConfigurationSection customModelDataSection = section.getConfigurationSection("custom-model-data");
        if (customModelDataSection != null) {
            CustomModelData.Builder builder = CustomModelData.customModelData();
            for (String customModelDataType : customModelDataSection.getKeys(false)) {
                customModelDataType = customModelDataType.toLowerCase();
                switch (customModelDataType) {
                    case "float":
                        builder.addFloats(customModelDataSection.getFloatList("float"));
                    case "flag":
                        builder.addFlags(customModelDataSection.getBooleanList("flag"));
                    case "string":
                        builder.addStrings(customModelDataSection.getStringList("string"));
                    case "color":
                        builder.addColors(CommonUtil.parseColorList(customModelDataSection.getStringList("color")));
                }
            }
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build());
        }

        // Max Stack
        int maxStackKey = section.getInt("max-stack", -1);
        if (maxStackKey > 0 && maxStackKey < 100) {
            item.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackKey);
        }

        // Food
        ConfigurationSection foodKey = section.getConfigurationSection("food");
        if (foodKey != null) {
            FoodProperties.Builder builder = FoodProperties.food();
            if (foodKey.contains("can-always-eat")) {
                builder.canAlwaysEat(foodKey.getBoolean("can-always-eat"));
            }
            int foodNutrition = foodKey.getInt("nutrition", -1);
            if (foodNutrition > 0) {
                builder.nutrition(foodNutrition);
            }
            double foodSaturation = foodKey.getDouble("saturation", -1);
            if (foodSaturation > 0) {
                builder.saturation((float) foodSaturation);
            }
            item.setData(DataComponentTypes.FOOD, builder.build());
        }

        // Tool
        ConfigurationSection toolKey = section.getConfigurationSection("tool");
        if (toolKey != null) {
            Tool.Builder builder = Tool.tool();
            int damagePerBlock = toolKey.getInt("damage-per-block", -1);
            if (damagePerBlock >= 0) {
                builder.damagePerBlock(damagePerBlock);
            }
            double miningSpeed = toolKey.getDouble("mining-speed", -1);
            if (miningSpeed > 0) {
                builder.defaultMiningSpeed((float) miningSpeed);
            }
            if (toolKey.contains("destroy-blocks-in-creative")) {
                builder.canDestroyBlocksInCreative(toolKey.getBoolean("destroy-blocks-in-creative"));
            }
            for (String rules : toolKey.getStringList("rules")) {
                Set<TypedKey<BlockType>> blockTypeKeys = new HashSet<>();
                String[] ruleParseResult = rules.replace(" ", "").split(",");
                if (ruleParseResult.length < 3) {
                    continue;
                }
                int i = 0;
                for (String singleMaterial : ruleParseResult) {
                    TypedKey<BlockType> key = RegistryKey.BLOCK.typedKey(singleMaterial);
                    if (Registry.BLOCK.get(key) == null) {
                        break;
                    }
                    blockTypeKeys.add(key);
                    i ++;
                }
                RegistryKeySet<@org.jetbrains.annotations.NotNull BlockType> blockTypes = RegistrySet.keySet(RegistryKey.BLOCK, blockTypeKeys);
                Tool.Rule rule = Tool.rule(blockTypes, Float.parseFloat(ruleParseResult[i]), TriState.byBoolean(Boolean.parseBoolean(ruleParseResult[i + 1])));
                builder.addRule(rule);
            }
            item.setData(DataComponentTypes.TOOL, builder.build());
        }

        // Jukebox
        String song = section.getString("song");
        if (song != null) {
            JukeboxSong jukeboxSong = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).getOrThrow(CommonUtil.parseNamespacedKey(song));
            JukeboxPlayable.Builder builder = JukeboxPlayable.jukeboxPlayable(jukeboxSong);
            item.setData(DataComponentTypes.JUKEBOX_PLAYABLE, builder.build());
        }

        // Glow
        if (section.get("glow") != null) {
            item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, section.getBoolean("glow"));
        }

        // Unbreakable
        if (section.get("unbreakable") != null && section.getBoolean("unbreakable")) {
            item.setData(DataComponentTypes.UNBREAKABLE);
        }

        // Rarity
        String rarityKey = section.getString("rarity");
        if (rarityKey != null) {
            item.setData(DataComponentTypes.RARITY, Enums.getIfPresent(ItemRarity.class, rarityKey).or(ItemRarity.COMMON));
        }

        // Flag
        List<String> itemFlagKey = section.getStringList("hide-tooltip");
        if (!itemFlagKey.isEmpty()) {
            TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
            for (String flag : itemFlagKey) {
                DataComponentType type = Registry.DATA_COMPONENT_TYPE.get(CommonUtil.parseNamespacedKey(flag));
                if (type != null) {
                    builder.addHiddenComponents(type);
                }
            }
            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
        }

        // Enchantments
        ConfigurationSection enchantsKey = section.getConfigurationSection("enchants");
        if (enchantsKey != null) {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
            for (String ench : enchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(CommonUtil.parseNamespacedKey(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    builder.add(vanillaEnchant, enchantsKey.getInt(ench, 1));
                }
            }
            item.setData(DataComponentTypes.ENCHANTMENTS, builder.build());
        }

        // Attribute
        ConfigurationSection attributesKey = section.getConfigurationSection("attributes");
        if (attributesKey != null) {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
            for (String attribute : attributesKey.getKeys(false)) {
                Attribute attributeInst = Registry.ATTRIBUTE.get(CommonUtil.parseNamespacedKey(attribute));
                if (attributeInst == null) {
                    continue;
                }
                ConfigurationSection subSection = attributesKey.getConfigurationSection(attribute);
                if (subSection == null) {
                    continue;
                }

                String attribName = subSection.getString("name");
                double attribAmount = subSection.getDouble("amount");
                String attribOperation = subSection.getString("operation");

                String attribSlot = subSection.getString("slot");

                EquipmentSlotGroup slot = EquipmentSlotGroup.ANY;

                if (attribSlot != null) {
                    EquipmentSlotGroup targetSlot = EquipmentSlotGroup.getByName(attribSlot);
                    if (targetSlot != null) {
                        slot = targetSlot;
                    }
                }

                if (attribName != null && attribOperation != null) {
                    AttributeModifier modifier = new AttributeModifier(
                                CommonUtil.parseNamespacedKey(attribName),
                                attribAmount,
                                Enums.getIfPresent(AttributeModifier.Operation.class, attribOperation)
                                        .or(AttributeModifier.Operation.ADD_NUMBER),
                                slot);
                    builder.addModifier(attributeInst, modifier, slot);
                }
            }
            item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        }

        // Damage
        int damageKey = section.getInt("damage", -1);
        if (damageKey >= 0) {
            item.setData(DataComponentTypes.DAMAGE, damageKey);
        }

        // Max Damage
        int maxDamageKey = section.getInt("max-damage", -1);
        if (maxDamageKey > 0) {
            item.setData(DataComponentTypes.MAX_DAMAGE, maxDamageKey);
        }

        // Stored Enchantments
        ConfigurationSection storedEnchantsKey = section.getConfigurationSection("stored-enchants");
        if (storedEnchantsKey != null) {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
            for (String ench : storedEnchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(CommonUtil.parseNamespacedKey(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    builder.add(vanillaEnchant, storedEnchantsKey.getInt(ench, 1));
                }
            }
            item.setData(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
        }

        // Banner
        ConfigurationSection bannerPatternsKey = section.getConfigurationSection("banner-patterns");
        if (bannerPatternsKey != null) {
            BannerPatternLayers.Builder builder = BannerPatternLayers.bannerPatternLayers();
            for (String pattern : bannerPatternsKey.getKeys(false)) {
                //PatternType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).get(CommonUtil.parseNamespacedKey(pattern));
                PatternType type = Registry.BANNER_PATTERN.get(CommonUtil.parseNamespacedKey(pattern));
                String bannerColor = bannerPatternsKey.getString(pattern);
                if (type != null && bannerColor != null) {
                    DyeColor color = Enums.getIfPresent(DyeColor.class, bannerColor.toUpperCase()).or(DyeColor.WHITE);
                    builder.add(new Pattern(color, type));
                }
            }
            item.setData(DataComponentTypes.BANNER_PATTERNS, builder.build());
        }

        // Potion
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
                PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(CommonUtil.parseNamespacedKey(effectParseResult[0]));
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

        // Potion Duration Scale
        if (section.contains("potion-duration-scale")) {
            item.setData(DataComponentTypes.POTION_DURATION_SCALE, (float) section.getDouble("potion-duration-scale"));
        }

        // Charged Projectiles
        ConfigurationSection chargedProjectilesKey = section.getConfigurationSection("charged-projectiles");
        if (chargedProjectilesKey != null) {
            ChargedProjectiles.Builder builder = ChargedProjectiles.chargedProjectiles();
            for (String key : chargedProjectilesKey.getKeys(false)) {
                ConfigurationSection contentItemSection = chargedProjectilesKey.getConfigurationSection(key);
                if (contentItemSection != null) {
                    builder.add(buildItemStack(player,
                            contentItemSection,
                            contentItemSection.getInt("amount"),
                            args));
                }
            }
            item.setData(DataComponentTypes.CHARGED_PROJECTILES, builder.build());
        }

        // Armor Trim
        ConfigurationSection trimKey = section.getConfigurationSection("trim");
        if (trimKey != null) {
            String trimMaterialKey = trimKey.getString("material");
            String trimPatternKey = trimKey.getString("pattern");
            if (trimMaterialKey != null && trimPatternKey != null) {
                NamespacedKey trimMaterialNamespacedKey = CommonUtil.parseNamespacedKey(trimMaterialKey);
                NamespacedKey trimPatternNamespacedKey = CommonUtil.parseNamespacedKey(trimPatternKey);
                if (trimMaterialNamespacedKey != null && trimPatternNamespacedKey != null) {
                    TrimMaterial trimMaterial = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(trimMaterialNamespacedKey);
                    TrimPattern trimPattern = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(trimPatternNamespacedKey);
                    if (trimMaterial != null && trimPattern != null) {
                        item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(trimMaterial, trimPattern)));
                    }
                }
            }
        }

        // Leather Armor Color
        String colorKey = section.getString("leather-color");
        if (colorKey != null) {
            item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(CommonUtil.parseColor(colorKey)));
        }

        // Axolotl Bucket
        String variantStr = section.getString("axolotl-variant");
        if (variantStr != null) {
            Axolotl.Variant variant = Enums.getIfPresent(Axolotl.Variant.class, variantStr.toUpperCase()).orNull();
            if (variant != null) {
                item.setData(DataComponentTypes.AXOLOTL_VARIANT, variant);
            }
        }

        // Tropical Fish Bucket
        String baseColorKey = section.getString("tropical-fish-base-color");
        if (baseColorKey != null) {
            DyeColor color = Enums.getIfPresent(DyeColor.class, baseColorKey).or(DyeColor.WHITE);
            item.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, color);
        }

        String patternColorKey = section.getString("tropical-fish-pattern-color");
        if (patternColorKey != null) {
            DyeColor patternColor = Enums.getIfPresent(DyeColor.class, patternColorKey).or(DyeColor.WHITE);
            item.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, patternColor);
        }

        String patternKey = section.getString("tropical-fish-pattern");
        if (patternKey != null) {
            TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, patternKey).or(TropicalFish.Pattern.BETTY);
            item.setData(DataComponentTypes.TROPICAL_FISH_PATTERN, pattern);
        }

        // Skull
        String skullTextureNameKey = section.getString("skull-meta", section.getString("skull"));
        if (skullTextureNameKey != null) {
            if (!UltimateShop.freeVersion) {
                skullTextureNameKey = TextUtil.withPAPI(skullTextureNameKey, player);
            }
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
            profile.setProperty(new ProfileProperty("textures", skullTextureNameKey));
            item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(profile));
        }

        // Firework
        ConfigurationSection fireworkKey = section.getConfigurationSection("firework");
        if (fireworkKey != null) {
            Fireworks.Builder builder = Fireworks.fireworks();
            for (String fws : fireworkKey.getKeys(false)) {
                ConfigurationSection fireworkFwsSection = fireworkKey.getConfigurationSection(fws);
                FireworkEffect.Builder fireworkBuilder = FireworkEffect.builder();
                if (fireworkFwsSection != null) {
                    fireworkBuilder.flicker(fireworkFwsSection.getBoolean("flicker"));
                    fireworkBuilder.trail(fireworkFwsSection.getBoolean("trail"));
                    String fireworkType = fireworkFwsSection.getString("type");
                    if (fireworkType != null) {
                        fireworkBuilder.with(Enums.getIfPresent(FireworkEffect.Type.class, fireworkType.toUpperCase())
                                .or(FireworkEffect.Type.STAR));
                    }
                    ConfigurationSection colorsSection = fireworkFwsSection.getConfigurationSection("colors");
                    if (colorsSection != null) {
                        List<Color> colors = new ArrayList<>();
                        for (String colorStr : colorsSection.getStringList("base")) {
                            colors.add(CommonUtil.parseColor(colorStr));
                        }
                        fireworkBuilder.withColor(colors);

                        colors = new ArrayList<>();
                        for (String colorStr : colorsSection.getStringList("fade")) {
                            colors.add(CommonUtil.parseColor(colorStr));
                        }
                        fireworkBuilder.withFade(colors);
                        builder.addEffect(fireworkBuilder.build());
                    }
                }
                int duration = fireworkKey.getInt("duration");
                if (duration >= 0 && duration <= 255) {
                    builder.flightDuration(duration);
                }
            }
            item.setData(DataComponentTypes.FIREWORKS, builder.build());
        }

        // Suspicious Stew
        List<String> suspiciousStewEffects = section.getStringList("suspicious-stew-effects");
        if (!suspiciousStewEffects.isEmpty()) {
            SuspiciousStewEffects.Builder builder = SuspiciousStewEffects.suspiciousStewEffects();
            for (String effects : suspiciousStewEffects) {
                String[] effectParseResult = effects.replace(" ", "").split(",");
                if (effectParseResult.length < 3) {
                    continue;
                }
                PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(CommonUtil.parseNamespacedKey(effectParseResult[0]));
                if (potionEffectType != null) {
                    builder.add(SuspiciousEffectEntry.create(potionEffectType, Integer.parseInt(effectParseResult[1])));
                }
            }
            item.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, builder.build());
        }

        // Bundle
        ConfigurationSection bundleContentKey = section.getConfigurationSection("bundle-contents");
        if (bundleContentKey != null) {
            BundleContents.Builder builder = BundleContents.bundleContents();
            for (String key : bundleContentKey.getKeys(false)) {
                ConfigurationSection contentItemSection = bundleContentKey.getConfigurationSection(key);
                if (contentItemSection != null) {
                    builder.add(buildItemStack(player,
                            contentItemSection,
                            contentItemSection.getInt("amount"),
                            args));
                }
            }
            item.setData(DataComponentTypes.BUNDLE_CONTENTS, builder.build());

        }

        // Ominous Bottle
        int ominousPowerKey = section.getInt("ominous-bottle-amplifier", -1);
        if (ominousPowerKey >= 0 && ominousPowerKey <= 4) {
            item.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, OminousBottleAmplifier.amplifier(ominousPowerKey));
        }

        // Music Instrument
        String musicKey = section.getString("music");
        if (musicKey != null) {
            MusicInstrument musicInstrument = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).get(CommonUtil.parseNamespacedKey(musicKey));
            if (musicInstrument != null) {
                item.setData(DataComponentTypes.INSTRUMENT, musicInstrument);
            }
        }

        // Repairable
        int repairCost = section.getInt("repair-cost", -1);
        if (repairCost >= 0) {
            item.setData(DataComponentTypes.REPAIR_COST, repairCost);
        }

        // Enchantable
        int enchantable = section.getInt("enchantable", -1);
        if (enchantable >= 0) {
            item.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantable));
        }

        // Glider
        if (section.getString("glider") != null && section.getBoolean("glider")) {
            item.setData(DataComponentTypes.GLIDER);
        }

        // Item Model
        String itemModel = section.getString("item-model", null);
        if (itemModel != null) {
            item.setData(DataComponentTypes.ITEM_MODEL, CommonUtil.parseNamespacedKey(itemModel));
        }

        // Tooltip Style
        String tooltipStyle = section.getString("tooltip-style", null);
        if (tooltipStyle != null) {
            item.setData(DataComponentTypes.TOOLTIP_STYLE, CommonUtil.parseNamespacedKey(tooltipStyle));
        }

        // Use Cooldown
        ConfigurationSection useCooldown = section.getConfigurationSection("use-cooldown");
        if (useCooldown != null) {
            int cooldownSeconds = useCooldown.getInt("cooldown-seconds", -1);
            if (cooldownSeconds >= 0) {
                UseCooldown.Builder builder = UseCooldown.useCooldown(cooldownSeconds);
                String cooldownGroup = useCooldown.getString("cooldown-group", null);
                if (cooldownGroup != null) {
                    builder.cooldownGroup(CommonUtil.parseNamespacedKey(cooldownGroup));
                }
                item.setData(DataComponentTypes.USE_COOLDOWN, builder.build());
            }
        }

        // Equippable
        ConfigurationSection equippable = section.getConfigurationSection("equippable");
        if (!UltimateShop.freeVersion && equippable != null) {
            String slot = equippable.getString("slot");
            if (slot != null) {
                item.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.valueOf(slot.toUpperCase())));
            }
        }

        // Weapon
        ConfigurationSection weaponKey = section.getConfigurationSection("weapon");
        if (!UltimateShop.freeVersion && weaponKey != null) {
            Weapon.Builder builder = Weapon.weapon();
            if (weaponKey.contains("damage-per-attack")) {
                builder.itemDamagePerAttack(weaponKey.getInt("damage-per-attack"));
            }
            if (weaponKey.contains("disable-blocking-seconds")) {
                builder.disableBlockingForSeconds((float) weaponKey.getDouble("disable-blocking-seconds"));
            }
            item.setData(DataComponentTypes.WEAPON, builder.build());
        }

        // Damage Resistant
        String damageResistant = section.getString("damage-resistant");
        if (damageResistant != null) {
            TagKey<DamageType> damageTypeTag = TagKey.create(RegistryKey.DAMAGE_TYPE, damageResistant);
            item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(damageTypeTag));
        }

        // Block Attack
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
            if (blocksAttacksKey.contains("bypassed-by")) {
                TagKey<DamageType> damageTypeTag = TagKey.create(RegistryKey.DAMAGE_TYPE, blocksAttacksKey.getString("bypassed-by", ""));
                builder.bypassedBy(damageTypeTag);
            }
            item.setData(DataComponentTypes.BLOCKS_ATTACKS, builder.build());
        }

        // Break Sound
        String breakSoundKey = section.getString("break-sound");
        if (breakSoundKey != null) {
            item.setData(DataComponentTypes.BREAK_SOUND, CommonUtil.parseNamespacedKey(breakSoundKey));
        }

        // Use Reminder
        ConfigurationSection useReminderKey = section.getConfigurationSection("use-reminder");
        if (useReminderKey != null) {
            item.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(buildItemStack(player, useReminderKey, useReminderKey.getInt("amount"))));
        }

        // Consumable
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
                                PotionEffectType potionEffectType = Registry.POTION_EFFECT_TYPE.get(CommonUtil.parseNamespacedKey(effectParseResult[0]));
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
        return item;
    }
}
