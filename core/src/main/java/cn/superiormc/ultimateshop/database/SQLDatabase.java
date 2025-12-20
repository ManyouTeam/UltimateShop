package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.database.sql.DatabaseDialect;
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
import cn.superiormc.ultimateshop.database.sql.*;

import java.util.List;

public class SQLDatabase extends AbstractDatabase {

    private HikariDataSource dataSource;

    private DatabaseDialect dialect;

    @Override
    public void onInit() {
        onClose();

        UltimateShop.methodUtil.sendMessage(
                null,
                TextUtil.pluginPrefix() + " §fConnecting to SQL database..."
        );

        String jdbcUrl = ConfigManager.configManager.getString("database.jdbc-url");
        initDialect(jdbcUrl);
        dialect.needExtraDownload(jdbcUrl);

        HikariConfig config = new HikariConfig();
        //config.setDriverClassName(ConfigManager.configManager.getString("database.jdbc-class"));
        config.setJdbcUrl(jdbcUrl);

        String user = ConfigManager.configManager.getString("database.properties.user");
        if (user != null) {
            config.setUsername(user);
            config.setPassword(
                    ConfigManager.configManager.getString("database.properties.password")
            );
        }

        config.setPoolName("UltimateShop-Hikari");
        config.setMaximumPoolSize(dialect.maxPoolSize());
        config.setMinimumIdle(dialect.minIdle());

        dataSource = new HikariDataSource(config);

        createTables();
    }

    @Override
    public void onClose() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /* =========================
       Dialect
     ========================= */

    private void initDialect(String jdbcUrl) {
        List<DatabaseDialect> dialects = List.of(
                new MySQLDialect(),
                new H2Dialect(),
                new PostgreSQLDialect(),
                new SQLiteDialect()
        );

        this.dialect = dialects.stream()
                .filter(d -> d.matches(jdbcUrl))
                .findFirst()
                .orElse(new MySQLDialect());
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(dialect.createUseTimesTable());

            if (!UltimateShop.freeVersion) {
                stmt.execute(dialect.createRandomPlaceholderTable());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkData(ObjectCache cache) {
        CompletableFuture.runAsync(
                () -> loadData(cache),
                DatabaseExecutor.EXECUTOR
        );
    }

    private void loadData(ObjectCache cache) {
        String playerUUID = cache.isServer()
                ? "Global-Server"
                : cache.getPlayer().getUniqueId().toString();

        try (Connection conn = dataSource.getConnection()) {

            loadUseTimes(conn, cache, playerUUID);

            if (!UltimateShop.freeVersion) {
                loadPlaceholders(conn, cache, playerUUID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUseTimes(Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = "SELECT * FROM ultimateshop_useTimes WHERE playerUUID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

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
        }
    }

    private void loadPlaceholders(Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = """
                SELECT placeholderID, nowValue, refreshDoneTime
                FROM ultimateshop_randomPlaceholders
                WHERE playerUUID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nowValue = rs.getString("nowValue");
                    String refreshDoneTime = rs.getString("refreshDoneTime");
                    if (nowValue == null || refreshDoneTime == null) continue;

                    cache.setRandomPlaceholderCache(
                            rs.getString("placeholderID"),
                            refreshDoneTime,
                            CommonUtil.translateString(nowValue)
                    );
                }
            }
        }
    }

    @Override
    public void updateData(ObjectCache cache, boolean quitServer) {
        CompletableFuture.runAsync(() -> {
            saveUseTimes(cache);
            if (!UltimateShop.freeVersion) {
                savePlaceholders(cache);
            }
            if (quitServer) {
                CacheManager.cacheManager.removeObjectCache(cache.getPlayer());
            }
        }, DatabaseExecutor.EXECUTOR);
    }

    private void saveUseTimes(ObjectCache cache) {
        String playerUUID = cache.isServer()
                ? "Global-Server"
                : cache.getPlayer().getUniqueId().toString();

        String sql = dialect.upsertUseTimes();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<ObjectItem, ObjectUseTimesCache> e
                    : cache.getUseTimesCache().entrySet()) {

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

                if (dialect.supportBatch()) {
                    ps.addBatch();
                } else {
                    ps.executeUpdate();
                }
            }

            if (dialect.supportBatch()) {
                ps.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePlaceholders(ObjectCache cache) {
        String playerUUID = cache.isServer()
                ? "Global-Server"
                : cache.getPlayer().getUniqueId().toString();

        String sql = dialect.upsertRandomPlaceholder();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (ObjectRandomPlaceholderCache ph
                    : cache.getRandomPlaceholderCache().values()) {

                if ("ONCE".equals(ph.getPlaceholder().getMode())) continue;

                ps.setString(1, playerUUID);
                ps.setString(2, ph.getPlaceholder().getID());
                ps.setString(3,
                        CommonUtil.translateStringList(ph.getNowValue()));
                ps.setString(4,
                        CommonUtil.timeToString(ph.getRefreshDoneTime()));

                if (dialect.supportBatch()) {
                    ps.addBatch();
                } else {
                    ps.executeUpdate();
                }
            }

            if (dialect.supportBatch()) {
                ps.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        saveUseTimes(cache);

        if (!UltimateShop.freeVersion) {
            savePlaceholders(cache);
        }

        CacheManager.cacheManager.removeObjectCache(cache.getPlayer());
    }
}