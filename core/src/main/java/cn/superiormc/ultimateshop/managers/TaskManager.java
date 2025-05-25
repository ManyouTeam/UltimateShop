package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TaskManager {

    public static TaskManager taskManager;

    private SchedulerUtil saveTask;

    public TaskManager() {
        taskManager = this;
        if (ConfigManager.configManager.getBoolean("auto-save.enabled")) {
            initSaveTasks();
        }
    }

    public void initSaveTasks() {
        saveTask = SchedulerUtil.runTaskTimer(() -> {
            if (!ConfigManager.configManager.getBoolean("auto-save.hide-message")) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fAuto saving data...");
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fIf this lead to server TPS drop, " +
                        "you should consider disable auto save feature at config.yml!");
            }
            ServerCache.serverCache.shutServerCache(false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (CacheManager.cacheManager.getPlayerCache(player) != null) {
                    CacheManager.cacheManager.getPlayerCache(player).shutPlayerCache(false);
                }
            }
        }, 180L, ConfigManager.configManager.config.getLong("auto-save.period-tick", 600));
    }

    public void cancelTask() {
        if (saveTask != null) {
            saveTask.cancel();
        }
    }
}
