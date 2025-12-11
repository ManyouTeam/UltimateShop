package cn.superiormc.ultimateshop;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.libs.bstats.Metrics;
import cn.superiormc.ultimateshop.managers.*;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import cn.superiormc.ultimateshop.utils.SpecialMethodUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateShop extends JavaPlugin {

    public static UltimateShop instance;

    public static final boolean freeVersion = true;

    public static SpecialMethodUtil methodUtil;

    public static boolean isFolia = false;

    public static boolean useGeyser = false;

    public static boolean usePacketEvents = false;

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
            Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: Can not get your Minecraft version! Default set to 1.0.0.");
        }
        if (CommonUtil.getClass("com.destroystokyo.paper.PaperConfig") && CommonUtil.getMinorVersion(17, 1)) {
            try {
                Class<?> paperClass = Class.forName("cn.superiormc.ultimateshop.paper.PaperMethodUtil");
                methodUtil = (SpecialMethodUtil) paperClass.getDeclaredConstructor().newInstance();
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPaper is found, entering Paper plugin mode...!");
            } catch (Throwable throwable) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } else {
            try {
                Class<?> spigotClass = Class.forName("cn.superiormc.ultimateshop.spigot.SpigotMethodUtil");
                methodUtil = (SpecialMethodUtil) spigotClass.getDeclaredConstructor().newInstance();
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSpigot is found, entering Spigot plugin mode...!");
            } catch (Throwable throwable) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.pluginPrefix() + " §cError: The plugin seems break, please download it again from site.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
        if (CommonUtil.getClass("io.papermc.paper.threadedregions.RegionizedServer")) {
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFolia is found, enabled Folia compatibility feature!");
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §6Warning: Folia support is not fully test, major bugs maybe found! " +
                    "Please do not use in production environment!");
            isFolia = true;
        }
        if (!UltimateShop.freeVersion) {
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cPREMIUM version found, thanks for your support and we hope you have good experience with this plugin!");
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
        if (ConfigManager.configManager.getBoolean("menu.title-update.enabled") && UltimateShop.methodUtil.methodID().equals("paper") &&
                CommonUtil.checkPluginLoad("packetevents") &&
                CommonUtil.checkPluginLoad("MythicChanger") && !UltimateShop.freeVersion) {
            usePacketEvents = true;
            new PacketInventoryUtil();
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDynamic title enabled. Hooking into packetevents...");
        }
        if (!CommonUtil.checkClass("com.mojang.authlib.properties.Property", "getValue") && CommonUtil.getMinorVersion(21, 1)) {
            newSkullMethod = true;
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fNew AuthLib found, enabled new skull get method!");
        }
        new Metrics(UltimateShop.instance, 20783);
        UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fYour Minecraft version is: 1." + majorVersion + "." + minorVersion + "!");
        UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is loaded. Author: PQguanfang.");
    }

    @Override
    public void onDisable() {
        if (ServerCache.serverCache != null) {
            ServerCache.serverCache.shutServerCacheOnDisable(true);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CacheManager.cacheManager.savePlayerCacheOnDisable(player, true);
        }
        CacheManager.cacheManager.database.onClose();
        if (BungeeCordManager.enableThis()) {
            BungeeCordManager.bungeeCordManager.disable();
        }
        UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fPlugin is disabled. Author: PQguanfang.");
    }

}
