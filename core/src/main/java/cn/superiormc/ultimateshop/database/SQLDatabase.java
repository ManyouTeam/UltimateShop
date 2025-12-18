package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLDatabase extends AbstractDatabase {

    private HikariDataSource dataSource;

    @Override
    public void onInit() {
        onClose();

        UltimateShop.methodUtil.sendMessage(
                null,
                TextUtil.pluginPrefix() + " §fConnecting to SQL database..."
        );

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(ConfigManager.configManager.getString("database.jdbc-class"));
        config.setJdbcUrl(ConfigManager.configManager.getString("database.jdbc-url"));

        String user = ConfigManager.configManager.getString("database.properties.user");
        if (user != null) {
            config.setUsername(user);
            config.setPassword(ConfigManager.configManager.getString("database.properties.password"));
        }

        // Hikari 推荐设置（安全）
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("UltimateShop-Hikari");

        dataSource = new HikariDataSource(config);
        createTable();
    }

    @Override
    public void onClose() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

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
                    CREATE TABLE IF NOT EXISTS ultimateshop_randomPlaceholders (
                        playerUUID VARCHAR(36) NOT NULL,
                        placeholderID VARCHAR(48) NOT NULL,
                        nowValue TEXT,
                        refreshDoneTime DATETIME,
                        PRIMARY KEY (playerUUID, placeholderID)
                    )
                """);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkData(ServerCache cache) {
        CompletableFuture
                .runAsync(() -> loadData(cache), DatabaseExecutor.EXECUTOR);
    }

    private void loadData(ServerCache cache) {
        String playerUUID = cache.server
                ? "Global-Server"
                : cache.player.getUniqueId().toString();

        try (Connection conn = dataSource.getConnection()) {

            loadUseTimes(conn, cache, playerUUID);

            if (!UltimateShop.freeVersion) {
                loadPlaceholders(conn, cache, playerUUID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUseTimes(Connection conn, ServerCache cache, String playerUUID) throws SQLException {
        String sql = "SELECT * FROM ultimateshop_useTimes WHERE playerUUID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    {
                        try {
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
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private void loadPlaceholders(Connection conn, ServerCache cache, String playerUUID) throws SQLException {
        String sql = """
            SELECT placeholderID, nowValue, refreshDoneTime
            FROM ultimateshop_randomPlaceholders
            WHERE playerUUID = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String placeholderID = rs.getString("placeholderID");
                    String nowValue = rs.getString("nowValue");
                    String refreshDoneTime = rs.getString("refreshDoneTime");

                    if (nowValue == null || refreshDoneTime == null) continue;

                    cache.setRandomPlaceholderCache(
                            placeholderID,
                            refreshDoneTime,
                            CommonUtil.translateString(nowValue)
                    );
                }
            }
        }
    }

    @Override
    public void updateData(ServerCache cache, boolean quitServer) {
        if (quitServer) {
            updateDataOnDisable(cache, false);
            return;
        }

        CompletableFuture.runAsync(
                () -> saveUseTimes(cache),
                DatabaseExecutor.EXECUTOR
        );

        if (!UltimateShop.freeVersion) {
            CompletableFuture.runAsync(
                    () -> savePlaceholders(cache),
                    DatabaseExecutor.EXECUTOR
            );
        }
    }

    private void saveUseTimes(ServerCache cache) {
        String playerUUID = cache.server
                ? "Global-Server"
                : cache.player.getUniqueId().toString();

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

            for (Map.Entry<ObjectItem, ObjectUseTimesCache> e : cache.getUseTimesCache().entrySet()) {
                ObjectUseTimesCache c = e.getValue();
                if (c == null || c.isEmpty()) continue;

                ps.setString(1, playerUUID);
                ps.setString(2, e.getKey().getShop());
                ps.setString(3, e.getKey().getProduct());
                ps.setInt(4, c.getBuyUseTimes());
                ps.setInt(5, c.getTotalBuyUseTimes());
                ps.setInt(6, c.getSellUseTimes());
                ps.setInt(7, c.getTotalSellUseTimes());
                ps.setString(8, c.getLastBuyTime());
                ps.setString(9, c.getLastSellTime());
                ps.setString(10, c.getLastResetBuyTime());
                ps.setString(11, c.getLastResetSellTime());
                ps.setString(12, c.getCooldownBuyTime());
                ps.setString(13, c.getCooldownSellTime());

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePlaceholders(ServerCache cache) {
        String playerUUID = cache.server
                ? "Global-Server"
                : cache.player.getUniqueId().toString();

        String sql = """
            INSERT INTO ultimateshop_randomPlaceholders
            (playerUUID, placeholderID, nowValue, refreshDoneTime)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                nowValue = VALUES(nowValue),
                refreshDoneTime = VALUES(refreshDoneTime)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (ObjectRandomPlaceholderCache ph : cache.getRandomPlaceholderCache().values()) {
                if ("ONCE".equals(ph.getPlaceholder().getMode())) continue;

                ps.setString(1, playerUUID);
                ps.setString(2, ph.getPlaceholder().getID());
                ps.setString(3, CommonUtil.translateStringList(ph.getNowValue()));
                ps.setString(4, CommonUtil.timeToString(ph.getRefreshDoneTime()));
                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDataOnDisable(ServerCache cache, boolean disable) {
        saveUseTimes(cache);

        if (cache.server && !UltimateShop.freeVersion) {
            savePlaceholders(cache);
        }

        CacheManager.cacheManager.removePlayerCache(cache.player);
    }
}