package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectPlaceholderCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class ObjectCustomPlaceholder extends AbstractCustomPlaceholder {

    private final String type;

    public ObjectCustomPlaceholder(String id, ConfigurationSection section) {
        super(id, section, section.getBoolean("per-player-value", false));
        this.type = section.getString("type", "default").toLowerCase(Locale.ROOT);
    }

    public String getType() {
        return type;
    }

    public boolean isNumber() {
        return type.equals("number");
    }

    public String getDefaultValue() {
        ConfigurationSection section = getConfig();
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

    public boolean hasReset() {
        return isNumber() && !"NEVER".equals(getMode());
    }

    public String getCustomValue(ObjectPlaceholderCache<?> state, LocalDateTime now) {
        synchronized (state) {
            List<String> values = state.getStoredValues();
            LocalDateTime resetTime = state.getStoredResetTime();
            if (!hasReset() || (resetTime != null && !state.isExpired(now))) {
                return values == null ? getDefaultValue() : values.get(0);
            }
            getNextResetTime(now);
            return values != null && resetTime == null ? values.get(0) : getDefaultValue();
        }
    }

    public LocalDateTime getCustomResetTime(ObjectPlaceholderCache<?> state, LocalDateTime now) {
        synchronized (state) {
            if (!hasReset()) {
                return null;
            }
            LocalDateTime resetTime = state.getStoredResetTime();
            if (resetTime != null && !state.isExpired(now)) {
                return resetTime;
            }
            return "TIMER".equals(getMode()) ? null : getNextResetTime(now);
        }
    }

    public String setCustomValue(ObjectPlaceholderCache<?> state, String value, LocalDateTime now) {
        synchronized (state) {
            LocalDateTime resetTime = state.getStoredResetTime();
            if (hasReset() && (resetTime == null || state.isExpired(now))) {
                resetTime = getNextResetTime(now);
            }
            String normalized = normalizeValue(value);
            state.restoreState(List.of(normalized), resetTime, state.getLastResetTime());
            return normalized;
        }
    }

    public String addCustomValue(ObjectPlaceholderCache<?> state, BigDecimal amount, LocalDateTime now) {
        synchronized (state) {
            String current = getCustomValue(state, now);
            if (amount.signum() == 0) {
                return current;
            }
            return setCustomValue(state, new BigDecimal(current).add(amount).toPlainString(), now);
        }
    }

    public void loadCustomValue(ObjectPlaceholderCache<?> state, String storedValue) {
        int separator = isNumber() ? storedValue.indexOf('|') : -1;
        if (separator < 0) {
            state.restoreState(List.of(storedValue), null, null);
        } else {
            state.restoreState(List.of(storedValue.substring(0, separator)),
                    LocalDateTime.parse(storedValue.substring(separator + 1)), null);
        }
    }

    public String snapshotCustomValue(ObjectPlaceholderCache<?> state) {
        synchronized (state) {
            List<String> values = state.getStoredValues();
            if (values == null) {
                return null;
            }
            LocalDateTime resetTime = state.getStoredResetTime();
            if (!hasReset() || resetTime == null) {
                return values.get(0);
            }
            return values.get(0) + "|" + resetTime;
        }
    }

    public static String getNowValue(Player player, String id) {
        ObjectCustomPlaceholder placeholder = ConfigManager.configManager.getCustomPlaceholder(id);
        if (placeholder == null) {
            return "Error: Unknown Placeholder";
        }
        ObjectCache cache = placeholder.getCache(player);
        if (cache == null) {
            return placeholder.getDefaultValue();
        }
        return cache.getCustomPlaceholderValue(placeholder);
    }

    public static String getResetTime(Player player, String id) {
        ObjectCustomPlaceholder placeholder = ConfigManager.configManager.getCustomPlaceholder(id);
        if (placeholder == null || !placeholder.hasReset()) {
            return ConfigManager.configManager.getStringWithLang(player, "placeholder.refresh.never");
        }
        ObjectCache cache = placeholder.getCache(player);
        try {
            LocalDateTime reset = cache == null ? null : cache.getCustomPlaceholderResetTime(placeholder);
            if (reset == null) {
                return ConfigManager.configManager.getStringWithLang(player, "placeholder.refresh.never");
            }
            return CommonUtil.timeToString(reset,
                    ConfigManager.configManager.getStringWithLang(player, "placeholder.refresh.format"));
        } catch (RuntimeException exception) {
            ErrorManager.errorManager.sendErrorMessage("\u00a7cError: Invalid reset time for custom placeholder " + id
                    + ": " + exception.getMessage());
            return ConfigManager.configManager.getStringWithLang(player, "placeholder.refresh.never");
        }
    }

}
