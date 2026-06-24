package cn.superiormc.ultimateshop.database.sql;

public class MySQLDialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:mysql:") || jdbcUrl.startsWith("jdbc:mariadb:");
    }

    @Override
    public String dateTimeType() {
        return "DATETIME";
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
                    lastBuyTime DATETIME NULL,
                    lastSellTime DATETIME NULL,
                    lastResetBuyTime DATETIME NULL,
                    lastResetSellTime DATETIME NULL,
                    cooldownBuyTime DATETIME NULL,
                    cooldownSellTime DATETIME NULL,
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
                refreshDoneTime DATETIME,
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
    }

    @Override
    public String upsertRandomPlaceholder() {
        return """
            INSERT INTO ultimateshop_randomPlaceholders
            (playerUUID, placeholderID, nowValue, refreshDoneTime)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                nowValue = VALUES(nowValue),
                refreshDoneTime = VALUES(refreshDoneTime)
        """;
    }

    @Override
    public String upsertCustomPlaceholder() {
        return """
            INSERT INTO ultimateshop_customPlaceholders
            (playerUUID, placeholderID, nowValue)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                nowValue = VALUES(nowValue)
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
                created_at DATETIME NOT NULL,
                player_uuid VARCHAR(36) NOT NULL,
                player_name VARCHAR(32) NOT NULL,
                shop_id VARCHAR(48) NOT NULL,
                shop_name VARCHAR(128) NOT NULL,
                item_id VARCHAR(48) NOT NULL,
                item_name VARCHAR(256) NOT NULL,
                action VARCHAR(4) NOT NULL,
                amount INT NOT NULL,
                multiplier DOUBLE NOT NULL,
                price_text TEXT,
                INDEX idx_us_transactions_created_at (created_at),
                INDEX idx_us_transactions_player_uuid (player_uuid)
            )
        """;
    }

    @Override
    public String insertTransactionLog() {
        return """
            INSERT INTO ultimateshop_transactions
            (created_at, player_uuid, player_name, shop_id, shop_name, item_id, item_name,
             action, amount, multiplier, price_text)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }

    @Override
    public String[] createTransactionLogIndexes() {
        return new String[0];
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            loadDriver("mariadb-java-client",
                    "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.1.4/mariadb-java-client-3.1.4.jar",
                    "org.mariadb.jdbc.Driver");
        }
    }
}
