package cn.superiormc.ultimateshop.managers;

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

    public void cancelTask() {
        if (saveTask != null) {
            saveTask.cancel();
        }
    }
}
