/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Crypto Morin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package cn.superiormc.ultimateshop.libs.xserieschanged;

import com.google.common.base.Strings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Potion type support for multiple aliases.
 * Uses EssentialsX potion list for aliases.
 * <p>
 * Duration: The duration of the effect in ticks. Values 0 or lower are treated as 1. Optional, and defaults to 1 tick.
 * Amplifier: The amplifier of the effect, with level I having value 0. Optional, and defaults to level I.
 * <p>
 * EssentialsX Potions: https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/com/earth2me/essentials/Potions.java
 * Status Effect: https://minecraft.wiki/w/Status_effect
 * Potions: https://minecraft.wiki/w/Potion
 *
 * @author Crypto Morin
 * @version 3.1.0
 * @see PotionEffect
 * @see PotionEffectType
 * @see PotionType
 */
public enum XPotion {
    ABSORPTION("ABSORB"),
    BAD_OMEN("OMEN_BAD", "PILLAGER"),
    BLINDNESS("BLIND"),
    CONDUIT_POWER("CONDUIT", "POWER_CONDUIT"),
    CONFUSION("NAUSEA", "SICKNESS", "SICK"),
    DAMAGE_RESISTANCE("RESISTANCE", "ARMOR", "DMG_RESIST", "DMG_RESISTANCE"),
    DARKNESS,
    DOLPHINS_GRACE("DOLPHIN", "GRACE"),
    FAST_DIGGING("HASTE", "SUPER_PICK", "DIGFAST", "DIG_SPEED", "QUICK_MINE", "SHARP"),
    FIRE_RESISTANCE("FIRE_RESIST", "RESIST_FIRE", "FIRE_RESISTANCE"),
    GLOWING("GLOW", "SHINE", "SHINY"),
    HARM("INJURE", "DAMAGE", "HARMING", "INFLICT", "INSTANT_DAMAGE"),
    HEAL("HEALTH", "INSTA_HEAL", "INSTANT_HEAL", "INSTA_HEALTH", "INSTANT_HEALTH"),
    HEALTH_BOOST("BOOST_HEALTH", "BOOST", "HP"),
    HERO_OF_THE_VILLAGE("HERO", "VILLAGE_HERO"),
    HUNGER("STARVE", "HUNGRY"),
    INCREASE_DAMAGE("STRENGTH", "BULL", "STRONG", "ATTACK"),
    INVISIBILITY("INVISIBLE", "VANISH", "INVIS", "DISAPPEAR", "HIDE"),
    JUMP("LEAP", "JUMP_BOOST"),
    LEVITATION("LEVITATE"),
    LUCK("LUCKY"),
    NIGHT_VISION("VISION", "VISION_NIGHT"),
    POISON("VENOM"),
    REGENERATION("REGEN"),
    SATURATION("FOOD"),
    SLOW("SLOWNESS", "SLUGGISH"),
    SLOW_DIGGING("FATIGUE", "DULL", "DIGGING", "SLOW_DIG", "DIG_SLOW"),
    SLOW_FALLING("SLOW_FALL", "FALL_SLOW"),
    SPEED("SPRINT", "RUNFAST", "SWIFT", "FAST"),
    UNLUCK("UNLUCKY"),
    WATER_BREATHING("WATER_BREATH", "UNDERWATER_BREATHING", "UNDERWATER_BREATH", "AIR"),
    WEAKNESS("WEAK"),
    WITHER("DECAY");

    /**
     * Cached list of {@link XPotion#values()} to avoid allocating memory for
     * calling the method every time.
     *
     * @since 1.0.0
     */
    public static final XPotion[] VALUES = values();

    /**
     * An unmodifiable set of "bad" potion effects.
     *
     * @since 1.1.0
     */
    public static final Set<XPotion> DEBUFFS = Collections.unmodifiableSet(EnumSet.of(
            BAD_OMEN, BLINDNESS, CONFUSION, HARM, HUNGER, LEVITATION, POISON,
            SLOW, SLOW_DIGGING, UNLUCK, WEAKNESS, WITHER)
    );

    /**
     * Efficient mapping to get {@link XPotion} from a {@link PotionEffectType}
     * Note that <code>values.length + 1</code> is intentional as it allocates one useless space since IDs start from 1
     */
    private static final XPotion[] POTIONEFFECTTYPE_MAPPING = new XPotion[VALUES.length + 1];

    static {
        for (XPotion pot : VALUES)
            if (pot.type != null) //noinspection deprecation
                POTIONEFFECTTYPE_MAPPING[pot.type.getId()] = pot;
    }

    private final PotionEffectType type;

    XPotion(@Nonnull String... aliases) {
        this.type = PotionEffectType.getByName(this.name());
        Data.NAMES.put(this.name(), this);
        for (String legacy : aliases) Data.NAMES.put(legacy, this);
    }

    /**
     * Attempts to build the string like an enum name.<br>
     * Removes all the spaces, numbers and extra non-English characters. Also removes some config/in-game based strings.
     * While this method is hard to maintain, it's extremely efficient. It's approximately more than x5 times faster than
     * the normal RegEx + String Methods approach for both formatted and unformatted material names.
     *
     * @param name the potion effect type name to format.
     * @return an enum name.
     * @since 1.0.0
     */
    @Nonnull
    private static String format(@Nonnull String name) {
        int len = name.length();
        char[] chs = new char[len];
        int count = 0;
        boolean appendUnderline = false;

        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);

