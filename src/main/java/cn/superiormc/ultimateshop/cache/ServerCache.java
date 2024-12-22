package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCache {

    public static ServerCache serverCache;

    public Map<ObjectItem, ObjectUseTimesCache> useTimesCache = new HashMap<>();

    public Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> randomPlaceholderCache = new HashMap<>();

    public boolean server;

    public Player player = null;

    public ServerCache() {
        this.server = true;
        serverCache = this;
        initServerCache();
    }

    public ServerCache(Player player) {
        this.server = false;
        this.player = player;
    }

    public void initServerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.checkData(this);
        } else {
            YamlDatabase.checkData(this);
        }
    }

    public void shutServerCache(boolean quitServer) {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateData(this, quitServer);
        } else {
            YamlDatabase.updateData(this, quitServer);
        }
    }

    public void shutServerCacheOnDisable(boolean disable) {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataOnDisable(this, disable);
        } else {
            YamlDatabase.updateData(this, true);
        }
    }

    public ObjectUseTimesCache createUseTimesCache(ObjectItem product) {
        if (product == null) {
            return null;
        }
        if (!useTimesCache.containsKey(product)) {
            useTimesCache.put(product, new ObjectUseTimesCache(this,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null,
                    product,
                    true));
        }
        return useTimesCache.get(product);
    }

    public void setUseTimesCache(String shop,
                                 String product,
                                 int buyUseTimes,
                                 int sellUseTimes,
                                 String lastBuyTime,
                                 String lastSellTime,
                                 String cooldownBuyTime,
                                 String cooldownSellTime
    ) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
        if (tempVal1 == null) {
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            return;
        }
        useTimesCache.put(tempVal2, new ObjectUseTimesCache(this,
                buyUseTimes,
                sellUseTimes,
                lastBuyTime,
                lastSellTime,
                cooldownBuyTime,
                cooldownSellTime,
                tempVal2,
                false));
    }

    public void setRandomPlaceholderCache(ObjectRandomPlaceholder placeholder,
                                          String refreshDoneTime,
                                          List<String> nowValue) {
        if (placeholder == null) {
            return;
        }
        if (nowValue == null) {
            return;
        }
        randomPlaceholderCache.put(placeholder, new ObjectRandomPlaceholderCache(placeholder, nowValue, CommonUtil.stringToTime(refreshDoneTime)));
    }

    public void setRandomPlaceholderCache(String id,
                                          String refreshDoneTime,
                                          List<String> nowValue) {
        if (nowValue == null) {
            return;
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return;
        }
        randomPlaceholderCache.put(tempVal1, new ObjectRandomPlaceholderCache(tempVal1, nowValue, CommonUtil.stringToTime(refreshDoneTime)));
    }

    public void addRandomPlaceholderCache(ObjectRandomPlaceholder placeholder) {
        if (placeholder == null) {
            return;
        }
        randomPlaceholderCache.put(placeholder, new ObjectRandomPlaceholderCache(placeholder));
    }

    public Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> getRandomPlaceholderCache() {
        return randomPlaceholderCache;
    }

    public Map<ObjectItem, ObjectUseTimesCache> getUseTimesCache() {
        return useTimesCache;
    }
}
