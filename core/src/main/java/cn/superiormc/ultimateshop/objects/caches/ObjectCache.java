package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectCache {

    private final Map<UseTimesStorageKey, ObjectUseTimesCache> sharedUseTimesCache = new ConcurrentHashMap<>();

    private final Map<ObjectItem, ObjectUseTimesCache> useTimesCache = new ConcurrentHashMap<>();

    private final Map<ObjectRandomPlaceholder, ObjectRandomPlaceholderCache> randomPlaceholderCache = new ConcurrentHashMap<>();

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
