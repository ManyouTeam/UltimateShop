package cn.superiormc.ultimateshop.database.sql;

public class SQLiteDialect extends DatabaseDialect {

    @Override
    public boolean matches(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:sqlite:");
    }

    @Override
    public String dateTimeType() {
        return "TEXT";
    }

    @Override
    public int maxPoolSize() {
        return 1;
    }

    @Override
    public int minIdle() {
        return 1;
    }

    @Override
    public boolean forceSingleConnection() {
        return true;
    }

    @Override
    public boolean supportBatch() {
        return false;
    }

    @Override
    public String createUseTimesTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_useTimes (
                playerUUID TEXT NOT NULL,
                shop TEXT NOT NULL,
                product TEXT NOT NULL,
                buyUseTimes INTEGER DEFAULT 0,
                totalBuyUseTimes INTEGER DEFAULT 0,
                sellUseTimes INTEGER DEFAULT 0,
                totalSellUseTimes INTEGER DEFAULT 0,
                lastBuyTime TEXT,
                lastSellTime TEXT,
                lastResetBuyTime TEXT,
                lastResetSellTime TEXT,
                cooldownBuyTime TEXT,
                cooldownSellTime TEXT,
                PRIMARY KEY (playerUUID, shop, product)
            )
        """;
    }

    @Override
    public String createRandomPlaceholderTable() {
        return """
            CREATE TABLE IF NOT EXISTS ultimateshop_randomPlaceholders (
                playerUUID TEXT NOT NULL,
                placeholderID TEXT NOT NULL,
                nowValue TEXT,
                refreshDoneTime TEXT,
                PRIMARY KEY (playerUUID, placeholderID)
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
            ON CONFLICT(playerUUID, shop, product)
            DO UPDATE SET
                buyUseTimes = excluded.buyUseTimes,
                totalBuyUseTimes = excluded.totalBuyUseTimes,
                sellUseTimes = excluded.sellUseTimes,
                totalSellUseTimes = excluded.totalSellUseTimes,
                lastBuyTime = excluded.lastBuyTime,
                lastSellTime = excluded.lastSellTime,
                lastResetBuyTime = excluded.lastResetBuyTime,
                lastResetSellTime = excluded.lastResetSellTime,
                cooldownBuyTime = excluded.cooldownBuyTime,
                cooldownSellTime = excluded.cooldownSellTime
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
            ON CONFLICT(playerUUID, placeholderID)
            DO UPDATE SET
                nowValue = excluded.nowValue,
                refreshDoneTime = excluded.refreshDoneTime
        """;
    }

    @Override
    public void needExtraDownload(String jdbcUrl) {
    }
}