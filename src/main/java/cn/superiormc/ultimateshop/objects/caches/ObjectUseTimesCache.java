package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ObjectUseTimesCache {

    private int buyUseTimes;

    private int sellUseTimes;

    private LocalDateTime lastBuyTime = null;

    private LocalDateTime lastSellTime = null;

    private LocalDateTime cooldownBuyTime = null;

    private LocalDateTime cooldownSellTime = null;

    private ObjectItem product;


    public ObjectUseTimesCache(int buyUseTimes,
                               int sellUseTimes,
                               String lastBuyTime,
                               String lastSellTime,
                               String cooldownBuyTime,
                               String cooldownSellTime,
                               ObjectItem product) {
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
        buyUseTimes = i;
    }

    public void setSellUseTimes(int i) {
        sellUseTimes = i;
    }

    public void setLastBuyTime(LocalDateTime time) {
        lastBuyTime = time;
    }

    public void setLastSellTime(LocalDateTime time) {
        lastSellTime = time;
    }

    public void setCooldownBuyTime() {
        String mode = product.getItemConfig().getString("buy-cooldown-mode");
        String tempVal1 = product.getItemConfig().getString("buy-cooldown-time");
        if (mode == null || tempVal1 == null) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cSet cooldown time to " + product);
        }
        if (cooldownBuyTime == null || !cooldownBuyTime.isAfter(LocalDateTime.now())) {
            if (mode.equals("TIMED")) {
                cooldownBuyTime = getTimedBuyRefreshTime(tempVal1);
            }
            else if (mode.equals("TIMER")) {
                cooldownBuyTime = getTimerBuyRefreshTime(tempVal1);
            }
        }
    }

    public void setCooldownSellTime() {
        String mode = product.getItemConfig().getString("sell-cooldown-mode");
        String tempVal1 = product.getItemConfig().getString("sell-cooldown-time");
        if (mode == null || tempVal1 == null) {
            return;
        }
        if (ConfigManager.configManager.getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cSet cooldown time to " + product);
        }
        if (cooldownSellTime == null || !cooldownSellTime.isAfter(LocalDateTime.now())) {
            if (mode.equals("TIMED")) {
                cooldownSellTime = getTimedSellRefreshTime(tempVal1);
            }
            else if (mode.equals("TIMER")) {
                cooldownSellTime = getTimerSellRefreshTime(tempVal1);
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

    public LocalDateTime getCooldownBuyRefreshTime() {
        return cooldownBuyTime;
    }

    public LocalDateTime getBuyRefreshTime() {
        if (lastBuyTime == null) {
            return LocalDateTime.now().withYear(2999);
        }
        String mode = product.getItemConfig().getString("buy-limits-reset-mode",
                ConfigManager.configManager.getString("use-times.default-reset-mode"));
        String tempVal1 = product.getItemConfig().getString("buy-limits-reset-time",
                ConfigManager.configManager.getString("use-times.default-reset-time"));
        if (mode.equals("TIMED")) {
            return getTimedBuyRefreshTime(tempVal1);
        }
        else if (mode.equals("TIMER")) {
            return getTimerBuyRefreshTime(tempVal1);
        }
        else {
            return LocalDateTime.now().withYear(2999);
        }
    }

    public LocalDateTime getCooldownSellRefreshTime() {
        return cooldownSellTime;
    }

    public LocalDateTime getSellRefreshTime() {
        if (lastSellTime == null) {
            return LocalDateTime.now().withYear(2999);
        }
        String mode = product.getItemConfig().getString("sell-limits-reset-mode",
                ConfigManager.configManager.getString("use-times.default-reset-mode"));
        String tempVal1 = product.getItemConfig().getString("sell-limits-reset-time",
                ConfigManager.configManager.getString("use-times.default-reset-time"));
        if (mode.equals("TIMED")) {
            return getTimedSellRefreshTime(tempVal1);
        }
        else if (mode.equals("TIMER")) {
            return getTimerSellRefreshTime(tempVal1);
        }
        else {
            return LocalDateTime.now().withYear(2999);
        }
    }

    public String getBuyCooldownTimeDisplayName() {
        LocalDateTime tempVal1 = getCooldownBuyRefreshTime();
        if (tempVal1 == null || !tempVal1.isAfter(LocalDateTime.now())) {
            return ConfigManager.configManager.getString("placeholder.cooldown.now");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.cooldown.format"));
    }

    public String getBuyRefreshTimeDisplayName() {
        LocalDateTime tempVal1 = getBuyRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.refresh.never");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.refresh.format"));
    }

    public String getSellCooldownTimeDisplayName() {
        LocalDateTime tempVal1 = getCooldownSellRefreshTime();
        if (tempVal1 == null || !tempVal1.isAfter(LocalDateTime.now())) {
            return ConfigManager.configManager.getString("placeholder.cooldown.now");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.cooldown.format"));
    }

    public String getSellRefreshTimeDisplayName() {
        LocalDateTime tempVal1 = getSellRefreshTime();
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.refresh.never");
        }
        return CommonUtil.timeToString(tempVal1, ConfigManager.configManager.getString("placeholder.refresh.format"));
    }

    private LocalDateTime getTimedBuyRefreshTime(String time) {
        String tempVal1 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        tempVal1 = tempVal1 + " " + time;
        LocalDateTime refreshResult = CommonUtil.stringToTime(tempVal1);
        if (lastBuyTime.isAfter(refreshResult)) {
            refreshResult = refreshResult.plusDays(1L);
        }
        return refreshResult;
    }

    private LocalDateTime getTimedSellRefreshTime(String time) {
        String tempVal1 = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        tempVal1 = tempVal1 + " " + time;
        LocalDateTime refreshResult = CommonUtil.stringToTime(tempVal1);
        if (lastSellTime.isAfter(refreshResult)) {
            refreshResult = refreshResult.plusDays(1L);
        }
        return refreshResult;
    }

    private LocalDateTime getTimerBuyRefreshTime(String time) {
        LocalDateTime refreshResult = lastBuyTime;
        refreshResult = refreshResult.plusHours(Long.parseLong(time.split(":")[0]));
        refreshResult = refreshResult.plusMinutes(Long.parseLong(time.split(":")[1]));
        refreshResult = refreshResult.plusSeconds(Long.parseLong(time.split(":")[2]));
        return refreshResult;
    }

    private LocalDateTime getTimerSellRefreshTime(String time) {
        LocalDateTime refreshResult = lastSellTime;
        refreshResult = refreshResult.plusHours(Long.parseLong(time.split(":")[0]));
        refreshResult = refreshResult.plusMinutes(Long.parseLong(time.split(":")[1]));
        refreshResult = refreshResult.plusSeconds(Long.parseLong(time.split(":")[2]));
        return refreshResult;
    }
}
