package cn.superiormc.ultimateshop;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.papi.PlaceholderAPIExpansion;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UltimateShop extends JavaPlugin {

    public static JavaPlugin instance;

    public static boolean freeVersion = false;

    @Override
    public void onEnable() {
        instance = this;
        new ErrorManager();
        new InitManager();
        new ConfigManager();
        new ItemManager();
        new LanguageManager();
        new CacheManager();
        new CommandManager();
        new ListenerManager();
        new TaskManager();
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            PlaceholderAPIExpansion.papi = new PlaceholderAPIExpansion(this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fHooking into PlaceholderAPI...");
            if (PlaceholderAPIExpansion.papi.register()){
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fFinished hook!");
            }
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCacheOnDisable();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.savePlayerCacheOnDisable(player);
        }
        SQLDatabase.closeSQL();
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is disabled. Author: PQguanfang.");
    }

}
