package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.utils.FoliaUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (UltimateShop.isFolia) {
            FoliaUtil.addCacheForFolia(event.getPlayer());
        } else {
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                CacheManager.cacheManager.addPlayerCache(event.getPlayer());
                if (ConfigManager.configManager.getBoolean("bungeecord-sync.enabled") && ServerCache.serverCache != null) {
                    ServerCache.serverCache.initServerCache();
                }
            }, 7L);
        }
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
