package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCache {

    private final Map<UseTimesStorageKey, ObjectUseTimesCache> sharedUseTimesCache = new ConcurrentHashMap<>();

    private final Map<ObjectItem, ObjectUseTimesCache> useTimesCache = new ConcurrentHashMap<>();

    private final Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> randomPlaceholderCache = new ConcurrentHashMap<>();

    private final Map<String, List<FavouriteProductReference>> favouriteProductCache = new ConcurrentHashMap<>();

    private final boolean server;

    private final Player player;

    public ObjectCache() {
        this.server = true;
        this.player = null;
        initCache();
    }

    public ObjectCache(Player player) {
        this.server = false;
        this.player = player;
        initCache();
    }

    public void initCache() {
        CacheManager.cacheManager.database.checkData(this);
    }

    public void shutCache(boolean quitServer) {
        CacheManager.cacheManager.database.updateData(this, quitServer);

        if (quitServer && ConfigManager.configManager.getBoolean("use-times.auto-reset-mode")) {
            sharedUseTimesCache.values().forEach(ObjectUseTimesCache::cancelResetTime);
        }
    }

    public void shutCacheOnDisable(boolean disable) {
        CacheManager.cacheManager.database.updateDataOnDisable(this, disable);

        if (disable && ConfigManager.configManager.getBoolean("use-times.auto-reset-mode")) {
            sharedUseTimesCache.values().forEach(ObjectUseTimesCache::cancelResetTime);
        }
    }

    public ObjectUseTimesCache getUseTimesCache(ObjectItem item) {
        if (item == null) {
            ErrorManager.errorManager.sendErrorMessage("§cThe product is null.");
            return null;
        }

        return useTimesCache.computeIfAbsent(item, key -> {
            UseTimesStorageKey storageKey = key.getUseTimesStorageKey();
            ObjectUseTimesCache existing = sharedUseTimesCache.get(storageKey);
            if (existing != null) {
                existing.bindProduct(key);
                return existing;
            }

            int defaultBuyTimes = 0;
            int defaultSellTimes = 0;

            if (ConfigManager.configManager.getBoolean("use-times.set-reset-value-by-default")) {
                defaultBuyTimes = key.getBuyTimesResetValue(player);
                defaultSellTimes = key.getSellTimesResetValue(player);
            }

            ObjectUseTimesCache created = new ObjectUseTimesCache(this,
                    defaultBuyTimes,
                    0,
                    defaultSellTimes,
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    key,
                    true);
            sharedUseTimesCache.put(storageKey, created);
            return created;
        });
    }

    public ObjectUseTimesCache createUseTimesCache(ObjectItem product) {
        return getUseTimesCache(product);
    }

    public void setUseTimesCache(String shop,
                                 String product,
                                 int buyUseTimes,
                                 int totalBuyUseTimes,
                                 int sellUseTimes,
                                 int totalSellUseTimes,
                                 String lastBuyTime,
                                 String lastSellTime,
                                 String lastResetBuyTime,
                                 String lastResetSellTime,
                                 String cooldownBuyTime,
                                 String cooldownSellTime) {
        sharedUseTimesCache.put(new UseTimesStorageKey(shop, product), new ObjectUseTimesCache(
                this,
                buyUseTimes,
                totalBuyUseTimes,
                sellUseTimes,
                totalSellUseTimes,
                lastBuyTime,
                lastSellTime,
                lastResetBuyTime,
                lastResetSellTime,
                cooldownBuyTime,
                cooldownSellTime,
                null,
                false));
    }

    public void setRandomPlaceholderCache(ObjectRandomPlaceholder placeholder,
                                          String refreshDoneTime,
                                          List<String> nowValue) {

        if (placeholder == null || nowValue == null) return;

        if (!checkPlaceholderScope(placeholder)) return;

        randomPlaceholderCache.put(
                placeholder,
                new ObjectRandomPlaceholderCache(
                        this,
                        placeholder,
                        nowValue,
                        CommonUtil.stringToTime(refreshDoneTime)
                )
        );
    }

    public void setRandomPlaceholderCache(String id,
                                          String refreshDoneTime,
                                          List<String> nowValue) {

        if (nowValue == null) return;

        ObjectRandomPlaceholder placeholder =
                ConfigManager.configManager.getRandomPlaceholder(id);

        if (placeholder == null) return;

        if (!checkPlaceholderScope(placeholder)) return;

        randomPlaceholderCache.put(
                placeholder,
                new ObjectRandomPlaceholderCache(
                        this,
                        placeholder,
                        nowValue,
                        CommonUtil.stringToTime(refreshDoneTime)
                )
        );
    }

    public void addRandomPlaceholderCache(ObjectRandomPlaceholder placeholder) {
        if (placeholder == null) return;
        if (!checkPlaceholderScope(placeholder)) return;

        randomPlaceholderCache.putIfAbsent(
                placeholder,
                new ObjectRandomPlaceholderCache(this, placeholder)
        );
    }

    public synchronized void setFavouriteProductCache(String menuName,
                                                      List<FavouriteProductReference> references) {
        if (menuName == null || menuName.isEmpty()) {
            return;
        }
        if (references == null || references.isEmpty()) {
            favouriteProductCache.remove(menuName);
            return;
        }
        favouriteProductCache.put(menuName, new ArrayList<>(references));
    }

    public synchronized boolean addFavouriteProduct(String menuName, ObjectItem item) {
        if (server || player == null || menuName == null || menuName.isEmpty() || item == null || !item.isAllowFavourite()) {
            return false;
        }
        FavouriteProductReference reference = FavouriteProductReference.fromItem(item);
        if (reference == null) {
            return false;
        }
        List<FavouriteProductReference> references = favouriteProductCache.computeIfAbsent(menuName, key -> new ArrayList<>());
        if (references.contains(reference)) {
            return false;
        }
        references.add(reference);
        return true;
    }

    public synchronized boolean hasFavouriteProduct(String menuName, ObjectItem item) {
        FavouriteProductReference reference = FavouriteProductReference.fromItem(item);
        if (menuName == null || menuName.isEmpty() || reference == null) {
            return false;
        }
        List<FavouriteProductReference> references = favouriteProductCache.get(menuName);
        return references != null && references.contains(reference);
    }

    public synchronized int getResolvedFavouriteProductAmount(String menuName) {
        return getResolvedFavouriteProducts(menuName).size();
    }

    public synchronized boolean removeFavouriteProduct(String menuName, ObjectItem item) {
        FavouriteProductReference reference = FavouriteProductReference.fromItem(item);
        if (reference == null) {
            return false;
        }
        return removeFavouriteProduct(menuName, reference);
    }

    public synchronized boolean removeFavouriteProduct(String menuName, FavouriteProductReference reference) {
        if (menuName == null || menuName.isEmpty() || reference == null) {
            return false;
        }
        List<FavouriteProductReference> references = favouriteProductCache.get(menuName);
        if (references == null) {
            return false;
        }
        boolean removed = references.remove(reference);
        if (references.isEmpty()) {
            favouriteProductCache.remove(menuName);
        }
        return removed;
    }

    public synchronized boolean toggleFavouriteProduct(String menuName, ObjectItem item) {
        if (removeFavouriteProduct(menuName, item)) {
            return false;
        }
        return addFavouriteProduct(menuName, item);
    }

    public synchronized boolean moveFavouriteProduct(String menuName, int fromIndex, int toIndex) {
        List<FavouriteProductReference> references = favouriteProductCache.get(menuName);
        if (references == null || fromIndex < 0 || toIndex < 0
                || fromIndex >= references.size() || toIndex >= references.size()) {
            return false;
        }
        if (fromIndex == toIndex) {
            return true;
        }
        FavouriteProductReference reference = references.remove(fromIndex);
        references.add(toIndex, reference);
        return true;
    }

    public synchronized void clearFavouriteProductCache(String menuName) {
        if (menuName == null || menuName.isEmpty()) {
            return;
        }
        favouriteProductCache.remove(menuName);
    }

    public synchronized List<FavouriteProductReference> getFavouriteProductReferences(String menuName) {
        List<FavouriteProductReference> references = favouriteProductCache.get(menuName);
        if (references == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(references);
    }

    public synchronized List<ObjectItem> getResolvedFavouriteItems(String menuName) {
        return new ArrayList<>(getResolvedFavouriteProducts(menuName).values());
    }

    public synchronized Map<FavouriteProductReference, ObjectItem> getResolvedFavouriteProducts(String menuName) {
        Map<FavouriteProductReference, ObjectItem> result = new LinkedHashMap<>();
        List<FavouriteProductReference> references = favouriteProductCache.get(menuName);
        if (server || player == null || references == null || references.isEmpty()) {
            return result;
        }

        List<FavouriteProductReference> validReferences = new ArrayList<>();
        for (FavouriteProductReference reference : references) {
            ObjectItem item = reference.resolve(player);
            if (item == null) {
                continue;
            }
            result.put(reference, item);
            validReferences.add(reference);
        }
        if (validReferences.isEmpty()) {
            favouriteProductCache.remove(menuName);
        } else if (validReferences.size() != references.size()) {
            favouriteProductCache.put(menuName, validReferences);
        }
        return result;
    }

    private boolean checkPlaceholderScope(ObjectRandomPlaceholder placeholder) {
        if (server && placeholder.isPerPlayer()) {
            ErrorManager.errorManager.sendErrorMessage(
                    "§cThe random placeholder is per player and can not sync data with server cache.");
            return false;
        }
        if (!server && !placeholder.isPerPlayer()) {
            ErrorManager.errorManager.sendErrorMessage(
                    "§cThe random placeholder is globally and can not sync data with player cache.");
            return false;
        }
        return true;
    }

    public Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> getRandomPlaceholderCache() {
        return Collections.unmodifiableMap(randomPlaceholderCache);
    }

    public Map<UseTimesStorageKey, ObjectUseTimesCache> getSharedUseTimesCache() {
        return Collections.unmodifiableMap(sharedUseTimesCache);
    }

    public Map<String, List<FavouriteProductReference>> getFavouriteProductCache() {
        Map<String, List<FavouriteProductReference>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<FavouriteProductReference>> entry : favouriteProductCache.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    public Map<ObjectItem, ObjectUseTimesCache> getUseTimesCache() {
        return Collections.unmodifiableMap(useTimesCache);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isServer() {
        return server;
    }
}
