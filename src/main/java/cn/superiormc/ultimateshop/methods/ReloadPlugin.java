package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadPlugin {

    public static void reload(CommandSender sender) {
        UltimateShop.instance.reloadConfig();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
    }
}
