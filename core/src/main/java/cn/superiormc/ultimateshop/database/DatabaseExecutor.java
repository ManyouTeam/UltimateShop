package cn.superiormc.ultimateshop.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseExecutor {
    public static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(
                    Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
                    r -> new Thread(r, "UltimateShop-DB")
            );
}