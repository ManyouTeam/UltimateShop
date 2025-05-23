package cn.superiormc.ultimateshop;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.libs.bstats.Metrics;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SpecialMethodUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateShop extends JavaPlugin {

    public static UltimateShop instance;

    public static final boolean freeVersion = true;

    public static SpecialMethodUtil methodUtil;

    public static boolean isPaper = false;

    public static boolean isFolia = false;

    public static boolean useGeyser = false;

    public static int majorVersion;

    public static int minorVersion;

    public static boolean newSkullMethod;

    @Override
    public void onEnable() {
        instance = this;
        try {
            String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            majorVersion = versionParts.length > 1 ? Integer.parseInt(versionParts[1]) : 0;
            minorVersion = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;
        } catch (Throwable throwable) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPaper is found, entering Paper plugin mode...!");
            try {
                Class<?> paperClass = Class.forName("cn.superiormc.ultimateshop.paper.PaperMethodUtil");
                methodUtil = (SpecialMethodUtil) paperClass.getDeclaredConstructor().newInstance();
            } catch (Throwable throwable) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
            isPaper = true;
        } else {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fSpigot is found, entering Spigot plugin mode...!");
            try {
                Class<?> spigotClass = Class.forName("cn.superiormc.ultimateshop.spigot.SpigotMethodUtil");
                methodUtil = (SpecialMethodUtil) spigotClass.getDeclaredConstructor().newInstance();
            } catch (Throwable throwable) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
        if (CommonUtil.getClass("io.papermc.paper.threadedregions.RegionizedServerInitEvent")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fFolia is found, enabled Folia compatibility feature!");
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §6Warning: Folia support is not fully test, major bugs maybe found! " +
                    "Please do not use in production environment!");
            isFolia = true;
        }
        if (!UltimateShop.freeVersion) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cPREMIUM version found, thanks for your support and we hope you have good experience with this plugin!");
        }
        new ErrorManager();
        new InitManager();
        new ActionManager();
        new ConditionManager();
        new ConfigManager();
        new HookManager();
        new ItemManager();
        new LanguageManager();
        new CacheManager();
        new CommandManager();
        new ListenerManager();
        new TaskManager();
        if (LocateManager.enableThis()) {
            new LocateManager();
        }
        if (BungeeCordManager.enableThis()) {
            new BungeeCordManager();
        }
        if (!CommonUtil.checkClass("com.mojang.authlib.properties.Property", "getValue") && CommonUtil.getMinorVersion(21, 1)) {
            newSkullMethod = true;
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fNew AuthLib found, enabled new skull get method!");
        }
        new Metrics(UltimateShop.instance, 20783);
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fYour Minecraft version is: 1." + majorVersion + "." + minorVersion + "!");
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCacheOnDisable(true);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.savePlayerCacheOnDisable(player, true);
        }
        SQLDatabase.closeSQL();
        if (BungeeCordManager.enableThis()) {
            BungeeCordManager.bungeeCordManager.disable();
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fPlugin is disabled. Author: PQguanfang.");
    }

}
