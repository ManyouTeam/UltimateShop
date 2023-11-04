package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ServerCache {

    public static ServerCache serverCache;

    public Map<ObjectItem, ObjectUseTimesCache> useTimesCache = new HashMap<>();

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
        }
        else {
            YamlDatabase.checkData(this);
        }
    }

    public void shutServerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateData(this);
        }
        else {
            YamlDatabase.updateData(this);
        }
    }

    public void shutServerCacheOnDisable() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataNoAsync(this);
        }
        else {
            YamlDatabase.updateData(this);
        }
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
        useTimesCache.put(tempVal2, new ObjectUseTimesCache(buyUseTimes,
                sellUseTimes,
                lastBuyTime,
                lastSellTime,
                cooldownBuyTime,
                cooldownSellTime,
                tempVal2));
    }

    public Map<ObjectItem, ObjectUseTimesCache> getUseTimesCache() {
        return useTimesCache;
    }
}
