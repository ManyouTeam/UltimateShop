package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.BungeeCordManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ObjectUseTimesCache {

    private int buyUseTimes;

    private int sellUseTimes;

    private LocalDateTime lastBuyTime = null;

    private LocalDateTime lastSellTime = null;

    private LocalDateTime cooldownBuyTime = null;

    private LocalDateTime cooldownSellTime = null;

    private final ObjectItem product;

    private final ServerCache cache;

    private final boolean firstInsert;

    public ObjectUseTimesCache(ServerCache cache,
                               int buyUseTimes,
                               int sellUseTimes,
                               String lastBuyTime,
                               String lastSellTime,
                               String cooldownBuyTime,
                               String cooldownSellTime,
                               ObjectItem product,
                               boolean firstInsert) {
        this.firstInsert = firstInsert;
        this.cache = cache;
        this.buyUseTimes = buyUseTimes;
        if (lastBuyTime != null) {
            this.lastBuyTime = CommonUtil.stringToTime(lastBuyTime);
        }
        this.sellUseTimes = sellUseTimes;
        if (lastSellTime != null) {
            this.lastSellTime = CommonUtil.stringToTime(lastSellTime);
        }
        if (cooldownBuyTime != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cSet cooldown time to " + product);
            }
            this.cooldownBuyTime = CommonUtil.stringToTime(cooldownBuyTime);
        }
        this.sellUseTimes = sellUseTimes;
        if (cooldownSellTime != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cSet cooldown time to " + product);
            }
            this.cooldownSellTime = CommonUtil.stringToTime(cooldownSellTime);
        }
        this.product = product;
    }

    public int getBuyUseTimes() {
        return buyUseTimes;
    }

    public int getSellUseTimes() {
        return sellUseTimes;
    }

    public void setBuyUseTimes(int i) {
        setBuyUseTimes(i, false);
    }

    public void setBuyUseTimes(int i, boolean notUseBungee) {
        if (i > Integer.MAX_VALUE - 10000) {
            setSellUseTimes(0);
            setBuyUseTimes(i - sellUseTimes);
        }
        buyUseTimes = i;
        if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    product.getShop(),
                    product.getProduct(),
                    "buy-times",
                    String.valueOf(i));
        }
    }

    public void setSellUseTimes(int i) {
        setSellUseTimes(i, false);
    }

    public void setSellUseTimes(int i, boolean notUseBungee) {
        if (i > Integer.MAX_VALUE - 10000) {
            setBuyUseTimes(0);
            setSellUseTimes(i - buyUseTimes);
        }
        sellUseTimes = i;
        if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
            BungeeCordManager.bungeeCordManager.sendToOtherServer(
                    product.getShop(),
                    product.getProduct(),
                    "sell-times",
                    String.valueOf(i));
        }
    }

    public void setLastBuyTime(LocalDateTime time) {
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
        if (cooldownBuyTime == null) {
            String mode = product.getBuyTimesResetMode();
            String tempVal1 = TextUtil.withPAPI(product.getBuyTimesResetTime(), cache.player);
            if (mode == null || tempVal1.isEmpty()) {
                return;
            }
            if (mode.equals("COOLDOWN_TIMED")) {
                cooldownBuyTime = getTimedBuyRefreshTime(tempVal1);
                if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                    BungeeCordManager.bungeeCordManager.sendToOtherServer(
                            product.getShop(),
                            product.getProduct(),
                            "cooldown-buy-time",
                            null);
                }
            } else if (mode.equals("COOLDOWN_TIMER")) {
                cooldownBuyTime = getTimerBuyRefreshTime(tempVal1);
                if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                    BungeeCordManager.bungeeCordManager.sendToOtherServer(
                            product.getShop(),
                            product.getProduct(),
                            "cooldown-buy-time",
                            null);
                }
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
        if (cooldownSellTime == null) {
            String mode = product.getBuyTimesResetMode();
            String tempVal1 = TextUtil.withPAPI(product.getBuyTimesResetTime(), cache.player);
            if (mode == null || tempVal1.isEmpty()) {
                return;
            }
            if (mode.equals("COOLDOWN_TIMED")) {
                cooldownSellTime = getTimedSellRefreshTime(tempVal1);
                if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                    BungeeCordManager.bungeeCordManager.sendToOtherServer(
                            product.getShop(),
                            product.getProduct(),
                            "cooldown-sell-time",
                            null);
                }
            } else if (mode.equals("COOLDOWN_TIMER")) {
                cooldownSellTime = getTimerSellRefreshTime(tempVal1);
                if (!notUseBungee && cache.server && BungeeCordManager.bungeeCordManager != null) {
                    BungeeCordManager.bungeeCordManager.sendToOtherServer(
                            product.getShop(),
                            product.getProduct(),
                            "cooldown-sell-time",
                            null);
                }
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

    public String getCooldownBuyTime() {
        if (cooldownBuyTime == null) {
            return null;
        }
        if (ConfigManager.configManager.getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cCooldown time: " + cooldownBuyTime);
        }
        return CommonUtil.timeToString(cooldownBuyTime);
    }

    public String getCooldownSellTime() {
        if (cooldownSellTime == null) {
            return null;
        }
        if (ConfigManager.configManager.getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cCooldown time: " + cooldownSellTime);
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

    public String getSellRefreshTimeDisplayName() {
        LocalDateTime tempVal1 = getSellRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.refresh.never");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.refresh.format"));
    }

    private LocalDateTime getTimedBuyRefreshTime(String time) {
        LocalDateTime refreshResult = null;
        String[] tempVal2 = time.split(":");
        int month = 0;
        int day = 0;
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your reset time " + time + " is invalid.");
            return LocalDateTime.now();
        }
        if (tempVal2.length == 5) {
            month = Integer.parseInt(tempVal2[0]);
        }
        if (tempVal2.length >= 4) {
            day = Integer.parseInt(tempVal2[tempVal2.length - 4]);
        }
        LocalDateTime checkTime = lastBuyTime;
        if (lastBuyTime == null) {
            checkTime = LocalDateTime.now();
        }
        refreshResult = checkTime.withHour(Integer.parseInt(tempVal2[tempVal2.length - 3])).withMinute(
                Integer.parseInt(tempVal2[tempVal2.length - 2])).withSecond(
                Integer.parseInt(tempVal2[tempVal2.length - 1]));
        refreshResult = refreshResult.plusDays(day).plusMonths(month);
        if (checkTime.isAfter(refreshResult)) {
            refreshResult = refreshResult.plusDays(1L);
        }
        return refreshResult;
    }

    private LocalDateTime getTimedSellRefreshTime(String time) {
        LocalDate nowTime = LocalDate.now();
        LocalDateTime refreshResult = null;
        String[] tempVal2 = time.split(":");
        int month = 0;
        int day = 0;
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your reset time " + time + " is invalid.");
            return LocalDateTime.now();
        }
        if (tempVal2.length == 5) {
            month = Integer.parseInt(tempVal2[0]);
        }
        if (tempVal2.length >= 4) {
            day = Integer.parseInt(tempVal2[1]);
        }
        LocalDateTime checkTime = lastSellTime;
        if (lastSellTime == null) {
            checkTime = LocalDateTime.now();
        }
        refreshResult = checkTime.withHour(Integer.parseInt(tempVal2[tempVal2.length - 3])).withMinute(
                Integer.parseInt(tempVal2[tempVal2.length - 2])).withSecond(
                Integer.parseInt(tempVal2[tempVal2.length - 1]));
        refreshResult = refreshResult.plusDays(day).plusMonths(month);
        if (checkTime.isAfter(refreshResult)) {
            refreshResult = refreshResult.plusDays(1L);
        }
        return refreshResult;
    }

    private LocalDateTime getTimerBuyRefreshTime(String time) {
        LocalDateTime refreshResult = lastBuyTime;
        if (refreshResult == null) {
            refreshResult = LocalDateTime.now();
        }
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your reset time " + time + " is invalid.");
            return LocalDateTime.now();
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
            refreshResult = LocalDateTime.now();
        }
        String[] tempVal2 = time.split(":");
        if (tempVal2.length < 3) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your reset time " + time + " is invalid.");
            return LocalDateTime.now();
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
                if (!UltimateShop.freeVersion) {
                    if (buyOrSell) {
                        setCooldownBuyTime();
                        return cooldownBuyTime;
                    }
                    setCooldownSellTime();
                    return cooldownSellTime;
                }
                return LocalDateTime.now();
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
                return ObjectRandomPlaceholder.getRefreshDoneTimeObject(time);
            default:
                return LocalDateTime.now().withYear(2999);
        }
    }

    public void refreshSellTimes() {
        LocalDateTime tempVal1 = getSellRefreshTime();
        if (tempVal1 != null && tempVal1.isBefore(LocalDateTime.now())) {
            setSellUseTimes(0);
            setLastSellTime(null);
            resetCooldownSellTime();
        }
    }

    public void refreshBuyTimes() {
        LocalDateTime tempVal1 = getBuyRefreshTime();
        if (tempVal1 != null && tempVal1.isBefore(LocalDateTime.now())) {
            setBuyUseTimes(0);
            setLastBuyTime(null);
            resetCooldownBuyTime();
        }
    }

    public boolean isFirstInsert() {
        return firstInsert;
    }
}
