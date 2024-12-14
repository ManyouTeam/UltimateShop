package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ObjectRandomPlaceholderCache {

    private List<String> nowValue = null;

    private LocalDateTime refreshDoneTime = null;

    private final ObjectRandomPlaceholder placeholder;

    public ObjectRandomPlaceholderCache(ObjectRandomPlaceholder placeholder) {
        this.placeholder = placeholder;
        setRefreshTime();
    }

    public ObjectRandomPlaceholderCache(ObjectRandomPlaceholder placeholder,
                                        List<String> nowValue,
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

    public List<String> getNowValue() {
        return getNowValue(true, false);
    }

    public List<String> getNowValue(boolean disable) {
        return getNowValue(true, disable);
    }

    public List<String> getNowValue(boolean needRefresh, boolean disable) {
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
        String time = TextUtil.withPAPI(placeholder.getConfig().getString("reset-time"), null);
        if (mode == null || time.isEmpty()) {
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
            switch (mode) {
                case "TIMED":
                    refreshDoneTime = getTimedRefreshTime(time);
                    break;
                case "TIMER":
                    refreshDoneTime = getTimerRefreshTime(time);
                    break;
                case "CUSTOM":
                    refreshDoneTime = CommonUtil.stringToTime(time, placeholder.getConfig().getString("time-format", "yyyy-MM-dd HH:mm:ss"));
                    break;
                default:
                    refreshDoneTime = LocalDateTime.now().withYear(2999);
                    break;
            }
            setPlaceholder(notUseBungee);
        }
    }

    public void setPlaceholder(boolean notUseBungee) {
        setPlaceholder(placeholder.getNewValue(), notUseBungee);
    }

    public void setPlaceholder(List<String> element, boolean notUseBungee) {
        if (element == null) {
            return;
        }
        nowValue = element;
        if (!placeholder.getMode().equals("ONCE") && refreshDoneTime != null) {
            ServerCache.serverCache.setRandomPlaceholderCache(placeholder, CommonUtil.timeToString(refreshDoneTime), nowValue);
        }
        if (!notUseBungee && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    placeholder.getID(),
                    CommonUtil.translateStringList(nowValue),
                    CommonUtil.timeToString(refreshDoneTime));
        }
    }

    private LocalDateTime getTimedRefreshTime(String time) {
        LocalDate nowTime = LocalDate.now();
        LocalDateTime refreshResult = null;
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your reset time " + time + " is invalid.");
            return LocalDateTime.now();
        }
        int month = 0;
        int day = 0;
        if (tempVal2.length == 5) {
            month = Integer.parseInt(tempVal2[0]);
        }
        if (tempVal2.length >= 4) {
            day = Integer.parseInt(tempVal2[tempVal2.length - 4]);
        }
        refreshResult = nowTime.atTime(Integer.parseInt(tempVal2[tempVal2.length - 3]),
                Integer.parseInt(tempVal2[tempVal2.length - 2]),
                Integer.parseInt(tempVal2[tempVal2.length - 1]));
        refreshResult = refreshResult.plusDays(day).plusMonths(month);
        if (LocalDateTime.now().isAfter(refreshResult)) {
            refreshResult = refreshResult.plusDays(1L);
        }
        return refreshResult;
    }

    private LocalDateTime getTimerRefreshTime(String time) {
        LocalDateTime refreshResult = LocalDateTime.now();
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your reset time " + time + " is invalid.");
            return LocalDateTime.now();
        }
        int month = 0;
        int day = 0;
        if (tempVal2.length == 5) {
            month = Integer.parseInt(tempVal2[0]);
        }
        if (tempVal2.length >= 4) {
            day = Integer.parseInt(tempVal2[tempVal2.length - 4]);
        }
        refreshResult = refreshResult.plusMonths(month).plusDays(day)
                .plusHours(Integer.parseInt(tempVal2[tempVal2.length - 3]))
                .plusMinutes(Integer.parseInt(tempVal2[tempVal2.length - 2]))
                .plusSeconds(Integer.parseInt(tempVal2[tempVal2.length - 1]));
        return refreshResult;
    }

}
