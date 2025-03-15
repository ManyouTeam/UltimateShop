package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        SchedulerUtil.runTaskLater(() -> {
            CacheManager.cacheManager.addPlayerCache(event.getPlayer());
            if (ConfigManager.configManager.getBoolean("bungeecord-sync.enabled") && ServerCache.serverCache != null) {
                ServerCache.serverCache.initServerCache();
            }
        }, ConfigManager.configManager.getLong("cache.load-delay", 7L));
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        CacheManager.cacheManager.savePlayerCache(event.getPlayer());
        if (ConfigManager.configManager.getBoolean("bungeecord-sync.enabled") && ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCache(false);
        }
        AbstractGUI.playerList.remove(event.getPlayer());
    }
}
