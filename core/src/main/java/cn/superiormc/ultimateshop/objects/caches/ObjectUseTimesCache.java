package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class ObjectUseTimesCache {

    private int buyUseTimes;

    private int totalBuyUseTimes;

    private int sellUseTimes;

    private int totalSellUseTimes;

    private LocalDateTime lastBuyTime = null;

    private LocalDateTime lastSellTime = null;

    private LocalDateTime lastResetBuyTime = null;

    private LocalDateTime lastResetSellTime = null;

    private LocalDateTime cooldownBuyTime = null;

    private LocalDateTime cooldownSellTime = null;

    private final ObjectItem product;

    private final ServerCache cache;

    private final boolean firstInsert;

    private SchedulerUtil buyResetTask;

    private SchedulerUtil sellResetTask;

    private SchedulerUtil guiUpdateTask;

    public ObjectUseTimesCache(ServerCache cache,
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
                               ObjectItem product,
                               boolean firstInsert) {
        this.firstInsert = firstInsert;
        this.cache = cache;
        this.buyUseTimes = buyUseTimes;
        this.totalBuyUseTimes = totalBuyUseTimes;
        if (lastBuyTime != null) {
            this.lastBuyTime = CommonUtil.stringToTime(lastBuyTime);
        }
        this.sellUseTimes = sellUseTimes;
        this.totalSellUseTimes = totalSellUseTimes;
        if (lastSellTime != null) {
            this.lastSellTime = CommonUtil.stringToTime(lastSellTime);
        }
        if (lastResetBuyTime != null) {
            this.lastResetBuyTime = CommonUtil.stringToTime(lastResetBuyTime);
        }
        if (lastResetSellTime != null) {
            this.lastResetSellTime = CommonUtil.stringToTime(lastResetSellTime);
        }
        if (cooldownBuyTime != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cSet cooldown time to " + product);
            }
            this.cooldownBuyTime = CommonUtil.stringToTime(cooldownBuyTime);
        }
        if (cooldownSellTime != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cSet cooldown time to " + product);
            }
            this.cooldownSellTime = CommonUtil.stringToTime(cooldownSellTime);
        }
        this.product = product;
        initAutoResetTask();
    }

    public void initAutoResetTask() {
        if (!ConfigManager.configManager.getBoolean("use-times.auto-reset-mode")) {
            refreshBuyTimes();
            refreshSellTimes();
        } else {
            initBuyResetTask();
            initSellResetTask();
        }
    }

    public void initBuyResetTask() {
        LocalDateTime refreshTime = getBuyRefreshTime();
        if (refreshTime == null || refreshTime.getYear() == 2999) {
            return;
        }

        if (buyResetTask != null) {
            buyResetTask.cancel();
            buyResetTask = null;
        }

        long delayMillis = Duration.between(CommonUtil.getNowTime(), refreshTime).toMillis();

        if (delayMillis <= 0) {
            refreshBuyTimes();
            return;
        }

        long delayTicks = delayMillis / 50;

        buyResetTask = SchedulerUtil.runTaskLater(this::refreshBuyTimes, delayTicks + 20);
    }

    public void initSellResetTask() {
        LocalDateTime refreshTime = getSellRefreshTime();
        if (refreshTime == null || refreshTime.getYear() == 2999) {
            return;
        }

        if (sellResetTask != null) {
            sellResetTask.cancel();
            sellResetTask = null;
        }

        long delayMillis = Duration.between(CommonUtil.getNowTime(), refreshTime).toMillis();

        if (delayMillis <= 0) {
            refreshSellTimes();
            return;
        }

        long delayTicks = delayMillis / 50;

        sellResetTask = SchedulerUtil.runTaskLater(this::refreshSellTimes, delayTicks + 20);
    }

    public void cancelAutoResetTask() {
        if (buyResetTask != null) {
            buyResetTask.cancel();
        }
        if (sellResetTask != null) {
            sellResetTask.cancel();
        }
    }

    public int getBuyUseTimes() {
        return buyUseTimes;
    }

    public int getTotalBuyUseTimes() {
        if (UltimateShop.freeVersion) {
            return 0;
        }
        return totalBuyUseTimes;
    }

    public int getSellUseTimes() {
        return sellUseTimes;
    }

    public int getTotalSellUseTimes() {
        if (UltimateShop.freeVersion) {
            return 0;
        }
        return totalSellUseTimes;
    }

    public void setBuyUseTimes(int i) {
        setBuyUseTimes(i, false, false);
    }

    public void setBuyUseTimes(int i, boolean isReset) {
        setBuyUseTimes(i, false, isReset);
    }

    public void setBuyUseTimes(int i, boolean notUseBungee, boolean isReset) {
        int maxTimes = product.getBuyTimesMaxValue(cache.player);
        if (i > Integer.MAX_VALUE - 10000) {
            setSellUseTimes(0);
            setBuyUseTimes(i - sellUseTimes);
        }
        if (!isReset) {
            if (totalBuyUseTimes > Integer.MAX_VALUE - 10000) {
                totalBuyUseTimes = i;
            } else {
                totalBuyUseTimes = totalBuyUseTimes + (i - buyUseTimes);
            }
            if (!UltimateShop.freeVersion && ConfigManager.configManager.getBoolean("use-times.max-value-for-total-only")
                    && maxTimes >= 0 && totalBuyUseTimes > maxTimes) {
                totalBuyUseTimes = maxTimes;
            }
        }
        if (!UltimateShop.freeVersion && maxTimes >= 0 && i > maxTimes) {
            buyUseTimes = maxTimes;
        } else {
            buyUseTimes = i;
        }
        if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    product.getShop(),
                    product.getProduct(),
                    "buy-times",
                    String.valueOf(i));
        }
    }

    public void setSellUseTimes(int i) {
        setSellUseTimes(i, false, false);
    }

    public void setSellUseTimes(int i, boolean isReset) {
        setSellUseTimes(i, false, isReset);
    }

    public void setSellUseTimes(int i, boolean notUseBungee, boolean isReset) {
        int maxTimes = product.getSellTimesMaxValue(cache.player);
        if (i > Integer.MAX_VALUE - 10000) {
            setBuyUseTimes(0);
            setSellUseTimes(i - buyUseTimes);
        }
        if (!isReset) {
            if (totalSellUseTimes > Integer.MAX_VALUE - 10000) {
                totalSellUseTimes = i;
            } else {
                totalSellUseTimes = totalSellUseTimes + (i - sellUseTimes);
            }
            if (!UltimateShop.freeVersion && ConfigManager.configManager.getBoolean("use-times.max-value-for-total-only")
                    && maxTimes >= 0 && totalSellUseTimes > maxTimes) {
                totalSellUseTimes = maxTimes;
            }
        }
        if (!UltimateShop.freeVersion && maxTimes >= 0 && i > maxTimes) {
            sellUseTimes = maxTimes;
        } else {
            sellUseTimes = i;
        }
        if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    product.getShop(),
                    product.getProduct(),
                    "sell-times",
                    String.valueOf(i));
        }
    }

    public void setLastBuyTime(LocalDateTime time) {
        if (time == null) {
            lastResetBuyTime = Objects.requireNonNullElseGet(cooldownBuyTime, LocalDateTime::now);
        }
        setLastBuyTime(time, false);
    }

    public void setLastBuyTime(LocalDateTime time, boolean notUseBungee) {
        lastBuyTime = time;
        if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    product.getShop(),
                    product.getProduct(),
                    "last-buy-time",
                    CommonUtil.timeToString(time));
        }
    }

    public void setLastSellTime(LocalDateTime time) {
        if (time == null) {
            lastResetSellTime = Objects.requireNonNullElseGet(cooldownSellTime, LocalDateTime::now);
        }
        setLastSellTime(time, false);
    }

    public void setLastSellTime(LocalDateTime time, boolean notUseBungee) {
        lastSellTime = time;
        if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    product.getShop(),
                    product.getProduct(),
                    "last-sell-time",
                    CommonUtil.timeToString(time));
        }
    }

    public void setCooldownBuyTime() {
        setCooldownBuyTime(false);
    }

    public void resetCooldownBuyTime() {
        cooldownBuyTime = null;
    }

    public void setCooldownBuyTime(boolean notUseBungee) {
        if (cooldownBuyTime == null || !cooldownBuyTime.isBefore(CommonUtil.getNowTime())) {
            String mode = product.getBuyTimesResetMode();
            String tempVal1 = TextUtil.withPAPI(product.getBuyTimesResetTime(), cache.player);
            if (mode == null || tempVal1.isEmpty()) {
                return;
            }
            switch (mode) {
                case "COOLDOWN_TIMED" -> {
                    cooldownBuyTime = getTimedBuyRefreshTime(tempVal1);
                    if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                        BungeeCordManager.bungeeCordManager.sendToOtherServer(
                                product.getShop(),
                                product.getProduct(),
                                "cooldown-buy-time",
                                null);
                    }
                }
                case "COOLDOWN_TIMER" -> {
                    cooldownBuyTime = getTimerBuyRefreshTime(tempVal1);
                    if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                        BungeeCordManager.bungeeCordManager.sendToOtherServer(
                                product.getShop(),
                                product.getProduct(),
                                "cooldown-buy-time",
                                null);
                    }
                }
                case "COOLDOWN_CUSTOM" -> cooldownBuyTime = CommonUtil.stringToTime(tempVal1,
                        TextUtil.withPAPI(product.getBuyTimesResetFormat(), cache.player));
            }
        }
    }

    public void setCooldownSellTime() {
        setCooldownSellTime(false);
    }

    public void resetCooldownSellTime() {
        cooldownSellTime = null;
    }

    public void setCooldownSellTime(boolean notUseBungee) {
        if (cooldownSellTime == null || !cooldownSellTime.isBefore(CommonUtil.getNowTime())) {
            String mode = product.getSellTimesResetMode();
            String tempVal1 = TextUtil.withPAPI(product.getSellTimesResetTime(), cache.player);
            if (mode == null || tempVal1.isEmpty()) {
                return;
            }
            switch (mode) {
                case "COOLDOWN_TIMED" -> {
                    cooldownSellTime = getTimedSellRefreshTime(tempVal1);
                    if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                        BungeeCordManager.bungeeCordManager.sendToOtherServer(
                                product.getShop(),
                                product.getProduct(),
                                "cooldown-sell-time",
                                null);
                    }
                }
                case "COOLDOWN_TIMER" -> {
                    cooldownSellTime = getTimerSellRefreshTime(tempVal1);
                    if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                        BungeeCordManager.bungeeCordManager.sendToOtherServer(
                                product.getShop(),
                                product.getProduct(),
                                "cooldown-sell-time",
                                null);
                    }
                }
                case "COOLDOWN_CUSTOM" -> cooldownSellTime = CommonUtil.stringToTime(tempVal1,
                        TextUtil.withPAPI(product.getSellTimesResetFormat(), cache.player));
            }
        }
    }

    public String getLastBuyTime() {
        if (lastBuyTime == null) {
            return null;
        }
        return CommonUtil.timeToString(lastBuyTime);
    }

    public String getLastSellTime() {
        if (lastSellTime == null) {
            return null;
        }
        return CommonUtil.timeToString(lastSellTime);
    }

    public String getLastResetBuyTime() {
        if (lastResetBuyTime == null) {
            return null;
        }
        return CommonUtil.timeToString(lastResetBuyTime);
    }

    public String getLastResetSellTime() {
        if (lastResetSellTime == null) {
            return null;
        }
        return CommonUtil.timeToString(lastResetSellTime);
    }

    public String getCooldownBuyTime() {
        if (cooldownBuyTime == null) {
            return null;
        }
        if (ConfigManager.configManager.getBoolean("debug")) {
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCooldown time: " + cooldownBuyTime);
        }
        return CommonUtil.timeToString(cooldownBuyTime);
    }

    public String getCooldownSellTime() {
        if (cooldownSellTime == null) {
            return null;
        }
        if (ConfigManager.configManager.getBoolean("debug")) {
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCooldown time: " + cooldownSellTime);
        }
        return CommonUtil.timeToString(cooldownSellTime);
    }

    public LocalDateTime getBuyRefreshTime() {
        String mode = product.getBuyTimesResetMode();
        String tempVal1 = TextUtil.withPAPI(product.getBuyTimesResetTime(), cache.player);
        return createRefreshTime(mode, tempVal1, true);
    }

    public LocalDateTime getSellRefreshTime() {
        String mode = product.getSellTimesResetMode();
        String tempVal1 = TextUtil.withPAPI(product.getSellTimesResetTime(), cache.player);
        return createRefreshTime(mode, tempVal1, false);
    }

    public String getBuyRefreshTimeDisplayName() {
        LocalDateTime tempVal1 = getBuyRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.refresh.never");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.refresh.format"));
    }

    public String getBuyRefreshTimeNextName() {
        if (UltimateShop.freeVersion) {
            return "";
        }
        LocalDateTime tempVal1 = getBuyRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.next.never");
        }
        Duration duration = Duration.between(CommonUtil.getNowTime(), tempVal1);
        long totalSeconds = duration.getSeconds();
        if (totalSeconds < 0) {
            return ConfigManager.configManager.getString("placeholder.next.never");
        }
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (days > 0) {
            return ConfigManager.configManager.getString("placeholder.next.with-day-format").replace("{d}", String.valueOf(days))
                    .replace("{h}", String.format("%02d", hours))
                    .replace("{m}", String.format("%02d", minutes))
                    .replace("{s}", String.format("%02d", seconds));
        }
        return ConfigManager.configManager.getString("placeholder.next.without-day-format").replace("{h}", String.valueOf(hours))
                .replace("{m}", String.format("%02d", minutes))
                .replace("{s}", String.format("%02d", seconds));
    }

    public String getBuyLastTimeName() {
        if (UltimateShop.freeVersion) {
            return "0";
        }
        LocalDateTime tempVal1 = lastBuyTime;
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return "0";
        }
        Duration duration = Duration.between(tempVal1, CommonUtil.getNowTime());
        return String.valueOf(duration.getSeconds());
    }

    public String getBuyLastResetTimeName() {
        if (UltimateShop.freeVersion) {
            return "0";
        }
        LocalDateTime tempVal1 = lastResetBuyTime;
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return getBuyLastTimeName();
        }
        Duration duration = Duration.between(tempVal1, CommonUtil.getNowTime());
        return String.valueOf(duration.getSeconds());
    }

    public String getSellRefreshTimeDisplayName() {
        LocalDateTime tempVal1 = getSellRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.refresh.never");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.refresh.format"));
    }

    public String getSellRefreshTimeNextName() {
        if (UltimateShop.freeVersion) {
            return "0";
        }
        LocalDateTime tempVal1 = getSellRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.next.never");
        }
        Duration duration = Duration.between(CommonUtil.getNowTime(), tempVal1);
        long totalSeconds = duration.getSeconds();
        if (totalSeconds < 0) {
            return ConfigManager.configManager.getString("placeholder.next.never");
        }
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (days > 0) {
            return ConfigManager.configManager.getString("placeholder.next.with-day-format").replace("{d}", String.valueOf(days))
                    .replace("{h}", String.format("%02d", hours))
                    .replace("{m}", String.format("%02d", minutes))
                    .replace("{s}", String.format("%02d", seconds));
        }
        return ConfigManager.configManager.getString("placeholder.next.without-day-format").replace("{h}", String.valueOf(hours))
                .replace("{m}", String.format("%02d", minutes))
                .replace("{s}", String.format("%02d", seconds));
    }

    public String getSellLastTimeName() {
        if (UltimateShop.freeVersion) {
            return "0";
        }
        LocalDateTime tempVal1 = lastSellTime;
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return "0";
        }
        Duration duration = Duration.between(tempVal1, CommonUtil.getNowTime());
        return String.valueOf(duration.getSeconds());
    }

    public String getSellLastResetTimeName() {
        if (UltimateShop.freeVersion) {
            return "0";
        }
        LocalDateTime tempVal1 = lastResetSellTime;
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return getSellLastTimeName();
        }
        Duration duration = Duration.between(tempVal1, CommonUtil.getNowTime());
        return String.valueOf(duration.getSeconds());
    }

    private LocalDateTime getTimedBuyRefreshTime(String time) {
        LocalDateTime refreshResult = null;
        String[] tempVal3 = time.split(";;");
        for (String tempVal4 : tempVal3) {
            LocalDateTime thisResult;
            String[] tempVal2 = tempVal4.split(":");
            int month = 0;
            int day = 0;
            if (tempVal2.length < 3) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + tempVal4 + " is invalid.");
                return CommonUtil.getNowTime();
            }
            if (tempVal2.length == 5) {
                month = Integer.parseInt(tempVal2[0]);
            }
            if (tempVal2.length >= 4) {
                day = Integer.parseInt(tempVal2[tempVal2.length - 4]);
            }
            LocalDateTime checkTime = lastBuyTime;
            if (lastBuyTime == null) {
                checkTime = CommonUtil.getNowTime();
            }
            thisResult = checkTime.withHour(Integer.parseInt(tempVal2[tempVal2.length - 3])).withMinute(
                    Integer.parseInt(tempVal2[tempVal2.length - 2])).withSecond(
                    Integer.parseInt(tempVal2[tempVal2.length - 1]));
            thisResult = thisResult.plusDays(day).plusMonths(month);
            if (!checkTime.isBefore(thisResult)) {
                thisResult = thisResult.plusDays(1L);
            }
            if (refreshResult == null || thisResult.isBefore(refreshResult)) {
                refreshResult = thisResult;
            }
            if (UltimateShop.freeVersion) {
                break;
            }
        }
        return refreshResult;
    }

    private LocalDateTime getTimedSellRefreshTime(String time) {
        LocalDateTime refreshResult = null;
        String[] tempVal3 = time.split(";;");
        for (String tempVal4 : tempVal3) {
            LocalDateTime thisResult;
            String[] tempVal2 = tempVal4.split(":");
            int month = 0;
            int day = 0;
            if (tempVal2.length < 3) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + tempVal4 + " is invalid.");
                return CommonUtil.getNowTime();
            }
            if (tempVal2.length == 5) {
                month = Integer.parseInt(tempVal2[0]);
            }
            if (tempVal2.length >= 4) {
                day = Integer.parseInt(tempVal2[1]);
            }
            LocalDateTime checkTime = lastSellTime;
            if (lastSellTime == null) {
                checkTime = CommonUtil.getNowTime();
            }
            thisResult = checkTime.withHour(Integer.parseInt(tempVal2[tempVal2.length - 3])).withMinute(
                    Integer.parseInt(tempVal2[tempVal2.length - 2])).withSecond(
                    Integer.parseInt(tempVal2[tempVal2.length - 1]));
            thisResult = thisResult.plusDays(day).plusMonths(month);
            if (!checkTime.isBefore(thisResult)) {
                thisResult = thisResult.plusDays(1L);
            }
            if (refreshResult == null || thisResult.isBefore(refreshResult)) {
                refreshResult = thisResult;
            }
            if (UltimateShop.freeVersion) {
                break;
            }
        }
        return refreshResult;
    }

    private LocalDateTime getTimerBuyRefreshTime(String time) {
        LocalDateTime refreshResult = lastBuyTime;
        if (refreshResult == null) {
            refreshResult = CommonUtil.getNowTime();
        }
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + time + " is invalid.");
            return CommonUtil.getNowTime();
        }
        int month = 0;
        int day = 0;
        if (!UltimateShop.freeVersion) {
            if (tempVal2.length == 5) {
                month = Integer.parseInt(tempVal2[0]);
            }
            if (tempVal2.length >= 4) {
                day = Integer.parseInt(tempVal2[1]);
            }
        }
        refreshResult = refreshResult.plusMonths(month).plusDays(day)
                .plusHours(Integer.parseInt(tempVal2[tempVal2.length - 3]))
                .plusMinutes(Integer.parseInt(tempVal2[tempVal2.length - 2]))
                .plusSeconds(Integer.parseInt(tempVal2[tempVal2.length - 1]));
        return refreshResult;
    }

    private LocalDateTime getTimerSellRefreshTime(String time) {
        LocalDateTime refreshResult = lastSellTime;
        if (refreshResult == null) {
            refreshResult = CommonUtil.getNowTime();
        }
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Your reset time " + time + " is invalid.");
            return CommonUtil.getNowTime();
        }
        int month = 0;
        int day = 0;
        if (!UltimateShop.freeVersion) {
            if (tempVal2.length == 5) {
                month = Integer.parseInt(tempVal2[0]);
            }
            if (tempVal2.length >= 4) {
                day = Integer.parseInt(tempVal2[1]);
            }
        }
        refreshResult = refreshResult.plusMonths(month).plusDays(day)
                .plusHours(Integer.parseInt(tempVal2[tempVal2.length - 3]))
                .plusMinutes(Integer.parseInt(tempVal2[tempVal2.length - 2]))
                .plusSeconds(Integer.parseInt(tempVal2[tempVal2.length - 1]));
        return refreshResult;
    }

    private LocalDateTime createRefreshTime(String mode, String time, boolean buyOrSell) {
        switch (mode) {
            case "COOLDOWN_TIMED":
            case "COOLDOWN_TIMER":
            case "COOLDOWN_CUSTOM":
                if (!UltimateShop.freeVersion) {
                    if (buyOrSell) {
                        setCooldownBuyTime();
                        return cooldownBuyTime;
                    }
                    setCooldownSellTime();
                    return cooldownSellTime;
                }
                return CommonUtil.getNowTime();
            case "TIMED":
                if (buyOrSell) {
                    return getTimedBuyRefreshTime(time);
                }
                return getTimedSellRefreshTime(time);
            case "TIMER":
                if (buyOrSell) {
                    return getTimerBuyRefreshTime(time);
                }
                return getTimerSellRefreshTime(time);
            case "CUSTOM":
                if (UltimateShop.freeVersion) {
                    return CommonUtil.stringToTime(time);
                }
                if (buyOrSell) {
                    return CommonUtil.stringToTime(time, TextUtil.withPAPI(product.getBuyTimesResetFormat(), cache.player));
                }
                return CommonUtil.stringToTime(time, TextUtil.withPAPI(product.getSellTimesResetFormat(), cache.player));
            case "RANDOM_PLACEHOLDER":
                return ObjectRandomPlaceholder.getRefreshDoneTimeObject(cache.player, time);
            default:
                return CommonUtil.getNowTime().withYear(2999);
        }
    }

    public void refreshBuyTimes() {
        LocalDateTime tempVal1 = getBuyRefreshTime();
        if (tempVal1 != null && tempVal1.isBefore(CommonUtil.getNowTime())) {
            setBuyUseTimes(product.getBuyTimesResetValue(cache.player), true);
            setLastBuyTime(null);
            resetCooldownBuyTime();
            updateGUI();
        }
    }

    public void refreshSellTimes() {
        LocalDateTime tempVal1 = getSellRefreshTime();
        if (tempVal1 != null && tempVal1.isBefore(CommonUtil.getNowTime())) {
            setSellUseTimes(product.getSellTimesResetValue(cache.player), true);
            setLastSellTime(null);
            resetCooldownSellTime();
            updateGUI();
        }
    }

    public void updateGUI() {
        if (cache.player != null) {
            GUIStatus guiStatus = AbstractGUI.playerList.get(cache.player);
            if (guiStatus != null && guiStatus.getGUI() != null) {
                if (guiUpdateTask != null) {
                    guiUpdateTask.cancel();
                    guiUpdateTask = null;
                }
                guiUpdateTask = SchedulerUtil.runTaskLater(() -> guiStatus.getGUI().constructGUI(), 20L);
            }
        }
    }

    public boolean isEmpty() {
        return buyUseTimes == 0 && totalBuyUseTimes == 0 && sellUseTimes == 0 && totalSellUseTimes == 0;
    }

    public boolean isFirstInsert() {
        return firstInsert;
    }
}
