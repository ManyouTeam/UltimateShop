package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.sql.DatabaseDialect;
import cn.superiormc.ultimateshop.database.sql.H2Dialect;
import cn.superiormc.ultimateshop.database.sql.MySQLDialect;
import cn.superiormc.ultimateshop.database.sql.PostgreSQLDialect;
import cn.superiormc.ultimateshop.database.sql.SQLiteDialect;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.UseTimesStorageKey;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class SQLDatabase extends AbstractDatabase {

    private HikariDataSource dataSource;

    private DatabaseDialect dialect;

    @Override
    public void onInit() {
        onClose();

        TextUtil.sendMessage(
                null,
                TextUtil.pluginPrefix() + " §fConnecting to SQL database..."
        );

        String jdbcUrl = ConfigManager.configManager.getString("database.jdbc-url");
        initDialect(jdbcUrl);
        dialect.needExtraDownload(jdbcUrl);

        HikariConfig config = new HikariConfig();
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
        if (dialect != null) {
            dialect.closeDrivers();
        }
        dataSource = null;
        dialect = null;
    }

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
            stmt.execute(dialect.createFavouriteTable());

            if (!UltimateShop.freeVersion) {
                stmt.execute(dialect.createRandomPlaceholderTable());
                ensureColumn(
                        conn,
                        stmt,
                        "ultimateshop_randomPlaceholders",
                        "lastResetTime",
                        dialect.addRandomPlaceholderLastResetTimeColumn()
                );
                stmt.execute(dialect.createCustomPlaceholderTable());
                stmt.execute(dialect.createTransactionLogTable());
                for (String indexSql : dialect.createTransactionLogIndexes()) {
                    stmt.execute(indexSql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkData(ObjectCache cache) {
        DatabaseExecutor.executeCacheLoad(cache, this::loadData);
    }

    private void ensureColumn(Connection conn,
                              Statement stmt,
                              String tableName,
                              String columnName,
                              String alterSql) throws SQLException {
        try (ResultSet columns = conn.getMetaData().getColumns(
                conn.getCatalog(), null, null, null)) {
            while (columns.next()) {
                if (tableName.equalsIgnoreCase(columns.getString("TABLE_NAME"))
                        && columnName.equalsIgnoreCase(columns.getString("COLUMN_NAME"))) {
                    return;
                }
            }
        }
        stmt.execute(alterSql);
    }

    private void loadData(ObjectCache cache) {
        String playerUUID = cache.isServer()
                ? "Global-Server"
                : cache.getPlayer().getUniqueId().toString();

        try (Connection conn = dataSource.getConnection()) {

            loadUseTimes(conn, cache, playerUUID);
            loadFavourites(conn, cache, playerUUID);

            if (!UltimateShop.freeVersion) {
                loadPlaceholders(conn, cache, playerUUID);
                loadCustomPlaceholders(conn, cache, playerUUID);
            }

            cache.ready();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFavourites(Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = """
                SELECT menuName, sortOrder, shop, product
                FROM ultimateshop_favourites
                WHERE playerUUID = ?
                ORDER BY menuName ASC, sortOrder ASC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                String currentMenu = null;
                List<FavouriteProductReference> references = new ArrayList<>();
                while (rs.next()) {
                    String menuName = rs.getString("menuName");
                    if (currentMenu != null && !currentMenu.equals(menuName)) {
                        cache.setFavouriteProductCache(currentMenu, references);
                        references = new ArrayList<>();
                    }
                    references.add(new FavouriteProductReference(
                            rs.getString("shop"),
                            rs.getString("product")
                    ));
                    currentMenu = menuName;
                }
                if (currentMenu != null) {
                    cache.setFavouriteProductCache(currentMenu, references);
                }
            }
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
                SELECT placeholderID, nowValue, refreshDoneTime, lastResetTime
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
                            rs.getString("lastResetTime"),
                            CommonUtil.translateString(nowValue)
                    );
                }
            }
        }
    }

    private void loadCustomPlaceholders(Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = """
                SELECT placeholderID, nowValue
                FROM ultimateshop_customPlaceholders
                WHERE playerUUID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nowValue = rs.getString("nowValue");
                    if (nowValue == null) continue;

                    cache.setCustomPlaceholderCache(
                            rs.getString("placeholderID"),
                            nowValue
                    );
                }
            }
        }
    }

    @Override
    public void updateData(PlayerDataSnapshot snapshot) {
        DatabaseExecutor.executePlayerSave(snapshot.storageId(), () -> saveData(snapshot));
    }

    private void saveData(PlayerDataSnapshot snapshot) {
        if (dataSource == null || dialect == null) {
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                saveUseTimes(connection, snapshot);
                saveFavourites(connection, snapshot);
                if (!UltimateShop.freeVersion) {
                    savePlaceholders(connection, snapshot);
                    saveCustomPlaceholders(connection, snapshot);
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void saveFavourites(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        if (snapshot.server()) {
            return;
        }
        String playerUUID = snapshot.playerUUID().toString();

        try (PreparedStatement deletePs = conn.prepareStatement(dialect.deleteFavourites());
             PreparedStatement insertPs = conn.prepareStatement(dialect.insertFavourite())) {

            deletePs.setString(1, playerUUID);
            deletePs.executeUpdate();

            for (Map.Entry<String, List<FavouriteProductReference>> entry : snapshot.favourites().entrySet()) {
                List<FavouriteProductReference> references = entry.getValue();
                for (int i = 0; i < references.size(); i++) {
                    FavouriteProductReference reference = references.get(i);
                    insertPs.setString(1, playerUUID);
                    insertPs.setString(2, entry.getKey());
                    insertPs.setInt(3, i);
                    insertPs.setString(4, reference.shop());
                    insertPs.setString(5, reference.product());
                    if (dialect.supportBatch()) {
                        insertPs.addBatch();
                    } else {
                        insertPs.executeUpdate();
                    }
                }
            }

            if (dialect.supportBatch()) {
                insertPs.executeBatch();
            }
        }
    }

    private void saveUseTimes(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        String playerUUID = snapshot.storageId();
        String sql = dialect.upsertUseTimes();

        try (PreparedStatement deletePs = conn.prepareStatement(dialect.deleteUseTimes());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            deletePs.setString(1, playerUUID);
            deletePs.executeUpdate();

            for (Map.Entry<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> entry
                    : snapshot.useTimes().entrySet()) {
                writeUseTimesCache(ps, playerUUID, entry.getKey(), entry.getValue());
            }

            if (dialect.supportBatch()) {
                ps.executeBatch();
            }
        }
    }

    private void writeUseTimesCache(PreparedStatement ps,
                                    String playerUUID,
                                    UseTimesStorageKey key,
                                    PlayerDataSnapshot.UseTimesSnapshot state) throws SQLException {
        if (state == null || state.isEmpty()) {
            return;
        }
        fillUseTimes(
                ps,
                playerUUID,
                key,
                state.buyUseTimes(),
                state.totalBuyUseTimes(),
                state.sellUseTimes(),
                state.totalSellUseTimes(),
                state.lastBuyTime(),
                state.lastSellTime(),
                state.lastResetBuyTime(),
                state.lastResetSellTime(),
                state.cooldownBuyTime(),
                state.cooldownSellTime()
        );
    }

    private void fillUseTimes(PreparedStatement ps,
                              String playerUUID,
                              UseTimesStorageKey key,
                              int buyUseTimes,
                              int totalBuyUseTimes,
                              int sellUseTimes,
                              int totalSellUseTimes,
                              String lastBuyTime,
                              String lastSellTime,
                              String lastResetBuyTime,
                              String lastResetSellTime,
                              String cooldownBuyTime,
                              String cooldownSellTime) throws SQLException {
        ps.setString(1, playerUUID);
        ps.setString(2, key.shop());
        ps.setString(3, key.product());
        ps.setInt(4, buyUseTimes);
        ps.setInt(5, totalBuyUseTimes);
        ps.setInt(6, sellUseTimes);
        ps.setInt(7, totalSellUseTimes);
        ps.setString(8, lastBuyTime);
        ps.setString(9, lastSellTime);
        ps.setString(10, lastResetBuyTime);
        ps.setString(11, lastResetSellTime);
        ps.setString(12, cooldownBuyTime);
        ps.setString(13, cooldownSellTime);

        if (dialect.supportBatch()) {
            ps.addBatch();
        } else {
            ps.executeUpdate();
        }
    }

    private void savePlaceholders(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        String playerUUID = snapshot.storageId();
        String sql = dialect.upsertRandomPlaceholder();

        try (PreparedStatement deletePs = conn.prepareStatement(dialect.deleteRandomPlaceholders());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            deletePs.setString(1, playerUUID);
            deletePs.executeUpdate();

            for (PlayerDataSnapshot.RandomPlaceholderSnapshot placeholder : snapshot.randomPlaceholders()) {
                ps.setString(1, playerUUID);
                ps.setString(2, placeholder.id());
                ps.setString(3, placeholder.nowValue());
                ps.setString(4, placeholder.refreshDoneTime());
                ps.setString(5, placeholder.lastResetTime());

                if (dialect.supportBatch()) {
                    ps.addBatch();
                } else {
                    ps.executeUpdate();
                }
            }

            if (dialect.supportBatch()) {
                ps.executeBatch();
            }
        }
    }

    private void saveCustomPlaceholders(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        String playerUUID = snapshot.storageId();
        String sql = dialect.upsertCustomPlaceholder();

        try (PreparedStatement deletePs = conn.prepareStatement(dialect.deleteCustomPlaceholders());
             PreparedStatement ps = conn.prepareStatement(sql)) {

            deletePs.setString(1, playerUUID);
            deletePs.executeUpdate();

            for (Map.Entry<String, String> entry : snapshot.customPlaceholders().entrySet()) {

                ps.setString(1, playerUUID);
                ps.setString(2, entry.getKey());
                ps.setString(3, entry.getValue());

                if (dialect.supportBatch()) {
                    ps.addBatch();
                } else {
                    ps.executeUpdate();
                }
            }

            if (dialect.supportBatch()) {
                ps.executeBatch();
            }
        }
    }

    @Override
    public void updateDataOnDisable(PlayerDataSnapshot snapshot, boolean disable) {
        saveData(snapshot);
    }

    public void logTransaction(LocalDateTime createdAt,
                               String playerUuid,
                               String playerName,
                               String shopId,
                               String shopName,
                               String itemId,
                               String itemName,
                               String action,
                               int amount,
                               double multiplier,
                               String priceText) {
        logTransactions(List.of(new TransactionLog(createdAt, playerUuid, playerName, shopId, shopName,
                itemId, itemName, action, amount, multiplier, priceText)));
    }

    public void logTransactions(List<TransactionLog> transactions) {
        if (dataSource == null || dialect == null) {
            return;
        }
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        DatabaseExecutor.executeTransaction(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(dialect.insertTransactionLog())) {
                for (TransactionLog transaction : transactions) {
                    ps.setTimestamp(1, Timestamp.valueOf(transaction.createdAt()));
                    ps.setString(2, transaction.playerUuid());
                    ps.setString(3, transaction.playerName());
                    ps.setString(4, transaction.shopId());
                    ps.setString(5, transaction.shopName());
                    ps.setString(6, transaction.itemId());
                    ps.setString(7, transaction.itemName());
                    ps.setString(8, transaction.action());
                    ps.setInt(9, transaction.amount());
                    ps.setDouble(10, transaction.multiplier());
                    ps.setString(11, transaction.priceText());
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
        });
    }

    public record TransactionLog(LocalDateTime createdAt,
                                 String playerUuid,
                                 String playerName,
                                 String shopId,
                                 String shopName,
                                 String itemId,
                                 String itemName,
                                 String action,
                                 int amount,
                                 double multiplier,
                                 String priceText) {
    }
}
