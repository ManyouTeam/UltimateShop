package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommandUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;

public class ObjectUseTimesCache {

    private static final int NEVER_REFRESH_YEAR = 2999;

    private final ObjectCache cache;

    private final UseTimesState buy = new UseTimesState();

    private final UseTimesState sell = new UseTimesState();

    private ObjectItem product;

    public ObjectUseTimesCache(ObjectCache cache) {
        this.cache = cache;
    }

    public ObjectUseTimesCache(ObjectCache cache,
                               int buyUseTimes,
                               int totalBuyUseTimes,
                               int sellUseTimes,
                               int totalSellUseTimes,
                               String lastBuyTime,
                               String lastSellTime,
                               String lastResetBuyTime,
                               String lastResetSellTime,
                               String cooldownBuyTime,
                               String cooldownSellTime,
                               ObjectItem product) {
        this(cache);
        loadState(buy, buyUseTimes, totalBuyUseTimes, lastBuyTime, lastResetBuyTime, cooldownBuyTime);
        loadState(sell, sellUseTimes, totalSellUseTimes, lastSellTime, lastResetSellTime, cooldownSellTime);
        this.product = product;
        if (product != null) {
            refreshTimes();
        }
    }

    private void loadState(UseTimesState state,
                           int useTimes,
                           int totalUseTimes,
                           String lastTime,
                           String lastResetTime,
                           String cooldownTime) {
        state.useTimes = useTimes;
        state.totalUseTimes = totalUseTimes;
        state.lastTime = parseTime(lastTime);
        state.lastResetTime = parseTime(lastResetTime);
        state.cooldownTime = parseTime(cooldownTime);
    }

    private LocalDateTime parseTime(String value) {
        return value == null ? null : CommonUtil.stringToTime(value);
    }

    public synchronized void bindProduct(ObjectItem product) {
        if (this.product != null || product == null) {
            return;
        }
        this.product = product;
        refreshTimes();
    }

    public synchronized ObjectItem getProduct() {
        return product;
    }

    public synchronized void refreshTimes() {
        refresh(Direction.BUY);
        refresh(Direction.SELL);
        initResetTask(Direction.BUY, null);
        initResetTask(Direction.SELL, null);
    }

    private void initResetTask(Direction direction, LocalDateTime refreshTime) {
        if (product == null || cache.canNotModify()
                || !ConfigManager.configManager.getBoolean("use-times.auto-reset-mode")) {
            return;
        }

        unregisterResetTask(direction);
        LocalDateTime target = refreshTime == null ? getRefreshTime(direction, false) : refreshTime;
        if (isNever(target)) {
            return;
        }

        if (direction == Direction.BUY) {
            ResetTaskPool.registerBuy(this, target);
        } else {
            ResetTaskPool.registerSell(this, target);
        }
    }

    private void unregisterResetTask(Direction direction) {
        if (direction == Direction.BUY) {
            ResetTaskPool.unregisterBuy(this);
        } else {
            ResetTaskPool.unregisterSell(this);
        }
    }

    public synchronized void cancelResetTime() {
        unregisterResetTask(Direction.BUY);
        unregisterResetTask(Direction.SELL);
    }

    public synchronized int getBuyUseTimes() {
        return buy.useTimes;
    }

    public synchronized int getTotalBuyUseTimes() {
        return UltimateShop.freeVersion ? 0 : buy.totalUseTimes;
    }

    public synchronized int getSellUseTimes() {
        return sell.useTimes;
    }

    public synchronized int getTotalSellUseTimes() {
        return UltimateShop.freeVersion ? 0 : sell.totalUseTimes;
    }

    public void setBuyUseTimes(int value) {
        setBuyUseTimes(value, false, false);
    }

    public synchronized void setBuyUseTimes(int value, boolean notUseBungee, boolean isReset) {
        setUseTimes(Direction.BUY, value, notUseBungee, isReset);
    }

    public void setSellUseTimes(int value) {
        setSellUseTimes(value, false, false);
    }

    public synchronized void setSellUseTimes(int value, boolean notUseBungee, boolean isReset) {
        setUseTimes(Direction.SELL, value, notUseBungee, isReset);
    }

