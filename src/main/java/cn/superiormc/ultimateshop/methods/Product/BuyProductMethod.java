package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ProductMethodStatus;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;

public class BuyProductMethod {

    public static ProductMethodStatus startBuy(String shop, String product, Player player, boolean quick) {
        return startBuy(shop, product, player, quick, false, 1);
    }

    public static ProductMethodStatus startBuy(String shop,
                                               String product,
                                               Player player,
                                               boolean quick,
                                               boolean test,
                                               int multi) {
        return startBuy(player.getInventory(),
                shop, product, player, quick, test, multi);
    }

    public static ProductMethodStatus startBuy(Inventory inventory,
                                               String shop,
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
        boolean shouldSendMessage = inventory instanceof PlayerInventory && !test && (quick ||
                tempVal1.getShopConfig().
                        getBoolean("settings.send-messages-after-buy", true));
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    product);
            return ProductMethodStatus.ERROR;
        }
        if (shouldSendMessage) {
            if (!tempVal2.getBuyCondition(player)) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-condition-not-meet",
                        "product",
                        product);
                return ProductMethodStatus.PERMISSION;
            }
        }
        if (tempVal2.getBuyPrice().empty) {
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
        // limit
        int playerUseTimes = 0;
        int serverUseTimes = 0;
        ObjectUseTimesCache tempVal9 = tempVal3.getUseTimesCache().get(tempVal2);
        ObjectUseTimesCache tempVal8 = tempVal11.getUseTimesCache().get(tempVal2);
        if (tempVal9 != null) {
            if (tempVal9.getBuyRefreshTime() != null && tempVal9.getBuyRefreshTime().isBefore(LocalDateTime.now())) {
                tempVal3.getUseTimesCache().get(tempVal2).setBuyUseTimes(0);
            }
            if (tempVal9.getCooldownBuyRefreshTime() != null && tempVal9.getCooldownBuyRefreshTime().isAfter(LocalDateTime.now())) {
                if (shouldSendMessage) {
                    LanguageManager.languageManager.sendStringText(player,
                            "buy-in-cooldown",
                            "item",
                            tempVal2.getDisplayName(player),
                            "refresh",
                            tempVal9.getBuyCooldownTimeDisplayName());
                }
                return ProductMethodStatus.IN_COOLDOWN;
            }
            playerUseTimes = tempVal9.getBuyUseTimes();
        }
        else {
            tempVal3.setUseTimesCache(shop,
                    product,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null);
            tempVal9 = tempVal3.getUseTimesCache().get(tempVal2);
        }
        if (tempVal2.getPlayerBuyLimit(player) != -1 &&
                playerUseTimes + multi - 1  >= tempVal2.getPlayerBuyLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-buy-player",
                        "item",
                        tempVal2.getDisplayName(player),
                        "times",
                        String.valueOf(playerUseTimes),
                        "limit",
                        String.valueOf(tempVal2.getPlayerBuyLimit(player)),
                        "refresh",
                        tempVal9.getBuyRefreshTimeDisplayName());

            }
            return ProductMethodStatus.PLAYER_MAX;
        }
        ObjectPrices tempVal5 = tempVal2.getBuyPrice();
        if (tempVal8 != null) {
            if (quick) {
                // 重置
                if (tempVal8.getBuyRefreshTime() != null && tempVal8.getBuyRefreshTime().isBefore(LocalDateTime.now())) {
                    ServerCache.serverCache.getUseTimesCache().get(tempVal2).setBuyUseTimes(0);
                    ServerCache.serverCache.getUseTimesCache().get(tempVal2).setLastBuyTime(null);
                }
            }
            serverUseTimes = ServerCache.serverCache.getUseTimesCache().get(tempVal2).getBuyUseTimes();
        }
        else {
            tempVal11.setUseTimesCache(shop,
                    product,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null);
            tempVal8 = tempVal11.getUseTimesCache().get(tempVal2);
        }
        if (tempVal2.getServerBuyLimit(player) != -1 &&
                serverUseTimes + multi - 1 > tempVal2.getServerBuyLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-buy-server",
                        "item",
                        tempVal2.getDisplayName(player),
                        "times",
                        String.valueOf(serverUseTimes),
                        "limit",
                        String.valueOf(tempVal2.getServerBuyLimit(player)),
                        "refresh",
                        tempVal8.getBuyRefreshTimeDisplayName());

            }
            return ProductMethodStatus.SERVER_MAX;
        }
        // price
        if (!tempVal5.takeThing(inventory, player, false, playerUseTimes, multi)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-price-not-enough",
                        "item",
                        tempVal2.getDisplayName(player),
                        "price",
                        tempVal5.getDisplayNameInChat(inventory,
                                player,
                                playerUseTimes,
                                multi));
            }
            return ProductMethodStatus.NOT_ENOUGH;
        }
        if (test) {
            return ProductMethodStatus.DONE;
        }
        // 尝试给物品
        tempVal2.getReward().giveThing(player, playerUseTimes, multi);
        // 扣钱
        tempVal5.takeThing(inventory, player, true, playerUseTimes, multi);
        // 执行动作
        tempVal2.getBuyAction().doAction(player, playerUseTimes, multi);
        // limit+1
        if (tempVal9 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("" +
                        "§x§9§8§F§B§9§8[UltimateShop] §aSet player limit value to " + tempVal9.getSellUseTimes() + multi + "!");
            }
            tempVal9.setBuyUseTimes(tempVal9.getBuyUseTimes() + multi);
            tempVal9.setLastBuyTime(LocalDateTime.now());
            tempVal9.setCooldownBuyTime();
            tempVal3.getUseTimesCache().put(tempVal2, tempVal9);
        }
        if (tempVal8 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("" +
                        "§x§9§8§F§B§9§8[UltimateShop] §aSet server limit value to " + tempVal8.getSellUseTimes() + multi + "!");
            }
            tempVal8.setBuyUseTimes(tempVal8.getBuyUseTimes() + multi);
            tempVal8.setLastBuyTime(LocalDateTime.now());
            tempVal11.getUseTimesCache().put(tempVal2, tempVal8);
        }
        if (tempVal1.getShopConfig().
                        getBoolean("settings.send-messages-after-buy", true)) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-buy",
                    "item",
                    tempVal2.getDisplayName(player),
                    "price",
                    tempVal5.getDisplayNameInChat(inventory,
                            player,
                            playerUseTimes,
                            multi),
                    "amount",
                    String.valueOf(multi));
        }
        return ProductMethodStatus.DONE;
    }
}
