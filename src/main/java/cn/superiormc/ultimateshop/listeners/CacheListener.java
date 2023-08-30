package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.managers.CacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        CacheManager.cacheManager.addPlayerCache(event.getPlayer());
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        CacheManager.cacheManager.savePlayerCache(event.getPlayer());
    }
}
