package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.DatabaseManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.entity.Player;

public final class TransactionLogger {

    private static boolean databaseMisconfigurationWarned;

    private TransactionLogger() {
    }

    public static void log(Player player,
                           ObjectItem item,
                           int amount,
                           String multiplier,
                           String action,
                           String priceText) {
        if (!ConfigManager.configManager.getBoolean("log-transaction.enabled") || UltimateShop.freeVersion) {
            return;
        }

        String storage = ConfigManager.configManager.getString("log-transaction.storage");
        if (storage == null || storage.isBlank()) {
            storage = "file";
        }
        storage = storage.toLowerCase();

        if ("database".equals(storage)) {
            logToDatabase(player, item, amount, multiplier, action, priceText);
            return;
        }

        logToFile(buildMessage(player, item, amount, multiplier, action, priceText));
    }

    private static void logToDatabase(Player player,
                                      ObjectItem item,
                                      int amount,
                                      String multiplier,
                                      String action,
                                      String priceText) {
        if (!ConfigManager.configManager.getBoolean("database.enabled")) {
            warnDatabaseMisconfiguration();
            return;
        }
        if (!(DatabaseManager.databaseManager.database instanceof SQLDatabase sqlDatabase)) {
            warnDatabaseMisconfiguration();
            return;
        }

        double multiplierValue;
        try {
            multiplierValue = Double.parseDouble(multiplier);
        } catch (NumberFormatException exception) {
            multiplierValue = 1.0;
        }

        sqlDatabase.logTransaction(
                CommonUtil.getNowTime(),
                player.getUniqueId().toString(),
                player.getName(),
                item.getShop(),
                item.getShopObject().getShopDisplayName(),
                item.getProduct(),
                TextUtil.parse(item.getDisplayName(player)),
                action,
                amount,
                multiplierValue,
                priceText
        );
    }

    private static String buildMessage(Player player,
                                       ObjectItem item,
                                       int amount,
                                       String multiplier,
                                       String action,
                                       String priceText) {
        return CommonUtil.modifyString(player, ConfigManager.configManager.getString("log-transaction.format"),
                "player", player.getName(),
                "player-uuid", player.getUniqueId().toString(),
                "shop", item.getShop(),
                "shop-name", item.getShopObject().getShopDisplayName(),
                "item", item.getProduct(),
                "item-name", TextUtil.parse(item.getDisplayName(player)),
                "amount", String.valueOf(amount),
                "multiplier", multiplier,
                "price", priceText,
                "buy-or-sell", action,
                "time", CommonUtil.timeToString(CommonUtil.getNowTime(),
                        ConfigManager.configManager.getString("log-transaction.time-format")));
    }

    private static void logToFile(String message) {
        String filePath = ConfigManager.configManager.getString("log-transaction.file");
        if (filePath == null || filePath.isEmpty()) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLog: " + message);
            return;
        }
        SchedulerUtil.runTaskAsynchronously(() -> CommonUtil.logFile(filePath, message));
    }

    private static void warnDatabaseMisconfiguration() {
        if (databaseMisconfigurationWarned) {
            return;
        }
        databaseMisconfigurationWarned = true;
        TextUtil.sendMessage(null, TextUtil.pluginPrefix()
                + " §cWarning: log-transaction.storage is database but database.enabled is false or SQL is unavailable. Transaction logs will not be saved.");
    }
}
