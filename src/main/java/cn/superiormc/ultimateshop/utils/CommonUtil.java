package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommonUtil {

    public static boolean checkPluginLoad(String pluginName) {
        if (pluginName == null) {
            return false;
        }
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
        return UltimateShop.majorVersion >= version;
    }

    public static boolean getMinorVersion(int majorVersion, int minorVersion) {
        return UltimateShop.majorVersion > majorVersion || (UltimateShop.majorVersion == majorVersion &&
                UltimateShop.minorVersion >= minorVersion);
    }

    public static void dispatchCommand(String command) {
        if (UltimateShop.isFolia) {
            Bukkit.getGlobalRegionScheduler().run(UltimateShop.instance, task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void dispatchCommand(Player player, String command) {
        if (UltimateShop.isFolia) {
            player.getScheduler().run(UltimateShop.instance, task -> Bukkit.dispatchCommand(player, command), () -> {
            });
            return;
        }
        Bukkit.dispatchCommand(player, command);
    }

    public static void dispatchOpCommand(Player player, String command) {
        if (UltimateShop.isFolia) {
            player.getScheduler().run(UltimateShop.instance, task -> {
                boolean playerIsOp = player.isOp();
                try {
                    player.setOp(true);
                    Bukkit.dispatchCommand(player, command);
                } finally {
                    player.setOp(playerIsOp);
                }
            }, () -> {
            });
            return;
        }
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
            MythicBukkit.inst().getMobManager().getMythicMob(mobID).ifPresent(mob -> mob.spawn(BukkitAdapter.adapt(location), level));
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
            String var1 = "{" + args[i] + "}";
            String var2 = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var1, "").replace(var2, "");
            }
            else {
                text = text.replace(var1, args[i + 1]).replace(var2, args[i + 1]);
            }
        }
        return text;
    }

    public static List<String> modifyList(Player player, List<String> config, String... args) {
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
                    resultList.add(TextUtil.parse(string, player));
                }
                continue;
            }
            resultList.add(TextUtil.parse(s, player));
        }
        return resultList;
    }

    public static LocalDateTime stringToTime(String time) {
        if (time.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime stringToTime(String time, String format) {
        if (time.equals("null")) {
            return null;
        }
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(format));
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
        try {
            Class.forName(className);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean checkClass(String className, String methodName) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method[] methods = targetClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }

            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isBedrockPlayer(Player player) {
        if (!UltimateShop.useGeyser || !ConfigManager.configManager.getBoolean("menu.bedrock.enabled")) {
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

    public static JSONObject fetchJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }

    public static void logFile(String filePath, String textToAppend) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UltimateShop.instance.getDataFolder() + "/" + filePath, true))) {
            writer.write(textToAppend);
            writer.newLine();
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not write log file: " + filePath);
        }
    }

    public static String translateStringList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String tempVal1 : list) {
            if (!first) {
                builder.append(";;");
            }
            builder.append(tempVal1);
            first = false;
        }
        return builder.toString();
    }

    public static List<String> translateString(String string) {
        if (string == null) {
            return null;
        }
        return new ArrayList<>(Arrays.asList(string.split(";;")));
    }
}
