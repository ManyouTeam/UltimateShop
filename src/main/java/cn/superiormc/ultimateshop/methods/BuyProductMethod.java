package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectPlayerUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectItem;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class BuyProductMethod {

    public static ProductMethodStatus startBuy(String shop, String product, Player player, boolean quick) {
        return startBuy(shop, product, player, quick, false);
    }

    public static ProductMethodStatus startBuy(String shop, String product, Player player, boolean quick, boolean test) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.shop-not-found",
                    "shop",
                    shop);
            return ProductMethodStatus.ERROR;
        }
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    product);
            return ProductMethodStatus.ERROR;
        }
        PlayerCache tempVal3 = CacheManager.cacheManager.playerCacheMap.get(player);
        if (tempVal3 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.player-not-found",
                    "player",
                    player.getName());
            return ProductMethodStatus.ERROR;
        }
        // limit
        int useTimes = 0;
        ObjectPlayerUseTimesCache tempVal9 = tempVal3.getPlayerUseTimesCache().get(tempVal2);
        if (tempVal9 != null) {
            if (quick) {
                // 重置
                if (tempVal9.getBuyRefreshTime().isAfter(LocalDateTime.now())) {
                    tempVal3.getPlayerUseTimesCache().get(tempVal2).setBuyUseTimes(0);
                }
            }
            useTimes = tempVal3.getPlayerUseTimesCache().get(tempVal2).getBuyUseTimes();
            if (tempVal2.getBuyLimit(player) != -1 && useTimes > tempVal2.getBuyLimit(player)) {
                if (quick) {
                    LanguageManager.languageManager.sendStringText(player,
                            "limit-reached-buy",
                            "item",
                            tempVal2.getDisplayName(),
                            "times",
                            String.valueOf(useTimes),
                            "limit",
                            String.valueOf(tempVal2.getBuyLimit(player)),
                            "refresh",
                            tempVal9.getBuyRefreshTimeDisplayName());

                }
                return ProductMethodStatus.MAX;
            }
        }
        // price
        ObjectPrices tempVal5 = tempVal2.getBuyPrice();
        if (!tempVal5.takeThing(player, false, useTimes)) {
            if (quick) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-price-not-enough",
                        "item",
                        tempVal2.getDisplayName(),
                        "price",
                        tempVal5.getDisplayNameWithOneLine(
                                useTimes,
                                ConfigManager.configManager.getString("placeholder.price.split-symbol")));
            }
            return ProductMethodStatus.NOT_ENOUGH;
        }
        if (test) {
            return ProductMethodStatus.DONE;
        }
        // 尝试给物品
        tempVal2.getReward().giveThing(player, useTimes);
        // 扣钱
        tempVal5.takeThing(player, true, useTimes);
        // 执行动作
        tempVal2.getBuyAction().doAction(player);
        // limit+1
        if (ConfigManager.configManager.getBoolean("database.enabled") && tempVal9 != null) {
            tempVal9.setBuyUseTimes(tempVal9.getBuyUseTimes() + 1);
            tempVal3.getPlayerUseTimesCache().put(tempVal2, tempVal9);
        }
        LanguageManager.languageManager.sendStringText(player,
                "success-buy",
                "item",
                tempVal2.getDisplayName(),
                "price",
                tempVal5.getDisplayNameWithOneLine(
                        useTimes,
                        ConfigManager.configManager.getString("placeholder.price.split-symbol")));
        return ProductMethodStatus.DONE;
    }
}
