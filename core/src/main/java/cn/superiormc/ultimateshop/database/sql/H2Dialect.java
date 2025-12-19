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
    public void needExtraDownload(String jdbcUrl) {
        loadDriver("h2",
                "https://repo1.maven.org/maven2/com/h2database/h2/2.2.220/h2-2.2.220.jar",
                "org.h2.Driver");
    }
}
