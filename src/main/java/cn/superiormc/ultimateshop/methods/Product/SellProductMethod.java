package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;

public class SellProductMethod {

    public static ProductTradeStatus startSell(String shop, String product, Player player, boolean quick) {
        return startSell(shop, product, player, quick, false, 1);
    }

    public static ProductTradeStatus startSell(String shop,
                                                                   String product,
                                                                   Player player,
                                                                   boolean quick,
                                                                   boolean test,
                                                                   int multi) {
        return startSell(shop, product, player, quick, test, false, multi);
    }

    public static ProductTradeStatus startSell(String shop,
                                                                   String product,
                                                                   Player player,
                                                                   boolean quick,
                                                                   boolean test,
                                                                   boolean ableMaxSell,
                                                                   int multi) {
        return startSell(player.getInventory(),
                shop, product, player, quick, test, false, ableMaxSell, false, multi);
    }

    public static ProductTradeStatus startSell(Inventory inventory,
                                                                   String shop,
                                                                   String product,
                                                                   Player player,
                                                                   boolean quick,
                                                                   boolean test,
                                                                   boolean hide,
                                                                   boolean ableMaxSell,
                                                                   boolean sellAll,
                                                                   int multi) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.shop-not-found",
                    "shop",
                    shop);
            return ProductTradeStatus.ERROR;
        }
        boolean shouldSendMessage = !hide && inventory instanceof PlayerInventory && !test && (quick ||
                !tempVal1.getShopConfig().getBoolean("settings.hide-message", false));
        ObjectItem tempVal2 = tempVal1.getProduct(product);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    product);
            return ProductTradeStatus.ERROR;
        }
        if (!tempVal2.getSellCondition(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-condition-not-meet",
                        "product",
                        product);
            }
            return ProductTradeStatus.PERMISSION;
        }
        if (tempVal2.getSellPrice().empty) {
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
            // 重置
            tempVal9.refreshSellTimes();
            playerUseTimes = tempVal9.getSellUseTimes();
        } else {
            tempVal9 = tempVal3.createUseTimesCache(tempVal2);
        }
        // 更改multi
        ObjectProducts tempVal5 = tempVal2.getReward();
        if (ableMaxSell) {
            if (tempVal5.getMaxAbleSellAmount(inventory, player, playerUseTimes) > 0) {
                multi = tempVal5.getMaxAbleSellAmount(inventory, player, playerUseTimes);
            }
            if (tempVal2.getPlayerSellLimit(player) != -1 &&
                    multi > tempVal2.getPlayerSellLimit(player) - playerUseTimes &&
                    tempVal2.getPlayerSellLimit(player) - playerUseTimes > 0) {
                multi = tempVal2.getPlayerSellLimit(player) - playerUseTimes;
            }
            int maxAmount = ConfigManager.configManager.getIntOrDefault("menu.sell-all.max-amount",
                    "sell.max-amount", 128);
            if (maxAmount >= 0 && multi >= maxAmount) {
                multi = maxAmount;
            }
        }

        if (tempVal2.getPlayerSellLimit(player) != -1 &&
                playerUseTimes + multi > tempVal2.getPlayerSellLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-sell-player",
                        "item",
                        tempVal2.getDisplayName(player),
                        "times",
                        String.valueOf(playerUseTimes),
                        "limit",
                        String.valueOf(tempVal2.getPlayerSellLimit(player)),
                        "refresh",
                        tempVal9.getSellRefreshTimeDisplayName());

            }
            return ProductTradeStatus.PLAYER_MAX;
        }
        if (tempVal8 != null) {
            tempVal8.refreshSellTimes();
            serverUseTimes = tempVal8.getSellUseTimes();
        } else {
            tempVal11.createUseTimesCache(tempVal2);
        }
        if (tempVal2.getServerSellLimit(player) != -1 &&
                serverUseTimes + multi > tempVal2.getServerSellLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-sell-server",
                        "item",
                        tempVal2.getDisplayName(player),
                        "times",
                        String.valueOf(serverUseTimes),
                        "limit",
                        String.valueOf(tempVal2.getServerSellLimit(player)),
                        "refresh",
                        tempVal8.getSellRefreshTimeDisplayName());

            }
            return ProductTradeStatus.SERVER_MAX;
        }
        TakeResult takeResult = tempVal5.takeSingleThing(inventory, player, playerUseTimes, multi, false);
        // price
        if (!takeResult.getResultBoolean()) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-products-not-enough",
                        "item",
                        tempVal2.getDisplayName(player));
            }
            return ProductTradeStatus.NOT_ENOUGH;
        }
        if (test) {
            return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult);
        }
        // 尝试给物品
        // 回收的价格就是给的
        GiveResult giveResult = tempVal2.getSellPrice().giveSingleThing(player, playerUseTimes, multi);
        // 尝试给物品
        if (!tempVal2.getSellPrice().giveThing(playerUseTimes, player, giveResult.getResultMap())) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player, "inventory-full");
            }
            return ProductTradeStatus.INVENTORY_FULL;
        }
        // 扣物品
        // 扣的是奖励中的东西
        tempVal5.takeThing(inventory, player, takeResult.getResultMap());
        // 执行动作
        tempVal2.getSellAction().runAllActions(new ObjectThingRun(player, playerUseTimes, multi, sellAll));
        // limit+1
        if (tempVal9 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §aSet player limit value to " + tempVal9.getSellUseTimes() + multi + "!");
            }
            tempVal9.setSellUseTimes(tempVal9.getSellUseTimes() + multi);
            tempVal9.setLastSellTime(LocalDateTime.now());
            tempVal9.setCooldownSellTime();
            tempVal3.getUseTimesCache().put(tempVal2, tempVal9);
        }
        if (tempVal8 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §aSet server limit value to " + tempVal8.getSellUseTimes() + multi + "!");
            }
            tempVal8.setSellUseTimes(tempVal8.getSellUseTimes() + multi);
            tempVal8.setLastSellTime(LocalDateTime.now());
            tempVal11.getUseTimesCache().put(tempVal2, tempVal8);
        }
        if (!hide && tempVal1.getShopConfig().getBoolean("settings.send-messages-after-buy", true) && !giveResult.empty && !takeResult.empty) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-sell",
                    "item",
                    tempVal2.getDisplayName(player),
                    "price",
                    ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            giveResult.getResultMap(),
                            tempVal2.getSellPrice().getMode(),
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
                            giveResult.getResultMap(),
                            tempVal2.getSellPrice().getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "buy-or-sell", "SELL");
            String filePath = ConfigManager.configManager.getString("log-transaction.file");
            if (filePath.isEmpty()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLog: " + log);
            }  else {
                SchedulerUtil.runTaskAsynchronously(() -> CommonUtil.logFile(filePath, log));
            }
        }
        return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult, giveResult, multi);
    }
}
