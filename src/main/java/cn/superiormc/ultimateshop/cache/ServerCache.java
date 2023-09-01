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

    public Player player;

    public ServerCache() {
        this.server = true;
        serverCache = this;
    }

    public ServerCache(Player player) {
        this.server = false;
        this.player = player;
    }

    public void initServerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.checkData(null);
        }
        else {
            YamlDatabase.checkData(null);
        }
    }

    public void shutServerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateData(null);
        }
        else {
            YamlDatabase.updateData(null);
        }
    }

    public void setUseTimesCache(String shop,
                                 String product,
                                 int buyUseTimes,
                                 int sellUseTimes,
                                 String lastBuyTime,
                                 String lastSellTime
    ) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(
                    "error.shop-not-found",
                    "shop",
                    shop);
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(
                    "error.product-not-found",
                    "product",
                    product);
            return;
        }
        useTimesCache.put(tempVal2, new ObjectUseTimesCache(buyUseTimes,
                sellUseTimes,
                lastBuyTime,
                lastSellTime,
                tempVal2));
    }

    public Map<ObjectItem, ObjectUseTimesCache> getUseTimesCache() {
        return useTimesCache;
    }
}
