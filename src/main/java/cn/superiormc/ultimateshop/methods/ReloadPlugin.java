package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        UltimateShop.instance.reloadConfig();
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCache();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.playerCacheMap.get(player).shutPlayerCache();
        }
        new ConfigManager();
        new LanguageManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.playerCacheMap.put(player, new PlayerCache(player));
            CacheManager.cacheManager.playerCacheMap.get(player).initPlayerCache();
        }
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
