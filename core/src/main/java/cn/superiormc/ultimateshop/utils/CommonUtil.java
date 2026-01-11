package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static boolean checkPluginLoad(String pluginName) {
        if (pluginName == null) {
            return false;
        }
        if (ConfigManager.configManager.getBoolean("bypass-plugin-check." + pluginName)) {
            return true;
        }
        return UltimateShop.instance.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public static boolean checkPermission(Player player, String permission) {
        if (CommonUtil.checkPluginLoad("LuckPerms")) {
            return LuckPermsProvider.get().getPlayerAdapter(Player.class).
                    getPermissionData(player).
                    checkPermission(permission).asBoolean();
        } else {
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

    public static LocalDateTime getNowTime() {
        LocalDateTime now = LocalDateTime.now();

        if (ConfigManager.configManager.getBoolean("time-offset.enabled")) {
            return now.plusHours(ConfigManager.configManager.getInt("time-offset.offset-hours", 0))
                    .plusMinutes(ConfigManager.configManager.getInt("time-offset.offset-minutes", 0))
                    .plusSeconds(ConfigManager.configManager.getInt("time-offset.offset-seconds", 0));
        }
        
        return now;
    }
    
    public static void summonMythicMobs(Location location, String mobID, int level) {
        MythicBukkit.inst().getMobManager().getMythicMob(mobID).ifPresent(mob -> mob.spawn(BukkitAdapter.adapt(location), level));
    }

    public static String modifyString(Player player, String text, String... args) {
        text = CommonUtil.parseLang(player, text);
        for (int i = 0 ; i < args.length ; i += 2) {
            String var1 = "{" + args[i] + "}";
            String var2 = "%" + args[i] + "%";
            if (args[i + 1] == null) {
                text = text.replace(var1, "").replace(var2, "");
            } else {
                text = text.replace(var1, args[i + 1]).replace(var2, args[i + 1]);
            }
        }
        return text;
    }

    public static List<String> modifyList(Player player, List<String> config, String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config) {
            s = CommonUtil.parseLang(player, s);
            for (int i = 0 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    s = s.replace(var, "");
                } else {
                    s = s.replace(var, args[i + 1]);
                }
            }
            String[] tempVal1 = s.split(";;");
            if (tempVal1.length > 1) {
                for (String string : tempVal1) {
                    resultList.add(TextUtil.withPAPI(string, player));
                }
                continue;
            }
            resultList.add(TextUtil.withPAPI(s, player));
        }
        return resultList;
    }

    public static String parseLang(Player player, String text) {
        Pattern pattern8 = Pattern.compile("\\{lang:(.*?)}");
        Matcher matcher8 = pattern8.matcher(text);
        while (matcher8.find()) {
            String placeholder = matcher8.group(1);
            text = text.replace("{lang:" + placeholder + "}", LanguageManager.languageManager.getStringText(player, "override-lang." + placeholder));
        }
        return text;
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
        if (CommonUtil.getMajorVersion(16)) {
            return NamespacedKey.fromString(key);
        }
        return new NamespacedKey("ultimateshop", "unknown");
    }

    public static Color parseColor(String color) {
        if (color == null || color.isEmpty()) {
            return Color.fromRGB(0, 0, 0);
        }

        color = color.trim();

        // 支持 #RRGGBB
        if (color.startsWith("#")) {
            return Color.fromRGB(Integer.parseInt(color.substring(1), 16));
        }

        // 支持 R,G,B
        String[] keySplit = color.replace(" ", "").split(",");
        if (keySplit.length == 3) {
            return Color.fromRGB(
                    Integer.parseInt(keySplit[0]),
                    Integer.parseInt(keySplit[1]),
                    Integer.parseInt(keySplit[2])
            );
        }

        // 默认：单值 RGB int
        return Color.fromRGB(Integer.parseInt(color));
    }

    public static String colorToString(Color color) {
        if (color == null) {
            return "0,0,0";
        }
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static List<Color> parseColorList(List<String> rawList) {
        List<Color> colors = new ArrayList<>();

        for (String value : rawList) {
            try {
                colors.add(parseColor(value));
            } catch (Exception e) {
                return colors;
            }
        }

        return colors;
    }

    public static void giveOrDrop(Player player, ItemStack... item) {
        if (player == null || item == null) {
            return;
        }
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
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not write log file: " + filePath);
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

    public static boolean containsAnyString(String text, String... keywords) {
        if (text == null || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    public static boolean actionIsLeftClick(Action action) {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    }

    public static boolean actionIsRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
