package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.caches.UseTimesStorageKey;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectCustomPlaceholder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PlayerDataSnapshot(
        UUID playerUUID,
        String playerName,
        boolean server,
        Map<UseTimesStorageKey, UseTimesSnapshot> useTimes,
        Map<String, List<FavouriteProductReference>> favourites,
        List<RandomPlaceholderSnapshot> randomPlaceholders,
        Map<String, String> customPlaceholders
) {

    public PlayerDataSnapshot {
        useTimes = Map.copyOf(useTimes);
        Map<String, List<FavouriteProductReference>> copiedFavourites = new LinkedHashMap<>();
        favourites.forEach((menu, references) -> copiedFavourites.put(menu, List.copyOf(references)));
        favourites = Map.copyOf(copiedFavourites);
        randomPlaceholders = List.copyOf(randomPlaceholders);
        customPlaceholders = Map.copyOf(customPlaceholders);
    }

    public static PlayerDataSnapshot from(ObjectCache cache) {
        boolean server = cache.isServer();
        UUID playerUUID = server ? null : cache.getPlayer().getUniqueId();
        String playerName = server ? "global" : cache.getPlayer().getName();

        Map<UseTimesStorageKey, UseTimesSnapshot> useTimes = new LinkedHashMap<>();
        for (Map.Entry<UseTimesStorageKey, ObjectUseTimesCache> entry
                : cache.getSharedUseTimesCache().entrySet()) {
            UseTimesSnapshot snapshot = entry.getValue().snapshot();
            if (!snapshot.isEmpty()) {
                useTimes.put(entry.getKey(), snapshot);
            }
        }

        Map<String, List<FavouriteProductReference>> favourites = new LinkedHashMap<>();
        cache.getFavouriteProductCache().forEach(
                (menu, references) -> favourites.put(menu, new ArrayList<>(references)));

        List<RandomPlaceholderSnapshot> randomPlaceholders = new ArrayList<>();
        Map<String, String> customPlaceholders = new LinkedHashMap<>();
        for (ObjectPlaceholderCache<?> state : cache.getPlaceholderStates()) {
            if (state instanceof ObjectRandomPlaceholderCache randomState) {
                RandomPlaceholderSnapshot snapshot = randomState.snapshot();
                if (snapshot != null) {
                    randomPlaceholders.add(snapshot);
                }
            } else if (state.getPlaceholder() instanceof ObjectCustomPlaceholder placeholder) {
                String value = placeholder.snapshotCustomValue(state);
                if (value != null) {
                    customPlaceholders.put(placeholder.getID(), value);
                }
            }
        }

        return new PlayerDataSnapshot(playerUUID, playerName, server, useTimes, favourites,
                randomPlaceholders, customPlaceholders);
    }

    public String storageId() {
        return server ? "Global-Server" : playerUUID.toString();
    }

    public String dataFileName() {
        return server ? "global.yml" : playerUUID + ".yml";
    }

    public record UseTimesSnapshot(
            int buyUseTimes,
            int totalBuyUseTimes,
            int sellUseTimes,
            int totalSellUseTimes,
            String lastBuyTime,
            String lastSellTime,
            String lastResetBuyTime,
            String lastResetSellTime,
            String cooldownBuyTime,
            String cooldownSellTime
    ) {
        public boolean isEmpty() {
            return buyUseTimes == 0 && totalBuyUseTimes == 0
                    && sellUseTimes == 0 && totalSellUseTimes == 0
                    && lastBuyTime == null && lastSellTime == null
                    && lastResetBuyTime == null && lastResetSellTime == null
                    && cooldownBuyTime == null && cooldownSellTime == null;
        }
    }

    public record RandomPlaceholderSnapshot(String id,
                                            String nowValue,
                                            String refreshDoneTime,
                                            String lastResetTime) {
    }
}
