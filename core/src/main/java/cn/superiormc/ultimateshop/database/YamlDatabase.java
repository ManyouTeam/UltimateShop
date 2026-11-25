package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.UseTimesStorageKey;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YamlDatabase extends AbstractDatabase {

    private final File dataDir = new File(UltimateShop.instance.getDataFolder(), "datas");

    @Override
    public void checkData(ObjectCache cache) {
        DatabaseExecutor.executeCacheLoad(cache, this::loadData);
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
                    String lastResetTime = phSection.getString("lastResetTime", null);

                    if (nowValue != null && refreshDoneTime != null) {
                        cache.setRandomPlaceholderCache(
                                phID,
                                refreshDoneTime,
                                lastResetTime,
                                CommonUtil.translateString(nowValue)
                        );
                    }
                });
            }
        }

        ConfigurationSection customSection = config.getConfigurationSection("customPlaceholder");
        if (customSection != null) {
            customSection.getKeys(false).forEach(phID -> {
                String nowValue = customSection.getString(phID, null);
                if (nowValue != null) {
                    cache.setCustomPlaceholderCache(phID, nowValue);
                }
            });
        }
        cache.ready();
    }

    @Override
    public void updateData(PlayerDataSnapshot snapshot) {
        DatabaseExecutor.executePlayerSave(snapshot.storageId(), () -> saveData(snapshot));
    }

    private void saveData(PlayerDataSnapshot snapshot) {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(dataDir, snapshot.dataFileName());

        YamlConfiguration config = new YamlConfiguration();
        config.set("playerName", snapshot.playerName());

        ConfigurationSection useTimesSection = config.createSection("useTimes");
        snapshot.useTimes().forEach((key, state) -> writeUseTimesCache(useTimesSection, key, state));

        ConfigurationSection favouriteSection = config.createSection("favourites");
        snapshot.favourites().forEach((menuName, references) -> {
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
            for (PlayerDataSnapshot.RandomPlaceholderSnapshot placeholder : snapshot.randomPlaceholders()) {
                ConfigurationSection phSection = randomSection.createSection(placeholder.id());
                phSection.set("nowValue", placeholder.nowValue());
                phSection.set("refreshDoneTime", placeholder.refreshDoneTime());
                phSection.set("lastResetTime", placeholder.lastResetTime());
            }
        }

        ConfigurationSection customSection = config.createSection("customPlaceholder");
        snapshot.customPlaceholders().forEach(customSection::set);

        try {
            config.save(file);
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not save data file: " + file.getName() + "!");
        }
    }

    private void writeUseTimesCache(ConfigurationSection root,
                                    UseTimesStorageKey key,
                                    PlayerDataSnapshot.UseTimesSnapshot state) {
        if (state == null || state.isEmpty()) {
            return;
        }
        ConfigurationSection productSection = getUseTimesSection(root, key);
        writeCommonUseTimes(
                productSection,
                state.buyUseTimes(),
                state.totalBuyUseTimes(),
                state.sellUseTimes(),
                state.totalSellUseTimes(),
                toTime(state.lastBuyTime()),
                toTime(state.lastSellTime()),
                toTime(state.lastResetBuyTime()),
                toTime(state.lastResetSellTime()),
                toTime(state.cooldownBuyTime()),
                toTime(state.cooldownSellTime())
        );
    }

    private ConfigurationSection getUseTimesSection(ConfigurationSection root, UseTimesStorageKey key) {
        ConfigurationSection shopSection = root.getConfigurationSection(key.shop());
        if (shopSection == null) {
            shopSection = root.createSection(key.shop());
        }

        ConfigurationSection productSection = shopSection.getConfigurationSection(key.product());
        if (productSection == null) {
            productSection = shopSection.createSection(key.product());
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
    public void updateDataOnDisable(PlayerDataSnapshot snapshot, boolean disable) {
        saveData(snapshot);
    }
}
