package cn.superiormc.ultimateshop;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UltimateShop extends JavaPlugin {

    public static JavaPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        new ErrorManager();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.saveDefaultConfig();
            new InitManager(true);
        } else {
            new InitManager(false);
        }
        new ConfigManager();
        new LanguageManager();
        new CacheManager();
        new CommandManager();
        new ListenerManager();
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        if (SQLDatabase.sqlManager != null) {
            SQLDatabase.closeSQL();
        }
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCache();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.playerCacheMap.get(player).shutPlayerCache();
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is disabled. Author: PQguanfang.");
    }

}
