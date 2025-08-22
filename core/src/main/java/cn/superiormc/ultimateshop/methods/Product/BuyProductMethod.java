package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ItemFinishTransactionEvent;
import cn.superiormc.ultimateshop.api.ItemPreTransactionEvent;
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
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;

public class BuyProductMethod {

    public static ProductTradeStatus startBuy(ObjectItem item, Player player, boolean hideMessage) {
        return startBuy(item, player, hideMessage, false, 1);
    }

    public static ProductTradeStatus startBuy(ObjectItem item,
                                               Player player,
                                               boolean hideMessage,
                                               boolean test,
                                               int multi) {
        return startBuy(player.getInventory(), item, player, hideMessage, test, multi);
    }

    public static ProductTradeStatus startBuy(Inventory inventory,
                                               ObjectItem item,
                                               Player player,
                                               boolean hideMessage,
                                               boolean test,
                                               int multi) {
        if (item == null) {
            return ProductTradeStatus.ERROR;
        }
        boolean shouldSendMessage = inventory instanceof PlayerInventory && !test && (hideMessage ||
                !item.getShopObject().getShopConfig().getBoolean("settings.hide-message", false));
        if (!item.getBuyCondition(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-condition-not-meet",
                        "product",
                        item.getProduct());
            }
            return ProductTradeStatus.PERMISSION;
        }
        if (item.getBuyPrice().empty) {
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
        ObjectUseTimesCache tempVal9 = tempVal3.getUseTimesCache().get(item);
        ObjectUseTimesCache tempVal8 = tempVal11.getUseTimesCache().get(item);
        if (tempVal9 != null) {
            tempVal9.refreshBuyTimes();
            playerUseTimes = tempVal9.getBuyUseTimes();
        } else {
            tempVal9 = tempVal3.createUseTimesCache(item);
        }
        if (item.getPlayerBuyLimit(player) != -1 &&
                playerUseTimes + multi > item.getPlayerBuyLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-buy-player",
                        "item",
                        item.getDisplayName(player),
                        "times",
                        String.valueOf(playerUseTimes),
                        "limit",
                        String.valueOf(item.getPlayerBuyLimit(player)),
                        "refresh",
                        tempVal9.getBuyRefreshTimeDisplayName(),
                        "next",
                        tempVal9.getBuyRefreshTimeNextName());

            }
            return ProductTradeStatus.PLAYER_MAX;
        }
        ObjectPrices tempVal5 = item.getBuyPrice();
        if (tempVal8 != null) {
            tempVal8.refreshBuyTimes();
            serverUseTimes = tempVal8.getBuyUseTimes();
        } else {
            tempVal8 = tempVal11.createUseTimesCache(item);
        }
        if (item.getServerBuyLimit(player) != -1 &&
                serverUseTimes + multi > item.getServerBuyLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-buy-server",
                        "item",
                        item.getDisplayName(player),
                        "times",
                        String.valueOf(serverUseTimes),
                        "limit",
                        String.valueOf(item.getServerBuyLimit(player)),
                        "refresh",
                        tempVal8.getBuyRefreshTimeDisplayName(),
                        "next",
                        tempVal8.getBuyRefreshTimeNextName());
            }
            return ProductTradeStatus.SERVER_MAX;
        }
        GiveResult giveResult = null;
        TakeResult takeResult = tempVal5.take(inventory, player, playerUseTimes, multi, false);
        // API
        if (!test) {
            giveResult = item.getReward().give(player, playerUseTimes, multi);
            ItemPreTransactionEvent event = new ItemPreTransactionEvent(true, player, multi, item, giveResult, takeResult);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        // price
        if (!takeResult.getResultBoolean()) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "buy-price-not-enough",
                        "item",
                        item.getDisplayName(player),
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
        // 尝试给物品
        if (!giveResult.give(playerUseTimes, multi, player, 1)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player, "inventory-full");
            }
            return ProductTradeStatus.INVENTORY_FULL;
        }
        // 扣钱
        takeResult.take(playerUseTimes, multi, inventory, player);
        int calculateAmount = multi * item.getDisplayItemObject().getAmountPlaceholder(player);
        // 执行动作
        item.getBuyAction().runAllActions(new ObjectThingRun(player, playerUseTimes, multi, calculateAmount));
        // limit+1
        if (tempVal9 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §aSet player limit value to " + tempVal9.getBuyUseTimes() + multi + "!");
            }
            tempVal9.setBuyUseTimes(tempVal9.getBuyUseTimes() + multi);
            tempVal9.setLastBuyTime(LocalDateTime.now());
            tempVal9.setCooldownBuyTime();
            tempVal3.getUseTimesCache().put(item, tempVal9);
        }
        if (tempVal8 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §aSet server limit value to " + tempVal8.getBuyUseTimes() + multi + "!");
            }
            tempVal8.setBuyUseTimes(tempVal8.getBuyUseTimes() + multi);
            tempVal8.setLastBuyTime(LocalDateTime.now());
            tempVal8.setCooldownBuyTime();
            tempVal11.getUseTimesCache().put(item, tempVal8);
        }
        if (!item.getShopObject().getShopConfig().getBoolean("settings.hide-message", false) && !giveResult.empty && !takeResult.empty) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-buy",
                    "item",
                    item.getDisplayName(player),
                    "price",
                    ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            takeResult.getResultMap(),
                            tempVal5.getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "amount",
                    String.valueOf(calculateAmount));
        }
        if (ConfigManager.configManager.getBoolean("log-transaction.enabled") && !UltimateShop.freeVersion) {
            String log = CommonUtil.modifyString(ConfigManager.configManager.getString("log-transaction.format"),
                    "player", player.getName(),
                    "player-uuid", player.getUniqueId().toString(),
                    "shop", item.getShop(),
                    "shop-name", item.getShopObject().getShopDisplayName(),
                    "item", item.getProduct(),
                    "item-name", item.getDisplayName(player),
                    "amount", String.valueOf(calculateAmount),
                    "price", ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            takeResult.getResultMap(),
                            tempVal5.getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "buy-or-sell", "BUY");
            String filePath = ConfigManager.configManager.getString("log-transaction.file");
            if (filePath.isEmpty()) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLog: " + log);
            } else {
                SchedulerUtil.runTaskAsynchronously(() -> CommonUtil.logFile(filePath, log));
            }
        }
        ItemFinishTransactionEvent event = new ItemFinishTransactionEvent(true, player, multi, item);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult, giveResult, multi);
    }
}