    private void setUseTimes(Direction direction, int requestedValue, boolean notUseBungee, boolean isReset) {
        UseTimesState state = state(direction);
        if (product == null) {
            state.useTimes = requestedValue;
            return;
        }

        int maxTimes = getMaxTimes(direction);
        if (!isReset) {
            state.totalUseTimes = addWithoutOverflow(
                    state.totalUseTimes,
                    (long) requestedValue - state.useTimes);
            if (!UltimateShop.freeVersion
                    && ConfigManager.configManager.getBoolean("use-times.max-value-for-total-only")
                    && maxTimes >= 0) {
                state.totalUseTimes = Math.min(state.totalUseTimes, maxTimes);
            }
        }

        state.useTimes = !UltimateShop.freeVersion && maxTimes >= 0
                ? Math.min(requestedValue, maxTimes)
                : requestedValue;
        sendBungee(direction.timesChannel, String.valueOf(requestedValue), notUseBungee);
    }

    private int addWithoutOverflow(int currentValue, long delta) {
        long result = currentValue + delta;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (result < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) result;
    }

    public synchronized void setLastBuyTime(LocalDateTime time) {
        setLastTime(Direction.BUY, time, false, true);
    }

    public synchronized void setLastBuyTime(LocalDateTime time, boolean notUseBungee) {
        setLastTime(Direction.BUY, time, notUseBungee, false);
    }

    public synchronized void setLastSellTime(LocalDateTime time) {
        setLastTime(Direction.SELL, time, false, true);
    }

    public synchronized void setLastSellTime(LocalDateTime time, boolean notUseBungee) {
        setLastTime(Direction.SELL, time, notUseBungee, false);
    }

    private void setLastTime(Direction direction,
                             LocalDateTime time,
                             boolean notUseBungee,
                             boolean updateLastResetTime) {
        UseTimesState state = state(direction);
        if (time == null && updateLastResetTime) {
            state.lastResetTime = state.cooldownTime == null ? CommonUtil.getNowTime() : state.cooldownTime;
        }
        state.lastTime = time;
        sendBungee(direction.lastTimeChannel, formatTime(time), notUseBungee);
        if (time != null && resetTimeDependsOnLastTime(direction)) {
            initResetTask(direction, null);
        }
    }

    public synchronized void setCooldownBuyTime() {
        initResetTask(Direction.BUY, ensureCooldownTime(Direction.BUY, false));
    }

    public synchronized void setCooldownBuyTime(boolean notUseBungee) {
        initResetTask(Direction.BUY, ensureCooldownTime(Direction.BUY, notUseBungee));
    }

    public synchronized void setCooldownSellTime() {
        initResetTask(Direction.SELL, ensureCooldownTime(Direction.SELL, false));
    }

    public synchronized void setCooldownSellTime(boolean notUseBungee) {
        initResetTask(Direction.SELL, ensureCooldownTime(Direction.SELL, notUseBungee));
    }

    private LocalDateTime ensureCooldownTime(Direction direction, boolean notUseBungee) {
        if (product == null) {
            return null;
        }

        UseTimesState state = state(direction);
        LocalDateTime now = CommonUtil.getNowTime();
        if (state.cooldownTime != null && !state.cooldownTime.isBefore(now)) {
            return state.cooldownTime;
        }

        String mode = getResetMode(direction);
        String time = getResetTime(direction);
        if (mode == null || time.isEmpty()) {
            return state.cooldownTime;
        }

        switch (mode) {
            case "COOLDOWN_TIMED":
                state.cooldownTime = createTimedRefreshTime(direction, time, true);
                sendBungee(direction.cooldownChannel, null, notUseBungee);
                break;
            case "COOLDOWN_TIMER":
                state.cooldownTime = createTimerRefreshTime(direction, time, true);
                sendBungee(direction.cooldownChannel, null, notUseBungee);
                break;
            case "COOLDOWN_CUSTOM":
                state.cooldownTime = UltimateShop.freeVersion
                        ? CommonUtil.stringToTime(time)
                        : CommonUtil.stringToTime(time, getResetFormat(direction));
                break;
            default:
                break;
        }
        return state.cooldownTime;
    }

