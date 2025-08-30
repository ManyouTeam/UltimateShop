package cn.superiormc.ultimateshop.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLDatabase {

    public static HikariDataSource dataSource;

    public static void initSQL() {
        UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fTrying connect to SQL database...");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(ConfigManager.configManager.getString("database.jdbc-class"));
        config.setJdbcUrl(ConfigManager.configManager.getString("database.jdbc-url"));
        if (ConfigManager.configManager.getString("database.properties.user") != null) {
            config.setUsername(ConfigManager.configManager.getString("database.properties.user"));
            config.setPassword(ConfigManager.configManager.getString("database.properties.password"));
        }
        dataSource = new HikariDataSource(config);
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(5)) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cFailed connect to SQL database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTable();
    }

    public static void closeSQL() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static void createTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 主表：使用唯一索引避免重复
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ultimateshop_useTimes (
                    playerUUID VARCHAR(36) NOT NULL,
                    shop VARCHAR(48) NOT NULL,
                    product VARCHAR(48) NOT NULL,
                    buyUseTimes INT DEFAULT 0,
                    totalBuyUseTimes INT DEFAULT 0,
                    sellUseTimes INT DEFAULT 0,
                    totalSellUseTimes INT DEFAULT 0,
                    lastBuyTime DATETIME NULL,
                    lastSellTime DATETIME NULL,
                    lastResetBuyTime DATETIME NULL,
                    lastResetSellTime DATETIME NULL,
                    cooldownBuyTime DATETIME NULL,
                    cooldownSellTime DATETIME NULL,
                    PRIMARY KEY (playerUUID, shop, product)
                )
                """);

            if (!UltimateShop.freeVersion) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS ultimateshop_randomPlaceholder (
                        placeholderID VARCHAR(48) NOT NULL PRIMARY KEY,
                        nowValue TEXT,
                        refreshDoneTime DATETIME
                    )
                    """);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void checkData(ServerCache cache) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                if (cache.server && !UltimateShop.freeVersion) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT placeholderID, nowValue, refreshDoneTime FROM ultimateshop_randomPlaceholder"
                    ); ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String placeholderID = rs.getString("placeholderID");
                            List<String> nowValue = CommonUtil.translateString(rs.getString("nowValue"));
                            String refreshDoneTime = rs.getString("refreshDoneTime");
                            if (nowValue != null && refreshDoneTime != null) {
                                cache.setRandomPlaceholderCache(placeholderID, refreshDoneTime, nowValue);
                            }
                        }
                    }
                }

                PreparedStatement ps;
                if (cache.server) {
                    ps = conn.prepareStatement(
                            "SELECT * FROM ultimateshop_useTimes WHERE playerUUID = 'Global-Server'"
                    );
                } else {
                    ps = conn.prepareStatement(
                            "SELECT * FROM ultimateshop_useTimes WHERE playerUUID = ?"
                    );
                    ps.setString(1, cache.player.getUniqueId().toString());
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        cache.setUseTimesCache(
                                rs.getString("shop"),
                                rs.getString("product"),
                                rs.getInt("buyUseTimes"),
                                rs.getInt("totalBuyUseTimes"),
                                rs.getInt("sellUseTimes"),
                                rs.getInt("totalSellUseTimes"),
                                rs.getString("lastBuyTime"),
                                rs.getString("lastSellTime"),
                                rs.getString("lastResetBuyTime"),
                                rs.getString("lastResetSellTime"),
                                rs.getString("cooldownBuyTime"),
                                rs.getString("cooldownSellTime")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    public static void updateData(ServerCache cache, boolean quitServer) {
        String playerUUID;
        if (cache.server) {
            playerUUID = "Global-Server";
        } else {
            playerUUID = cache.player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem item : tempVal1.keySet()) {
            ObjectUseTimesCache tempCache = tempVal1.get(item);
            if (tempCache == null || tempCache.isEmpty()) {
                continue;
            }
            CompletableFuture.runAsync(() -> {
                String sql = """
                        INSERT INTO ultimateshop_useTimes
                        (playerUUID, shop, product, buyUseTimes, totalBuyUseTimes, sellUseTimes, totalSellUseTimes,
                         lastBuyTime, lastSellTime, lastResetBuyTime, lastResetSellTime, cooldownBuyTime, cooldownSellTime)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            buyUseTimes = VALUES(buyUseTimes),
                            totalBuyUseTimes = VALUES(totalBuyUseTimes),
                            sellUseTimes = VALUES(sellUseTimes),
                            totalSellUseTimes = VALUES(totalSellUseTimes),
                            lastBuyTime = VALUES(lastBuyTime),
                            lastSellTime = VALUES(lastSellTime),
                            lastResetBuyTime = VALUES(lastResetBuyTime),
                            lastResetSellTime = VALUES(lastResetSellTime),
                            cooldownBuyTime = VALUES(cooldownBuyTime),
                            cooldownSellTime = VALUES(cooldownSellTime)
                        """;

                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, playerUUID);
                    ps.setString(2, item.getShop());
                    ps.setString(3, item.getProduct());
                    ps.setInt(4, tempCache.getBuyUseTimes());
                    ps.setInt(5, tempCache.getTotalBuyUseTimes());
                    ps.setInt(6, tempCache.getSellUseTimes());
                    ps.setInt(7, tempCache.getTotalSellUseTimes());
                    ps.setString(8, tempCache.getLastBuyTime());
                    ps.setString(9, tempCache.getLastSellTime());
                    ps.setString(10, tempCache.getLastResetBuyTime());
                    ps.setString(11, tempCache.getLastResetSellTime());
                    ps.setString(12, tempCache.getCooldownBuyTime());
                    ps.setString(13, tempCache.getCooldownSellTime());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        if (cache.server && !UltimateShop.freeVersion) {
            for (ObjectRandomPlaceholderCache phCache : cache.getRandomPlaceholderCache().values()) {
                if ("ONCE".equals(phCache.getPlaceholder().getMode())) {
                    continue;
                }

                String sql = """
                    INSERT INTO ultimateshop_randomPlaceholder (placeholderID, nowValue, refreshDoneTime)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        nowValue = VALUES(nowValue),
                        refreshDoneTime = VALUES(refreshDoneTime)
                    """;
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, phCache.getPlaceholder().getID());
                    ps.setString(2, CommonUtil.translateStringList(phCache.getNowValue()));
                    ps.setString(3, CommonUtil.timeToString(phCache.getRefreshDoneTime()));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (quitServer) {
            CacheManager.cacheManager.removePlayerCache(cache.player);
        }
    }

    public static void updateDataOnDisable(ServerCache cache, boolean disable) {
        String playerUUID;
        if (cache.server) {
            playerUUID = "Global-Server";
        } else {
            playerUUID = cache.player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem item : tempVal1.keySet()) {
            ObjectUseTimesCache tempCache = tempVal1.get(item);
            if (tempCache == null || tempCache.isEmpty()) {
                continue;
            }
            String sql = """
            INSERT INTO ultimateshop_useTimes
            (playerUUID, shop, product, buyUseTimes, totalBuyUseTimes, sellUseTimes, totalSellUseTimes,
             lastBuyTime, lastSellTime, lastResetBuyTime, lastResetSellTime, cooldownBuyTime, cooldownSellTime)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                buyUseTimes = VALUES(buyUseTimes),
                totalBuyUseTimes = VALUES(totalBuyUseTimes),
                sellUseTimes = VALUES(sellUseTimes),
                totalSellUseTimes = VALUES(totalSellUseTimes),
                lastBuyTime = VALUES(lastBuyTime),
                lastSellTime = VALUES(lastSellTime),
                lastResetBuyTime = VALUES(lastResetBuyTime),
                lastResetSellTime = VALUES(lastResetSellTime),
                cooldownBuyTime = VALUES(cooldownBuyTime),
                cooldownSellTime = VALUES(cooldownSellTime)
            """;

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerUUID);
                ps.setString(2, item.getShop());
                ps.setString(3, item.getProduct());
                ps.setInt(4, tempCache.getBuyUseTimes());
                ps.setInt(5, tempCache.getTotalBuyUseTimes());
                ps.setInt(6, tempCache.getSellUseTimes());
                ps.setInt(7, tempCache.getTotalSellUseTimes());
                ps.setString(8, tempCache.getLastBuyTime());
                ps.setString(9, tempCache.getLastSellTime());
                ps.setString(10, tempCache.getLastResetBuyTime());
                ps.setString(11, tempCache.getLastResetSellTime());
                ps.setString(12, tempCache.getCooldownBuyTime());
                ps.setString(13, tempCache.getCooldownSellTime());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (cache.server && !UltimateShop.freeVersion) {
            for (ObjectRandomPlaceholderCache phCache : cache.getRandomPlaceholderCache().values()) {
                if ("ONCE".equals(phCache.getPlaceholder().getMode())) {
                    continue;
                }

                String sql = """
                    INSERT INTO ultimateshop_randomPlaceholder (placeholderID, nowValue, refreshDoneTime)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        nowValue = VALUES(nowValue),
                        refreshDoneTime = VALUES(refreshDoneTime)
                    """;
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, phCache.getPlaceholder().getID());
                    ps.setString(2, CommonUtil.translateStringList(phCache.getNowValue(disable)));
                    ps.setString(3, CommonUtil.timeToString(phCache.getRefreshDoneTime()));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        CacheManager.cacheManager.removePlayerCache(cache.player);
    }

}
