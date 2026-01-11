package cn.superiormc.ultimateshop.paper.utils;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.superiormc.ultimateshop.utils.TextUtil.GRADIENT_PATTERN;
import static cn.superiormc.ultimateshop.utils.TextUtil.LEGACY_COLOR_PATTERN;
import static cn.superiormc.ultimateshop.utils.TextUtil.SINGLE_HEX_PATTERN;

public class PaperTextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    // Regex patterns
    private static final Pattern SECTION_HEX_PATTERN = Pattern.compile("§x(§[A-Fa-f0-9]){6}");

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

        // Step 1: §x hex colors (e.g. §x§1§2§3§4§5§6 → <#123456>)
        Matcher sectionHexMatcher = SECTION_HEX_PATTERN.matcher(input);
        StringBuilder sectionHexBuffer = new StringBuilder();
        while (sectionHexMatcher.find()) {
            String match = sectionHexMatcher.group();
            // Extract the 6 hex digits from the §x§1§2... form
            String hex = match.replace("§x", "").replace("§", "");
            sectionHexMatcher.appendReplacement(sectionHexBuffer, "<#" + hex + ">");
        }
        sectionHexMatcher.appendTail(sectionHexBuffer);

        String afterSectionHex = sectionHexBuffer.toString();

        // Step 2: Gradients
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(afterSectionHex);
        StringBuilder gradientBuffer = new StringBuilder();
        while (gradientMatcher.find() && gradientMatcher.groupCount() >= 3) {
            String start = gradientMatcher.group(1);
            String text = gradientMatcher.group(2);
            String end = gradientMatcher.group(3);
            String replacement = String.format("<gradient:#%s:#%s>%s</gradient>", start, end, text);
            gradientMatcher.appendReplacement(gradientBuffer, Matcher.quoteReplacement(replacement));
        }
        gradientMatcher.appendTail(gradientBuffer);

        // Step 3: &#hex to MiniMessage
        Matcher hexMatcher = SINGLE_HEX_PATTERN.matcher(gradientBuffer.toString());
        StringBuilder hexBuffer = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            hexMatcher.appendReplacement(hexBuffer, "<#" + hex + ">");
        }
        hexMatcher.appendTail(hexBuffer);

        // Step 4: Legacy & § codes
        Matcher legacyMatcher = LEGACY_COLOR_PATTERN.matcher(hexBuffer.toString());
        StringBuilder finalBuffer = new StringBuilder();
        while (legacyMatcher.find()) {
            char code = Character.toLowerCase(legacyMatcher.group(1).charAt(0));
            String tag = LEGACY_COLORS.getOrDefault(code, "");
            legacyMatcher.appendReplacement(finalBuffer, "<" + tag + ">");
        }
        legacyMatcher.appendTail(finalBuffer);

        return finalBuffer.toString().replace("\n", "<newline>");
    }


    public static Component modernParse(String text) {
        try {
            if (containsLegacyCodes(text)) {
                text = convertToMiniMessage(text);
            }
            return MINI_MESSAGE.deserialize(text);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not parse text: " + text);
            // 如果 MiniMessage 格式失败，退回兼容旧的 & 颜色代码
            return LEGACY_SERIALIZER.deserialize(TextUtil.colorize(text).replace('§', '&'));
        }
    }

    public static Component modernParse(String text, Player player) {
        text = TextUtil.withPAPI(text, player);
        return modernParse(text);
    }

    public static String changeToString(Component component) {
        if (component == null) {
            return null;
        }
        return MINI_MESSAGE.serialize(component);
    }

    public static List<String> changeToString(List<Component> component) {
        if (component == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (Component singleComponent : component) {
            result.add(changeToString(singleComponent));
        }
        return result;
    }

    public static boolean containsLegacyCodes(String text) {
        return LEGACY_COLOR_PATTERN.matcher(text).find() ||
                SINGLE_HEX_PATTERN.matcher(text).find() ||
                GRADIENT_PATTERN.matcher(text).find() ||
                SECTION_HEX_PATTERN.matcher(text).find()
                || text.contains("§");
    }

}