    public synchronized String getLastBuyTime() {
        return formatTime(buy.lastTime);
    }

    public synchronized String getLastSellTime() {
        return formatTime(sell.lastTime);
    }

    public synchronized String getLastResetBuyTime() {
        return formatTime(buy.lastResetTime);
    }

    public synchronized String getLastResetSellTime() {
        return formatTime(sell.lastResetTime);
    }

    public synchronized String getCooldownBuyTime() {
        return formatTime(buy.cooldownTime);
    }

    public synchronized String getCooldownSellTime() {
        return formatTime(sell.cooldownTime);
    }

    private String formatTime(LocalDateTime time) {
        return time == null ? null : CommonUtil.timeToString(time);
    }

    private LocalDateTime getRefreshTime(Direction direction, boolean updateCooldown) {
        if (product == null) {
            return null;
        }

        String mode = getResetMode(direction);
        String time = getResetTime(direction);
        if (isCooldownMode(mode)) {
            UseTimesState state = state(direction);
            if (updateCooldown || state.cooldownTime == null) {
                return ensureCooldownTime(direction, false);
            }
            return state.cooldownTime;
        }
        return createRefreshTime(direction, mode, time);
    }

    private boolean isCooldownMode(String mode) {
        return "COOLDOWN_TIMED".equals(mode)
                || "COOLDOWN_TIMER".equals(mode)
                || "COOLDOWN_CUSTOM".equals(mode);
    }

    private boolean resetTimeDependsOnLastTime(Direction direction) {
        String mode = getResetMode(direction);
        return "TIMED".equals(mode) || "TIMER".equals(mode);
    }

    private LocalDateTime createRefreshTime(Direction direction, String mode, String time) {
        if (mode == null) {
            return neverRefresh();
        }
        return switch (mode) {
            case "TIMED" -> createTimedRefreshTime(direction, time, false);
            case "TIMER" -> createTimerRefreshTime(direction, time, false);
            case "CUSTOM" -> UltimateShop.freeVersion
                    ? neverRefresh()
                    : CommonUtil.stringToTime(time, getResetFormat(direction));
            case "RANDOM_PLACEHOLDER" -> ObjectRandomPlaceholder.getRefreshDoneTimeObject(cache.getPlayer(), time);
            default -> neverRefresh();
        };
    }

    private LocalDateTime createTimedRefreshTime(Direction direction,
                                                 String configuredTime,
                                                 boolean startWithoutTrade) {
        LocalDateTime earliest = null;
        LocalDateTime checkTime = state(direction).lastTime;
        if (checkTime == null) {
            if (!startWithoutTrade) {
                return neverRefresh();
            }
            checkTime = CommonUtil.getNowTime();
        }

        for (String candidate : configuredTime.split(";;")) {
            String[] parts = candidate.split(":");
            if (parts.length < 3) {
                reportInvalidResetTime(candidate);
                return CommonUtil.getNowTime();
            }

            try {
                LocalDateTime result = getLocalDateTime(parts, checkTime);
                if (earliest == null || result.isBefore(earliest)) {
                    earliest = result;
                }
            } catch (NumberFormatException | java.time.DateTimeException exception) {
                reportInvalidResetTime(candidate);
                return CommonUtil.getNowTime();
            }

            if (UltimateShop.freeVersion) {
                break;
            }
        }
        return earliest;
    }

    private @NonNull LocalDateTime getLocalDateTime(String[] parts, LocalDateTime checkTime) {
        int month = parts.length == 5 ? Integer.parseInt(parts[0]) : 0;
        int day = parts.length >= 4 ? Integer.parseInt(parts[parts.length - 4]) : 0;
        LocalDateTime result = checkTime
                .withHour(Integer.parseInt(parts[parts.length - 3]))
                .withMinute(Integer.parseInt(parts[parts.length - 2]))
                .withSecond(Integer.parseInt(parts[parts.length - 1]))
                .plusMonths(month)
                .plusDays(day);
        if (!checkTime.isBefore(result)) {
            result = result.plusDays(1);
        }
        return result;
    }

