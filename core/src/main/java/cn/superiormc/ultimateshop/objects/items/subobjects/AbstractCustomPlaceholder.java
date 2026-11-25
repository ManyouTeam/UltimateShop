package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Matcher;

public abstract class AbstractCustomPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    private final boolean perPlayer;

    public AbstractCustomPlaceholder(String id, ConfigurationSection section, boolean perPlayer) {
        this.id = id;
        this.section = section;
        this.perPlayer = perPlayer;
    }

    public String getID() {
        return id;
    }

    public ConfigurationSection getConfig() {
        return section;
    }

    public boolean isPerPlayer() {
        return perPlayer;
    }

    public String getMode() {
        return section.getString("reset-mode", "NEVER").toUpperCase(Locale.ROOT);
    }

    public String resolveConfiguredResetTime() {
        return TextUtil.withPAPI(section.getString("reset-time", ""), null);
    }

    public LocalDateTime calculateResetTime(String time, LocalDateTime now) {
        return switch (getMode()) {
            case "TIMER" -> CommonUtil.getTimerResetTime(time, now);
            case "TIMED" -> CommonUtil.getTimedResetTime(time, now);
            case "CUSTOM" -> CommonUtil.stringToTime(time, section.getString("time-format", "yyyy-MM-dd HH:mm:ss"));
            default -> throw new IllegalArgumentException("Unknown reset mode: " + getMode());
        };
    }

    public LocalDateTime getNextResetTime(LocalDateTime now) {
        Matcher cron = TextUtil.pattern7.matcher(section.getString("reset-time", ""));
        LocalDateTime resetTime = "CUSTOM".equals(getMode()) && cron.matches()
                ? TextUtil.getNextCronExecution(cron.group(1), now)
                : calculateResetTime(resolveConfiguredResetTime(), now);
        if (resetTime == null || !resetTime.isAfter(now)) {
            throw new IllegalArgumentException("Reset time must be in the future");
        }
        return resetTime;
    }

    protected ObjectCache getCache(Player player) {
        if (!isPerPlayer()) {
            return CacheManager.cacheManager.serverCache;
        }
        return player == null ? null : CacheManager.cacheManager.getObjectCache(player);
    }

    @Override
    public boolean equals(Object object) {
        return object != null && getClass() == object.getClass()
                && id.equals(((AbstractCustomPlaceholder) object).id);
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode() + id.hashCode();
    }
}
