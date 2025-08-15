package cn.superiormc.ultimateshop.database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.action.query.QueryAction;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SQLDatabase {
    public static SQLManager sqlManager;

    public static void initSQL() {
        UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fTrying connect to SQL database...");
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(ConfigManager.configManager.getString("database.jdbc-class"));
        config.setJdbcUrl(ConfigManager.configManager.getString("database.jdbc-url"));
        if (ConfigManager.configManager.getString("database.properties.user") != null) {
            config.setUsername(ConfigManager.configManager.getString("database.properties.user"));
            config.setPassword(ConfigManager.configManager.getString("database.properties.password"));
        }
        sqlManager = EasySQL.createManager(config);
        try {
            if (!sqlManager.getConnection().isValid(5)) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cFailed connect to SQL database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createTable();
    }

    public static void closeSQL() {
        if (Objects.nonNull(sqlManager)) {
            EasySQL.shutdownManager(sqlManager);
            sqlManager = null;
        }
    }

    public static void createTable() {
        sqlManager.createTable("ultimateshop_useTimes")
                .addColumn("playerUUID", "VARCHAR(36)")
                .addColumn("shop", "VARCHAR(48)")
                .addColumn("product", "VARCHAR(48)")
                .addColumn("buyUseTimes", "INT")
                .addColumn("totalBuyUseTimes", "INT")
                .addColumn("sellUseTimes", "INT")
                .addColumn("totalSellUseTimes", "INT")
                .addColumn("lastBuyTime", "DATETIME")
                .addColumn("lastSellTime", "DATETIME")
                .addColumn("lastResetBuyTime", "DATETIME")
                .addColumn("lastResetSellTime", "DATETIME")
                .addColumn("cooldownBuyTime", "DATETIME")
                .addColumn("cooldownSellTime", "DATETIME")
                .build().execute(null);
        if (!UltimateShop.freeVersion) {
            sqlManager.createTable("ultimateshop_randomPlaceholder")
                    .addColumn("placeholderID", "VARCHAR(48) NOT NULL PRIMARY KEY")
                    .addColumn("nowValue", "TEXT")
                    .addColumn("refreshDoneTime", "DATETIME")

                    .build().execute(null);
        }
    }

    public static void updateTable() {
        sqlManager.alterTable("ultimateshop_useTimes")
                .addColumn("totalBuyUseTimes", "INT")
                .executeAsync();
        sqlManager.alterTable("ultimateshop_useTimes")
                .addColumn("totalSellUseTimes", "INT")
                .executeAsync();
        sqlManager.alterTable("ultimateshop_useTimes")
                .addColumn("lastResetBuyTime", "DATETIME")
                .executeAsync();
        sqlManager.alterTable("ultimateshop_useTimes")
                .addColumn("lastResetSellTime", "DATETIME")
                .executeAsync();
    }

    public static void checkData(ServerCache cache) {
        QueryAction queryAction2;
        if (cache.server && !UltimateShop.freeVersion) {
            queryAction2 = sqlManager.createQuery()
                    .inTable("ultimateshop_randomPlaceholder")
                    .selectColumns("placeholderID", "nowValue",
                            "refreshDoneTime")
                    .build();
            queryAction2.executeAsync((result) -> {
                while (result.getResultSet().next()) {
                    String placeholderID = result.getResultSet().getString("placeholderID");
                    List<String> nowValue = CommonUtil.translateString(result.getResultSet().getString("nowValue"));
                    String refreshDoneTime = result.getResultSet().getString("refreshDoneTime");
                    if (nowValue != null && refreshDoneTime != null) {
                        cache.setRandomPlaceholderCache(placeholderID, refreshDoneTime, nowValue);
                    }
                }
            });
        }

        QueryAction queryAction1;
        if (cache.server) {
            queryAction1 = sqlManager.createQuery()
                    .inTable("ultimateshop_useTimes")
                    .selectColumns("playerUUID",
                            "shop", "product",
                            "buyUseTimes",  "sellUseTimes",
                            "totalBuyUseTimes", "totalSellUseTimes",
                            "lastBuyTime", "lastSellTime",
                            "lastResetBuyTime", "lastResetSellTime",
                            "cooldownBuyTime", "cooldownSellTime")
                    .addCondition("playerUUID = 'Global-Server'")
                    .build();
        } else {
            queryAction1 = sqlManager.createQuery()
                    .inTable("ultimateshop_useTimes")
                    .selectColumns("playerUUID",
                            "shop", "product",
                            "buyUseTimes", "sellUseTimes",
                            "totalBuyUseTimes", "totalSellUseTimes",
                            "lastBuyTime", "lastSellTime",
                            "lastResetBuyTime", "lastResetSellTime",
                            "cooldownBuyTime", "cooldownSellTime")
                    .addCondition("playerUUID = '" + cache.player.getUniqueId() + "'")
                    .build();
        }
        queryAction1.executeAsync((result) -> {
            while (result.getResultSet().next()) {
                String shop = result.getResultSet().getString("shop");
                String product = result.getResultSet().getString("product");
                int buyUseTimes = result.getResultSet().getInt("buyUseTimes");
                int totalBuyUseTimes = result.getResultSet().getInt("totalBuyUseTimes");
                int sellUseTimes = result.getResultSet().getInt("sellUseTimes");
                int totalSellUseTimes = result.getResultSet().getInt("totalSellUseTimes");
                String lastPurchaseTime = result.getResultSet().getString("lastBuyTime");
                String lastSellTime = result.getResultSet().getString("lastSellTime");
                String lastResetBuyTime = result.getResultSet().getString("lastResetBuyTime");
                String lastResetSellTime = result.getResultSet().getString("lastResetSellTime");
                String cooldownPurchaseTime = result.getResultSet().getString("cooldownBuyTime");
                String cooldownSellTime = result.getResultSet().getString("cooldownSellTime");
                cache.setUseTimesCache(shop, product,
                        buyUseTimes, totalBuyUseTimes,
                        sellUseTimes, totalSellUseTimes,
                        lastPurchaseTime,  lastSellTime, lastResetBuyTime, lastResetSellTime,
                        cooldownPurchaseTime, cooldownSellTime);
            }
        });
    }

    public static void updateData(ServerCache cache, boolean quitServer) {
        String playerUUID;
        if (cache.server) {
            playerUUID = "Global-Server";
        }
        else {
            playerUUID = cache.player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem tempVal2 : tempVal1.keySet()) {
            ObjectUseTimesCache tempCache = tempVal1.get(tempVal2);
            int buyUseTimes = tempCache.getBuyUseTimes();
            int totalBuyUseTimes = tempCache.getTotalBuyUseTimes();
            int sellUseTimes = tempCache.getSellUseTimes();
            int totalSellUseTimes = tempCache.getTotalSellUseTimes();
            String lastBuyTime = tempCache.getLastBuyTime();
            String lastSellTime = tempCache.getLastSellTime();
            String lastResetBuyTime = tempCache.getLastResetBuyTime();
            String lastResetSellTime = tempCache.getLastResetSellTime();
            String cooldownBuyTime = tempCache.getCooldownBuyTime();
            String cooldownSellTime = tempCache.getCooldownSellTime();
            if (buyUseTimes == 0 && sellUseTimes == 0 && lastBuyTime == null && lastSellTime == null
            && cooldownBuyTime == null && cooldownSellTime == null) {
                continue;
            }
            if (tempVal1.get(tempVal2).isFirstInsert()) {
                sqlManager.createInsert("ultimateshop_useTimes")
                        .setColumnNames("playerUUID",
                                "shop",
                                "product",
                                "buyUseTimes",
                                "totalBuyUseTimes",
                                "sellUseTimes",
                                "totalSellUseTimes",
                                "lastBuyTime",
                                "lastSellTime",
                                "lastResetBuyTime",
                                "lastResetSellTime",
                                "cooldownBuyTime",
                                "cooldownSellTime")
                        .setParams(playerUUID,
                                tempVal2.getShop(),
                                tempVal2.getProduct(),
                                buyUseTimes,
                                totalBuyUseTimes,
                                sellUseTimes,
                                totalSellUseTimes,
                                lastBuyTime,
                                lastSellTime,
                                lastResetBuyTime,
                                lastResetSellTime,
                                cooldownBuyTime,
                                cooldownSellTime)
                        .executeAsync();
            } else {
                String[] keys = {"buyUseTimes", "totalBuyUseTimes", "sellUseTimes", "totalSellUseTimes",
                        "lastBuyTime", "lastSellTime", "lastResetBuyTime", "lastResetSellTime",
                        "cooldownBuyTime", "cooldownSellTime"};
                Object[] values = {buyUseTimes, totalBuyUseTimes, sellUseTimes, totalSellUseTimes,
                        lastBuyTime, lastSellTime, lastResetBuyTime, lastResetSellTime,
                        cooldownBuyTime, cooldownSellTime};
                sqlManager.createUpdate("ultimateshop_useTimes")
                        .addCondition("playerUUID = '" + playerUUID + "'")
                        .addCondition("shop = '" + tempVal2.getShop() + "'")
                        .addCondition("product = '" + tempVal2.getProduct() + "'")
                        .setColumnValues(keys, values)
                        .build()
                        .executeAsync();
            }
        }

        if (cache.server && !UltimateShop.freeVersion) {
            Collection<ObjectRandomPlaceholderCache> tempVal3 = cache.getRandomPlaceholderCache().values();
            for (ObjectRandomPlaceholderCache tempVal4 : tempVal3) {
                if (tempVal4.getPlaceholder().getMode().equals("ONCE")) {
                    continue;
                }
                String placeholderID = tempVal4.getPlaceholder().getID();
                String nowValue = CommonUtil.translateStringList(tempVal4.getNowValue());
                String refreshDoneTime = CommonUtil.timeToString(tempVal4.getRefreshDoneTime());
                sqlManager.createReplace("ultimateshop_randomPlaceholder")
                        .setColumnNames("placeholderID", "nowValue",
                                "refreshDoneTime")
                        .setParams(placeholderID, nowValue, refreshDoneTime)
                        .executeAsync();
            }
        }
        if (quitServer) {
            CacheManager.cacheManager.removePlayerCache(cache.player);
        }
    }

    public static void updateDataOnDisable(ServerCache cache, boolean disable) {
        String playerUUID;
        if (cache.server) {
            playerUUID = "Global-Server";
        } else {
            playerUUID = cache.player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem tempVal2 : tempVal1.keySet()) {
            ObjectUseTimesCache tempCache = tempVal1.get(tempVal2);
            int buyUseTimes = tempCache.getBuyUseTimes();
            int totalBuyUseTimes = tempCache.getTotalBuyUseTimes();
            int sellUseTimes = tempCache.getSellUseTimes();
            int totalSellUseTimes = tempCache.getTotalSellUseTimes();
            String lastBuyTime = tempCache.getLastBuyTime();
            String lastSellTime = tempCache.getLastSellTime();
            String lastResetBuyTime = tempCache.getLastResetBuyTime();
            String lastResetSellTime = tempCache.getLastResetSellTime();
            String cooldownBuyTime = tempCache.getCooldownBuyTime();
            String cooldownSellTime = tempCache.getCooldownSellTime();
            if (buyUseTimes == 0 && sellUseTimes == 0 && lastBuyTime == null && lastSellTime == null
                    && cooldownBuyTime == null && cooldownSellTime == null) {
                continue;
            }
            try {
                if (tempVal1.get(tempVal2).isFirstInsert()) {
                    sqlManager.createInsert("ultimateshop_useTimes")
                            .setColumnNames("playerUUID",
                                    "shop",
                                    "product",
                                    "buyUseTimes",
                                    "totalBuyUseTimes",
                                    "sellUseTimes",
                                    "totalSellUseTimes",
                                    "lastBuyTime",
                                    "lastSellTime",
                                    "lastResetBuyTime",
                                    "lastResetSellTime",
                                    "cooldownBuyTime",
                                    "cooldownSellTime")
                            .setParams(playerUUID,
                                    tempVal2.getShop(),
                                    tempVal2.getProduct(),
                                    buyUseTimes,
                                    totalBuyUseTimes,
                                    sellUseTimes,
                                    totalSellUseTimes,
                                    lastBuyTime,
                                    lastSellTime,
                                    lastResetBuyTime,
                                    lastResetSellTime,
                                    cooldownBuyTime,
                                    cooldownSellTime)
                            .execute();
                } else {
                    String[] keys = {"buyUseTimes", "totalBuyUseTimes", "sellUseTimes", "totalSellUseTimes",
                            "lastBuyTime", "lastSellTime", "lastResetBuyTime", "lastResetSellTime",
                            "cooldownBuyTime", "cooldownSellTime"};
                    Object[] values = {buyUseTimes, totalBuyUseTimes, sellUseTimes, totalSellUseTimes,
                            lastBuyTime, lastSellTime, lastResetBuyTime, lastResetSellTime,
                            cooldownBuyTime, cooldownSellTime};
                    sqlManager.createUpdate("ultimateshop_useTimes")
                            .addCondition("playerUUID = '" + playerUUID + "'")
                            .addCondition("shop = '" + tempVal2.getShop() + "'")
                            .addCondition("product = '" + tempVal2.getProduct() + "'")
                            .setColumnValues(keys, values)
                            .build()
                            .execute();
                }
            } catch (Throwable ignored) {

            }
        }
        if (cache.server && !UltimateShop.freeVersion) {
            Collection<ObjectRandomPlaceholderCache> tempVal3 = cache.getRandomPlaceholderCache().values();
            for (ObjectRandomPlaceholderCache tempVal4 : tempVal3) {
                if (tempVal4.getPlaceholder().getMode().equals("ONCE")) {
                    continue;
                }
                String placeholderID = tempVal4.getPlaceholder().getID();
                String nowValue = CommonUtil.translateStringList(tempVal4.getNowValue(disable));
                String refreshDoneTime = CommonUtil.timeToString(tempVal4.getRefreshDoneTime());
                try {
                    sqlManager.createReplace("ultimateshop_randomPlaceholder")
                            .setColumnNames("placeholderID", "nowValue",
                                    "refreshDoneTime")
                            .setParams(placeholderID, nowValue, refreshDoneTime)
                            .execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        CacheManager.cacheManager.removePlayerCache(cache.player);
    }

}
