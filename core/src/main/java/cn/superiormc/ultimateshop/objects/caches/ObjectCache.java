package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.DatabaseManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.database.PlayerDataSnapshot;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.subobjects.AbstractCustomPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectCustomPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCache {

    private final Map<UseTimesStorageKey, ObjectUseTimesCache> sharedUseTimesCache = new ConcurrentHashMap<>();

    private final Map<ObjectItem, ObjectUseTimesCache> useTimesCache = new ConcurrentHashMap<>();

    private final Map<AbstractCustomPlaceholder, ObjectPlaceholderCache<?>> placeholderStates = new ConcurrentHashMap<>();

    private final Map<String, List<FavouriteProductReference>> favouriteProductCache = new ConcurrentHashMap<>();

    private final boolean server;

    private final Player player;

    private volatile boolean initialized = false;

    private volatile boolean ready = false;

    private volatile boolean closed = false;

    public ObjectCache() {
        this.server = true;
        this.player = null;
    }

    public ObjectCache(Player player) {
        this.server = false;
        this.player = player;
    }

    public void initCache() {
        if (closed || initialized) {
            return;
        }
        initialized = true;
        DatabaseManager.databaseManager.database.checkData(this);
    }

    public void shutCache(boolean quitServer) {
        if (canNotModify()) {
            return;
        }
        PlayerDataSnapshot snapshot = PlayerDataSnapshot.from(this);
        DatabaseManager.databaseManager.database.updateData(snapshot);
        if (quitServer) {
            CacheManager.cacheManager.removeObjectCache(this);
            cancelResetTasks();
        }
    }

    public void shutCacheOnDisable(boolean disable) {
        if (canNotModify()) {
            return;
        }
        PlayerDataSnapshot snapshot = PlayerDataSnapshot.from(this);
        DatabaseManager.databaseManager.database.updateDataOnDisable(snapshot, disable);
        CacheManager.cacheManager.removeObjectCache(this);
        cancelResetTasks();
    }

    public synchronized void cancelResetTasks() {
        closed = true;
        sharedUseTimesCache.values().forEach(ObjectUseTimesCache::cancelResetTime);
        getRandomPlaceholderCache().values().forEach(ObjectRandomPlaceholderCache::cancelResetTask);
    }

    /*
    USE TIMES CACHE
     */
    public ObjectUseTimesCache getUseTimesCache(ObjectItem item) {
        if (item == null) {
            return new ObjectUseTimesCache(this);
        }

        ObjectUseTimesCache existing = useTimesCache.get(item);
        if (existing != null) {
            return existing;
        }

        synchronized (this) {
            if (closed) {
                return new ObjectUseTimesCache(this);
            }

            return useTimesCache.computeIfAbsent(item, key -> {
                UseTimesStorageKey storageKey = key.getUseTimesStorageKey();
                ObjectUseTimesCache shared = sharedUseTimesCache.get(storageKey);
                if (shared != null) {
                    shared.bindProduct(key);
                    return shared;
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
                        key
                );
                sharedUseTimesCache.put(storageKey, created);
                return created;
            });
        }
    }

    public synchronized void setUseTimesCache(String shop,
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
        if (closed) {
            return;
        }
        ObjectUseTimesCache created = new ObjectUseTimesCache(
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
                null
        );
        ObjectUseTimesCache previous = sharedUseTimesCache.put(new UseTimesStorageKey(shop, product), created);
        if (previous != null) {
            ObjectItem boundProduct = previous.getProduct();
            if (boundProduct != null) {
                created.bindProduct(boundProduct);
                useTimesCache.replaceAll((item, cache) -> cache == previous ? created : cache);
            }
            previous.cancelResetTime();
        }
    }

    public Map<ObjectItem, ObjectUseTimesCache> getUseTimesCache() {
        return Collections.unmodifiableMap(useTimesCache);
    }

    public Map<UseTimesStorageKey, ObjectUseTimesCache> getSharedUseTimesCache() {
        return Collections.unmodifiableMap(sharedUseTimesCache);
    }

    /*
    RANDOM PLACEHOLDER CACHE
     */
    public void setRandomPlaceholderCache(ObjectRandomPlaceholder placeholder,
                                          String refreshDoneTime,
                                          List<String> nowValue) {
        setRandomPlaceholderCache(placeholder, refreshDoneTime, null, nowValue);
    }

    public void setRandomPlaceholderCache(ObjectRandomPlaceholder placeholder,
                                          String refreshDoneTime,
                                          String lastResetTime,
                                          List<String> nowValue) {

        if (closed || placeholder == null || nowValue == null) {
            return;
        }
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        if (!(state instanceof ObjectRandomPlaceholderCache placeholderCache)) {
            return;
        }
        placeholderCache.loadState(
                nowValue,
                CommonUtil.stringToTime(refreshDoneTime),
                lastResetTime == null ? null : CommonUtil.stringToTime(lastResetTime)
        );
    }

    public void setRandomPlaceholderCache(String id,
                                          String refreshDoneTime,
                                          List<String> nowValue) {
        setRandomPlaceholderCache(id, refreshDoneTime, null, nowValue);
    }

    public void setRandomPlaceholderCache(String id,
                                          String refreshDoneTime,
                                          String lastResetTime,
                                          List<String> nowValue) {

        if (nowValue == null) {
            return;
        }
        ObjectRandomPlaceholder placeholder = ConfigManager.configManager.getRandomPlaceholder(id);
        setRandomPlaceholderCache(placeholder, refreshDoneTime, lastResetTime, nowValue);
    }

    public ObjectRandomPlaceholderCache getRandomPlaceholderCache(ObjectRandomPlaceholder placeholder) {
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        if (!(state instanceof ObjectRandomPlaceholderCache randomState)) {
            return null;
        }
        randomState.initialize();
        return randomState;
    }

    public Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> getRandomPlaceholderCache() {
        Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> result = new LinkedHashMap<>();
        for (ObjectPlaceholderCache<?> state : placeholderStates.values()) {
            if (state instanceof ObjectRandomPlaceholderCache randomState) {
                result.put(randomState.getPlaceholder(), randomState);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public Collection<ObjectPlaceholderCache<?>> getPlaceholderStates() {
        return Collections.unmodifiableCollection(placeholderStates.values());
    }

    private ObjectPlaceholderCache<?> getPlaceholderState(AbstractCustomPlaceholder placeholder) {
        if (placeholder == null) {
            return null;
        }
        if (server == placeholder.isPerPlayer()) {
            String scope = server ? "per-player" : "server-wide";
            ErrorManager.errorManager.sendErrorMessage("§cError: Cannot use " + scope + " placeholder "
                    + placeholder.getID() + " with the " + (server ? "server" : "player") + " cache.");
            return null;
        }
        return placeholderStates.computeIfAbsent(placeholder, key -> {
            if (key instanceof ObjectRandomPlaceholder random) {
                return new ObjectRandomPlaceholderCache(this, random);
            }
            return new ObjectPlaceholderCache<>(this, key);
        });
    }

    public String getCustomPlaceholderValue(ObjectCustomPlaceholder placeholder) {
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        return state == null ? placeholder.getDefaultValue() : placeholder.getCustomValue(state, CommonUtil.getNowTime());
    }

    public LocalDateTime getCustomPlaceholderResetTime(ObjectCustomPlaceholder placeholder) {
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        return state == null ? null : placeholder.getCustomResetTime(state, CommonUtil.getNowTime());
    }

    public void setCustomPlaceholderCache(ObjectCustomPlaceholder placeholder, String nowValue) {
        if (nowValue == null) {
            return;
        }
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        if (state != null) {
            placeholder.setCustomValue(state, nowValue, CommonUtil.getNowTime());
        }
    }

    public void setCustomPlaceholderCache(String id, String nowValue) {
        if (nowValue == null) {
            return;
        }
        ObjectCustomPlaceholder placeholder = ConfigManager.configManager.getCustomPlaceholder(id);
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        if (state != null) {
            placeholder.loadCustomValue(state, nowValue);
        }
    }

    public String addCustomPlaceholderValue(ObjectCustomPlaceholder placeholder, BigDecimal amount) {
        if (canNotModify()) {
            return null;
        }
        ObjectPlaceholderCache<?> state = getPlaceholderState(placeholder);
        return state == null ? null : placeholder.addCustomValue(state, amount, CommonUtil.getNowTime());
    }

    public Map<ObjectCustomPlaceholder, String> getCustomPlaceholderCache() {
        Map<ObjectCustomPlaceholder, String> result = new LinkedHashMap<>();
        for (ObjectPlaceholderCache<?> state : placeholderStates.values()) {
            if (state.getPlaceholder() instanceof ObjectCustomPlaceholder placeholder) {
                String value = placeholder.snapshotCustomValue(state);
                if (value != null) {
                    result.put(placeholder, value);
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /*
    FAVOURITE
     */
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

    public synchronized List<FavouriteProductReference> getFavouriteProductReferences(String menuName) {
        List<FavouriteProductReference> references = favouriteProductCache.get(menuName);
        if (references == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(references);
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

    public Map<String, List<FavouriteProductReference>> getFavouriteProductCache() {
        Map<String, List<FavouriteProductReference>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<FavouriteProductReference>> entry : favouriteProductCache.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isServer() {
        return server;
    }

    public void close() {
        cancelResetTasks();
        if (player != null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fUnloaded player data: " + player.getName() + ".");
        }
    }

    public void ready() {
        ready = true;
        getRandomPlaceholderCache().values().forEach(
                ObjectRandomPlaceholderCache::activateResetTask
        );
        if (player != null) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded player data: " + player.getName() + ".");
        }
    }

    public boolean canNotModify() {
        return closed || !ready;
    }
}
