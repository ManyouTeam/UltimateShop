package cn.superiormc.ultimateshop.api;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.GiveItemStack;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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

    public static int getBuyUseTimes(ObjectItem item, Player player) {
        ObjectUseTimesCache useTimesCache = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        if (useTimesCache == null) {
            useTimesCache = CacheManager.cacheManager.getPlayerCache(player).createUseTimesCache(item);
        }
        return useTimesCache.getBuyUseTimes();
    }

    public static int getSellUseTimes(ObjectItem item, Player player) {
        ObjectUseTimesCache useTimesCache = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        if (useTimesCache == null) {
            useTimesCache = CacheManager.cacheManager.getPlayerCache(player).createUseTimesCache(item);
        }
        return useTimesCache.getSellUseTimes();
    }

    @Nullable
    public static ObjectItem getTargetItem(ItemStack[] items, Player player) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST);
        inventory.setStorageContents(items);
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(inventory, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    return item;
                }
            }
        }
        return null;
    }

    @Nullable
    public static TakeResult getBuyPrices(ItemStack[] items, Player player, int amount) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST);
        inventory.setStorageContents(items);
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(inventory, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    return item.getBuyPrice().take(inventory, player, getBuyUseTimes(item, player), amount, false);
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getBuyPricesDisplay(ItemStack[] items, Player player, int amount) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST);
        inventory.setStorageContents(items);
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(inventory, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    TakeResult anotherTakeResult = item.getBuyPrice().take(inventory, player, getBuyUseTimes(item, player), amount, false);
                    if (anotherTakeResult != null) {
                        return ObjectPrices.getDisplayNameInLine(player, amount, anotherTakeResult.getResultMap(), item.getBuyPrice().getMode(), true);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static GiveResult getSellPrices(ItemStack[] items, Player player, int amount) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST);
        inventory.setStorageContents(items);
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(inventory, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    return item.getSellPrice().give(player, getBuyUseTimes(item, player), amount);
                }
            }
        }
        return null;
    }

    @Nullable
    public static String getSellPricesDisplay(ItemStack[] items, Player player, int amount) {
        Inventory inventory = Bukkit.createInventory(player, InventoryType.CHEST);
        inventory.setStorageContents(items);
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(inventory, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    GiveResult anotherGiveResult = item.getSellPrice().give(player, getBuyUseTimes(item, player), amount);
                    return ObjectPrices.getDisplayNameInLine(player, amount, anotherGiveResult.getResultMap(), item.getReward().getMode(), true);
                }
            }
        }
        return null;
    }

    public static void takeThing(int times, int multi, Inventory inventory, Player player, Map<AbstractSingleThing, BigDecimal> result) {
        for (AbstractSingleThing singleThing : result.keySet()) {
            double cost = result.get(singleThing).doubleValue();
            singleThing.playerHasEnough(inventory, player, true, cost);
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
}
