package cn.superiormc.ultimateshop.api;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.SearchResult;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.*;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.MathUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        if (cache == null) {
            return null;
        }
        return cache.getUseTimesCache(item);
    }

    public static ObjectUseTimesCache getServerUseTimesCache(ObjectItem item) {
        return CacheManager.cacheManager.serverCache.getUseTimesCache(item);
    }

    public static int getBuyUseTimes(ObjectItem item, Player player) {
        ObjectUseTimesCache useTimesCache = getPlayerUseTimesCache(item, player);
        if (useTimesCache == null) {
            return 0;
        }
        return useTimesCache.getBuyUseTimes();
    }

    public static int getSellUseTimes(ObjectItem item, Player player) {
        ObjectUseTimesCache useTimesCache = getPlayerUseTimesCache(item, player);
        if (useTimesCache == null) {
            return 0;
        }
        return useTimesCache.getSellUseTimes();
    }

    @Nullable
    public static ObjectItem getTargetItem(ItemStorage storage, Player player) {
        List<ObjectItem> items = getTargetItems(storage, player);
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    @Nullable
    public static ObjectItem getTargetItem(ItemStack[] items, Player player) {
        return getTargetItem(ItemStorage.of(items), player);
    }

    public static List<ObjectItem> getTargetItems(ItemStorage storage, Player player) {
        Set<ObjectItem> result = new LinkedHashSet<>();
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductListNotHidden(player)) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    result.add(item);
                }
            }
        }
        return new ArrayList<>(result);
    }

    public static List<ObjectItem> getTargetItems(ItemStack[] items, Player player) {
        return getTargetItems(ItemStorage.of(items), player);
    }

    public static List<ObjectItem> getTargetItems(ItemStorage storage, Player player, String searchText) {
        return new SearchResult(storage, player, searchText).getItems();
    }

    public static List<ObjectItem> getTargetItems(ItemStack[] items, Player player, String searchText) {
        return getTargetItems(ItemStorage.of(items), player, searchText);
    }

    public static List<ObjectItem> getTargetItems(String searchText, Player player) {
        return new SearchResult(player, searchText).getItems();
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

    public static double getVaultBuyPrice(ItemStack[] items, Player player, int amount) {
        TakeResult takeResult = getBuyPrices(items, player, amount);
        if (takeResult == null) {
            return -1;
        }
        Map<AbstractSingleThing, BigDecimal> result = takeResult.getResultMap();
        if (result.size() != 1) {
            return -1;
        }
        AbstractSingleThing thing = result.keySet().iterator().next();
        if (thing.empty || thing.type != ThingType.HOOK_ECONOMY) {
            return -1;
        }
        if (thing.getSingleSection().getString("economy-plugin", "").equals("Vault")) {
            BigDecimal bigDecimal = result.get(thing);
            if (bigDecimal == null) {
                return -1;
            }
            return bigDecimal.doubleValue();
        }
        return -1;
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

    public static double getVaultSellPrice(ItemStack[] items, Player player, int amount) {
        GiveResult giveResult = getSellPrices(items, player, amount);
        if (giveResult == null) {
            return -1;
        }
        Map<AbstractSingleThing, BigDecimal> result = giveResult.getResultMap();
        if (result.size() != 1) {
            return -1;
        }
        AbstractSingleThing thing = result.keySet().iterator().next();
        if (thing.empty || thing.type != ThingType.HOOK_ECONOMY) {
            return -1;
        }
        if (thing.getSingleSection().getString("economy-plugin", "").equals("Vault")) {
            BigDecimal bigDecimal = result.get(thing);
            if (bigDecimal == null) {
                return -1;
            }
            return bigDecimal.doubleValue();
        }
        return -1;
    }

    @Nullable
    public static String getSellPricesDisplay(ItemStorage storage, Player player, int amount) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                TakeResult takeResult = item.getReward().take(storage, player, 1, 1, false);
                if (takeResult != null && !takeResult.empty && takeResult.getResultBoolean()) {
                    GiveResult anotherGiveResult = item.getSellPrice().give(player, getBuyUseTimes(item, player), amount);
                    return ObjectPrices.getDisplayNameInLine(player, amount, anotherGiveResult.getResultMapForSellMultiplierDisplay(player), item.getReward().getMode(), true);
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
        for (AbstractSingleThing singleThing : result.keySet()) {
            BigDecimal newValue = result.get(singleThing).multiply(BigDecimal.valueOf(multiplier));
            result.put(singleThing, newValue);
        }
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
            giveItemStack.giveToPlayer(times, multi, player);
        }
        return true;
    }

    public static double getSellMultiplier(Player player) {
        return getSellMultiplier(player, null);
    }

    public static double getSellMultiplier(Player player, @Nullable ObjectItem item) {
        if (player == null || UltimateShop.freeVersion) {
            return 1D;
        }

        ConfigurationSection multiplierSection = ConfigManager.configManager.getSection("sell.multiplier");
        if (multiplierSection == null || !multiplierSection.getBoolean("enabled")) {
            return 1D;
        }

        if (item != null) {
            if (multiplierSection.getStringList("black-shops").contains(item.getShop())) {
                return 1D;
            }
            if (multiplierSection.getBoolean("black-dynamic-price", true)
                    && item.getSellPrice().singlePrices.stream().anyMatch(price -> !price.isStatic())) {
                return 1D;
            }
        }

        ConfigurationSection valueSection = multiplierSection.getConfigurationSection("value");
        if (valueSection == null) {
            return 1D;
        }

        double defaultValue = valueSection.getDouble("default", 1D);
        String mode = multiplierSection.getString("mode", "MAX");
        double result = defaultValue;

        for (String key : valueSection.getKeys(false)) {
            if ("default".equalsIgnoreCase(key)) {
                continue;
            }
            if (!isSellMultiplierActive(player, key)) {
                continue;
            }

            double value = valueSection.getDouble(key, 1D);
            if ("STACK".equalsIgnoreCase(mode)) {
                result = MathUtil.multiply(result, value);
                continue;
            }
            result = Math.max(result, value);
        }
        return result;
    }

    public static boolean isSellMultiplierActive(Player player, String key) {
        if (player == null || key == null || key.isEmpty() || UltimateShop.freeVersion) {
            return false;
        }
        if (!ConfigManager.configManager.getBoolean("sell.multiplier.enabled")) {
            return false;
        }
        ConfigurationSection conditionSection = ConfigManager.configManager.getSection("sell.multiplier.value-conditions." + key);
        if (conditionSection == null) {
            return false;
        }
        return new ObjectCondition(conditionSection).getAllBoolean(new ObjectThingRun(player));
    }

    public static Map<AbstractSingleThing, BigDecimal> sellAll(Player player, Inventory inventory, double multiplier) {
        return sellAll(player, ItemStorage.of(inventory), multiplier);
    }

    public static Map<AbstractSingleThing, BigDecimal> sellAll(Player player, ItemStorage storage, double multiplier) {
        if (storage.isEmpty()) {
            return new HashMap<>();
        }
        Map<AbstractSingleThing, BigDecimal> result = new HashMap<>();
        boolean hasActionExecuted = false;

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
                                hasActionExecuted,
                                false,
                                1,
                                multiplier
                        );

                if (status.getStatus() == ProductTradeStatus.Status.DONE
                        && status.getGiveResult() != null) {
                    result.putAll(status.getGiveResult().getResultMap());
                }

                if (!products.getSellAction().isEmpty()) {
                    hasActionExecuted = true;
                }
            }
        }
        return result;
    }

    public static void sellMainHandStack(Player player) {
        ItemStorage handStorage = createMainHandStorage(player);
        ObjectItem item = ShopHelper.getTargetItem(handStorage, player);
        if (item == null || item.getSellPrice().empty) {
            LanguageManager.languageManager.sendStringText(player, "error.result-empty");
            return;
        }
        SellProductMethod.startSell(handStorage, item, player, false, false, true, false, 1, 1);
    }

    public static ItemStorage createMainHandStorage(Player player) {
        return new ItemStorage() {
            @Override
            public ItemStack[] getStorageContents() {
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem == null || handItem.getType().isAir()) {
                    return new ItemStack[1];
                }
                return new ItemStack[]{handItem.clone()};
            }

            @Override
            public void setStorageContents(ItemStack[] contents) {
                PlayerInventory inventory = player.getInventory();
                if (contents == null || contents.length == 0 || contents[0] == null || contents[0].getType().isAir()) {
                    inventory.setItemInMainHand(new ItemStack(Material.AIR));
                    return;
                }
                inventory.setItemInMainHand(contents[0].clone());
            }

            @Override
            public boolean isPlayerInventory() {
                return true;
            }
        };
    }

    @Nullable
    public static ObjectItem getUseTimesGroupLinkedProduct(String sharedGroup) {
        for (ObjectShop shop : ConfigManager.configManager.getShops()) {
            for (ObjectItem item : shop.getProductList()) {
                if (sharedGroup.equals(item.getSharedUseTimesKey())) {
                    return item;
                }
            }
        }
        return null;
    }

    @Nullable
    public static ObjectMenu getOpeningMenu(Player player) {
        return MenuStatusManager.menuStatusManager.getOpeningMenu(player);
    }

    public static ProductTradeStatus startBuy(Inventory inventory,
                                              ObjectItem item,
                                              Player player,
                                              boolean forceDisplayMessage,
                                              boolean notCost,
                                              int multi) {
        return BuyProductMethod.startBuy(ItemStorage.of(inventory), item, player, forceDisplayMessage, notCost, multi);
    }

    public static ProductTradeStatus startBuy(ItemStack[] contents,
                                              ObjectItem item,
                                              Player player,
                                              boolean forceDisplayMessage,
                                              boolean notCost,
                                              int multi) {
        return BuyProductMethod.startBuy(ItemStorage.of(contents), item, player, forceDisplayMessage, notCost, multi);
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
        return SellProductMethod.startSell(ItemStorage.of(inventory), item, player, forceDisplayMessage, notCost, ableMaxSell, sellAll, multi, multiplier);
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
        return SellProductMethod.startSell(ItemStorage.of(contents), item, player, forceDisplayMessage, notCost, ableMaxSell, sellAll, multi, multiplier);
    }
}