            if (!appendUnderline && count != 0 && (ch == '-' || ch == ' ' || ch == '_') && chs[count] != '_')
                appendUnderline = true;
            else {
                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                    if (appendUnderline) {
                        chs[count++] = '_';
                        appendUnderline = false;
                    }
                    chs[count++] = (char) (ch & 0x5f);
                }
            }
        }

        return new String(chs, 0, count);
    }

    /**
     * Parses a potion effect type from the given string.
     * Supports type IDs.
     *
     * @param potion the type of the type's ID of the potion effect type.
     * @return a potion effect type.
     * @since 1.0.0
     */
    @Nonnull
    public static Optional<XPotion> matchXPotion(@Nonnull String potion) {
        if (potion == null || potion.isEmpty())
            throw new IllegalArgumentException("Cannot match XPotion of a null or empty potion effect type");
        PotionEffectType idType = fromId(potion);
        if (idType != null) {
            XPotion type = Data.NAMES.get(idType.getName());
            if (type == null) throw new NullPointerException("Unsupported potion effect type ID: " + idType);
            return Optional.of(type);
        }
        return Optional.ofNullable(Data.NAMES.get(format(potion)));
    }


    /**
     * Parses the type ID if available.
     *
     * @param type the ID of the potion effect type.
     * @return a potion effect type from the ID, or null if it's not an ID or the effect is not found.
     * @since 1.0.0
     */
    @Nullable
    @SuppressWarnings("deprecation")
    private static PotionEffectType fromId(@Nonnull String type) {
        try {
            int id = Integer.parseInt(type);
            return PotionEffectType.getById(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static List<String> split(@Nonnull String str, @SuppressWarnings("SameParameterValue") char separatorChar) {
        List<String> list = new ArrayList<>(5);
        boolean match = false, lastMatch = false;
        int len = str.length();
        int start = 0;

        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }

                // This is important, it should not be i++
                start = i + 1;
                continue;
            }

            lastMatch = false;
            match = true;
        }

        if (match || lastMatch) {
            list.add(str.substring(start, len));
        }
        return list;
    }

    /**
     * Parse a {@link PotionEffect} from a string, usually from config.
     * Supports potion type IDs.
     * <br>
     * Format: <b>Potion, Duration (in seconds), Amplifier (level) [%chance]</b>
     * <pre>
     *     WEAKNESS, 30, 1
     *     SLOWNESS 200 10
     *     1, 10000, 100 %50
     * </pre>
     * The last argument (the amplifier can also have a chance which if not met, returns null.
     *
     * @param potion the potion string to parse.
     * @return a potion effect, or null if the potion type is wrong.
     * @see #buildPotionEffect(int, int)
     * @since 1.0.0
     */
    @Nullable
    public static Effect parseEffect(@Nullable String potion) {
        if (Strings.isNullOrEmpty(potion) || potion.equalsIgnoreCase("none")) return null;
        List<String> split = split(potion.replace(" ", ""), ',');
        if (split.isEmpty()) split = split(potion, ' ');

        double chance = 100;
        int chanceIndex = 0;
        if (split.size() > 2) {
            chanceIndex = split.get(2).indexOf('%');
            if (chanceIndex != -1) {
                try {
                    chance = Double.parseDouble(split.get(2).substring(chanceIndex + 1));
                } catch (NumberFormatException ex) {
                    chance = 100;
                }
            }
        }

        Optional<XPotion> typeOpt = matchXPotion(split.get(0));
        if (!typeOpt.isPresent()) return null;
        PotionEffectType type = typeOpt.get().type;
        if (type == null) return null;

        int duration = 2400; // 20 ticks * 60 seconds * 2 minutes
        int amplifier = 0;
        if (split.size() > 1) {
            duration = toInt(split.get(1), 1) * 20;
            if (split.size() > 2)
                amplifier = toInt(chanceIndex <= 0 ? split.get(2) : split.get(2).substring(0, chanceIndex), 1) - 1;
        }

        return new Effect(new PotionEffect(type, duration, amplifier), chance);
    }

    private static int toInt(String str, @SuppressWarnings("SameParameterValue") int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    /**
     * In most cases you should be using {@link #name()} instead.
     *
     * @return a friendly readable string name.
     */
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(t -> t.charAt(0) + t.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * Used for data that need to be accessed during enum initialization.
     *
     * @since 2.0.0
     */
    private static final class Data {
        private static final Map<String, XPotion> NAMES = new HashMap<>();
    }

    /**
     * For now, this merely acts as a chance wrapper for potion effects.
     *
     * @since 3.0.0
     */
    public static class Effect {
        private PotionEffect effect;
        private double chance;

        public Effect(PotionEffect effect, double chance) {
            this.effect = effect;
            this.chance = chance;
        }

        public boolean hasChance() {
            return chance >= 100 || ThreadLocalRandom.current().nextDouble(0, 100) <= chance;
        }

        public void apply(LivingEntity entity) {
            if (hasChance()) entity.addPotionEffect(effect);
        }

        public PotionEffect getEffect() {
            return effect;
        }
    }
}