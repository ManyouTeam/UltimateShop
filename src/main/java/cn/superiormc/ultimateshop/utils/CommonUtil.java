package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;
import pers.neige.neigeitems.utils.ItemUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonUtil {

    public static boolean checkPluginLoad(String pluginName){
        return UltimateShop.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean checkPermission(Player player, String permission) {
        if (CommonUtil.checkPluginLoad("LuckPerms")) {
            return LuckPermsProvider.get().getPlayerAdapter(Player.class).
                    getPermissionData(player).
                    checkPermission(permission).asBoolean();
        }
        else {
            return player.hasPermission(permission);
        }
    }

    public static boolean getMajorVersion(int version) {
        String[] parts = Bukkit.getVersion().split("\\.");
        if (parts.length >= 2) {
            try {
                return Integer.parseInt(parts[1]) >= version;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        if (!getMajorVersion(majorVersion)) {
            return false;
        }
        String[] parts = Bukkit.getVersion().split("\\.");
        if (parts.length >= 3) {
            try {
                return Integer.parseInt(parts[2]) >= minorVersion;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    public static void dispatchCommand(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void dispatchCommand(Player player, String command){
        Bukkit.dispatchCommand(player, command);
    }

    public static void dispatchOpCommand(Player player, String command){
        boolean playerIsOp = player.isOp();
        try {
            player.setOp(true);
            Bukkit.dispatchCommand(player, command);
        } finally {
            player.setOp(playerIsOp);
        }
    }

    public static void summonMythicMobs(Location location, String mobID, int level) {
        try {
            MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobID).orElse(null);
            if (mob != null) {
                mob.spawn(BukkitAdapter.adapt(location), level);
            }
        }
        catch (NoClassDefFoundError ep) {
            io.lumine.xikage.mythicmobs.mobs.MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(mobID);
            if (mob != null) {
                mob.spawn(io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter.adapt(location), level);
            }
        }
    }

    public static String modifyString(String text, String... args) {
        for (int i = 0 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            }
            else {
                text = text.replace(var, args[i + 1]);
            }
        }
        return text;
    }

    public static List<String> modifyList(List<String> config, String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config) {
            for (int i = 0 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    s = s.replace(var, "");
                }
                else {
                    s = s.replace(var, args[i + 1]);
                }
            }
            String[] tempVal1 = s.split(";;");
            if (tempVal1.length > 1) {
                for (String string : tempVal1) {
                    resultList.add(TextUtil.parse(string));
                }
                continue;
            }
            resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }

    public static LocalDateTime stringToTime(String time) {
        if (time.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String timeToString(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String timeToString(LocalDateTime time, String format) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(format));
    }

    public static void mkDir(File dir) {
        if (!dir.exists()) {
            File parentFile = dir.getParentFile();
            if (parentFile == null) {
                return;
            }
            String parentPath = parentFile.getPath();
            mkDir(new File(parentPath));
            dir.mkdir();
        }
    }

    public static boolean getClass(String className) {
        if (!ConfigManager.configManager.getBoolean("check-class.enabled")) {
            return ConfigManager.configManager.config.getStringList("check-class.classes").contains(className);
        }
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String getItemName(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            return ItemUtils.getItemName(displayItem);
        }
        return getItemNameWithoutVanilla(displayItem);
    }

    public static String getItemNameWithoutVanilla(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        StringBuilder result = new StringBuilder();
        for (String word : displayItem.getType().name().toLowerCase().split("_")) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                result.append(firstChar).append(restOfWord).append(" ");
            }
        }
        return result.toString();
    }

    public static boolean isBedrockPlayer(Player player) {
        if (!UltimateShop.useGeyser) {
            return false;
        }
        if (ConfigManager.configManager.getString("menu.bedrock.check-method", "FLOODGATE").equalsIgnoreCase("FLOODGATE")) {
            return FloodgateApi.getInstance().getPlayer(player.getUniqueId()) != null;
        } else {
            return player.getUniqueId().toString().startsWith("00000000-0000-0000-000");
        }
    }

    public static NamespacedKey parseNamespacedKey(String key) {
        String[] keySplit = key.split(":");
        if (keySplit.length == 1) {
            return NamespacedKey.minecraft(key.toLowerCase());
        }
        return NamespacedKey.fromString(key);
    }

    public static Color parseColor(String color) {
        String[] keySplit = color.replace(" ", "").split(",");
        if (keySplit.length == 3) {
            return Color.fromRGB(Integer.parseInt(keySplit[0]), Integer.parseInt(keySplit[1]), Integer.parseInt(keySplit[2]));
        }
        return Color.fromRGB(Integer.parseInt(color));
    }

    public static void giveOrDrop(Player player, ItemStack... item) {
        HashMap<Integer, ItemStack> result = player.getInventory().addItem(item);
        if (!result.isEmpty()) {
            for (int id : result.keySet()) {
                player.getWorld().dropItem(player.getLocation(), result.get(id));
            }
        }
    }
}
