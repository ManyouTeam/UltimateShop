package cn.superiormc.ultimateshop.methods.Product;

import cn.superiormc.ultimateshop.api.ItemFinishTransactionEvent;
import cn.superiormc.ultimateshop.api.ItemPreTransactionEvent;
import cn.superiormc.ultimateshop.api.ShopHelper;

import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.objects.items.MaxSellResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import cn.superiormc.ultimateshop.managers.TransactionLoggerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class SellProductMethod {

    public static ProductTradeStatus startSell(ObjectItem item, Player player, boolean forceDisplayMessage) {
        return startSell(item, player, forceDisplayMessage, false, 1);
    }

    public static ProductTradeStatus startSell(ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               int multi) {
        return startSell(item, player, forceDisplayMessage, notCost, false, multi);
    }

    public static ProductTradeStatus startSell(ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               boolean ableMaxSell,
                                               int multi) {
        return startSell(ItemStorage.of(player.getInventory()), item, player, forceDisplayMessage, notCost, ableMaxSell, false, multi, 1);
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
        return startSell(ItemStorage.of(inventory), item, player, forceDisplayMessage, notCost, ableMaxSell, sellAll, multi, multiplier);
    }

    public static ProductTradeStatus startSell(ItemStack[] contents,
                                               ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               boolean ableMaxSell,
                                               boolean sellAll,
                                               int multi,
                                               double multiplier) {
        return startSell(ItemStorage.of(contents), item, player, forceDisplayMessage, notCost, ableMaxSell, sellAll, multi, multiplier);
    }

    public static ProductTradeStatus startSell(ItemStorage storage,
                                               ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               boolean ableMaxSell,
                                               boolean sellAll,
                                               int multi,
                                               double multiplier) {
        return startSell(storage, item, player, forceDisplayMessage, notCost, ableMaxSell, sellAll, false, multi, multiplier);
    }

    public static ProductTradeStatus startSell(ItemStorage storage,
                                               ObjectItem item,
                                               Player player,
                                               boolean forceDisplayMessage,
                                               boolean notCost,
                                               boolean ableMaxSell,
                                               boolean sellAll,
                                               boolean ignoreSellMultiplier,
                                               int multi,
                                               double multiplier) {
        return startSell(storage, item, player, forceDisplayMessage, notCost, ableMaxSell,
                sellAll, ignoreSellMultiplier, false, multi, multiplier);
    }

    public static ProductTradeStatus startPriceModifierSell(ItemStorage storage,
                                                            ObjectItem item,
                                                            Player player,
                                                            boolean forceDisplayMessage,
                                                            boolean ableMaxSell,
                                                            boolean sellAll,
                                                            int multi,
                                                            double multiplier) {
        return startSell(storage, item, player, forceDisplayMessage, false, ableMaxSell,
                sellAll, false, true, multi, multiplier);
    }

    private static ProductTradeStatus startSell(ItemStorage storage,
                                                ObjectItem item,
                                                Player player,
                                                boolean forceDisplayMessage,
                                                boolean notCost,
                                                boolean ableMaxSell,
                                                boolean sellAll,
                                                boolean ignoreSellMultiplier,
                                                boolean allowPriceModifier,
                                                int multi,
                                                double multiplier) {
        if (item == null) {
            return ProductTradeStatus.ERROR;
        }
        if (item.getShopObject().getProductNotHidden(player, item) == null) {
            return ProductTradeStatus.ERROR;
        }
        if (multi >= 10000) {
            multi = 9999;
        }
        if (!ConfigManager.configManager.getBoolean("force-display-fail-message")) {
            forceDisplayMessage = false;
        }
        boolean shouldSendMessage = storage.isPlayerInventory() && !notCost && (forceDisplayMessage || !item.getHideMessage());
        if (!ableMaxSell && !item.getSellCondition(player, multi)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-condition-not-meet",
                        "product",
                        item.getProduct());
            }
            return ProductTradeStatus.PERMISSION;
        }
        ObjectPrices sellPrice = allowPriceModifier ? item.getRawSellPrice() : item.getSellPrice();
        if (sellPrice.empty) {
            return ProductTradeStatus.ERROR;
        }
        ObjectCache tempVal3 = CacheManager.cacheManager.getObjectCache(player);
        ObjectCache tempVal11 = CacheManager.cacheManager.serverCache;
        if (tempVal3 == null || tempVal11 == null || tempVal3.canNotModify() || tempVal11.canNotModify()) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.player-not-found",
                    "player",
                    player.getName());
            return ProductTradeStatus.ERROR;
        }
        // limit
        int playerUseTimes = 0;
        int serverUseTimes = 0;
        ObjectUseTimesCache tempVal9 = tempVal3.getUseTimesCache(item);
        ObjectUseTimesCache tempVal8 = tempVal11.getUseTimesCache(item);
        if (tempVal9 != null) {
            // 重置
            tempVal9.refreshTimes();
            playerUseTimes = tempVal9.getSellUseTimes();
        } else {
            return ProductTradeStatus.ERROR;
        }
        if (tempVal8 != null) {
            tempVal8.refreshTimes();
            serverUseTimes = tempVal8.getSellUseTimes();
        } else {
            return ProductTradeStatus.ERROR;
        }
        // 更改multi
        ObjectProducts tempVal5 = item.getReward();
        MaxSellResult sellResult = null;
        boolean noValidMaxSellAmount = false;
        if (ableMaxSell) {
            sellResult = tempVal5.getMaxSellAmount(storage, player, playerUseTimes, serverUseTimes,
                    item, notCost ? null : sellPrice, ignoreSellMultiplier, multiplier);
            if (sellResult.getMaxAmount() > 0) {
                multi = sellResult.getMaxAmount();
            } else if (!notCost) {
                // Validate one trade to report why the maximum sale is unavailable.
                noValidMaxSellAmount = true;
                multi = 1;
            }
            if (!item.getSellCondition(player, multi)) {
                if (shouldSendMessage) {
                    LanguageManager.languageManager.sendStringText(player,
                            "sell-condition-not-meet",
                            "product",
                            item.getProduct());
                }
                return ProductTradeStatus.PERMISSION;
            }
        }
        long useTimesIncrement = (long) multi * item.getSellTimesIncrement();
        if (item.getPlayerSellLimit(player) != -1 &&
                playerUseTimes + useTimesIncrement > item.getPlayerSellLimit(player)) {
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
                        tempVal9.getSellRefreshTimeDisplayName(player),
                        "next",
                        tempVal9.getSellRefreshTimeNextName(player));

            }
            return ProductTradeStatus.PLAYER_MAX;
        }
        if (item.getServerSellLimit(player) != -1 &&
                serverUseTimes + useTimesIncrement > item.getServerSellLimit(player)) {
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
                        tempVal8.getSellRefreshTimeDisplayName(player),
                        "next",
                        tempVal8.getSellRefreshTimeNextName(player));

            }
            return ProductTradeStatus.SERVER_MAX;
        }
        GiveResult giveResult = null;
        TakeResult takeResult = null;
        if (sellResult != null && !noValidMaxSellAmount) {
            takeResult = sellResult.getTakeResult();
        } else {
            takeResult = tempVal5.take(storage, player, playerUseTimes, multi, false);
        }
        // API
        if (!notCost) {
            giveResult = sellPrice.give(player, playerUseTimes, multi);
            ItemPreTransactionEvent event = new ItemPreTransactionEvent(false, player, multi, item, giveResult, takeResult);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return ProductTradeStatus.API_CANCEL;
            }
        }
        // price
        if (!takeResult.getResultBoolean() || (noValidMaxSellAmount && takeResult.empty)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-products-not-enough",
                        "item",
                        item.getDisplayName(player),
                        "amount",
                        String.valueOf(multi * item.getDisplayItemObject().getAmountPlaceholder(player)));
            }
            return ProductTradeStatus.NOT_ENOUGH;
        }
        // single thing condition not meet
        if (!takeResult.getConditionBoolean()) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-condition-not-meet",
                        "product",
                        item.getProduct());
            }
            return ProductTradeStatus.REQUIRE_CONDITION_NOT_MEET;
        }
        if (notCost) {
            return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult);
        }
        // 尝试给物品
        BigDecimal finalMultiplier = ShopHelper.getFinalSellMultiplier(player, item, storage, playerUseTimes, multi,
                giveResult.getResultMap().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add),
                ignoreSellMultiplier, multiplier);
        giveResult.setMultiplier(finalMultiplier);
        if (!giveResult.checkSellPriceConditions(playerUseTimes, multi, player) || noValidMaxSellAmount) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player, "sell-condition-not-meet",
                        "product", item.getProduct());
            }
            return ProductTradeStatus.REQUIRE_CONDITION_NOT_MEET;
        }
        if (!giveResult.give(playerUseTimes, multi, player)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player, "inventory-full");
            }
            return ProductTradeStatus.INVENTORY_FULL;
        }
        // 扣物品
        // 扣的是奖励中的东西
        takeResult.take(playerUseTimes, multi, storage, player);
        int calculateAmount = multi * item.getDisplayItemObject().getAmountPlaceholder(player);
        // 执行动作
        item.getSellAction().runAllActions(new ObjectThingRun(player, playerUseTimes, multi, calculateAmount, sellAll));
        // Record weighted use times.
        if (ConfigManager.configManager.getBoolean("debug")) {
            long newValue = Math.min(Integer.MAX_VALUE, tempVal9.getSellUseTimes() + useTimesIncrement);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §aSet player limit value to " + newValue + "!");
        }
        tempVal9.addSellUseTimes(useTimesIncrement);
        tempVal9.setLastSellTime(CommonUtil.getNowTime());
        tempVal9.setCooldownSellTime();

        if (ConfigManager.configManager.getBoolean("debug")) {
            long newValue = Math.min(Integer.MAX_VALUE, tempVal8.getSellUseTimes() + useTimesIncrement);
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §aSet server limit value to " + newValue + "!");
        }
        tempVal8.addSellUseTimes(useTimesIncrement);
        tempVal8.setLastSellTime(CommonUtil.getNowTime());
        tempVal8.setCooldownSellTime();
        if ((!sellAll || !ConfigManager.configManager.getBoolean("sell.auto-hide-sell-all-message")) && !item.getHideMessage() && !giveResult.empty && !takeResult.empty) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-sell",
                    "item",
                    item.getDisplayName(player),
                    "multiplier", MathUtil.toDisplayString(finalMultiplier),
                    "price",
                    ObjectPrices.getDisplayNameInLine(player,
                            multi,
                            giveResult.getResultMap(),
                            sellPrice.getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")),
                    "amount",
                    String.valueOf(calculateAmount));
        }
        TransactionLoggerManager.transactionLoggerManager.log(player, item, calculateAmount, MathUtil.toDisplayString(finalMultiplier), "SELL",
                ObjectPrices.getDisplayNameInLine(player,
                        multi,
                        giveResult.getResultMap(),
                        sellPrice.getMode(),
                        !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")));
        ItemFinishTransactionEvent event = new ItemFinishTransactionEvent(false, player, multi, item);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return new ProductTradeStatus(ProductTradeStatus.Status.DONE, takeResult, giveResult, multi);
    }

}
