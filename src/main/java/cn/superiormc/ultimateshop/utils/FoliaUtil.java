package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FoliaUtil {

    public static void closeInvForFolia(InvGUI gui) {
        Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> {
            if (gui.guiMode == GUIMode.NOT_EDITING) {
                gui.getMenu().doCloseAction(gui.getPlayer());
            }
        }, 4L);
    }

    public static void addCacheForFolia(Player player) {
        Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> {
            CacheManager.cacheManager.addPlayerCache(player);
            if (ConfigManager.configManager.getBoolean("bungeecord-sync.enabled") && ServerCache.serverCache != null) {
                ServerCache.serverCache.initServerCache();
            }
        }, 7L);
    }
}
