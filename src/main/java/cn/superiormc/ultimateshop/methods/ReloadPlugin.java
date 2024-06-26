package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.listeners.ClickListener;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
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
            CacheManager.cacheManager.savePlayerCacheOnDisable(player);
            AbstractGUI.playerList.remove(player);
            if (!UltimateShop.freeVersion) {
                ClickListener.playerList.remove(player);
            }
        }
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCacheOnDisable();
        }
        TaskManager.taskManager.cancelTask();
        ObjectMenu.buyMoreMenuNames.clear();
        ObjectMenu.commonMenus.clear();
        ObjectMenu.shopMenuNames.clear();
        ObjectMenu.shopMenus.clear();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        new CacheManager();
        new TaskManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addPlayerCache(player);
        }
        MathUtil.scale = ConfigManager.configManager.getInt("math.scale", 2);
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fUsing " + ConfigManager.configManager.getStringOrDefault("sell-mode", "sell.sell-method", "Bukkit") + " sell method!");
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
