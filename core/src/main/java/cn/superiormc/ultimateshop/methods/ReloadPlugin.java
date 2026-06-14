package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.DatabaseExecutor;
import cn.superiormc.ultimateshop.listeners.SellStickListener;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        reload(sender, false);
    }

    public static void reload(CommandSender sender, boolean reloadDatabase) {
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloading");
        UltimateShop.instance.reloadConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            MenuStatusManager.menuStatusManager.removeGUIStatus(player);
            if (!UltimateShop.freeVersion) {
                SellStickListener.playerList.remove(player);
            }
        }
        if (reloadDatabase) {
            reloadDatabase();
        }
        TaskManager.taskManager.cancelTask();
        ObjectMenu.commonMenus.clear();
        ObjectMenu.notCommonMenuNames.clear();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        if (reloadDatabase) {
            new CacheManager();
        }
        new TaskManager();
        if (reloadDatabase) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                CacheManager.cacheManager.addObjectCache(player);
            }
        }
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }

    private static void reloadDatabase() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.saveObjectCacheOnDisable(player, false);
        }
        if (CacheManager.cacheManager.serverCache != null) {
            CacheManager.cacheManager.serverCache.shutCacheOnDisable(false);
        }
        DatabaseExecutor.await();
        CacheManager.cacheManager.database.onClose();
        CacheManager.cacheManager.shutdown();
    }
}
