package cn.superiormc.ultimateshop.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DatabaseExecutor {

    private static TrackingExecutor executor;

    private static TrackingExecutor createExecutor() {
        int threadCount = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        return new TrackingExecutor(
                threadCount,
                threadCount,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
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

    public static void await() {
        TrackingExecutor currentExecutor;
        synchronized (DatabaseExecutor.class) {
            currentExecutor = executor;
        }
        if (currentExecutor != null) {
            currentExecutor.awaitTasks();
        }
    }

    public static synchronized void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private static class TrackingExecutor extends ThreadPoolExecutor {

        private final Object taskLock = new Object();

        private int pendingTasks;

        private TrackingExecutor(int corePoolSize,
                                 int maximumPoolSize,
                                 long keepAliveTime,
                                 TimeUnit unit,
                                 LinkedBlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                    runnable -> new Thread(runnable, "UltimateShop-DB"));
        }

        @Override
        public void execute(Runnable command) {
            synchronized (taskLock) {
                pendingTasks++;
            }
            try {
                super.execute(() -> {
                    try {
                        command.run();
                    } finally {
                        synchronized (taskLock) {
                            pendingTasks--;
                            if (pendingTasks == 0) {
                                taskLock.notifyAll();
                            }
                        }
                    }
                });
            } catch (RejectedExecutionException exception) {
                synchronized (taskLock) {
                    pendingTasks--;
                    if (pendingTasks == 0) {
                        taskLock.notifyAll();
                    }
                }
                throw exception;
            }
        }

        private void awaitTasks() {
            synchronized (taskLock) {
                while (pendingTasks > 0) {
                    try {
                        taskLock.wait();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }
}
