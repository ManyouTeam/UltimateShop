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
                ServerCache.serverCache.shutServerCache();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CacheManager.cacheManager.playerCacheMap.get(player).shutPlayerCache();
                }
            }

        }.runTaskTimer(UltimateShop.instance, 180L, ConfigManager.configManager.config.getLong("auto-save.period-tick", 600));
    }
}
