package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.database.AbstractDatabase;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    public static CacheManager cacheManager;

    private final Map<Player, ObjectCache> ObjectCacheMap = new ConcurrentHashMap<>();

    public ObjectCache serverCache;

    public AbstractDatabase database;

    public CacheManager() {
        cacheManager = this;
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            database = new SQLDatabase();
        } else {
            database = new YamlDatabase();
        }
        database.onInit();
        serverCache = new ObjectCache();
    }

    public void addObjectCache(Player player) {
        ObjectCacheMap.put(player, new ObjectCache(player));
        ObjectCacheMap.get(player).initCache();
    }

    public ObjectCache getObjectCache(Player player) {
        ObjectCache tempVal1 = ObjectCacheMap.get(player);
        if (tempVal1 == null) {
            addObjectCache(player);
            tempVal1 = ObjectCacheMap.get(player);
        }
        return tempVal1;
    }

    public void saveObjectCache(Player player) {
        if (ObjectCacheMap.get(player) == null) {
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        ObjectCacheMap.get(player).shutCache(true);
    }

    public void saveObjectCacheOnDisable(Player player, boolean disable) {
        if (ObjectCacheMap.get(player) == null) {
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not save player data: " + player.getName() + "! " +
                    "This is usually because this player joined the server before server fully started OR other plugins kicked this player" +
                    ", ask him rejoin the server.");
            return;
        }
        ObjectCacheMap.get(player).shutCacheOnDisable(disable);
    }

    public void removeObjectCache(Player player) {
        if (player != null) {
            ObjectCacheMap.remove(player);
        }
    }

}
