package cn.superiormc.ultimateshop.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public final class ConfigurationUtil {

    private ConfigurationUtil() {
    }

    public static void merge(ConfigurationSection target, ConfigurationSection source) {
        merge(target, source, true);
    }

    public static void mergeMissing(ConfigurationSection target, ConfigurationSection source) {
        merge(target, source, false);
    }

    private static void merge(ConfigurationSection target, ConfigurationSection source, boolean overwrite) {
        if (source == null) {
            return;
        }
        for (String key : source.getKeys(false)) {
            Object value = source.get(key);
            if (value instanceof ConfigurationSection sourceSection) {
                ConfigurationSection targetSection = target.getConfigurationSection(key);
                if (targetSection == null) {
                    targetSection = target.createSection(key);
                }
                merge(targetSection, sourceSection, overwrite);
            } else if (overwrite || !target.contains(key)) {
                target.set(key, value);
            }
        }
    }

    /**
     * Combines flattened configuration paths into a new root section.
     * This preserves the getValues(true) overlay behavior used by UI placements.
     */
    public static ConfigurationSection merged(ConfigurationSection defaults, ConfigurationSection overrides) {
        MemoryConfiguration result = new MemoryConfiguration();
        if (defaults != null) {
            defaults.getValues(true).forEach(result::set);
        }
        if (overrides != null) {
            overrides.getValues(true).forEach(result::set);
        }
        return result;
    }
}
