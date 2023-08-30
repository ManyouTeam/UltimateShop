package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectPlayerUseTimesCache;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectItem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCache {

    private Player player;

    private Map<ObjectItem, ObjectPlayerUseTimesCache> useTimesCache = new HashMap<>();

    public PlayerCache(Player player) {
        this.player = player;
        initPlayerCache();
    }

    private void initPlayerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.checkData(player);
        }
    }

    public void shutPlayerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateData(player);
        }
    }

    public void setPlayerUseTimesCache(String shop,
                                       String product,
                                       int buyUseTimes,
                                       int sellUseTimes,
                                       String lastBuyTime,
                                       String lastSellTime
                                       ) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.shop-not-found",
                    "shop",
                    shop);
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    product);
            return;
        }
        useTimesCache.put(tempVal2, new ObjectPlayerUseTimesCache(buyUseTimes,
                sellUseTimes,
                lastBuyTime,
                lastSellTime,
                tempVal2));
    }

    public Map<ObjectItem, ObjectPlayerUseTimesCache> getPlayerUseTimesCache() {
        return useTimesCache;
    }

    public void setPlayerDynamicPriceCache(String shop, String product, double price) {
        // TODO...
    }
}
