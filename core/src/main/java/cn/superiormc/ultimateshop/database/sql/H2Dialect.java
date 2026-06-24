package cn.superiormc.ultimateshop.database.sql;

public class H2Dialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:h2:");
    }

    @Override
    public String dateTimeType() {
        return "TIMESTAMP";
    }

    @Override
    public int maxPoolSize() {
        return 5;
    }

    @Override
    public int minIdle() {
        return 1;
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
            MERGE INTO ultimateshop_useTimes
            (playerUUID, shop, product,
             buyUseTimes, totalBuyUseTimes,
             sellUseTimes, totalSellUseTimes,
             lastBuyTime, lastSellTime,
             lastResetBuyTime, lastResetSellTime,
             cooldownBuyTime, cooldownSellTime)
            KEY (playerUUID, shop, product)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }

    @Override
    public String upsertRandomPlaceholder() {
        return """
            MERGE INTO ultimateshop_randomPlaceholders
            (playerUUID, placeholderID,
             nowValue, refreshDoneTime)
            KEY (playerUUID, placeholderID)
            VALUES (?, ?, ?, ?)
        """;
    }

    @Override
    public String upsertCustomPlaceholder() {
        return """
            MERGE INTO ultimateshop_customPlaceholders
            (playerUUID, placeholderID, nowValue)
            KEY (playerUUID, placeholderID)
            VALUES (?, ?, ?)
        """;
    }

    @Override
    public String deleteFavourites() {
        return "DELETE FROM ultimateshop_favourites WHERE playerUUID = ?";
    }

    @Override
    public String insertFavourite() {
        return """
            INSERT INTO ultimateshop_favourites
            (playerUUID, menuName, sortOrder, shop, product)
            VALUES (?, ?, ?, ?, ?)
        """;
    }

    @Override
    public String createTransactionLogTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_transactions (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                created_at TIMESTAMP NOT NULL,
                player_uuid VARCHAR(36) NOT NULL,
                player_name VARCHAR(32) NOT NULL,
                shop_id VARCHAR(48) NOT NULL,
                shop_name VARCHAR(128) NOT NULL,
                item_id VARCHAR(48) NOT NULL,
                item_name VARCHAR(256) NOT NULL,
                action VARCHAR(4) NOT NULL,
                amount INT NOT NULL,
                multiplier DOUBLE NOT NULL,
                price_text CLOB,
                message CLOB
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
        loadDriver("h2",
                "https://repo1.maven.org/maven2/com/h2database/h2/2.2.220/h2-2.2.220.jar",
                "org.h2.Driver");
    }
}
