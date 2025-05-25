package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.StaticPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectConditionalPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static final Pattern SINGLE_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static final Pattern GRADIENT_PATTERN = Pattern.compile("&<#([A-Fa-f0-9]{6})>(.*?)&<#([A-Fa-f0-9]{6})>");
    public static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("[&§]([0-9a-frlomn])", Pattern.CASE_INSENSITIVE);

    // Minecraft 16 原生颜色码映射
    private static final Map<Character, Color> LEGACY_COLORS = Map.ofEntries(
            Map.entry('0', new Color(0, 0, 0)),
            Map.entry('1', new Color(0, 0, 170)),
            Map.entry('2', new Color(0, 170, 0)),
            Map.entry('3', new Color(0, 170, 170)),
            Map.entry('4', new Color(170, 0, 0)),
            Map.entry('5', new Color(170, 0, 170)),
            Map.entry('6', new Color(255, 170, 0)),
            Map.entry('7', new Color(170, 170, 170)),
            Map.entry('8', new Color(85, 85, 85)),
            Map.entry('9', new Color(85, 85, 255)),
            Map.entry('a', new Color(85, 255, 85)),
            Map.entry('b', new Color(85, 255, 255)),
            Map.entry('c', new Color(255, 85, 85)),
            Map.entry('d', new Color(255, 85, 255)),
            Map.entry('e', new Color(255, 255, 85)),
            Map.entry('f', new Color(255, 255, 255))
    );

    public static String pluginPrefix() {
        if (!CommonUtil.getMajorVersion(16)) {
            return "§a[UltimateShop]";
        }
        return "§x§9§8§F§B§9§8[UltimateShop]";
    }

    public static String colorize(String input) {

        boolean supportHex = CommonUtil.getMajorVersion(16);

        if (input == null || input.isEmpty()) {
            return input;
        }

        input = applyGradients(input, supportHex);

        Matcher hexMatcher = SINGLE_HEX_PATTERN.matcher(input);
        StringBuilder hexBuffer = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            if (supportHex) {
                hexMatcher.appendReplacement(hexBuffer, "§x" + toMinecraftHex(hex));
            } else {
                char legacy = getClosestLegacyColor(hex);
                Bukkit.getConsoleSender().sendMessage(legacy + "");
                hexMatcher.appendReplacement(hexBuffer, "§" + legacy);
            }
        }
        hexMatcher.appendTail(hexBuffer);
        input = hexBuffer.toString();

        // 处理传统颜色符号
        Matcher legacyMatcher = LEGACY_COLOR_PATTERN.matcher(input);
        StringBuilder legacyBuffer = new StringBuilder();
        while (legacyMatcher.find()) {
            legacyMatcher.appendReplacement(legacyBuffer, "§" + legacyMatcher.group(1).toLowerCase());
        }
        legacyMatcher.appendTail(legacyBuffer);
        input = legacyBuffer.toString();

        return input;
    }

    private static String applyGradients(String input, boolean supportHex) {
        Matcher matcher = GRADIENT_PATTERN.matcher(input);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String startColor = matcher.group(1);
            String text = matcher.group(2);
            String endColor = matcher.group(3);

            String gradientText = supportHex
                    ? applyGradient(startColor, endColor, text)
                    : applyLegacyGradient(startColor, endColor, text);

            matcher.appendReplacement(buffer, gradientText);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyGradient(String startHex, String endHex, String text) {
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);

        int length = text.length();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("%02x%02x%02x", red, green, blue);
            builder.append("§x").append(toMinecraftHex(hex)).append(text.charAt(i));
        }

        return builder.toString();
    }

    private static String applyLegacyGradient(String startHex, String endHex, String text) {
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);

        int length = text.length();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("%02x%02x%02x", red, green, blue);
            char legacyColor = getClosestLegacyColor(hex);
            builder.append("§").append(legacyColor).append(text.charAt(i));
        }

        return builder.toString();
    }

    private static String toMinecraftHex(String hex) {
        StringBuilder builder = new StringBuilder();
        for (char c : hex.toCharArray()) {
            builder.append("§").append(c);
        }
        return builder.toString();
    }

    private static char getClosestLegacyColor(String hex) {
        Color target = Color.decode("#" + hex);
        double minDistance = Double.MAX_VALUE;
        char closest = 'f';

        for (Map.Entry<Character, Color> entry : LEGACY_COLORS.entrySet()) {
            double distance = colorDistance(target, entry.getValue());
            if (distance < minDistance) {
                minDistance = distance;
                closest = entry.getKey();
            }
        }

        return closest;
    }

    private static double colorDistance(Color c1, Color c2) {
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return 0.3 * r * r + 0.59 * g * g + 0.11 * b * b;
    }

    public static String parse(String text) {
        return UltimateShop.methodUtil.legacyParse(text);
    }

    public static String parse(String text, Player player) {
        return parse(withPAPI(text, player));
    }

    public static String parse(Player player, String text) {
        return parse(withPAPI(text, player));
    }

    public static String withPAPI(String text, Player player) {
        if (text == null) {
            return "";
        }
        if (text.matches("[0-9]+")) {
            return text;
        }
        if (text.contains("%") && CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        return parseBuiltInPlaceholder(text, player);
    }

    public static String parseBuiltInPlaceholder(String text, Player player) {
        text = text.replace("{discount_", "{conditional_");
        if (player != null) {
            Pattern pattern1 = Pattern.compile("\\{conditional_(.*?)}");
            Matcher matcher1 = pattern1.matcher(text);
            while (matcher1.find()) {
                String discount = matcher1.group(1);
                text = text.replace("{conditional_" + discount + "}",
                        String.valueOf(ObjectConditionalPlaceholder.getNowValue(discount, player)));
            }
        }
        Pattern pattern2 = Pattern.compile("\\{random_(.*?)}");
        Matcher matcher2 = pattern2.matcher(text);
        while (matcher2.find()) {
            String placeholder = matcher2.group(1);
            String[] tempVal1 = placeholder.split(";;");
            int number = 1;
            if (tempVal1.length > 1) {
                placeholder = tempVal1[0];
                number = Integer.parseInt(tempVal1[1]);
            }
            text = text.replace("{random_" + matcher2.group(1) + "}",
                    ObjectRandomPlaceholder.getNowValue(placeholder, number));
        }
        Pattern pattern3 = Pattern.compile("\\{random-times_(.*?)}");
        Matcher matcher3 = pattern3.matcher(text);
        while (matcher3.find()) {
            String placeholder = matcher3.group(1);
            text = text.replace("{random-times_" + placeholder + "}",
                    ObjectRandomPlaceholder.getRefreshDoneTime(placeholder));
        }
        Pattern pattern4 = Pattern.compile("\\{compare_([\\d.]+)_([\\d.]+)}");
        Matcher matcher4 = pattern4.matcher(text);
        while (matcher4.find()) {
            String compareNumber = matcher4.group(1);
            String baseNumber = matcher4.group(2);
            text = text.replace("{compare_" + compareNumber + "_" + baseNumber + "}",
                    StaticPlaceholder.getCompareValue(new BigDecimal(baseNumber), new BigDecimal(compareNumber)));
        }
        Pattern pattern5 = Pattern.compile("\\{math_(.*?)}");
        Matcher matcher5 = pattern5.matcher(text);
        while (matcher5.find()) {
            String placeholder = matcher5.group(1);
            text = text.replace("{math_" + placeholder + "}",
                    MathUtil.doCalculate(placeholder, ConfigManager.configManager.getInt("placeholder.math.scale", 0)).toString());
        }
        Pattern pattern6 = Pattern.compile("\\{random-next_(.*?)}");
        Matcher matcher6 = pattern6.matcher(text);
        while (matcher6.find()) {
            String placeholder = matcher6.group(1);
            text = text.replace("{random-next_" + placeholder + "}",
                    ObjectRandomPlaceholder.getNextTime(placeholder));
        }
        return text;
    }
}
