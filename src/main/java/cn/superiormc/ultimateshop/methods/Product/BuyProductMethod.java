package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;

public class BuyProductMethod {

    public static ProductTradeStatus startBuy(String shop, String product, Player player, boolean quick) {
        return startBuy(shop, product, player, quick, false, 1);
    }

    public static ProductTradeStatus startBuy(String shop,
                                               String product,
                                               Player player,
                                               boolean quick,
                                               boolean test,
                                               int multi) {
        return startBuy(player.getInventory(),
                shop, product, player, quick, test, multi);
    }

    public static ProductTradeStatus startBuy(Inventory inventory,
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
            return ProductTradeStatus.ERROR;
        }
        boolean shouldSendMessage = inventory instanceof PlayerInventory && !test && (quick ||
                !tempVal1.getShopConfig().getBoolean("settings.hide-message", false));
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    product);
            return ProductTradeStatus.ERROR;
        }
        if (!tempVal2.getBuyCondition(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-condition-not-meet",
                        "product",
                        product);
            }
            return ProductTradeStatus.PERMISSION;
        }
        if (tempVal2.getBuyPrice().empty) {
            return ProductTradeStatus.ERROR;
        }
        PlayerCache tempVal3 = CacheManager.cacheManager.getPlayerCache(player);
        ServerCache tempVal11 = ServerCache.serverCache;
        if (tempVal3 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.player-not-found",
                    "player",
                    player.getName());
            return ProductTradeStatus.ERROR;
        }
        // limit
        int playerUseTimes = 0;
        int serverUseTimes = 0;
        ObjectUseTimesCache tempVal9 = tempVal3.getUseTimesCache().get(tempVal2);
        ObjectUseTimesCache tempVal8 = tempVal11.getUseTimesCache().get(tempVal2);
        if (tempVal9 != null) {
            tempVal9.refreshBuyTimes();
            playerUseTimes = tempVal9.getBuyUseTimes();
        }
        else {
            tempVal3.setUseTimesCache(tempVal2,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null);
            tempVal9 = tempVal3.getUseTimesCache().get(tempVal2);
        }
        if (tempVal2.getPlayerBuyLimit(player) != -1 &&
                playerUseTimes + multi > tempVal2.getPlayerBuyLimit(player)) {
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
            return ProductTradeStatus.PLAYER_MAX;
        }
        ObjectPrices tempVal5 = tempVal2.getBuyPrice();
        if (tempVal8 != null) {
            if (quick) {
                // 重置
                tempVal8.refreshBuyTimes();
            }
            serverUseTimes = ServerCache.serverCache.getUseTimesCache().get(tempVal2).getBuyUseTimes();
        }
        else {
            tempVal11.setUseTimesCache(tempVal2,
                    0,
                    0,
                    null,
                    null,
                    null,
                    null);
            tempVal8 = tempVal11.getUseTimesCache().get(tempVal2);
        }
        if (tempVal2.getServerBuyLimit(player) != -1 &&
                serverUseTimes + multi > tempVal2.getServerBuyLimit(player)) {
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
            return ProductTradeStatus.SERVER_MAX;
        }
        // price
        TakeResult takeResult = tempVal5.takeSingleThing(inventory, player, playerUseTimes, multi, false);
        if (!takeResult.getResultBoolean()) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-price-not-enough",
                        "item",
                        tempVal2.getDisplayName(player),
                        "price",
                        ObjectPrices.getDisplayNameInLine(player,
                                multi,
                                takeResult.getResultMap(),
                                tempVal5.getMode(),
                                !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")));
            }
            return ProductTradeStatus.NOT_ENOUGH;
        }
        if (test) {
            return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult);
        }
        GiveResult giveResult = tempVal2.getReward().giveSingleThing(player, playerUseTimes, multi);
        // 尝试给物品
        if (!tempVal2.getReward().giveThing(playerUseTimes, player, giveResult.getResultMap())) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player, "inventory-full");
            }
            return ProductTradeStatus.INVENTORY_FULL;
        }
        // 扣钱
        tempVal5.takeThing(inventory, player, takeResult.getResultMap());
        // 执行动作
        tempVal2.getBuyAction().runAllActions(new ObjectThingRun(player, playerUseTimes, multi));
        // limit+1
        if (tempVal9 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §aSet player limit value to " + tempVal9.getBuyUseTimes() + multi + "!");
            }
            tempVal9.setBuyUseTimes(tempVal9.getBuyUseTimes() + multi);
            tempVal9.setLastBuyTime(LocalDateTime.now());
            tempVal9.setCooldownBuyTime();
            tempVal3.getUseTimesCache().put(tempVal2, tempVal9);
        }
        if (tempVal8 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §aSet server limit value to " + tempVal8.getBuyUseTimes() + multi + "!");
            }
            tempVal8.setBuyUseTimes(tempVal8.getBuyUseTimes() + multi);
            tempVal8.setLastBuyTime(LocalDateTime.now());
            tempVal11.getUseTimesCache().put(tempVal2, tempVal8);
        }
        if (tempVal1.getShopConfig().getBoolean("settings.send-messages-after-buy", true) && !giveResult.empty && !takeResult.empty) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-buy",
                    "item",
                    tempVal2.getDisplayName(player),
                    "price",
                    ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            takeResult.getResultMap(),
                            tempVal5.getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "amount",
                    String.valueOf(multi));
        }
        if (ConfigManager.configManager.getBoolean("log-transaction.enabled") && !UltimateShop.freeVersion) {
            String log = CommonUtil.modifyString(ConfigManager.configManager.getString("log-transaction.format"),
                    "player", player.getName(),
                    "player-uuid", player.getUniqueId().toString(),
                    "shop", shop,
                    "shop-name", tempVal1.getShopDisplayName(),
                    "item", product,
                    "item-name", tempVal2.getDisplayName(player),
                    "amount", String.valueOf(multi),
                    "price", ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            takeResult.getResultMap(),
                            tempVal5.getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "buy-or-sell", "BUY");
            String filePath = ConfigManager.configManager.getString("log-transaction.file");
            if (filePath.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLog: " + log);
            } else {
                SchedulerUtil.runTaskAsynchronously(() -> CommonUtil.logFile(filePath, log));
            }
        }
        return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult, giveResult, multi);
    }
}
