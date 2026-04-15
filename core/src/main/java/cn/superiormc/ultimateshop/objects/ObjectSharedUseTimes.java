package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSharedUseTimes {

    private final String id;

    private final ConfigurationSection sharedConfig;

    public ObjectSharedUseTimes(String id, ConfigurationSection section) {
        this.id = id;
        this.sharedConfig = section;
    }

    public String getId() {
        return id;
    }

    public ConfigurationSection getSharedUseTimesSection(String path) {
        if (sharedConfig == null) {
            return null;
        }
        return sharedConfig.getConfigurationSection(path);
    }

    public String getSharedUseTimesString(String path) {
        return getSharedUseTimesString(path, null);
    }

    public String getSharedUseTimesStringOrDefault(String path, String defaultValue) {
        String value = getSharedUseTimesString(path, null);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public String getSharedUseTimesString(String path, String legacyPath) {
        if (sharedConfig == null) {
            return null;
        }
        String value = sharedConfig.getString(path);
        if (value == null && legacyPath != null) {
            value = sharedConfig.getString(legacyPath);
        }
        return value;
    }

    public String getSharedUseTimesStringOrDefault(String path, String legacyPath, String defaultValue) {
        String value = getSharedUseTimesString(path, legacyPath);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public int getSharedUseTimesIntWithPAPI(Player player, String path) {
        if (sharedConfig == null || !sharedConfig.contains(path)) {
            return Integer.MIN_VALUE;
        }
        return Integer.parseInt(TextUtil.withPAPI(sharedConfig.getString(path), player));
    }

    public ConfigurationSection getSection() {
        return sharedConfig;
    }
}
