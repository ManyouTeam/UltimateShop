package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.database.PlayerDataSnapshot.RandomPlaceholderSnapshot;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommandUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ObjectRandomPlaceholderCache extends ObjectPlaceholderCache<ObjectRandomPlaceholder> {

    private static final int NEVER_REFRESH_YEAR = 2999;

    private boolean initialized;

    public ObjectRandomPlaceholderCache(ObjectCache cache, ObjectRandomPlaceholder placeholder) {
        super(cache, placeholder);
    }

    public synchronized void initialize() {
        if (initialized || cache.canNotModify()) {
            return;
        }
        initialized = true;
        setRefreshTime();
    }

    public synchronized void loadState(List<String> value,
                                       LocalDateTime refreshTime,
                                       LocalDateTime loadedLastResetTime) {
        if (value == null) {
            return;
        }
        cancelResetTask();
        restoreState(value, refreshTime, loadedLastResetTime);
        migrateLegacyCustomState();
        this.initialized = true;
        activateResetTask();
    }

    public synchronized void loadState(List<String> value, LocalDateTime refreshTime) {
        loadState(value, refreshTime, null);
    }

    public synchronized LocalDateTime getRefreshDoneTime() {
        if ("CUSTOM".equals(placeholder.getMode())
                || isExpired(CommonUtil.getNowTime())) {
            setRefreshTime();
        }
        return refreshDoneTime;
    }

    public synchronized void removeRefreshDoneTime() {
        refreshDoneTime = null;
        lastResetTime = null;
    }

    public synchronized void cancelResetTask() {
        RandomPlaceholderResetTaskPool.unregister(this);
    }

    public synchronized void activateResetTask() {
        if (!initialized || cache.canNotModify()) {
            return;
        }
        if ("CUSTOM".equals(placeholder.getMode())) {
            setRefreshTime();
            scheduleResetTask();
        } else {
            scheduleResetTask();
        }
    }

    public List<String> getNowValue() {
        return getNowValue(true, false);
    }

    public List<String> getNowValue(boolean disable) {
        return getNowValue(true, disable);
    }

    public synchronized List<String> getNowValue(boolean needRefresh, boolean disable) {
        if (needRefresh) {
            setRefreshTime(disable);
        }
        return hasValue() ? new ArrayList<>(getStoredValues()) : null;
    }

    public synchronized void setRefreshTime() {
        setRefreshTime(false);
    }

    public synchronized void setRefreshTime(boolean notUseBungee) {
        if (cache.canNotModify()) {
            cancelResetTask();
            return;
        }
        String mode = placeholder.getMode();
        String time = placeholder.resolveConfiguredResetTime();
        if (time.isEmpty()) {
            if (!hasValue()) {
                setPlaceholder(notUseBungee);
            }
            return;
        }
        if (mode.equals("ONCE")) {
            lastResetTime = CommonUtil.getNowTime();
            setPlaceholder(notUseBungee);
            return;
        }
        LocalDateTime now = CommonUtil.getNowTime();
        LocalDateTime customRefreshTime = null;
        if (mode.equals("CUSTOM")) {
            customRefreshTime = getCustomRefreshTime(time, now);
            if (customRefreshTime == null) {
                refreshDoneTime = neverRefresh();
                cancelResetTask();
                if (!hasValue()) {
                    setPlaceholder(notUseBungee);
                }
                return;
            }
            if (lastResetTime != null && !lastResetTime.isBefore(customRefreshTime)) {
                refreshDoneTime = neverRefresh();
                cancelResetTask();
                if (!hasValue()) {
                    setPlaceholder(notUseBungee);
                }
                return;
            }
            if (customRefreshTime.isAfter(now)
                    && hasValue()
                    && refreshDoneTime != null
                    && (isNeverRefresh(refreshDoneTime) || refreshDoneTime.isAfter(now))
                    && !customRefreshTime.equals(refreshDoneTime)) {
                refreshDoneTime = customRefreshTime;
                cancelResetTask();
                scheduleResetTask();
                return;
            }
        }
        boolean hadValue = hasValue();
        boolean needRefresh = !hasValue() || refreshDoneTime == null
                || isExpired(now)
                || mode.equals("CUSTOM") && !customRefreshTime.isAfter(now);
        for (ObjectRandomPlaceholder tempVal1 : placeholder.getNotSameAs()) {
            if (tempVal1.equals(getPlaceholder())) {
                continue;
            }
            if (tempVal1.getNowValue(cache).equals(getStoredValues())) {
                needRefresh = true;
            }
        }
        if (needRefresh) {
            switch (mode) {
                case "TIMED":
                case "TIMER":
                    refreshDoneTime = placeholder.calculateResetTime(time, now);
                    break;
                case "CUSTOM":
                    refreshDoneTime = customRefreshTime;
                    if (!customRefreshTime.isAfter(now)) {
                        refreshDoneTime = neverRefresh();
                    }
                    break;
                case "RANDOM_PLACEHOLDER":
                    if (time.equals(placeholder.getID())) {
                        refreshDoneTime = neverRefresh();
                    } else {
                        refreshDoneTime = ObjectRandomPlaceholder.getRefreshDoneTimeObject(cache.getPlayer(), time);
                    }
                    break;
                default:
                    refreshDoneTime = neverRefresh();
                    break;
            }

            cancelResetTask();
            scheduleResetTask();
            if (hadValue || mode.equals("CUSTOM") && !customRefreshTime.isAfter(now)) {
                lastResetTime = now;
            }
            setPlaceholder(notUseBungee);
            CommandUtil.updateGUI(cache.getPlayer());
        }
    }

    private void scheduleResetTask() {
        String mode = placeholder.getMode();
        if (!isSchedulableMode(mode)
                || !ConfigManager.configManager.getBoolean("use-times.auto-reset-mode")
                || refreshDoneTime == null
                || isNeverRefresh(refreshDoneTime)) {
            return;
        }

        RandomPlaceholderResetTaskPool.register(this, refreshDoneTime);
    }

    private boolean isSchedulableMode(String mode) {
        return "TIMED".equals(mode)
                || "TIMER".equals(mode)
                || "CUSTOM".equals(mode)
                || "RANDOM_PLACEHOLDER".equals(mode);
    }

    private LocalDateTime getCustomRefreshTime(String time, LocalDateTime now) {
        try {
            return placeholder.calculateResetTime(time, now);
        } catch (DateTimeException exception) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + time + " is invalid.");
            return null;
        }
    }

    private void migrateLegacyCustomState() {
        if (!"CUSTOM".equals(placeholder.getMode())
                || lastResetTime != null
                || !isNeverRefresh(refreshDoneTime)) {
            return;
        }
        lastResetTime = CommonUtil.getNowTime();
    }

    private LocalDateTime neverRefresh() {
        return CommonUtil.getNowTime().withYear(NEVER_REFRESH_YEAR);
    }

    private boolean isNeverRefresh(LocalDateTime time) {
        return time != null && time.getYear() == NEVER_REFRESH_YEAR;
    }

    public void setPlaceholder(boolean notUseBungee) {
        setPlaceholder(placeholder.getNewValue(cache), notUseBungee);
    }

    public synchronized void setPlaceholder(List<String> element, boolean notUseBungee) {
        if (element == null || cache.canNotModify()) {
            return;
        }
        storeValues(element);
        if (!notUseBungee && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendRandomPlaceholderToOtherServer(
                    placeholder.getID(),
                    CommonUtil.translateStringList(getStoredValues()),
                    CommonUtil.timeToString(refreshDoneTime),
                    CommonUtil.timeToString(lastResetTime));
        }
    }

    public synchronized RandomPlaceholderSnapshot snapshot() {
        if (!hasValue() || "ONCE".equals(placeholder.getMode())) {
            return null;
        }
        return new RandomPlaceholderSnapshot(
                placeholder.getID(),
                CommonUtil.translateStringList(getStoredValues()),
                CommonUtil.timeToString(refreshDoneTime),
                CommonUtil.timeToString(lastResetTime)
        );
    }

}
