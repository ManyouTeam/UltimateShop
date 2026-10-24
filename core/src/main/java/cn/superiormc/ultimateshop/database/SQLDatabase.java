package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.sql.DatabaseDialect;
import cn.superiormc.ultimateshop.database.sql.H2Dialect;
import cn.superiormc.ultimateshop.database.sql.MySQLDialect;
import cn.superiormc.ultimateshop.database.sql.PostgreSQLDialect;
import cn.superiormc.ultimateshop.database.sql.SQLiteDialect;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
            reportDatabaseError("create database tables", e);
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
            reportDatabaseError("load data for " + playerUUID, e);
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

    private Map<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> loadUseTimes(
            Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = "SELECT * FROM ultimateshop_useTimes WHERE playerUUID = ?";
        Map<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> states = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UseTimesStorageKey key = new UseTimesStorageKey(rs.getString("shop"), rs.getString("product"));
                    PlayerDataSnapshot.UseTimesSnapshot state = new PlayerDataSnapshot.UseTimesSnapshot(
                            rs.getInt("buyUseTimes"),
                            rs.getInt("totalBuyUseTimes"),
                            rs.getInt("sellUseTimes"),
                            rs.getInt("totalSellUseTimes"),
                            readTime(rs, "lastBuyTime"),
                            readTime(rs, "lastSellTime"),
                            readTime(rs, "lastResetBuyTime"),
                            readTime(rs, "lastResetSellTime"),
                            readTime(rs, "cooldownBuyTime"),
                            readTime(rs, "cooldownSellTime")
                    );
                    states.put(key, state);
                    if (cache != null) {
                        cache.setUseTimesCache(key.shop(), key.product(), state.buyUseTimes(),
                                state.totalBuyUseTimes(), state.sellUseTimes(), state.totalSellUseTimes(),
                                state.lastBuyTime(), state.lastSellTime(), state.lastResetBuyTime(),
                                state.lastResetSellTime(), state.cooldownBuyTime(), state.cooldownSellTime());
                    }
                }
            }
        }
        return states;
    }

    private Map<String, PlayerDataSnapshot.RandomPlaceholderSnapshot> loadPlaceholders(
            Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = """
                SELECT placeholderID, nowValue, refreshDoneTime, lastResetTime
                FROM ultimateshop_randomPlaceholders
                WHERE playerUUID = ?
                """;
        Map<String, PlayerDataSnapshot.RandomPlaceholderSnapshot> states = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nowValue = rs.getString("nowValue");
                    String id = rs.getString("placeholderID");
                    String refreshDoneTime = readTime(rs, "refreshDoneTime");
                    String lastResetTime = readTime(rs, "lastResetTime");
                    states.put(id, new PlayerDataSnapshot.RandomPlaceholderSnapshot(
                            id, nowValue, refreshDoneTime, lastResetTime));
                    if (cache != null && nowValue != null && refreshDoneTime != null) {
                        cache.setRandomPlaceholderCache(id, refreshDoneTime, lastResetTime,
                                CommonUtil.translateString(nowValue));
                    }
                }
            }
        }
        return states;
    }

    private Map<String, String> loadCustomPlaceholders(Connection conn, ObjectCache cache, String playerUUID)
            throws SQLException {

        String sql = """
                SELECT placeholderID, nowValue
                FROM ultimateshop_customPlaceholders
                WHERE playerUUID = ?
                """;
        Map<String, String> states = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUUID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nowValue = rs.getString("nowValue");
                    String id = rs.getString("placeholderID");
                    states.put(id, nowValue);
                    if (cache != null && nowValue != null) {
                        cache.setCustomPlaceholderCache(id, nowValue);
                    }
                }
            }
        }
        return states;
    }

    private String readTime(ResultSet rs, String column) throws SQLException {
        if ("TEXT".equals(dialect.dateTimeType())) {
            return rs.getString(column);
        }
        Timestamp timestamp = rs.getTimestamp(column);
        return timestamp == null ? null : CommonUtil.timeToString(timestamp.toLocalDateTime());
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
            reportDatabaseError("save data for " + snapshot.storageId(), exception);
        }
    }

    private void saveFavourites(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        if (snapshot.server()) {
            return;
        }
        String playerUUID = snapshot.playerUUID().toString();
        Map<String, Map<Integer, FavouriteProductReference>> stored = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT menuName, sortOrder, shop, product FROM ultimateshop_favourites WHERE playerUUID = ?")) {
            ps.setString(1, playerUUID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stored.computeIfAbsent(rs.getString("menuName"), key -> new HashMap<>())
                            .put(rs.getInt("sortOrder"), new FavouriteProductReference(
                                    rs.getString("shop"), rs.getString("product")));
                }
            }
        }
        Map<String, Map<Integer, FavouriteProductReference>> current = new HashMap<>();
        for (Map.Entry<String, List<FavouriteProductReference>> entry : snapshot.favourites().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                Map<Integer, FavouriteProductReference> references = new HashMap<>();
                for (int i = 0; i < entry.getValue().size(); i++) {
                    references.put(i, entry.getValue().get(i));
                }
                current.put(entry.getKey(), references);
            }
        }
        if (stored.equals(current)) {
            return;
        }

        try (PreparedStatement deletePs = conn.prepareStatement(
                "DELETE FROM ultimateshop_favourites WHERE playerUUID = ? AND menuName = ? AND sortOrder = ?");
             PreparedStatement updatePs = conn.prepareStatement(
                     "UPDATE ultimateshop_favourites SET shop = ?, product = ?"
                             + " WHERE playerUUID = ? AND menuName = ? AND sortOrder = ?");
             PreparedStatement insertPs = conn.prepareStatement(dialect.insertFavourite())) {
            int deletes = 0;
            int updates = 0;
            int inserts = 0;
            for (Map.Entry<String, Map<Integer, FavouriteProductReference>> entry : stored.entrySet()) {
                Map<Integer, FavouriteProductReference> references = current.getOrDefault(entry.getKey(), Map.of());
                for (int index : removedKeys(entry.getValue(), references)) {
                    deletePs.setString(1, playerUUID);
                    deletePs.setString(2, entry.getKey());
                    deletePs.setInt(3, index);
                    queueUpdate(deletePs);
                    deletes++;
                }
            }
            for (Map.Entry<String, Map<Integer, FavouriteProductReference>> entry : current.entrySet()) {
                Map<Integer, FavouriteProductReference> previous = stored.getOrDefault(entry.getKey(), Map.of());
                for (Map.Entry<Integer, FavouriteProductReference> changed
                        : changedEntries(previous, entry.getValue()).entrySet()) {
                    FavouriteProductReference reference = changed.getValue();
                    if (previous.containsKey(changed.getKey())) {
                        updatePs.setString(1, reference.shop());
                        updatePs.setString(2, reference.product());
                        updatePs.setString(3, playerUUID);
                        updatePs.setString(4, entry.getKey());
                        updatePs.setInt(5, changed.getKey());
                        queueUpdate(updatePs);
                        updates++;
                    } else {
                        insertPs.setString(1, playerUUID);
                        insertPs.setString(2, entry.getKey());
                        insertPs.setInt(3, changed.getKey());
                        insertPs.setString(4, reference.shop());
                        insertPs.setString(5, reference.product());
                        queueUpdate(insertPs);
                        inserts++;
                    }
                }
            }
            executeBatch(deletePs, deletes);
            executeBatch(updatePs, updates);
            executeBatch(insertPs, inserts);
        }
    }

    private void saveUseTimes(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        String playerUUID = snapshot.storageId();
        Map<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> stored = loadUseTimes(conn, null, playerUUID);
        Map<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> current = new HashMap<>(snapshot.useTimes());
        current.values().removeIf(PlayerDataSnapshot.UseTimesSnapshot::isEmpty);
        Map<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> changes = changedEntries(stored, current);
        Set<UseTimesStorageKey> removed = removedKeys(stored, current);
        if (changes.isEmpty() && removed.isEmpty()) {
            return;
        }
        try (PreparedStatement deletePs = conn.prepareStatement(
                "DELETE FROM ultimateshop_useTimes WHERE playerUUID = ? AND shop = ? AND product = ?");
             PreparedStatement ps = conn.prepareStatement(dialect.upsertUseTimes())) {
            for (UseTimesStorageKey key : removed) {
                deletePs.setString(1, playerUUID);
                deletePs.setString(2, key.shop());
                deletePs.setString(3, key.product());
                queueUpdate(deletePs);
            }
            for (Map.Entry<UseTimesStorageKey, PlayerDataSnapshot.UseTimesSnapshot> entry
                    : changes.entrySet()) {
                writeUseTimesCache(ps, playerUUID, entry.getKey(), entry.getValue());
            }
            executeBatch(deletePs, removed.size());
            executeBatch(ps, changes.size());
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

        queueUpdate(ps);
    }

    private void savePlaceholders(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        String playerUUID = snapshot.storageId();
        Map<String, PlayerDataSnapshot.RandomPlaceholderSnapshot> stored = loadPlaceholders(conn, null, playerUUID);
        Map<String, PlayerDataSnapshot.RandomPlaceholderSnapshot> current = new HashMap<>();
        for (PlayerDataSnapshot.RandomPlaceholderSnapshot placeholder : snapshot.randomPlaceholders()) {
            current.put(placeholder.id(), placeholder);
        }
        Map<String, PlayerDataSnapshot.RandomPlaceholderSnapshot> changes = changedEntries(stored, current);
        Set<String> removed = removedKeys(stored, current);
        if (changes.isEmpty() && removed.isEmpty()) {
            return;
        }
        try (PreparedStatement deletePs = conn.prepareStatement(
                "DELETE FROM ultimateshop_randomPlaceholders WHERE playerUUID = ? AND placeholderID = ?");
             PreparedStatement ps = conn.prepareStatement(dialect.upsertRandomPlaceholder())) {
            for (String id : removed) {
                deletePs.setString(1, playerUUID);
                deletePs.setString(2, id);
                queueUpdate(deletePs);
            }
            for (PlayerDataSnapshot.RandomPlaceholderSnapshot placeholder : changes.values()) {
                ps.setString(1, playerUUID);
                ps.setString(2, placeholder.id());
                ps.setString(3, placeholder.nowValue());
                ps.setString(4, placeholder.refreshDoneTime());
                ps.setString(5, placeholder.lastResetTime());

                queueUpdate(ps);
            }
            executeBatch(deletePs, removed.size());
            executeBatch(ps, changes.size());
        }
    }

    private void saveCustomPlaceholders(Connection conn, PlayerDataSnapshot snapshot) throws SQLException {
        String playerUUID = snapshot.storageId();
        Map<String, String> stored = loadCustomPlaceholders(conn, null, playerUUID);
        Map<String, String> current = snapshot.customPlaceholders();
        Map<String, String> changes = changedEntries(stored, current);
        Set<String> removed = removedKeys(stored, current);
        if (changes.isEmpty() && removed.isEmpty()) {
            return;
        }
        try (PreparedStatement deletePs = conn.prepareStatement(
                "DELETE FROM ultimateshop_customPlaceholders WHERE playerUUID = ? AND placeholderID = ?");
             PreparedStatement ps = conn.prepareStatement(dialect.upsertCustomPlaceholder())) {
            for (String id : removed) {
                deletePs.setString(1, playerUUID);
                deletePs.setString(2, id);
                queueUpdate(deletePs);
            }
            for (Map.Entry<String, String> entry : changes.entrySet()) {
                ps.setString(1, playerUUID);
                ps.setString(2, entry.getKey());
                ps.setString(3, entry.getValue());

                queueUpdate(ps);
            }
            executeBatch(deletePs, removed.size());
            executeBatch(ps, changes.size());
        }
    }

    private <K, V> Map<K, V> changedEntries(Map<K, V> stored, Map<K, V> current) {
        Map<K, V> changes = new HashMap<>();
        for (Map.Entry<K, V> entry : current.entrySet()) {
            if (!stored.containsKey(entry.getKey()) || !Objects.equals(stored.get(entry.getKey()), entry.getValue())) {
                changes.put(entry.getKey(), entry.getValue());
            }
        }
        return changes;
    }

    private <K> Set<K> removedKeys(Map<K, ?> stored, Map<K, ?> current) {
        Set<K> removed = new HashSet<>(stored.keySet());
        removed.removeAll(current.keySet());
        return removed;
    }

    private void queueUpdate(PreparedStatement ps) throws SQLException {
        if (dialect.supportBatch()) {
            ps.addBatch();
        } else {
            ps.executeUpdate();
        }
    }

    private void executeBatch(PreparedStatement ps, int size) throws SQLException {
        if (dialect.supportBatch() && size > 0) {
            ps.executeBatch();
        }
    }

    private void reportDatabaseError(String operation, SQLException exception) {
        ErrorManager.errorManager.sendErrorMessage("§cError: Could not " + operation + ": " + exception.getMessage());
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
                reportDatabaseError("save transaction logs", e);
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
