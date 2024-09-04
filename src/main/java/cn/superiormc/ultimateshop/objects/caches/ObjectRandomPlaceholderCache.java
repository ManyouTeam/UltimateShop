package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ObjectRandomPlaceholderCache {

    private String nowValue = null;

    private LocalDateTime refreshDoneTime = null;

    private final ObjectRandomPlaceholder placeholder;

    public ObjectRandomPlaceholderCache(ObjectRandomPlaceholder placeholder) {
        this.placeholder = placeholder;
        setRefreshTime();
    }

    public ObjectRandomPlaceholderCache(ObjectRandomPlaceholder placeholder,
                                        String nowValue,
                                        LocalDateTime refreshDoneTime) {
        this.placeholder = placeholder;
        this.nowValue = nowValue;
        this.refreshDoneTime = refreshDoneTime;
    }

    public ObjectRandomPlaceholder getPlaceholder() {
        return placeholder;
    }

    public LocalDateTime getRefreshDoneTime() {
        return refreshDoneTime;
    }

    public void removeRefreshDoneTime() {
        refreshDoneTime = null;
    }

    public String getNowValue() {
        return getNowValue(true, false);
    }

    public String getNowValue(boolean disable) {
        return getNowValue(true, disable);
    }

    public String getNowValue(boolean needRefresh, boolean disable) {
        if (needRefresh) {
            setRefreshTime(disable);
        }
        return nowValue;
    }

    public void setRefreshTime() {
        setRefreshTime(false);
    }

    public void setRefreshTime(boolean notUseBungee) {
        String mode = placeholder.getMode();
        String time = placeholder.getConfig().getString("reset-time");
        if (mode == null || time == null) {
            if (nowValue == null) {
                setPlaceholder(notUseBungee);
            }
            return;
        }
        if (mode.equals("ONCE")) {
            setPlaceholder(notUseBungee);
            return;
        }
        boolean needRefresh = nowValue == null || refreshDoneTime == null || !refreshDoneTime.isAfter(LocalDateTime.now());
        for (ObjectRandomPlaceholder tempVal1 : placeholder.getNotSameAs()) {
            if (tempVal1.equals(getPlaceholder())) {
                continue;
            }
            if (tempVal1.getNowValue().equals(nowValue)) {
                needRefresh = true;
            }
        }
        if (needRefresh) {
            if (mode.equals("TIMED")) {
                refreshDoneTime = getTimedRefreshTime(time);
                setPlaceholder(notUseBungee);
            }
            else if (mode.equals("TIMER")) {
                refreshDoneTime = getTimerRefreshTime(time);
                setPlaceholder(notUseBungee);
            }
        }
    }

    public void setPlaceholder(boolean notUseBungee) {
        setPlaceholder(placeholder.getNewValue(), notUseBungee);
    }

    public void setPlaceholder(String element, boolean notUseBungee) {
        nowValue = element;
        if (placeholder.getMode().equals("TIMED") || placeholder.getMode().equals("TIMER")) {
            ServerCache.serverCache.setRandomPlaceholderCache(placeholder.getID(), CommonUtil.timeToString(refreshDoneTime), nowValue);
        }
        if (!notUseBungee && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    placeholder.getID(),
                    nowValue,
                    CommonUtil.timeToString(refreshDoneTime));
        }
    }

    private LocalDateTime getTimedRefreshTime(String time) {
        String tempVal1 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        tempVal1 = tempVal1 + " " + time;
        LocalDateTime refreshResult = CommonUtil.stringToTime(tempVal1);
        if (LocalDateTime.now().isAfter(refreshResult)) {
            refreshResult = refreshResult.plusDays(1L);
        }
        return refreshResult;
    }


    private LocalDateTime getTimerRefreshTime(String time) {
        LocalDateTime refreshResult = LocalDateTime.now();
        refreshResult = refreshResult.plusHours(Long.parseLong(time.split(":")[0]));
        refreshResult = refreshResult.plusMinutes(Long.parseLong(time.split(":")[1]));
        refreshResult = refreshResult.plusSeconds(Long.parseLong(time.split(":")[2]));
        return refreshResult;
    }

}
