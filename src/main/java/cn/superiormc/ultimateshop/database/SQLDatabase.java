package cn.superiormc.ultimateshop.database;

import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.action.query.QueryAction;
import cc.carm.lib.easysql.hikari.HikariConfig;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
                .build().execute(null);
    }

    public static void checkData(Player player) {
        QueryAction queryAction = null;
        if (player == null) {
            queryAction = sqlManager.createQuery()
                    .inTable("ultimateshop_useTimes")
                    .selectColumns("playerUUID", "shop", "product", "buyUseTimes",  "sellUseTimes", "lastPurchaseTime")
                    .addCondition("uuid = 'Global-Server'")
                    .build();
        }
        else {
            queryAction = sqlManager.createQuery()
                    .inTable("ultimateshop_useTimes")
                    .selectColumns("playerUUID", "shop", "product", "buyUseTimes", "sellUseTimes", "lastPurchaseTime")
                    .addCondition("uuid = '" + player.getUniqueId().toString() + "'")
                    .build();
        }
        queryAction.executeAsync((result) -> {
            ServerCache cache = null;
            if (player == null) {
                cache = ServerCache.serverCache;
                if (cache == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found server cache object," +
                            " there maybe some issues...");
                    return;
                }
            }
            else {
                if (CacheManager.cacheManager.playerCacheMap.containsKey(player)) {
                    cache = CacheManager.cacheManager.playerCacheMap.get(player);
                }
                if (cache == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found player cache object," +
                            " there maybe some issues...");
                    return;
                }
            }
            while (result.getResultSet().next()) {
                String shop = result.getResultSet().getString("shop");
                String product = result.getResultSet().getString("product");
                int buyUseTimes = result.getResultSet().getInt("buyUseTimes");
                int sellUseTimes = result.getResultSet().getInt("sellUseTimes");
                String lastPurchaseTime = result.getResultSet().getString("lastBuyTime");
                String lastSellTime = result.getResultSet().getString("lastSellTime");
                cache.setUseTimesCache(shop, product, buyUseTimes, sellUseTimes, lastPurchaseTime,  lastSellTime);
            }
        });
    }

    public static void updateData(Player player) {
        ServerCache cache = null;
        String playerUUID = null;
        if (player == null) {
            cache = ServerCache.serverCache;
            if (cache == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found server cache object," +
                        " there maybe some issues...");
                return;
            }
            playerUUID = "Global-Server";
        }
        else {
            cache = CacheManager.cacheManager.playerCacheMap.get(player);
            if (cache == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found player cache object," +
                        " there maybe some issues...");
                return;
            }
            playerUUID = player.getUniqueId().toString();
        }
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem tempVal2 : tempVal1.keySet()) {
            sqlManager.createReplace("ultimateshop_useTimes")
                    .setColumnNames("playerUUID",
                            "shop",
                            "product",
                            "buyUseTimes",
                            "sellUseTimes",
                            "lastBuyTime",
                            "lastSellTime")
                    .setParams(playerUUID,
                            tempVal2.getShop(),
                            tempVal2.getProduct(),
                            tempVal1.get(tempVal2).getBuyUseTimes(),
                            tempVal1.get(tempVal2).getSellUseTimes(),
                            tempVal1.get(tempVal2).getLastBuyTime(),
                            tempVal1.get(tempVal2).getLastSellTime())
                    .executeAsync();
        }
    }

}
