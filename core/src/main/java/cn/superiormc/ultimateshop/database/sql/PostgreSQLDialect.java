package cn.superiormc.ultimateshop.database.sql;

public class PostgreSQLDialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:postgresql:");
    }

    @Override
    public String dateTimeType() {
        return "TIMESTAMP";
    }

    @Override
    public int maxPoolSize() {
        return 10;
    }

    @Override
    public int minIdle() {
        return 2;
    }

    @Override
    public String createUseTimesTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_useTimes (
                playerUUID VARCHAR(36) NOT NULL,
                shop VARCHAR(48) NOT NULL,
                product VARCHAR(48) NOT NULL,
                buyUseTimes INT DEFAULT 0,
                totalBuyUseTimes INT DEFAULT 0,
                sellUseTimes INT DEFAULT 0,
                totalSellUseTimes INT DEFAULT 0,
                lastBuyTime TIMESTAMP,
                lastSellTime TIMESTAMP,
                lastResetBuyTime TIMESTAMP,
                lastResetSellTime TIMESTAMP,
                cooldownBuyTime TIMESTAMP,
                cooldownSellTime TIMESTAMP,
                PRIMARY KEY (playerUUID, shop, product)
            )
        """;
    }

    @Override
    public String createRandomPlaceholderTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_randomPlaceholders (
                playerUUID VARCHAR(36) NOT NULL,
                placeholderID VARCHAR(48) NOT NULL,
                nowValue TEXT,
                refreshDoneTime TIMESTAMP,
                PRIMARY KEY (playerUUID, placeholderID)
            )
        """;
    }

    @Override
    public String createCustomPlaceholderTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_customPlaceholders (
                playerUUID VARCHAR(36) NOT NULL,
                placeholderID VARCHAR(48) NOT NULL,
                nowValue TEXT,
                PRIMARY KEY (playerUUID, placeholderID)
            )
        """;
    }

    @Override
    public String createFavouriteTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_favourites (
                playerUUID VARCHAR(36) NOT NULL,
                menuName VARCHAR(48) NOT NULL,
                sortOrder INT NOT NULL,
                shop VARCHAR(48) NOT NULL,
                product VARCHAR(48) NOT NULL,
                PRIMARY KEY (playerUUID, menuName, sortOrder)
            )
        """;
    }

    @Override
    public String upsertUseTimes() {
        return """
            INSERT INTO ultimateshop_useTimes (
                playerUUID, shop, product,
                buyUseTimes, totalBuyUseTimes,
                sellUseTimes, totalSellUseTimes,
                lastBuyTime, lastSellTime,
                lastResetBuyTime, lastResetSellTime,
                cooldownBuyTime, cooldownSellTime
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (playerUUID, shop, product)
            DO UPDATE SET
                buyUseTimes = EXCLUDED.buyUseTimes,
                totalBuyUseTimes = EXCLUDED.totalBuyUseTimes,
                sellUseTimes = EXCLUDED.sellUseTimes,
                totalSellUseTimes = EXCLUDED.totalSellUseTimes,
                lastBuyTime = EXCLUDED.lastBuyTime,
                lastSellTime = EXCLUDED.lastSellTime,
                lastResetBuyTime = EXCLUDED.lastResetBuyTime,
                lastResetSellTime = EXCLUDED.lastResetSellTime,
                cooldownBuyTime = EXCLUDED.cooldownBuyTime,
                cooldownSellTime = EXCLUDED.cooldownSellTime
        """;
    }

    @Override
    public String upsertRandomPlaceholder() {
        return """
            INSERT INTO ultimateshop_randomPlaceholders (
                playerUUID, placeholderID,
                nowValue, refreshDoneTime
            )
            VALUES (?, ?, ?, ?)
            ON CONFLICT (playerUUID, placeholderID)
            DO UPDATE SET
                nowValue = EXCLUDED.nowValue,
                refreshDoneTime = EXCLUDED.refreshDoneTime
        """;
    }

    @Override
    public String upsertCustomPlaceholder() {
        return """
            INSERT INTO ultimateshop_customPlaceholders (
                playerUUID, placeholderID, nowValue
            )
            VALUES (?, ?, ?)
            ON CONFLICT (playerUUID, placeholderID)
            DO UPDATE SET
                nowValue = EXCLUDED.nowValue
        """;
    }

    @Override
    public String deleteFavourites() {
        return "DELETE FROM ultimateshop_favourites WHERE playerUUID = ?";
    }

    @Override
    public String insertFavourite() {
        return """
            INSERT INTO ultimateshop_favourites (
                playerUUID, menuName, sortOrder, shop, product
            )
            VALUES (?, ?, ?, ?, ?)
        """;
    }

    @Override
    public String createTransactionLogTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_transactions (
                id BIGSERIAL PRIMARY KEY,
                created_at TIMESTAMP NOT NULL,
                player_uuid VARCHAR(36) NOT NULL,
                player_name VARCHAR(32) NOT NULL,
                shop_id VARCHAR(48) NOT NULL,
                shop_name VARCHAR(128) NOT NULL,
                item_id VARCHAR(48) NOT NULL,
                item_name VARCHAR(256) NOT NULL,
                action VARCHAR(4) NOT NULL,
                amount INT NOT NULL,
                multiplier DOUBLE PRECISION NOT NULL,
                price_text TEXT,
                message TEXT
            )
        """;
    }

    @Override
    public String insertTransactionLog() {
        return """
            INSERT INTO ultimateshop_transactions
            (created_at, player_uuid, player_name, shop_id, shop_name, item_id, item_name,
             action, amount, multiplier, price_text, message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {
        loadDriver("postgresql",
                "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar",
                "org.postgresql.Driver");
    }
}