    private LocalDateTime createTimerRefreshTime(Direction direction,
                                                 String configuredTime,
                                                 boolean startWithoutTrade) {
        String[] parts = configuredTime.split(":");
        if (parts.length < 3) {
            reportInvalidResetTime(configuredTime);
            return CommonUtil.getNowTime();
        }

        try {
            int month = !UltimateShop.freeVersion && parts.length == 5 ? Integer.parseInt(parts[0]) : 0;
            int day = !UltimateShop.freeVersion && parts.length >= 4
                    ? Integer.parseInt(parts[parts.length - 4])
                    : 0;
            LocalDateTime result = state(direction).lastTime;
            if (result == null) {
                if (!startWithoutTrade) {
                    return neverRefresh();
                }
                result = CommonUtil.getNowTime();
            }
            return result.plusMonths(month)
                    .plusDays(day)
                    .plusHours(Integer.parseInt(parts[parts.length - 3]))
                    .plusMinutes(Integer.parseInt(parts[parts.length - 2]))
                    .plusSeconds(Integer.parseInt(parts[parts.length - 1]));
        } catch (NumberFormatException | java.time.DateTimeException exception) {
            reportInvalidResetTime(configuredTime);
            return CommonUtil.getNowTime();
        }
    }

    private void reportInvalidResetTime(String time) {
        ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + time + " is invalid.");
    }

    public synchronized String getBuyRefreshTimeDisplayName(Player player) {
        return getRefreshTimeDisplayName(Direction.BUY, player);
    }

    public synchronized String getSellRefreshTimeDisplayName(Player player) {
        return getRefreshTimeDisplayName(Direction.SELL, player);
    }

    private String getRefreshTimeDisplayName(Direction direction, Player player) {
        LocalDateTime refreshTime = getRefreshTime(direction, true);
        if (isNever(refreshTime)) {
            return ConfigManager.configManager.getStringWithLang(player, "placeholder.refresh.never");
        }
        return CommonUtil.timeToString(
                refreshTime,
                ConfigManager.configManager.getStringWithLang(player, "placeholder.refresh.format"));
    }

    public synchronized String getBuyRefreshTimeNextName(Player player) {
        return UltimateShop.freeVersion ? "" : getRefreshTimeNextName(Direction.BUY, player);
    }

    public synchronized String getSellRefreshTimeNextName(Player player) {
        return UltimateShop.freeVersion ? "0" : getRefreshTimeNextName(Direction.SELL, player);
    }

    private String getRefreshTimeNextName(Direction direction, Player player) {
        LocalDateTime refreshTime = getRefreshTime(direction, true);
        if (isNever(refreshTime)) {
            return ConfigManager.configManager.getStringWithLang(player, "placeholder.next.never");
        }

        long totalSeconds = Duration.between(CommonUtil.getNowTime(), refreshTime).getSeconds();
        if (totalSeconds < 0) {
            return ConfigManager.configManager.getStringWithLang(player, "placeholder.next.never");
        }

        long days = totalSeconds / 86400;
        long hours = totalSeconds % 86400 / 3600;
        long minutes = totalSeconds % 3600 / 60;
        long seconds = totalSeconds % 60;
        if (days > 0) {
            return ConfigManager.configManager.getStringWithLang(player, "placeholder.next.with-day-format")
                    .replace("{d}", String.valueOf(days))
                    .replace("{h}", String.format("%02d", hours))
                    .replace("{m}", String.format("%02d", minutes))
                    .replace("{s}", String.format("%02d", seconds));
        }
        return ConfigManager.configManager.getStringWithLang(player, "placeholder.next.without-day-format")
                .replace("{h}", String.valueOf(hours))
                .replace("{m}", String.format("%02d", minutes))
                .replace("{s}", String.format("%02d", seconds));
    }

    public synchronized String getBuyLastTimeName() {
        return getLastTimeName(buy.lastTime);
    }

    public synchronized String getSellLastTimeName() {
        return getLastTimeName(sell.lastTime);
    }

    public synchronized String getBuyLastResetTimeName() {
        return getLastResetTimeName(buy);
    }

    public synchronized String getSellLastResetTimeName() {
        return getLastResetTimeName(sell);
    }

    private String getLastResetTimeName(UseTimesState state) {
        return state.lastResetTime == null || state.lastResetTime.getYear() == NEVER_REFRESH_YEAR
                ? getLastTimeName(state.lastTime)
                : getLastTimeName(state.lastResetTime);
    }

    private String getLastTimeName(LocalDateTime time) {
        if (UltimateShop.freeVersion || time == null || time.getYear() == NEVER_REFRESH_YEAR) {
            return "0";
        }
        return String.valueOf(Duration.between(time, CommonUtil.getNowTime()).getSeconds());
    }

    public synchronized void refreshBuyTimes() {
        refresh(Direction.BUY);
    }

    public synchronized void refreshSellTimes() {
        refresh(Direction.SELL);
    }

    private void refresh(Direction direction) {
        if (product == null) {
            return;
        }

        LocalDateTime refreshTime = getRefreshTime(direction, false);
        if (refreshTime == null || refreshTime.isAfter(CommonUtil.getNowTime())) {
            return;
        }

        setUseTimes(direction, getResetValue(direction), false, true);
        setLastTime(direction, null, false, true);
        state(direction).cooldownTime = null;
        if (resetTimeDependsOnLastTime(direction)) {
            unregisterResetTask(direction);
        } else {
            initResetTask(direction, null);
        }
        CommandUtil.updateGUI(cache.getPlayer());
    }

    public synchronized boolean isEmpty() {
        return buy.isEmpty() && sell.isEmpty();
    }

    private UseTimesState state(Direction direction) {
        return direction == Direction.BUY ? buy : sell;
    }

    private int getMaxTimes(Direction direction) {
        return direction == Direction.BUY
                ? product.getBuyTimesMaxValue(cache.getPlayer())
                : product.getSellTimesMaxValue(cache.getPlayer());
    }

    private int getResetValue(Direction direction) {
        return direction == Direction.BUY
                ? product.getBuyTimesResetValue(cache.getPlayer())
                : product.getSellTimesResetValue(cache.getPlayer());
    }

    private String getResetMode(Direction direction) {
        return direction == Direction.BUY ? product.getBuyTimesResetMode() : product.getSellTimesResetMode();
    }

    private String getResetTime(Direction direction) {
        String configuredTime = direction == Direction.BUY
                ? product.getBuyTimesResetTime()
                : product.getSellTimesResetTime();
        return TextUtil.withPAPI(configuredTime, cache.getPlayer());
    }

    private String getResetFormat(Direction direction) {
        String configuredFormat = direction == Direction.BUY
                ? product.getBuyTimesResetFormat()
                : product.getSellTimesResetFormat();
        return TextUtil.withPAPI(configuredFormat, cache.getPlayer());
    }

    private void sendBungee(String channel, String value, boolean disabled) {
        if (disabled || product == null || !cache.isServer() || BungeeCordManager.bungeeCordManager == null) {
            return;
        }
        BungeeCordManager.bungeeCordManager.sendToOtherServer(
                product.getUseTimesStorageShop(),
                product.getUseTimesStorageProduct(),
                channel,
                value);
    }

    private LocalDateTime neverRefresh() {
        return CommonUtil.getNowTime().withYear(NEVER_REFRESH_YEAR);
    }

    private boolean isNever(LocalDateTime time) {
        return time == null || time.getYear() == NEVER_REFRESH_YEAR;
    }

    @Override
    public String toString() {
        return cache.isServer() ? "Server Cache" : "Player Cache: " + cache.getPlayer().getName();
    }

    private enum Direction {
        BUY("buy-times", "last-buy-time", "cooldown-buy-time"),
        SELL("sell-times", "last-sell-time", "cooldown-sell-time");

        private final String timesChannel;
        private final String lastTimeChannel;
        private final String cooldownChannel;

        Direction(String timesChannel, String lastTimeChannel, String cooldownChannel) {
            this.timesChannel = timesChannel;
            this.lastTimeChannel = lastTimeChannel;
            this.cooldownChannel = cooldownChannel;
        }
    }

    private static final class UseTimesState {

        private int useTimes;
        private int totalUseTimes;
        private LocalDateTime lastTime;
        private LocalDateTime lastResetTime;
        private LocalDateTime cooldownTime;

        private boolean isEmpty() {
            return useTimes == 0 && totalUseTimes == 0;
        }
    }
}
