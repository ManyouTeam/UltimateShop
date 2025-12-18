package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class YamlDatabase extends AbstractDatabase {

    private final File dataDir = new File(UltimateShop.instance.getDataFolder(), "datas");

    @Override
    public void checkData(ServerCache cache) {
        CompletableFuture.runAsync(() -> loadData(cache), DatabaseExecutor.EXECUTOR);
    }

    private void loadData(ServerCache cache) {
        if (!dataDir.exists()) dataDir.mkdirs();

        File file = cache.server
                ? new File(dataDir, "global.yml")
                : new File(dataDir, cache.player.getUniqueId() + ".yml");

        try {
            if (!file.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                config.set("playerName", cache.server ? "global" : cache.player.getName());
                config.save(file);
            }
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage(
                    "§cError: Can not create new data file: " + file.getName() + "!"
            );
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // 读取 useTimes
        ConfigurationSection useTimeSection = config.getConfigurationSection("useTimes");
        if (useTimeSection != null) {
            useTimeSection.getKeys(false).forEach(shopID -> {
                ConfigurationSection shopSection = useTimeSection.getConfigurationSection(shopID);
                if (shopSection == null) return;

                shopSection.getKeys(false).forEach(productID -> {
                    ConfigurationSection productSection = shopSection.getConfigurationSection(productID);
                    if (productSection == null) return;

                    String lastBuy = productSection.getString("lastBuyTime", null);
                    String lastSell = productSection.getString("lastSellTime", null);
                    String lastResetBuy = productSection.getString("lastResetBuyTime", null);
                    String lastResetSell = productSection.getString("lastResetSellTime", null);
                    String cooldownBuy = productSection.getString("cooldownBuyTime", null);
                    String cooldownSell = productSection.getString("cooldownSellTime", null);

                    int buyUseTimes = productSection.getInt("buyUseTimes", 0);
                    int totalBuyUseTimes = productSection.getInt("totalBuyUseTimes", 0);
                    int sellUseTimes = productSection.getInt("sellUseTimes", 0);
                    int totalSellUseTimes = productSection.getInt("totalSellUseTimes", 0);

                    cache.setUseTimesCache(shopID, productID,
                            buyUseTimes, totalBuyUseTimes,
                            sellUseTimes, totalSellUseTimes,
                            lastBuy, lastSell, lastResetBuy, lastResetSell,
                            cooldownBuy, cooldownSell);
                });
            });
        }

        // 读取随机变量
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
    public void updateData(ServerCache cache, boolean quitServer) {
        CompletableFuture.runAsync(() -> {
            saveData(cache);
            if (quitServer) {
                CacheManager.cacheManager.removePlayerCache(cache.player);
            }
        }, DatabaseExecutor.EXECUTOR);
    }

    private void saveData(ServerCache cache) {
        if (!dataDir.exists()) dataDir.mkdirs();

        File file = cache.server
                ? new File(dataDir, "global.yml")
                : new File(dataDir, cache.player.getUniqueId() + ".yml");

        YamlConfiguration config = new YamlConfiguration();

        // 储存 useTimes
        ConfigurationSection useTimesSection = config.createSection("useTimes");
        cache.getUseTimesCache().forEach((item, c) -> {
            ConfigurationSection shopSection = useTimesSection.getConfigurationSection(item.getShop());
            if (shopSection == null) shopSection = useTimesSection.createSection(item.getShop());

            ConfigurationSection productSection = shopSection.getConfigurationSection(item.getProduct());
            if (productSection == null) productSection = shopSection.createSection(item.getProduct());

            if (c.getBuyUseTimes() != 0) productSection.set("buyUseTimes", c.getBuyUseTimes());
            if (c.getTotalBuyUseTimes() != 0) productSection.set("totalBuyUseTimes", c.getTotalBuyUseTimes());
            if (c.getSellUseTimes() != 0) productSection.set("sellUseTimes", c.getSellUseTimes());
            if (c.getTotalSellUseTimes() != 0) productSection.set("totalSellUseTimes", c.getTotalSellUseTimes());
            if (c.getLastBuyTime() != null) productSection.set("lastBuyTime", c.getLastBuyTime());
            if (c.getLastSellTime() != null) productSection.set("lastSellTime", c.getLastSellTime());
            if (c.getLastResetBuyTime() != null) productSection.set("lastResetBuyTime", c.getLastResetBuyTime());
            if (c.getLastResetSellTime() != null) productSection.set("lastResetSellTime", c.getLastResetSellTime());
            if (c.getCooldownBuyTime() != null) productSection.set("cooldownBuyTime", c.getCooldownBuyTime());
            if (c.getCooldownSellTime() != null) productSection.set("cooldownSellTime", c.getCooldownSellTime());
        });

        // 储存随机变量
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

    @Override
    public void updateDataOnDisable(ServerCache cache, boolean disable) {
        saveData(cache);
        CacheManager.cacheManager.removePlayerCache(cache.player);
    }
}