package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
        serverCache.initCache();
        reloadCache();
    }

    public void reloadCache() {
        shutdown();
        for (Player player : Bukkit.getOnlinePlayers()) {
            addObjectCache(player);
            loadPlayerCache(player);
        }
    }

    public void addObjectCache(Player player) {
        if (player != null) {
            UUID playerUUID = player.getUniqueId();
            playerCacheMap.put(playerUUID, new ObjectCache(player));
        }
    }

    public void loadPlayerCache(Player player) {
        ObjectCache cache = playerCacheMap.get(player.getUniqueId());
        if (cache != null) {
            cache.initCache();
        }
    }

    @Nullable
    public ObjectCache getObjectCache(Player player) {
        return playerCacheMap.get(player.getUniqueId());
    }

    public void saveObjectCache(Player player) {
        ObjectCache cache = getObjectCache(player);
        if (cache == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        if (cache.canNotModify()) {
            removeObjectCache(cache);
            cache.close();
        } else {
            cache.shutCache(true);
        }
    }

    public void saveObjectCacheOnDisable(Player player, boolean disable) {
        ObjectCache cache = getObjectCache(player);
        if (cache == null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        if (cache.canNotModify()) {
            removeObjectCache(cache);
            cache.close();
        } else {
            cache.shutCacheOnDisable(disable);
        }
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
