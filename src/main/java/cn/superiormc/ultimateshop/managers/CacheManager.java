package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static CacheManager cacheManager;

    public Map<Player, PlayerCache> playerCacheMap = new HashMap<>();

    public ServerCache serverCache;

    public CacheManager() {
        cacheManager = this;
        serverCache = new ServerCache();
        serverCache.initServerCache();
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.initSQL();
        }
    }

    public void addPlayerCache(Player player) {
        playerCacheMap.put(player, new PlayerCache(player));
        playerCacheMap.get(player).initPlayerCache();
    }

    public void savePlayerCache(Player player) {
        playerCacheMap.get(player).shutPlayerCache();
    }

}
