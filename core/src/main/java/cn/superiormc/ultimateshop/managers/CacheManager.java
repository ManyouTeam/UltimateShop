package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.database.AbstractDatabase;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<UUID, ObjectCache> playerCacheMap = new ConcurrentHashMap<>();

    public ObjectCache serverCache;

    public CacheManager() {
        cacheManager = this;
        serverCache = new ObjectCache();
        reloadCache();
    }

    public void reloadCache() {
        shutdown();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addObjectCache(player);
        }
    }

    public void addObjectCache(Player player) {
        UUID playerUUID = player.getUniqueId();
        ObjectCache previous = playerCacheMap.put(playerUUID, new ObjectCache(player));
        if (previous != null) {
            previous.cancelResetTasks();
        }
    }

    public ObjectCache getObjectCache(Player player) {
        UUID playerUUID = player.getUniqueId();
        ObjectCache tempVal1 = playerCacheMap.get(playerUUID);
        if (tempVal1 == null) {
            addObjectCache(player);
            tempVal1 = playerCacheMap.get(playerUUID);
        }
        return tempVal1;
    }

    public void saveObjectCache(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerCacheMap.get(playerUUID) == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        playerCacheMap.get(playerUUID).shutCache(true);
    }

    public void saveObjectCacheOnDisable(Player player, boolean disable) {
        UUID playerUUID = player.getUniqueId();
        if (playerCacheMap.get(playerUUID) == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        playerCacheMap.get(playerUUID).shutCacheOnDisable(disable);
    }

    public void shutdown() {
        playerCacheMap.values().forEach(ObjectCache::cancelResetTasks);
        playerCacheMap.clear();
    }

    public void addServerCache() {
        serverCache = new ObjectCache();
    }

    public void removeObjectCache(Player player) {
        if (player != null) {
            UUID playerUUID = player.getUniqueId();
            playerCacheMap.remove(playerUUID);
        }
    }

    public void removeObjectCache(ObjectCache cache) {
        if (cache != null && cache.getPlayer() != null) {
            playerCacheMap.remove(cache.getPlayer().getUniqueId(), cache);
        }
    }

}
