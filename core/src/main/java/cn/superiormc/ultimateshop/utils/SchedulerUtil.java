package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerUtil {

    private BukkitTask bukkitTask;

    private ScheduledTask scheduledTask;

    public SchedulerUtil(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public SchedulerUtil(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public void cancel() {
        if (UltimateShop.isFolia) {
            scheduledTask.cancel();
        } else {
            bukkitTask.cancel();
        }
    }

    // 在主线程上运行任务
    public static void runSync(Runnable task) {
        if (UltimateShop.isFolia) {
            Bukkit.getGlobalRegionScheduler().execute(UltimateShop.instance, task);
        } else {
            Bukkit.getScheduler().runTask(UltimateShop.instance, task);
        }
    }

    // 在异步线程上运行任务
    public static void runTaskAsynchronously(Runnable task) {
        if (UltimateShop.isFolia) {
            task.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, task);
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLater(Runnable task, long delayTicks) {
        if (UltimateShop.isFolia) {
            if (delayTicks <= 0) {
                delayTicks = 1;
            }
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance,
                    scheduledTask -> task.run(), delayTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskLater(UltimateShop.instance, task, delayTicks));
        }
    }

    // 定时循环任务
    public static SchedulerUtil runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (UltimateShop.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runAtFixedRate(UltimateShop.instance,
                    scheduledTask -> task.run(), delayTicks, periodTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskTimer(UltimateShop.instance, task, delayTicks, periodTicks));
        }
    }

    // 延迟执行任务
    public static SchedulerUtil runTaskLaterAsynchronously(Runnable task, long delayTicks) {
        if (UltimateShop.isFolia) {
            return new SchedulerUtil(Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance,
                    scheduledTask -> task.run(), delayTicks));
        } else {
            return new SchedulerUtil(Bukkit.getScheduler().runTaskLaterAsynchronously(UltimateShop.instance, task, delayTicks));
        }
    }

}
