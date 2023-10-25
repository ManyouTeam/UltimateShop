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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.time.LocalDateTime;

public class SellProductMethod {

    public static ProductMethodStatus startSell(String shop, String product, Player player, boolean quick) {
        return startSell(shop, product, player, quick, false, 1);
    }

    public static ProductMethodStatus startSell(String shop,
                                                String product,
                                                Player player,
                                                boolean quick,
                                                boolean test,
                                                int multi) {
        return startSell(shop, product, player, quick, test, false, multi);
    }

    public static ProductMethodStatus startSell(String shop,
                                                String product,
                                                Player player,
                                                boolean quick,
                                                boolean test,
                                                boolean ableMaxSell,
                                                int multi) {
        return startSell(player.getInventory(),
                shop, product, player, quick, test, ableMaxSell, multi);
    }

    public static ProductMethodStatus startSell(Inventory inventory,
                                                String shop,
                                                String product,
                                                Player player,
                                                boolean quick,
                                                boolean test,
                                                boolean ableMaxSell,
                                                int multi) {
        boolean shouldSendMessage = inventory instanceof PlayerInventory && !test && (quick ||
                ConfigManager.configManager.config.
                        getBoolean("send-messages-after-buy", true));
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
        if (shouldSendMessage) {
            if (!tempVal2.getSellCondition(player)) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-condition-not-meet",
                        "product",
                        product);
                return ProductMethodStatus.PERMISSION;
            }
        }
        if (tempVal2.getSellPrice().empty) {
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
            // 重置
            if (tempVal9.getSellRefreshTime() != null && tempVal9.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                if (ConfigManager.configManager.getBoolean("debug")) {
                    Bukkit.getConsoleSender().sendMessage("" +
                            "§x§9§8§F§B§9§8[UltimateShop] §bReset player sell data by GUI open check!");
                }
                tempVal3.getUseTimesCache().get(tempVal2).setSellUseTimes(0);
                tempVal3.getUseTimesCache().get(tempVal2).setLastSellTime(null);
            }
            if (tempVal9.getCooldownSellRefreshTime() != null && tempVal9.getCooldownSellRefreshTime().isAfter(LocalDateTime.now())) {
                if (shouldSendMessage) {
                    LanguageManager.languageManager.sendStringText(player,
                            "sell-in-cooldown",
                            "item",
                            tempVal2.getDisplayName(player),
                            "refresh",
                            tempVal9.getSellCooldownTimeDisplayName());
                }
                return ProductMethodStatus.IN_COOLDOWN;
            }
            playerUseTimes = tempVal9.getSellUseTimes();
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
        }

        if (tempVal2.getPlayerSellLimit(player) != -1 &&
                playerUseTimes + multi - 1 >= tempVal2.getPlayerSellLimit(player)) {
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
            return ProductMethodStatus.PLAYER_MAX;
        }
        if (tempVal8 != null) {
            if (quick) {
                // 重置
                if (tempVal8.getSellRefreshTime() != null && tempVal8.getSellRefreshTime().isBefore(LocalDateTime.now())) {
                    if (ConfigManager.configManager.getBoolean("debug")) {
                        Bukkit.getConsoleSender().sendMessage("" +
                                "§x§9§8§F§B§9§8[UltimateShop] §bReset server sell data by GUI open check!");
                    }
                    ServerCache.serverCache.getUseTimesCache().get(tempVal2).setSellUseTimes(0);
                }
            }
            serverUseTimes = ServerCache.serverCache.getUseTimesCache().get(tempVal2).getSellUseTimes();
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
        }
        if (tempVal2.getServerSellLimit(player) != -1 &&
                serverUseTimes + multi - 1 >= tempVal2.getServerSellLimit(player)) {
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
            return ProductMethodStatus.SERVER_MAX;
        }
        // price
        if (!tempVal5.takeThing(inventory, player, false, playerUseTimes, multi)) {
            if (shouldSendMessage) {
                LanguageManager.languageManager.sendStringText(player,
                        "sell-products-not-enough",
                        "item",
                        tempVal2.getDisplayName(player));
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
        tempVal5.takeThing(inventory, player, true, playerUseTimes, multi);
        // 执行动作
        tempVal2.getSellAction().doAction(player, playerUseTimes, multi);
        // limit+1
        if (tempVal9 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("" +
                        "§x§9§8§F§B§9§8[UltimateShop] §aSet player limit value to " + tempVal9.getSellUseTimes() + multi + "!");
            }
            tempVal9.setSellUseTimes(tempVal9.getSellUseTimes() + multi);
            tempVal9.setLastSellTime(LocalDateTime.now());
            tempVal9.setCooldownSellTime();
            tempVal3.getUseTimesCache().put(tempVal2, tempVal9);
        }
        if (tempVal8 != null) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                Bukkit.getConsoleSender().sendMessage("" +
                        "§x§9§8§F§B§9§8[UltimateShop] §aSet server limit value to " + tempVal8.getSellUseTimes() + multi + "!");
            }
            tempVal8.setSellUseTimes(tempVal8.getSellUseTimes() + multi);
            tempVal8.setLastSellTime(LocalDateTime.now());
            tempVal8.setCooldownSellTime();
            tempVal11.getUseTimesCache().put(tempVal2, tempVal8);
        }
        if (quick ||
        ConfigManager.configManager.config.
                getBoolean("send-messages-after-buy", true)) {
            LanguageManager.languageManager.sendStringText(player,
                    "success-sell",
                    "item",
                    tempVal2.getDisplayName(player),
                    "price",
                    tempVal2.getSellPrice().getDisplayNameInChat(inventory,
                            player,
                            playerUseTimes,
                            multi),
                    "amount",
                    String.valueOf(multi));
        }
        return ProductMethodStatus.DONE;
    }
}
