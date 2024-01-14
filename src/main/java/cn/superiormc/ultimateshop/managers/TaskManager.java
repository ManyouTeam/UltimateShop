package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {

    private BukkitTask saveTask;

    public TaskManager() {
        if (ConfigManager.configManager.getBoolean("auto-save.enabled")) {
            initSaveTasks();
        }
    }

    public void initSaveTasks() {
        saveTask = new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fAuto saving data...");
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fIf this lead to server TPS drop, " +
                        "you should consider disable auto save feature at config.yml!");
                ServerCache.serverCache.shutServerCache();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (CacheManager.cacheManager.getPlayerCache(player) != null) {
                        CacheManager.cacheManager.getPlayerCache(player).shutPlayerCache();
                    }
                }
            }

        }.runTaskTimer(UltimateShop.instance, 180L, ConfigManager.configManager.config.getLong("auto-save.period-tick", 600));
    }
}
