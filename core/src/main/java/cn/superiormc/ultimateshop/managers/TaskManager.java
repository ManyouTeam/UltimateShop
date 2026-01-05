package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TaskManager {

    public static TaskManager taskManager;

    private SchedulerUtil saveTask;

    private SchedulerUtil sellChestTask;

    public TaskManager() {
        taskManager = this;
        if (ConfigManager.configManager.getBoolean("auto-save.enabled")) {
            initSaveTasks();
        }
        initSellChestTasks();
    }

    public void initSaveTasks() {
        saveTask = SchedulerUtil.runTaskTimer(() -> {
            if (!ConfigManager.configManager.getBoolean("auto-save.hide-message")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fAuto saving data...");
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fIf this lead to server TPS drop, " +
                        "you should consider disable auto save feature at config.yml!");
            }
            CacheManager.cacheManager.serverCache.shutCache(false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (CacheManager.cacheManager.getObjectCache(player) != null) {
                    CacheManager.cacheManager.getObjectCache(player).shutCache(false);
                }
            }
        }, 180L, ConfigManager.configManager.config.getLong("auto-save.period-tick", 600));
    }

    public void initSellChestTasks() {
        if (!ConfigManager.configManager.getSellChests().isEmpty() && !UltimateShop.isFolia) {
            sellChestTask = SchedulerUtil.runTaskTimer(
                    () -> SellChestManager.sellChestManager.tick(),
                    20L,
                    ConfigManager.configManager.getLong("sell.sell-chest.period-ticks", 60L)
            );
        }
    }

    public void cancelTask() {
        if (saveTask != null) {
            saveTask.cancel();
        }
        if (sellChestTask != null) {
            sellChestTask.cancel();
        }
    }
}
