package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        sender.sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fTrying reload the plugin, server may have TPS " +
                "drop or even crash if you have much shop products and online players because we are trying save" +
                "their data in server main thread. We recommend you restart the server instead of reload plugin" +
                "to keep the data save.");
        UltimateShop.instance.reloadConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.savePlayerCacheOnDisable(player);
            AbstractGUI.playerList.remove(player);
        }
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCacheOnDisable();
        }
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        new CacheManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.addPlayerCache(player);
        }
        MathUtil.scale = ConfigManager.configManager.getInt("math.scale", 2);
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
