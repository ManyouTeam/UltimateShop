package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ObjectRandomPlaceholderCache {

    private List<String> nowValue = null;

    private LocalDateTime refreshDoneTime = null;

    private final ObjectRandomPlaceholder placeholder;

    private final ServerCache cache;

    public ObjectRandomPlaceholderCache(ServerCache cache,
                                        ObjectRandomPlaceholder placeholder) {
        this.cache = cache;
        this.placeholder = placeholder;
        setRefreshTime();
    }

    public ObjectRandomPlaceholderCache(ServerCache cache,
                                        ObjectRandomPlaceholder placeholder,
                                        List<String> nowValue,
                                        LocalDateTime refreshDoneTime) {
        this.cache = cache;
        this.placeholder = placeholder;
        this.nowValue = nowValue;
        this.refreshDoneTime = refreshDoneTime;
    }

    public ObjectRandomPlaceholder getPlaceholder() {
        return placeholder;
    }

    public LocalDateTime getRefreshDoneTime() {
        if (refreshDoneTime != null && !refreshDoneTime.isAfter(CommonUtil.getNowTime())) {
            setRefreshTime();
        }
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
        boolean needRefresh = nowValue == null || refreshDoneTime == null || !refreshDoneTime.isAfter(CommonUtil.getNowTime());
        for (ObjectRandomPlaceholder tempVal1 : placeholder.getNotSameAs()) {
            if (tempVal1.equals(getPlaceholder())) {
                continue;
            }
            if (tempVal1.getNowValue(cache).equals(nowValue)) {
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
                case "RANDOM_PLACEHOLDER":
                    if (time.equals(placeholder.getID())) {
                        refreshDoneTime = CommonUtil.getNowTime().withYear(2999);
                    } else {
                        refreshDoneTime = ObjectRandomPlaceholder.getRefreshDoneTimeObject(cache.player, time);
                    }
                    break;
                default:
                    refreshDoneTime = CommonUtil.getNowTime().withYear(2999);
                    break;
            }
            setPlaceholder(notUseBungee);
        }
    }

    public void setPlaceholder(boolean notUseBungee) {
        setPlaceholder(placeholder.getNewValue(cache), notUseBungee);
    }

    public void setPlaceholder(List<String> element, boolean notUseBungee) {
        if (element == null) {
            return;
        }
        nowValue = element;
        if (!placeholder.getMode().equals("ONCE") && refreshDoneTime != null) {
            cache.setRandomPlaceholderCache(placeholder, CommonUtil.timeToString(refreshDoneTime), nowValue);
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
        String[] tempVal3 = time.split(";;");
        for (String tempVal4 : tempVal3) {
            LocalDateTime thisResult;
            String[] tempVal2 = tempVal4.split(":");
            if (tempVal2.length < 3) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + tempVal4 + " is invalid.");
                return CommonUtil.getNowTime();
            }
            int month = 0;
            int day = 0;
            if (tempVal2.length == 5) {
                month = Integer.parseInt(tempVal2[0]);
            }
            if (tempVal2.length >= 4) {
                day = Integer.parseInt(tempVal2[tempVal2.length - 4]);
            }
            thisResult = nowTime.atTime(Integer.parseInt(tempVal2[tempVal2.length - 3]),
                    Integer.parseInt(tempVal2[tempVal2.length - 2]),
                    Integer.parseInt(tempVal2[tempVal2.length - 1]));
            thisResult= thisResult.plusDays(day).plusMonths(month);
            if (CommonUtil.getNowTime().isAfter(thisResult)) {
                thisResult = thisResult.plusDays(1L);
            }
            if (refreshResult == null || thisResult.isBefore(refreshResult)) {
                refreshResult = thisResult;
            }
        }
        return refreshResult;
    }

    private LocalDateTime getTimerRefreshTime(String time) {
        LocalDateTime refreshResult = CommonUtil.getNowTime();
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + time + " is invalid.");
            return CommonUtil.getNowTime();
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
