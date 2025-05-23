package cn.superiormc.ultimateshop.paper.utils;

import cn.superiormc.ultimateshop.libs.easyplugin.ColorParser;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperTextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    // Regex patterns
    private static final Pattern SINGLE_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("&<#([A-Fa-f0-9]{6})>(.*?)&<#([A-Fa-f0-9]{6})>");
    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("&([0-9a-frlomn])", Pattern.CASE_INSENSITIVE);

    // Legacy color map
    private static final Map<Character, String> LEGACY_COLORS = new HashMap<>();

    static {
        LEGACY_COLORS.put('0', "black");
        LEGACY_COLORS.put('1', "dark_blue");
        LEGACY_COLORS.put('2', "dark_green");
        LEGACY_COLORS.put('3', "dark_aqua");
        LEGACY_COLORS.put('4', "dark_red");
        LEGACY_COLORS.put('5', "dark_purple");
        LEGACY_COLORS.put('6', "gold");
        LEGACY_COLORS.put('7', "gray");
        LEGACY_COLORS.put('8', "dark_gray");
        LEGACY_COLORS.put('9', "blue");
        LEGACY_COLORS.put('a', "green");
        LEGACY_COLORS.put('b', "aqua");
        LEGACY_COLORS.put('c', "red");
        LEGACY_COLORS.put('d', "light_purple");
        LEGACY_COLORS.put('e', "yellow");
        LEGACY_COLORS.put('f', "white");
        LEGACY_COLORS.put('r', "reset");
        LEGACY_COLORS.put('l', "bold");
        LEGACY_COLORS.put('o', "italic");
        LEGACY_COLORS.put('n', "underlined");
        LEGACY_COLORS.put('m', "strikethrough");
    }

    public static String convertToMiniMessage(String input) {
        // First: handle gradient
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(input);
        StringBuffer gradientBuffer = new StringBuffer();

        while (gradientMatcher.find()) {
            String startColor = gradientMatcher.group(1);
            String text = gradientMatcher.group(2);
            String endColor = gradientMatcher.group(3);
            String replacement = String.format("<gradient:#%s:#%s>%s</gradient>", startColor, endColor, text);
            gradientMatcher.appendReplacement(gradientBuffer, Matcher.quoteReplacement(replacement));
        }
        gradientMatcher.appendTail(gradientBuffer);

        String afterGradient = gradientBuffer.toString();

        // Second: handle single hex color
        Matcher hexMatcher = SINGLE_HEX_PATTERN.matcher(afterGradient);
        StringBuffer hexBuffer = new StringBuffer();

        while (hexMatcher.find()) {
            String color = hexMatcher.group(1);
            hexMatcher.appendReplacement(hexBuffer, Matcher.quoteReplacement("<#" + color + ">"));
        }
        hexMatcher.appendTail(hexBuffer);

        String afterHex = hexBuffer.toString();

        // Third: handle legacy color codes
        Matcher legacyMatcher = LEGACY_COLOR_PATTERN.matcher(afterHex);
        StringBuffer finalBuffer = new StringBuffer();

        while (legacyMatcher.find()) {
            char code = Character.toLowerCase(legacyMatcher.group(1).charAt(0));
            String miniTag = LEGACY_COLORS.getOrDefault(code, "");
            legacyMatcher.appendReplacement(finalBuffer, Matcher.quoteReplacement("<" + miniTag + ">"));
        }
        legacyMatcher.appendTail(finalBuffer);

        return finalBuffer.toString().replace("\n", "<newline>");
    }


    public static Component modernParse(String text) {
        try {
            if ((!text.contains("<") && !text.contains(">")) || text.startsWith("<!i>")) {
                text = convertToMiniMessage(text);
            }
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not parse text: " + text);
            // 如果 MiniMessage 格式失败，退回兼容旧的 & 颜色代码
            return LEGACY_SERIALIZER.deserialize(ColorParser.parse(text).replace('§', '&'));
        }
    }

    public static Component modernParse(String text, Player player) {
        text = TextUtil.withPAPI(text, player);
        return modernParse(text);
    }

}
