package cn.superiormc.ultimateshop.api;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.*;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShopHelper {

    @Nullable
    public static ObjectItem getItemFromID(String shop, String product) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(shop);
        if (tempVal1 == null) {
            return null;
        }
        return tempVal1.getProduct(product);
    }

    public static ObjectUseTimesCache getPlayerUseTimesCache(ObjectItem item, Player player) {
        ObjectUseTimesCache useTimesCache = CacheManager.cacheManager.getObjectCache(player).getUseTimesCache().get(item);
        if (useTimesCache == null) {
            useTimesCache = CacheManager.cacheManager.getObjectCache(player).createUseTimesCache(item);
        }
        return useTimesCache;
    }

    public static ObjectUseTimesCache getServerUseTimesCache(ObjectItem item) {
        ObjectUseTimesCache useTimesCache = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
        if (useTimesCache == null) {
            useTimesCache = CacheManager.cacheManager.serverCache.createUseTimesCache(item);
        }
        return useTimesCache;
    }

    public static int getBuyUseTimes(ObjectItem item, Player player) {
        return getPlayerUseTimesCache(item, player).getBuyUseTimes();
    }

    public static int getSellUseTimes(ObjectItem item, Player player) {
        return getPlayerUseTimesCache(item, player).getSellUseTimes();
    }

    @Nullable
    public static ObjectItem getTargetItem(ItemStorage storage, Player player) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    return item;
                }
            }
        }
        return null;
    }

    @Nullable
    public static ObjectItem getTargetItem(ItemStack[] items, Player player) {
        return getTargetItem(ItemStorage.of(items), player);
    }

    @Nullable
    public static TakeResult getBuyPrices(ItemStorage storage, Player player, int amount) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    return item.getBuyPrice().take(storage, player, getBuyUseTimes(item, player), amount, false);
                }
            }
        }
        return null;
    }

    @Nullable
    public static TakeResult getBuyPrices(ItemStack[] items, Player player, int amount) {
        return getBuyPrices(ItemStorage.of(items), player, amount);
    }

    @Nullable
    public static String getBuyPricesDisplay(ItemStorage storage, Player player, int amount) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    TakeResult anotherTakeResult = item.getBuyPrice().take(storage, player, getBuyUseTimes(item, player), amount, false);
                    if (anotherTakeResult != null) {
                        return ObjectPrices.getDisplayNameInLine(player, amount, anotherTakeResult.getResultMap(), item.getBuyPrice().getMode(), true);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getBuyPricesDisplay(ItemStack[] items, Player player, int amount) {
        return getBuyPricesDisplay(ItemStorage.of(items), player, amount);
    }

    @Nullable
    public static GiveResult getSellPrices(ItemStorage storage, Player player, int amount) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    return item.getSellPrice().give(player, getBuyUseTimes(item, player), amount);
                }
            }
        }
        return null;
    }

    @Nullable
    public static GiveResult getSellPrices(ItemStack[] items, Player player, int amount) {
        return getSellPrices(ItemStorage.of(items), player, amount);
    }

    @Nullable
    public static String getSellPricesDisplay(ItemStorage storage, Player player, int amount) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    GiveResult anotherGiveResult = item.getSellPrice().give(player, getBuyUseTimes(item, player), amount);
                    return ObjectPrices.getDisplayNameInLine(player, amount, anotherGiveResult.getResultMap(), item.getReward().getMode(), true);
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getSellPricesDisplay(ItemStack[] items, Player player, int amount) {
        return getSellPricesDisplay(ItemStorage.of(items), player, amount);
    }

    public static void takeThing(int times, int multi, Inventory inventory, Player player, Map<AbstractSingleThing, BigDecimal> result) {
        takeThing(times, multi, ItemStorage.of(inventory), player, result);
    }

    public static void takeThing(int times, int multi, ItemStorage storage, Player player, Map<AbstractSingleThing, BigDecimal> result) {
        for (AbstractSingleThing singleThing : result.keySet()) {
            double cost = result.get(singleThing).doubleValue();
            singleThing.playerHasEnough(storage, player, true, cost);
            singleThing.takeAction.runAllActions(new ObjectThingRun(player, times, multi, cost));
        }
    }

    public static boolean giveThing(int times, int multi, Player player, double multiplier, Map<AbstractSingleThing, BigDecimal> result) {
        boolean resultBoolean = true;
        Collection<GiveItemStack> giveItemStacks = new ArrayList<>();
        for (AbstractSingleThing singleThing: result.keySet()) {
            GiveItemStack giveItemStack = singleThing.playerCanGive(player, result.get(singleThing).doubleValue());
            giveItemStacks.add(giveItemStack);
            if (!giveItemStack.isCanGive()) {
                resultBoolean = false;
            }
        }
        if (!resultBoolean) {
            return false;
        }
        for (GiveItemStack giveItemStack : giveItemStacks) {
            giveItemStack.giveToPlayer(times, multi, multiplier, player);
        }
        return true;
    }

    public static Map<AbstractSingleThing, BigDecimal> sellAll(Player player, Inventory inventory, double multiplier) {
        return sellAll(player, ItemStorage.of(inventory), multiplier);
    }

    public static Map<AbstractSingleThing, BigDecimal> sellAll(Player player, ItemStorage storage, double multiplier) {

        if (storage.isEmpty()) {
            return new HashMap<>();
        }

        Map<AbstractSingleThing, BigDecimal> result = new HashMap<>();
        boolean firstSell = false;

        for (String shop : ConfigManager.configManager.shopConfigs.keySet()) {
            for (ObjectItem products :
                    ConfigManager.configManager
                            .getShop(shop)
                            .getProductListNotHidden(player)) {

                ProductTradeStatus status =
                        SellProductMethod.startSell(
                                storage,
                                products,
                                player,
                                false,
                                false,
                                true,
                                firstSell,
                                1,
                                multiplier
                        );

                if (status.getStatus() == ProductTradeStatus.Status.DONE
                        && status.getGiveResult() != null) {
                    result.putAll(status.getGiveResult().getResultMap());
                }

                if (!products.getSellAction().isEmpty()) {
                    firstSell = true;
                }
            }
        }
        return result;
    }
}
