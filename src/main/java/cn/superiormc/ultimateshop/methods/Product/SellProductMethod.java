package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ProductMethodStatus;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class SellProductMethod {

    public static ProductMethodStatus startSell(String shop, String product, Player player, boolean quick) {
        return startSell(shop, product, player, quick, false);
    }

    public static ProductMethodStatus startSell(String shop, String product, Player player, boolean quick, boolean test) {
        return startSell(shop, product, player, quick, test, 1);
    }

    public static ProductMethodStatus startSell(String shop,
                                                String product,
                                                Player player,
                                                boolean quick,
                                                boolean test,
                                                int multi) {
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
        ServerCache tempVal11 = ServerCache.serverCache;
        if (tempVal3 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.player-not-found",
                    "player",
                    player.getName());
            return ProductMethodStatus.ERROR;
        }
        /*
        if (quick && !(player.hasPermission("ultimateshop.quicksell." + shop + "." + product) ||
                player.hasPermission("ultimateshop.quicksell.*"))) {
            return "NoPermission";
        }
         */
        // limit
        int playerUseTimes = 0;
        int serverUseTimes = 0;
        ObjectUseTimesCache tempVal9 = tempVal3.getUseTimesCache().get(tempVal2);
        ObjectUseTimesCache tempVal8 = tempVal11.getUseTimesCache().get(tempVal2);
        if (tempVal9 != null) {
            if (quick) {
                // 重置
                if (tempVal9.getSellRefreshTime() != null && tempVal9.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                    tempVal3.getUseTimesCache().get(tempVal2).setSellUseTimes(0);
                    tempVal3.getUseTimesCache().get(tempVal2).setLastSellTime(null);
                }
            }
            playerUseTimes = tempVal9.getSellUseTimes();
            if (tempVal2.getPlayerSellLimit(player) != -1 &&
                    playerUseTimes + multi - 1 >= tempVal2.getPlayerSellLimit(player)) {
                if (quick) {
                    LanguageManager.languageManager.sendStringText(player,
                            "limit-reached-sell-player",
                            "item",
                            tempVal2.getDisplayName(),
                            "times",
                            String.valueOf(playerUseTimes),
                            "limit",
                            String.valueOf(tempVal2.getPlayerSellLimit(player)),
                            "refresh",
                            tempVal9.getSellRefreshTimeDisplayName());

                }
                return ProductMethodStatus.PLAYER_MAX;
            }
        }
        else {
            tempVal3.setUseTimesCache(shop,
                    product,
                    0,
                    0,
                    null,
                    null);
        }
        if (tempVal8 != null) {
            if (quick) {
                // 重置
                if (tempVal8.getSellRefreshTime() != null && tempVal8.getSellRefreshTime().isAfter(LocalDateTime.now())) {
                    ServerCache.serverCache.getUseTimesCache().get(tempVal2).setSellUseTimes(0);
                }
            }
            serverUseTimes = ServerCache.serverCache.getUseTimesCache().get(tempVal2).getSellUseTimes();
            if (tempVal2.getServerSellLimit(player) != -1 &&
                    serverUseTimes + multi - 1 >= tempVal2.getServerSellLimit(player)) {
                if (quick) {
                    LanguageManager.languageManager.sendStringText(player,
                            "limit-reached-sell-server",
                            "item",
                            tempVal2.getDisplayName(),
                            "times",
                            String.valueOf(serverUseTimes),
                            "limit",
                            String.valueOf(tempVal2.getServerSellLimit(player)),
                            "refresh",
                            tempVal8.getSellRefreshTimeDisplayName());

                }
                return ProductMethodStatus.SERVER_MAX;
            }
        }
        else {
            tempVal11.setUseTimesCache(shop,
                    product,
                    0,
                    0,
                    null,
                    null);
        }
        // price
        ObjectProducts tempVal5 = tempVal2.getReward();
        if (!tempVal5.takeThing(player, false, playerUseTimes, multi)) {
            if (quick) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-products-not-enough",
                        "item",
                        tempVal2.getDisplayName());
            }
            return ProductMethodStatus.NOT_ENOUGH;
        }
        if (test) {
            return ProductMethodStatus.DONE;
        }
        // 尝试给物品
        // 回收的价格就是给的
        tempVal2.getSellPrice().giveThing(player, playerUseTimes, multi);
        // 扣物品
        // 扣的是奖励中的东西
        tempVal5.takeThing(player, true, playerUseTimes, multi);
        // 执行动作
        tempVal2.getSellAction().doAction(player, multi);
        // limit+1
        if (tempVal9 != null) {
            tempVal9.setSellUseTimes(tempVal9.getSellUseTimes() + multi);
            tempVal9.setLastSellTime(LocalDateTime.now());
            tempVal3.getUseTimesCache().put(tempVal2, tempVal9);
        }
        if (tempVal8 != null) {
            tempVal8.setSellUseTimes(tempVal8.getSellUseTimes() + multi);
            tempVal8.setLastSellTime(LocalDateTime.now());
            tempVal11.getUseTimesCache().put(tempVal2, tempVal8);
        }
        LanguageManager.languageManager.sendStringText(player,
                "success-sell",
                "item",
                tempVal2.getDisplayName(),
                "price",
                tempVal2.getSellPrice().getDisplayNameWithOneLine(
                        playerUseTimes,
                        multi));
        return ProductMethodStatus.DONE;
    }
}
