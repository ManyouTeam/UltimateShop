package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static CacheManager cacheManager;

    public Map<Player, PlayerCache> playerCacheMap = new ConcurrentHashMap<>();

    public ServerCache serverCache;

    public CacheManager() {
        cacheManager = this;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.closeSQL();
            SQLDatabase.initSQL();
        }
        serverCache = new ServerCache();
    }

    public void addPlayerCache(Player player) {
        playerCacheMap.put(player, new PlayerCache(player));
        playerCacheMap.get(player).initPlayerCache();
    }

    public void savePlayerCache(Player player) {
        if (playerCacheMap.get(player) == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCache();
    }

    public void savePlayerCacheOnDisable(Player player) {
        if (playerCacheMap.get(player) == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not save player data: " + player.getName() + "!");
            return;
        }
        playerCacheMap.get(player).shutPlayerCacheOnDisable();
    }

    public void removePlayerCache(Player player) {
        if (player != null) {
            playerCacheMap.remove(player);
        }
    }

}
