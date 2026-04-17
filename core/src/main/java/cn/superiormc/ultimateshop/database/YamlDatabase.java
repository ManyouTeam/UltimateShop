package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.caches.UseTimesStorageKey;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class YamlDatabase extends AbstractDatabase {

    private final File dataDir = new File(UltimateShop.instance.getDataFolder(), "datas");

    @Override
    public void checkData(ObjectCache cache) {
        CompletableFuture.runAsync(() -> loadData(cache), DatabaseExecutor.EXECUTOR);
    }

    private void loadData(ObjectCache cache) {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = cache.isServer()
                ? new File(dataDir, "global.yml")
                : new File(dataDir, cache.getPlayer().getUniqueId() + ".yml");

        try {
            if (!file.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                config.set("playerName", cache.isServer() ? "global" : cache.getPlayer().getName());
                config.save(file);
            }
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage(
                    "§cError: Can not create new data file: " + file.getName() + "!"
            );
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection useTimeSection = config.getConfigurationSection("useTimes");
        if (useTimeSection != null) {
            useTimeSection.getKeys(false).forEach(shopID -> {
                ConfigurationSection shopSection = useTimeSection.getConfigurationSection(shopID);
                if (shopSection == null) return;

                shopSection.getKeys(false).forEach(productID -> {
                    ConfigurationSection productSection = shopSection.getConfigurationSection(productID);
                    if (productSection == null) return;

                    cache.setUseTimesCache(
                            shopID,
                            productID,
                            productSection.getInt("buyUseTimes", 0),
                            productSection.getInt("totalBuyUseTimes", 0),
                            productSection.getInt("sellUseTimes", 0),
                            productSection.getInt("totalSellUseTimes", 0),
                            productSection.getString("lastBuyTime", null),
                            productSection.getString("lastSellTime", null),
                            productSection.getString("lastResetBuyTime", null),
                            productSection.getString("lastResetSellTime", null),
                            productSection.getString("cooldownBuyTime", null),
                            productSection.getString("cooldownSellTime", null)
                    );
                });
            });
        }

        ConfigurationSection favouriteSection = config.getConfigurationSection("favourites");
        if (favouriteSection != null) {
            favouriteSection.getKeys(false).forEach(menuName -> {
                List<String> rawEntries = favouriteSection.getStringList(menuName);
                List<FavouriteProductReference> references = new ArrayList<>();
                for (String rawEntry : rawEntries) {
                    FavouriteProductReference reference = FavouriteProductReference.deserialize(rawEntry);
                    if (reference != null) {
                        references.add(reference);
                    }
                }
                cache.setFavouriteProductCache(menuName, references);
            });
        }

        if (!UltimateShop.freeVersion) {
            ConfigurationSection randomSection = config.getConfigurationSection("randomPlaceholder");
            if (randomSection != null) {
                randomSection.getKeys(false).forEach(phID -> {
                    ConfigurationSection phSection = randomSection.getConfigurationSection(phID);
                    if (phSection == null) return;

                    String nowValue = phSection.getString("nowValue", null);
                    String refreshDoneTime = phSection.getString("refreshDoneTime", null);

                    if (nowValue != null && refreshDoneTime != null) {
                        cache.setRandomPlaceholderCache(phID, refreshDoneTime, CommonUtil.translateString(nowValue));
                    }
                });
            }
        }
    }

    @Override
    public void updateData(ObjectCache cache, boolean quitServer) {
        CompletableFuture.runAsync(() -> {
            saveData(cache);
            if (quitServer) {
                CacheManager.cacheManager.removeObjectCache(cache.getPlayer());
            }
        }, DatabaseExecutor.EXECUTOR);
    }

    private void saveData(ObjectCache cache) {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = cache.isServer()
                ? new File(dataDir, "global.yml")
                : new File(dataDir, cache.getPlayer().getUniqueId() + ".yml");

        YamlConfiguration config = new YamlConfiguration();

        ConfigurationSection useTimesSection = config.createSection("useTimes");
        cache.getSharedUseTimesCache().forEach((key, state) -> writeUseTimesCache(useTimesSection, key, state));

        ConfigurationSection favouriteSection = config.createSection("favourites");
        cache.getFavouriteProductCache().forEach((menuName, references) -> {
            List<String> rawReferences = new ArrayList<>();
            for (FavouriteProductReference reference : references) {
                rawReferences.add(reference.serialize());
            }
            if (!rawReferences.isEmpty()) {
                favouriteSection.set(menuName, rawReferences);
            }
        });

        if (!UltimateShop.freeVersion) {
            ConfigurationSection randomSection = config.createSection("randomPlaceholder");
            Collection<ObjectRandomPlaceholderCache> placeholders = cache.getRandomPlaceholderCache().values();

            for (ObjectRandomPlaceholderCache ph : placeholders) {
                if ("ONCE".equals(ph.getPlaceholder().getMode())) continue;

                ConfigurationSection phSection = randomSection.createSection(ph.getPlaceholder().getID());
                phSection.set("nowValue", CommonUtil.translateStringList(ph.getNowValue()));
                phSection.set("refreshDoneTime", CommonUtil.timeToString(ph.getRefreshDoneTime()));
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not save data file: " + file.getName() + "!");
        }
    }

    private void writeUseTimesCache(ConfigurationSection root,
                                    UseTimesStorageKey key,
                                    ObjectUseTimesCache cache) {
        if (cache == null || cache.isEmpty()) {
            return;
        }
        ConfigurationSection productSection = getUseTimesSection(root, key);
        writeCommonUseTimes(
                productSection,
                cache.getBuyUseTimes(),
                cache.getTotalBuyUseTimes(),
                cache.getSellUseTimes(),
                cache.getTotalSellUseTimes(),
                toTime(cache.getLastBuyTime()),
                toTime(cache.getLastSellTime()),
                toTime(cache.getLastResetBuyTime()),
                toTime(cache.getLastResetSellTime()),
                toTime(cache.getCooldownBuyTime()),
                toTime(cache.getCooldownSellTime())
        );
    }

    private ConfigurationSection getUseTimesSection(ConfigurationSection root, UseTimesStorageKey key) {
        ConfigurationSection shopSection = root.getConfigurationSection(key.getShop());
        if (shopSection == null) {
            shopSection = root.createSection(key.getShop());
        }

        ConfigurationSection productSection = shopSection.getConfigurationSection(key.getProduct());
        if (productSection == null) {
            productSection = shopSection.createSection(key.getProduct());
        }
        return productSection;
    }

    private void writeCommonUseTimes(ConfigurationSection productSection,
                                     int buyUseTimes,
                                     int totalBuyUseTimes,
                                     int sellUseTimes,
                                     int totalSellUseTimes,
                                     LocalDateTime lastBuyTime,
                                     LocalDateTime lastSellTime,
                                     LocalDateTime lastResetBuyTime,
                                     LocalDateTime lastResetSellTime,
                                     LocalDateTime cooldownBuyTime,
                                     LocalDateTime cooldownSellTime) {
        if (buyUseTimes != 0) productSection.set("buyUseTimes", buyUseTimes);
        if (totalBuyUseTimes != 0) productSection.set("totalBuyUseTimes", totalBuyUseTimes);
        if (sellUseTimes != 0) productSection.set("sellUseTimes", sellUseTimes);
        if (totalSellUseTimes != 0) productSection.set("totalSellUseTimes", totalSellUseTimes);
        if (lastBuyTime != null) productSection.set("lastBuyTime", CommonUtil.timeToString(lastBuyTime));
        if (lastSellTime != null) productSection.set("lastSellTime", CommonUtil.timeToString(lastSellTime));
        if (lastResetBuyTime != null) productSection.set("lastResetBuyTime", CommonUtil.timeToString(lastResetBuyTime));
        if (lastResetSellTime != null) productSection.set("lastResetSellTime", CommonUtil.timeToString(lastResetSellTime));
        if (cooldownBuyTime != null) productSection.set("cooldownBuyTime", CommonUtil.timeToString(cooldownBuyTime));
        if (cooldownSellTime != null) productSection.set("cooldownSellTime", CommonUtil.timeToString(cooldownSellTime));
    }

    private LocalDateTime toTime(String value) {
        return value == null ? null : CommonUtil.stringToTime(value);
    }

    @Override
    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        saveData(cache);
        CacheManager.cacheManager.removeObjectCache(cache.getPlayer());
    }
}
