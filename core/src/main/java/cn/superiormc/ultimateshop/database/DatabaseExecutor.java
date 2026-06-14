package cn.superiormc.ultimateshop.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class DatabaseExecutor {

    private static ExecutorService executor;

    private static ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                r -> new Thread(r, "UltimateShop-DB")
        );
    }

    public static synchronized void start() {
        if (executor == null || executor.isShutdown()) {
            executor = createExecutor();
        }
    }

    public static synchronized ExecutorService getExecutor() {
        if (executor == null || executor.isShutdown()) {
            throw new RejectedExecutionException("UltimateShop database executor is not running");
        }
        return executor;
    }

    public static synchronized void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }
}
