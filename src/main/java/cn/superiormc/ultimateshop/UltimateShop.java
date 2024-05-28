package cn.superiormc.ultimateshop;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.papi.PlaceholderAPIExpansion;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateShop extends JavaPlugin {

    public static UltimateShop instance;

    public static boolean freeVersion = true;

    public static boolean isPaper = false;

    public static boolean isFolia = false;

    public static boolean useGeyser = false;

    @Override
    public void onEnable() {
        instance = this;
        new ErrorManager();
        new InitManager();
        new ConfigManager();
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPaper is found, enabled Paper only feature!");
            isPaper = true;
        }
        if (CommonUtil.getClass("io.papermc.paper.threadedregions.RegionizedServerInitEvent")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fFolia is found, enabled Folia compatibility feature!");
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §6Warning: Folia support is not fully test, major bugs maybe found! " +
                    "Please do not use in production environment!");
            isFolia = true;
        }
        new ItemManager();
        new LanguageManager();
        new CacheManager();
        new CommandManager();
        new ListenerManager();
        new TaskManager();
        if (BungeeCordManager.enableThis()) {
            new BungeeCordManager();
        }
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            PlaceholderAPIExpansion.papi = new PlaceholderAPIExpansion(this);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fHooking into PlaceholderAPI...");
            if (PlaceholderAPIExpansion.papi.register()){
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fFinished hook!");
            }
        }
        if (!UltimateShop.freeVersion && CommonUtil.getClass("org.geysermc.floodgate.api.FloodgateApi")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fHooking into Floodgate...");
            useGeyser = true;
        }
        String[] parts = Bukkit.getVersion().split("\\.");
        if (parts.length == 3) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fYour Minecraft version is: 1." + parts[1] +
                    "." + parts[2].replace(")", "") + "!");
        } else if (parts.length == 2) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fYour Minecraft version is: 1." + parts[1] + ".0!");
        }
        new Metrics(this, 20783);
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
        if (BungeeCordManager.enableThis()) {
            BungeeCordManager.bungeeCordManager.disable();
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is disabled. Author: PQguanfang.");
    }

}
