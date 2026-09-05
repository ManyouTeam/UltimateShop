package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public final class TransactionLoggerManager extends AbstractManager {

    public static TransactionLoggerManager transactionLoggerManager;

    private boolean databaseMisconfigurationWarned;

    private final Queue<SQLDatabase.TransactionLog> pendingDatabaseLogs = new ConcurrentLinkedQueue<>();

    private final AtomicInteger pendingDatabaseLogCount = new AtomicInteger();

    private final Queue<FileLog> pendingFileLogs = new ConcurrentLinkedQueue<>();

    private final AtomicInteger pendingFileLogCount = new AtomicInteger();

    private SchedulerUtil flushTask;

    private boolean stopping;

    private final Queue<FutureTask<Void>> pendingFileWrites = new ConcurrentLinkedQueue<>();

    private final Object fileWriteLock = new Object();

    public TransactionLoggerManager() {
        transactionLoggerManager = this;
    }

    public void log(Player player,
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

    private void logToDatabase(Player player,
                                      ObjectItem item,
                                      int amount,
                                      String multiplier,
                                      String action,
                                      String priceText) {
        if (getSqlDatabase() == null) {
            return;
        }

        double multiplierValue;
        try {
            multiplierValue = Double.parseDouble(multiplier);
        } catch (NumberFormatException exception) {
            multiplierValue = 1.0;
        }

        int pendingCount = pendingDatabaseLogCount.incrementAndGet();
        pendingDatabaseLogs.add(new SQLDatabase.TransactionLog(
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
        ));
        if (pendingCount >= getMaxBatchSize()) {
            flushDatabaseLogs(false);
        }
    }

    private boolean isLogCacheEnabled() {
        return ConfigManager.configManager.getBoolean("log-transaction.enabled") && !UltimateShop.freeVersion;
    }

    public void flushLogCache(boolean flushAll) {
        flushDatabaseLogs(flushAll);
        flushFileLogs(flushAll);
    }

    private void flushDatabaseLogs(boolean flushAll) {
        if (pendingDatabaseLogs.isEmpty()) {
            return;
        }
        SQLDatabase sqlDatabase = getSqlDatabase();
        if (sqlDatabase == null) {
            return;
        }

        int maxBatchSize = getMaxBatchSize();
        do {
            List<SQLDatabase.TransactionLog> batch = new ArrayList<>(maxBatchSize);
            for (int i = 0; i < maxBatchSize; i++) {
                SQLDatabase.TransactionLog log = pendingDatabaseLogs.poll();
                if (log == null) {
                    break;
                }
                pendingDatabaseLogCount.decrementAndGet();
                batch.add(log);
            }
            if (batch.isEmpty()) {
                return;
            }
            sqlDatabase.logTransactions(batch);
        } while (flushAll && !pendingDatabaseLogs.isEmpty());
    }

    private SQLDatabase getSqlDatabase() {
        if (!ConfigManager.configManager.getBoolean("database.enabled")) {
            warnDatabaseMisconfiguration();
            return null;
        }
        if (!(DatabaseManager.databaseManager.database instanceof SQLDatabase sqlDatabase)) {
            warnDatabaseMisconfiguration();
            return null;
        }
        return sqlDatabase;
    }

    private int getMaxBatchSize() {
        return Math.max(1, ConfigManager.configManager.getInt("log-transaction.cache.max-batch-size", 500));
    }

    private String buildMessage(Player player,
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

    private synchronized void logToFile(String message) {
        String filePath = ConfigManager.configManager.getString("log-transaction.file");
        if (filePath == null || filePath.isEmpty()) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLog: " + message);
            return;
        }
        int pendingCount = pendingFileLogCount.incrementAndGet();
        pendingFileLogs.add(new FileLog(filePath, message));
        if (stopping || pendingCount >= getMaxBatchSize()) {
            flushFileLogs(false);
        }
    }

    private synchronized void flushFileLogs(boolean flushAll) {
        boolean synchronous = flushAll || stopping;
        finishFileWrites(synchronous);
        int maxBatchSize = getMaxBatchSize();
        do {
            List<FileLog> batch = new ArrayList<>(maxBatchSize);
            for (int i = 0; i < maxBatchSize; i++) {
                FileLog log = pendingFileLogs.poll();
                if (log == null) {
                    break;
                }
                pendingFileLogCount.decrementAndGet();
                batch.add(log);
            }
            if (batch.isEmpty()) {
                return;
            }
            FutureTask<Void> write = new FutureTask<>(() -> {
                synchronized (fileWriteLock) {
                    writeFileBatch(batch);
                }
                return null;
            });
            pendingFileWrites.add(write);
            if (synchronous) {
                finishFileWrites(true);
            } else {
                SchedulerUtil.runTaskAsynchronously(write);
            }
        } while (synchronous && !pendingFileLogs.isEmpty());
    }

    private void finishFileWrites(boolean finishAll) {
        boolean interrupted = false;
        FutureTask<Void> write;
        while ((write = pendingFileWrites.peek()) != null) {
            if (!finishAll && !write.isDone()) {
                break;
            }
            if (finishAll) {
                // Claim queued writes on this thread; an already running task
                // executes only once, and get() waits for it to finish.
                write.run();
            }
            while (true) {
                try {
                    write.get();
                    break;
                } catch (InterruptedException exception) {
                    interrupted = true;
                } catch (ExecutionException exception) {
                    UltimateShop.instance.getLogger().log(Level.SEVERE,
                            "Failed to write transaction logs", exception.getCause());
                    break;
                }
            }
            pendingFileWrites.remove();
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void writeFileBatch(List<FileLog> logs) {
        Map<String, List<String>> messagesByPath = new LinkedHashMap<>();
        for (FileLog log : logs) {
            messagesByPath.computeIfAbsent(log.path(), ignored -> new ArrayList<>()).add(log.message());
        }
        for (Map.Entry<String, List<String>> entry : messagesByPath.entrySet()) {
            CommonUtil.logFile(entry.getKey(), String.join(System.lineSeparator(), entry.getValue()));
        }
    }

    private void warnDatabaseMisconfiguration() {
        if (databaseMisconfigurationWarned) {
            return;
        }
        databaseMisconfigurationWarned = true;
        TextUtil.sendMessage(null, TextUtil.pluginPrefix()
                + " §cWarning: log-transaction.storage is database but database.enabled is false or SQL is unavailable. Transaction logs will not be saved.");
    }

    private record FileLog(String path, String message) {
    }

    @Override
    public void onInit() {
        if (!isLogCacheEnabled()) {
            return;
        }
        long period = Math.max(1L, ConfigManager.configManager.getLong(
                "log-transaction.cache.flush-period-ticks", 200L));
        flushTask = SchedulerUtil.runTaskTimer(() -> flushLogCache(false), period, period);
    }

    @Override
    public void onPluginReload() {
        stopAndFlush();
    }

    @Override
    public void onPluginDisable() {
        stopAndFlush();
    }

    private synchronized void stopAndFlush() {
        stopping = true;
        if (flushTask != null) {
            flushTask.cancel();
            flushTask = null;
        }
        flushLogCache(true);
    }
}
