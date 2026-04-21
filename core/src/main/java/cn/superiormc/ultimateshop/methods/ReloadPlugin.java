package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.SellStickListener;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloading");
        UltimateShop.instance.reloadConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.saveObjectCacheOnDisable(player, false);
            MenuStatusManager.menuStatusManager.removeGUIStatus(player);
            if (!UltimateShop.freeVersion) {
                SellStickListener.playerList.remove(player);
            }
        }
        if (CacheManager.cacheManager.serverCache != null) {
            CacheManager.cacheManager.serverCache.shutCacheOnDisable(false);
        }
        TaskManager.taskManager.cancelTask();
        ObjectMenu.commonMenus.clear();
        ObjectMenu.notCommonMenuNames.clear();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        new CacheManager();
        new TaskManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addObjectCache(player);
        }
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
