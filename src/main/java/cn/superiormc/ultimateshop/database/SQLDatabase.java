package cn.superiormc.ultimateshop.database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.action.query.QueryAction;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class SQLDatabase {
    public static SQLManager sqlManager;

    public static void initSQL() {
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fTrying connect to SQL database...");
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
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cFailed connect to SQL database!");
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
                .addColumn("product", "VARCHAR(1)")
                .addColumn("buyUseTimes", "INT")
                .addColumn("sellUseTimes", "INT")
                .addColumn("lastBuyTime", "DATETIME")
                .addColumn("lastSellTime", "DATETIME")
                .addColumn("cooldownBuyTime", "DATETIME")
                .addColumn("cooldownSellTime", "DATETIME")
                .build().execute(null);
    }

    public static void checkData(ServerCache cache) {
        QueryAction queryAction = null;
        if (cache.server) {
            queryAction = sqlManager.createQuery()
                    .inTable("ultimateshop_useTimes")
                    .selectColumns("playerUUID",
                            "shop", "product",
                            "buyUseTimes",  "sellUseTimes",
                            "lastBuyTime", "lastSellTime",
                            "cooldownBuyTime", "cooldownSellTime")
                    .addCondition("playerUUID = 'Global-Server'")
                    .build();
        }
        else {
            queryAction = sqlManager.createQuery()
                    .inTable("ultimateshop_useTimes")
                    .selectColumns("playerUUID",
                            "shop", "product",
                            "buyUseTimes", "sellUseTimes",
                            "lastBuyTime", "lastSellTime",
                            "cooldownBuyTime", "cooldownSellTime")
                    .addCondition("playerUUID = '" + cache.player.getUniqueId().toString() + "'")
                    .build();
        }
        queryAction.executeAsync((result) -> {
            while (result.getResultSet().next()) {
                String shop = result.getResultSet().getString("shop");
                String product = result.getResultSet().getString("product");
                int buyUseTimes = result.getResultSet().getInt("buyUseTimes");
                int sellUseTimes = result.getResultSet().getInt("sellUseTimes");
                String lastPurchaseTime = result.getResultSet().getString("lastBuyTime");
                String lastSellTime = result.getResultSet().getString("lastSellTime");
                String cooldownPurchaseTime = result.getResultSet().getString("cooldownBuyTime");
                String cooldownSellTime = result.getResultSet().getString("cooldownSellTime");
                cache.setUseTimesCache(shop, product,
                        buyUseTimes, sellUseTimes,
                        lastPurchaseTime,  lastSellTime,
                        cooldownPurchaseTime, cooldownSellTime);
            }
        });
    }

    public static void updateData(ServerCache cache) {
        String playerUUID = null;
        if (cache.server) {
            playerUUID = "Global-Server";
        }
        else {
            playerUUID = cache.player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem tempVal2 : tempVal1.keySet()) {
            int buyUseTimes = tempVal1.get(tempVal2).getBuyUseTimes();
            int sellUseTimes = tempVal1.get(tempVal2).getSellUseTimes();
            String lastBuyTime = tempVal1.get(tempVal2).getLastBuyTime();
            String lastSellTime = tempVal1.get(tempVal2).getLastSellTime();
            String cooldownBuyTime = tempVal1.get(tempVal2).getCooldownBuyTime();
            String cooldownSellTime = tempVal1.get(tempVal2).getCooldownSellTime();
            if (buyUseTimes == 0 && sellUseTimes == 0 && lastBuyTime == null && lastSellTime == null
            && cooldownBuyTime == null && cooldownSellTime == null) {
                continue;
            }
            if (cache.server) {
                cooldownBuyTime = null;
                cooldownSellTime = null;
            }
            sqlManager.createReplace("ultimateshop_useTimes")
                    .setColumnNames("playerUUID",
                            "shop",
                            "product",
                            "buyUseTimes",
                            "sellUseTimes",
                            "lastBuyTime",
                            "lastSellTime",
                            "cooldownBuyTime",
                            "cooldownSellTime")
                    .setParams(playerUUID,
                            tempVal2.getShop(),
                            tempVal2.getProduct(),
                            buyUseTimes,
                            sellUseTimes,
                            lastBuyTime,
                            lastSellTime,
                            cooldownBuyTime,
                            cooldownSellTime)
                    .executeAsync();
        }
        CacheManager.cacheManager.removePlayerCache(cache.player);
    }

    public static void updateDataNoAsync(ServerCache cache) {
        String playerUUID = null;
        if (cache.server) {
            playerUUID = "Global-Server";
        }
        else {
            playerUUID = cache.player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem tempVal2 : tempVal1.keySet()) {
            try {
                int buyUseTimes = tempVal1.get(tempVal2).getBuyUseTimes();
                int sellUseTimes = tempVal1.get(tempVal2).getSellUseTimes();
                String lastBuyTime = tempVal1.get(tempVal2).getLastBuyTime();
                String lastSellTime = tempVal1.get(tempVal2).getLastSellTime();
                String cooldownBuyTime = tempVal1.get(tempVal2).getCooldownBuyTime();
                String cooldownSellTime = tempVal1.get(tempVal2).getCooldownSellTime();
                if (buyUseTimes == 0 && sellUseTimes == 0 && lastBuyTime == null && lastSellTime == null
                        && cooldownBuyTime == null && cooldownSellTime == null) {
                    continue;
                }
                if (cache.server) {
                    cooldownBuyTime = null;
                    cooldownSellTime = null;
                }
                sqlManager.createReplace("ultimateshop_useTimes")
                        .setColumnNames("playerUUID",
                                "shop",
                                "product",
                                "buyUseTimes",
                                "sellUseTimes",
                                "lastBuyTime",
                                "lastSellTime",
                                "cooldownBuyTime",
                                "cooldownSellTime")
                        .setParams(playerUUID,
                                tempVal2.getShop(),
                                tempVal2.getProduct(),
                                buyUseTimes,
                                sellUseTimes,
                                lastBuyTime,
                                lastSellTime,
                                cooldownBuyTime,
                                cooldownSellTime)
                        .execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        CacheManager.cacheManager.removePlayerCache(cache.player);
    }

}
