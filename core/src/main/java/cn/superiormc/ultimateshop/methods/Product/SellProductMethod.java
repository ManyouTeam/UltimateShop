package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ItemFinishTransactionEvent;
import cn.superiormc.ultimateshop.api.ItemPreTransactionEvent;

import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.MaxSellResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class SellProductMethod {

    public static ProductTradeStatus startSell(ObjectItem item, Player player, boolean forceDisplayMessage) {
        return startSell(item, player, forceDisplayMessage, false, 1);
    }

    public static ProductTradeStatus startSell(ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean test,
                                               int multi) {
        return startSell(item, player, forceDisplayMessage, test, false, multi);
    }

    public static ProductTradeStatus startSell(ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               boolean ableMaxSell,
                                               int multi) {
        return startSell(player.getInventory(), item, player, forceDisplayMessage, notCost, ableMaxSell, false, multi, 1);
    }

    public static ProductTradeStatus startSell(Inventory inventory,
                                               ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               boolean ableMaxSell,
                                               boolean sellAll,
                                               int multi,
                                               double multiplier) {
        if (item == null) {
            return ProductTradeStatus.ERROR;
        }
        if (item.getShopObject().getProductNotHidden(player, item) == null) {
            return ProductTradeStatus.ERROR;
        }
        boolean shouldSendMessage = inventory instanceof PlayerInventory && !notCost && (forceDisplayMessage ||
                !item.getShopObject().getShopConfig().getBoolean("settings.hide-message", false));
        if (!item.getSellCondition(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-condition-not-meet",
                        "product",
                        item.getProduct());
            }
            return ProductTradeStatus.PERMISSION;
        }
        if (item.getSellPrice().empty) {
            return ProductTradeStatus.ERROR;
        }
        ObjectCache tempVal3 = CacheManager.cacheManager.getObjectCache(player);
        ObjectCache tempVal11 = CacheManager.cacheManager.serverCache;
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
            // 重置
            tempVal9.refreshTimes();
            playerUseTimes = tempVal9.getSellUseTimes();
        } else {
            tempVal9 = tempVal3.createUseTimesCache(item);
        }
        // 更改multi
        ObjectProducts tempVal5 = item.getReward();
        MaxSellResult sellResult = null;
        if (ableMaxSell) {
            int maxAmount = Integer.MAX_VALUE;
            if (item.getPlayerSellLimit(player) != -1) {
                maxAmount = item.getPlayerSellLimit(player) - playerUseTimes;
            }
            if (item.getServerSellLimit(player) != -1 &&
                    maxAmount > item.getServerSellLimit(player) - serverUseTimes) {
                maxAmount = item.getServerSellLimit(player) - serverUseTimes;
            }
            int configMaxAmount = ConfigManager.configManager.getIntOrDefault("menu.sell-all.max-amount",
                    "sell.max-amount", 128);
            if (configMaxAmount >= 0 && maxAmount > configMaxAmount) {
                maxAmount = configMaxAmount;
            }
            if (maxAmount < 0) {
                maxAmount = 0;
            }
            sellResult = tempVal5.getMaxAbleSellAmount(inventory, player, playerUseTimes, multi, maxAmount);
            if (sellResult.getMaxAmount() > 0) {
                multi = sellResult.getMaxAmount();
            }
        }
        if (item.getPlayerSellLimit(player) != -1 &&
                playerUseTimes + multi > item.getPlayerSellLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-sell-player",
                        "item",
                        item.getDisplayName(player),
                        "times",
                        String.valueOf(playerUseTimes),
                        "limit",
                        String.valueOf(item.getPlayerSellLimit(player)),
                        "refresh",
                        tempVal9.getSellRefreshTimeDisplayName(),
                        "next",
                        tempVal9.getSellRefreshTimeNextName());

            }
            return ProductTradeStatus.PLAYER_MAX;
        }
        if (tempVal8 != null) {
            tempVal8.refreshTimes();
            serverUseTimes = tempVal8.getSellUseTimes();
        } else {
            tempVal8 = tempVal11.createUseTimesCache(item);
        }
        if (item.getServerSellLimit(player) != -1 &&
                serverUseTimes + multi > item.getServerSellLimit(player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "limit-reached-sell-server",
                        "item",
                        item.getDisplayName(player),
                        "times",
                        String.valueOf(serverUseTimes),
                        "limit",
                        String.valueOf(item.getServerSellLimit(player)),
                        "refresh",
                        tempVal8.getSellRefreshTimeDisplayName(),
                        "next",
                        tempVal8.getSellRefreshTimeNextName());

            }
            return ProductTradeStatus.SERVER_MAX;
        }
        GiveResult giveResult = null;
        TakeResult takeResult = null;
        if (sellResult != null) {
            takeResult = sellResult.getTakeResult();
        } else {
            takeResult = tempVal5.take(inventory, player, playerUseTimes, multi, false);
        }
        // API
        if (!notCost) {
            giveResult = item.getSellPrice().give(player, playerUseTimes, multi);
            ItemPreTransactionEvent event = new ItemPreTransactionEvent(false, player, multi, item, giveResult, takeResult);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        // price
        if (!takeResult.getResultBoolean()) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-products-not-enough",
                        "item",
                        item.getDisplayName(player));
            }
            return ProductTradeStatus.NOT_ENOUGH;
        }
        if (notCost) {
            return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult);
        }
        // 尝试给物品
        if (!giveResult.give(playerUseTimes, multi, player, multiplier)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player, "inventory-full");
            }
            return ProductTradeStatus.INVENTORY_FULL;
        }
        // 扣物品
        // 扣的是奖励中的东西
        takeResult.take(playerUseTimes, multi, inventory, player);
        int calculateAmount = multi * item.getDisplayItemObject().getAmountPlaceholder(player);
        // 执行动作
        item.getSellAction().runAllActions(new ObjectThingRun(player, playerUseTimes, multi, calculateAmount, sellAll));
        // limit+1
        if (tempVal9 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                int newValue = tempVal9.getSellUseTimes() + multi;
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §aSet player limit value to " + newValue + "!");
            }
            tempVal9.setSellUseTimes(tempVal9.getSellUseTimes() + multi);
            tempVal9.setLastSellTime(CommonUtil.getNowTime());
            tempVal9.setCooldownSellTime();
        }
        if (tempVal8 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                int newValue = tempVal8.getSellUseTimes() + multi;
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §aSet server limit value to " + newValue + "!");
            }
            tempVal8.setSellUseTimes(tempVal8.getSellUseTimes() + multi);
            tempVal8.setLastSellTime(CommonUtil.getNowTime());
            tempVal8.setCooldownSellTime();
        }
        if (!item.getShopObject().getShopConfig().getBoolean("settings.hide-message", false) && !giveResult.empty && !takeResult.empty) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-sell",
                    "item",
                    item.getDisplayName(player),
                    "price",
                    ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            giveResult.getResultMap(),
                            item.getSellPrice().getMode(),
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
                    "item-name", TextUtil.parse(item.getDisplayName(player)),
                    "amount", String.valueOf(calculateAmount),
                    "price", ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            giveResult.getResultMap(),
                            item.getSellPrice().getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "buy-or-sell", "SELL",
                    "time", CommonUtil.timeToString(CommonUtil.getNowTime(), ConfigManager.configManager.getString("log-transaction.time-format")));
            String filePath = ConfigManager.configManager.getString("log-transaction.file");
            if (filePath.isEmpty()) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLog: " + log);
            }  else {
                SchedulerUtil.runTaskAsynchronously(() -> CommonUtil.logFile(filePath, log));
            }
        }
        ItemFinishTransactionEvent event = new ItemFinishTransactionEvent(true, player, multi, item);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult, giveResult, multi);
    }
}
