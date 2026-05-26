package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class ObjectCustomPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    private final String type;

    private final boolean perPlayer;

    public ObjectCustomPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.type = section.getString("type", "default").toLowerCase();
        this.perPlayer = section.getBoolean("per-player-value", false);
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded custom placeholder: " + id + ".yml!");
    }

    public String getID() {
        return id;
    }

    public ConfigurationSection getConfig() {
        return section;
    }

    public String getType() {
        return type;
    }

    public boolean isNumber() {
        return type.equals("number");
    }

    public boolean isPerPlayer() {
        return perPlayer;
    }

    public String getDefaultValue() {
        String value = section.getString("default-value", section.getString("default", ""));
        if (isNumber()) {
            return normalizeNumber(value);
        }
        return value;
    }

    public String normalizeValue(String value) {
        if (!isNumber()) {
            return value;
        }
        return normalizeNumber(value);
    }

    public static String normalizeNumber(String value) {
        if (value == null || value.isEmpty()) {
            return "0";
        }
        try {
            return new BigDecimal(value).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException ignored) {
            return "0";
        }
    }

    public static String getNowValue(Player player, String id) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ObjectCustomPlaceholder placeholder = ConfigManager.configManager.getCustomPlaceholder(id);
        if (placeholder == null) {
            return "Error: Unknown Placeholder";
        }
        if (placeholder.isPerPlayer() && player == null) {
            return placeholder.getDefaultValue();
        }
        String value = placeholder.isPerPlayer()
                ? CacheManager.cacheManager.getObjectCache(player).getCustomPlaceholderCache().get(placeholder)
                : CacheManager.cacheManager.serverCache.getCustomPlaceholderCache().get(placeholder);
        if (value == null) {
            return placeholder.getDefaultValue();
        }
        return value;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ObjectCustomPlaceholder) {
            return ((ObjectCustomPlaceholder) object).getID().equals(getID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
