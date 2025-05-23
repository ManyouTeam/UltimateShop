package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<Player, PlayerCache> playerCacheMap = new ConcurrentHashMap<>();

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

    public PlayerCache getPlayerCache(Player player) {
        PlayerCache tempVal1 = playerCacheMap.get(player);
        if (tempVal1 == null) {
            addPlayerCache(player);
            tempVal1 = playerCacheMap.get(player);
        }
        return tempVal1;
    }

    public void savePlayerCache(Player player) {
        if (playerCacheMap.get(player) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        playerCacheMap.get(player).shutPlayerCache(true);
    }

    public void savePlayerCacheOnDisable(Player player, boolean disable) {
        if (playerCacheMap.get(player) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        playerCacheMap.get(player).shutPlayerCacheOnDisable(disable);
    }

    public void removePlayerCache(Player player) {
        if (player != null) {
            playerCacheMap.remove(player);
        }
    }

}
