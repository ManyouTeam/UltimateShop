package cn.superiormc.ultimateshop.objects.caches;


import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ResetTaskPool {

    private static final long MERGE_SECONDS = 1;

    private static final Set<ResetTaskGroup> GROUPS =
            ConcurrentHashMap.newKeySet();

    private ResetTaskPool() {}

    public static void registerBuy(
            ObjectUseTimesCache cache,
            LocalDateTime refreshTime
    ) {
        register(cache, refreshTime, true);
    }

    public static void registerSell(
            ObjectUseTimesCache cache,
            LocalDateTime refreshTime
    ) {
        register(cache, refreshTime, false);
    }

    private static void register(
            ObjectUseTimesCache cache,
            LocalDateTime refreshTime,
            boolean buy
    ) {
        if (refreshTime == null || refreshTime.getYear() == 2999) {
            return;
        }

        ResetTaskGroup group = findOrCreateGroup(refreshTime, buy);
        group.add(cache);

        if (ConfigManager.configManager.getBoolean("debug-2")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fReset Time: " + group.triggerTime + " Cache: " + cache + " Group Amount: " + GROUPS.size() + "!");
            for (ObjectUseTimesCache dc : group.caches) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fOther Caches: " + dc + "!");
            }
        }
    }

    public static void unregisterBuy(
            ObjectUseTimesCache cache
    ) {
        unregister(cache, true);
    }

    public static void unregisterSell(
            ObjectUseTimesCache cache
    ) {
        unregister(cache, false);
    }

    private static void unregister(
            ObjectUseTimesCache cache,
            boolean buy
    ) {
        for (ResetTaskGroup group : GROUPS) {
            if (group.isBuy() == buy && group.remove(cache)) {
                if (group.isEmpty()) {
                    group.cancel();
                    GROUPS.remove(group);
                }
                return;
            }
        }
    }

    private static ResetTaskGroup findOrCreateGroup(
            LocalDateTime refreshTime,
            boolean buy
    ) {
        for (ResetTaskGroup group : GROUPS) {
            if (group.isBuy() == buy && group.isNear(refreshTime)) {
                group.updateTriggerTime(refreshTime);
                return group;
            }
        }

        ResetTaskGroup group = new ResetTaskGroup(refreshTime, buy);
        GROUPS.add(group);
        return group;
    }

    private static final class ResetTaskGroup {

        private final boolean buy;
        private final Set<ObjectUseTimesCache> caches =
                ConcurrentHashMap.newKeySet();

        private volatile LocalDateTime triggerTime;
        private SchedulerUtil task;

        ResetTaskGroup(LocalDateTime time, boolean buy) {
            this.buy = buy;
            this.triggerTime = time;
            schedule();
        }

        boolean isBuy() {
            return buy;
        }

        boolean isNear(LocalDateTime time) {
            if (ConfigManager.configManager.getBoolean("debug-2")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fTrigger Time: " + triggerTime + " Object Time: " + time + "!");
            }
            long diff = Math.abs(
                    Duration.between(triggerTime, time).toSeconds()
            );
            return diff <= MERGE_SECONDS;
        }

        void updateTriggerTime(LocalDateTime time) {
            if (time.isAfter(triggerTime)) {
                triggerTime = time;
                reschedule();
            }
        }

        void add(ObjectUseTimesCache cache) {
            caches.add(cache);
        }

        boolean remove(ObjectUseTimesCache cache) {
            return caches.remove(cache);
        }

        boolean isEmpty() {
            return caches.isEmpty();
        }

        void cancel() {
            if (task != null) {
                task.cancel();
                task = null;
            }
        }

        private void schedule() {
            long delayMillis = Duration.between(
                    CommonUtil.getNowTime(),
                    triggerTime
            ).toMillis();

            if (delayMillis <= 0) {
                run();
                return;
            }

            long delayTicks = delayMillis / 50;
            if (ConfigManager.configManager.getBoolean("debug-2")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDelay: " + delayTicks + "!");
            }
            task = SchedulerUtil.runTaskLater(this::run, delayTicks + 10);
        }

        private void reschedule() {
            cancel();
            schedule();
        }

        private void run() {
            for (ObjectUseTimesCache cache : caches) {
                if (buy) {
                    cache.refreshBuyTimes();
                } else {
                    cache.refreshSellTimes();
                }
                if (ConfigManager.configManager.getBoolean("debug-2")) {
                    TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fAuto reset actived for cache: " + cache + "!");
                }
            }
            caches.clear();
            GROUPS.remove(this);
            if (ConfigManager.configManager.getBoolean("debug-2")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fGroup removed, left amount: " + GROUPS.size() + "!");
            }
        }
    }
}