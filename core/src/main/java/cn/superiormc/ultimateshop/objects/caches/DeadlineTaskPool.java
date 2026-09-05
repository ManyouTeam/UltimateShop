package cn.superiormc.ultimateshop.objects.caches;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

final class DeadlineTaskPool {

    private static final int DEFAULT_MERGE_SECONDS = 1;

    private static final int MAX_MERGE_SECONDS = 3600;

    private static final int DEFAULT_MAX_TASKS_PER_TICK = 100;

    private static final int MAX_TASKS_PER_TICK_LIMIT = 10000;

    enum TaskType {
        USE_TIMES_BUY(0),
        USE_TIMES_SELL(0),
        RANDOM_PLACEHOLDER(1);

        private final int executionPriority;

        TaskType(int executionPriority) {
            this.executionPriority = executionPriority;
        }
    }

    private static final Map<TaskKey, DeadlineEntry> ENTRIES = new HashMap<>();

    private static final PriorityQueue<DeadlineEntry> QUEUE = new PriorityQueue<>();

    private static SchedulerUtil ticker;

    private static long tickerGeneration;

    private static long nextSequence;

    private DeadlineTaskPool() {}

    static synchronized void schedule(
            Object owner,
            TaskType type,
            LocalDateTime deadline,
            Runnable action
    ) {
        if (owner == null || deadline == null || action == null) {
            return;
        }

        TaskKey key = new TaskKey(owner, type);
        LocalDateTime mergedDeadline = mergeDeadline(deadline);
        DeadlineEntry previous = ENTRIES.get(key);
        if (previous != null && previous.deadline.equals(mergedDeadline)) {
            return;
        }

        ensureTicker();
        ENTRIES.remove(key);
        if (previous != null) {
            QUEUE.remove(previous);
            previous.cancel();
        }

        DeadlineEntry entry = new DeadlineEntry(
                key,
                mergedDeadline,
                nextSequence++,
                action
        );
        ENTRIES.put(key, entry);
        QUEUE.add(entry);
    }

    static synchronized void cancel(Object owner, TaskType type) {
        DeadlineEntry entry = ENTRIES.remove(new TaskKey(owner, type));
        if (entry == null) {
            return;
        }

        QUEUE.remove(entry);
        entry.cancel();
        stopTickerIfIdle();
    }

    static synchronized void cancelAll(TaskType... types) {
        EnumSet<TaskType> selectedTypes = EnumSet.noneOf(TaskType.class);
        for (TaskType type : types) {
            selectedTypes.add(type);
        }

        Iterator<Map.Entry<TaskKey, DeadlineEntry>> iterator = ENTRIES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<TaskKey, DeadlineEntry> mapEntry = iterator.next();
            if (!selectedTypes.contains(mapEntry.getKey().type)) {
                continue;
            }
            mapEntry.getValue().cancel();
            iterator.remove();
        }
        QUEUE.removeIf(DeadlineEntry::isCancelled);
        stopTickerIfIdle();
    }

    private static void ensureTicker() {
        if (ticker != null) {
            return;
        }

        long scheduledGeneration = ++tickerGeneration;
        try {
            ticker = SchedulerUtil.runTaskTimer(
                    () -> tick(scheduledGeneration),
                    1,
                    1
            );
        } catch (RuntimeException | Error error) {
            tickerGeneration++;
            throw error;
        }
    }

    private static void tick(long scheduledGeneration) {
        List<DeadlineEntry> dueEntries = new ArrayList<>();
        synchronized (DeadlineTaskPool.class) {
            if (scheduledGeneration != tickerGeneration) {
                return;
            }

            LocalDateTime now = CommonUtil.getNowTime();
            int maxTasksPerTick = getMaxTasksPerTick();
            while (dueEntries.size() < maxTasksPerTick
                    && !QUEUE.isEmpty()
                    && !QUEUE.peek().deadline.isAfter(now)) {
                DeadlineEntry entry = QUEUE.poll();
                if (!entry.isCancelled() && ENTRIES.get(entry.key) == entry) {
                    dueEntries.add(entry);
                }
            }
        }

        for (DeadlineEntry entry : dueEntries) {
            try {
                entry.execute();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                synchronized (DeadlineTaskPool.class) {
                    ENTRIES.remove(entry.key, entry);
                    entry.cancel();
                }
            }
        }

        synchronized (DeadlineTaskPool.class) {
            if (scheduledGeneration == tickerGeneration) {
                stopTickerIfIdle();
            }
        }
    }

    private static void stopTickerIfIdle() {
        if (!QUEUE.isEmpty() || ticker == null) {
            return;
        }

        SchedulerUtil task = ticker;
        ticker = null;
        tickerGeneration++;
        try {
            task.cancel();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static LocalDateTime mergeDeadline(LocalDateTime deadline) {
        int mergeSeconds = getConfiguredInt(
                "cache.reset-task.merge-seconds",
                DEFAULT_MERGE_SECONDS,
                0,
                MAX_MERGE_SECONDS
        );
        if (mergeSeconds == 0) {
            return deadline;
        }

        long epochSecond = deadline.toEpochSecond(ZoneOffset.UTC);
        long bucketStart = Math.floorDiv(epochSecond, mergeSeconds) * mergeSeconds;
        if (epochSecond == bucketStart && deadline.getNano() == 0) {
            return deadline;
        }
        return LocalDateTime.ofEpochSecond(
                bucketStart + mergeSeconds,
                0,
                ZoneOffset.UTC
        );
    }

    private static int getMaxTasksPerTick() {
        return getConfiguredInt(
                "cache.reset-task.max-tasks-per-tick",
                DEFAULT_MAX_TASKS_PER_TICK,
                1,
                MAX_TASKS_PER_TICK_LIMIT
        );
    }

    private static int getConfiguredInt(
            String path,
            int defaultValue,
            int minValue,
            int maxValue
    ) {
        int configuredValue = ConfigManager.configManager == null
                ? defaultValue
                : ConfigManager.configManager.getInt(path, defaultValue);
        return Math.max(minValue, Math.min(maxValue, configuredValue));
    }

    private static final class TaskKey {

        private final Object owner;
        private final TaskType type;

        private TaskKey(Object owner, TaskType type) {
            this.owner = owner;
            this.type = type;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof TaskKey)) {
                return false;
            }
            TaskKey other = (TaskKey) object;
            return owner == other.owner && type == other.type;
        }

        @Override
        public int hashCode() {
            return 31 * System.identityHashCode(owner) + type.hashCode();
        }
    }

    private static final class DeadlineEntry implements Comparable<DeadlineEntry> {

        private final TaskKey key;
        private final LocalDateTime deadline;
        private final long sequence;
        private volatile boolean cancelled;
        private volatile Runnable action;

        private DeadlineEntry(
                TaskKey key,
                LocalDateTime deadline,
                long sequence,
                Runnable action
        ) {
            this.key = key;
            this.deadline = deadline;
            this.sequence = sequence;
            this.action = action;
        }

        private boolean isCancelled() {
            return cancelled;
        }

        private void cancel() {
            cancelled = true;
            action = null;
        }

        private void execute() {
            Runnable currentAction = action;
            if (!cancelled && currentAction != null) {
                currentAction.run();
            }
        }

        @Override
        public int compareTo(DeadlineEntry other) {
            int timeCompare = deadline.compareTo(other.deadline);
            if (timeCompare != 0) {
                return timeCompare;
            }
            int priorityCompare = Integer.compare(
                    key.type.executionPriority,
                    other.key.type.executionPriority
            );
            return priorityCompare != 0
                    ? priorityCompare
                    : Long.compare(sequence, other.sequence);
        }
    }
}
