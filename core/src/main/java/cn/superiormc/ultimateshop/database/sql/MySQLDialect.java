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
    public void needExtraDownload(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mariadb:")) {
            loadDriver("mariadb-java-client",
                    "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.1.4/mariadb-java-client-3.1.4.jar",
                    "org.mariadb.jdbc.Driver");
        }
    }
}