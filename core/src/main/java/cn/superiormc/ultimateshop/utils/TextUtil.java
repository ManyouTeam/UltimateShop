package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.MinecraftResourceManager;
import cn.superiormc.ultimateshop.methods.StaticPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectConditionalPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectCustomPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static final Pattern SINGLE_HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static final Pattern GRADIENT_PATTERN = Pattern.compile("&<#([A-Fa-f0-9]{6})>(.*?)&<#([A-Fa-f0-9]{6})>");

    public static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("[&§]([0-9a-frlomn])", Pattern.CASE_INSENSITIVE);

    private static final Pattern WRAP_COLOR_TOKEN = Pattern.compile(
        "§[xX](?:§[0-9A-Fa-f]){6}|[&§]#[0-9A-Fa-f]{6}|[&§][0-9A-Fa-fK-Ok-oRr]");

    private static final Pattern VANILLA_TRANSLATION = Pattern.compile(
        "<(?:lang|translate|translatable):['\"]?((?:item|block)\\.minecraft\\.[a-z0-9_.]+)['\"]?>");

    public static int getGlyphWidth(int code) {
        if (code == '\n') {
            return 0;
        }
        if (code == ' ') {
            return 4;
        }
        if (code < 128 && "!.,:;i|'".indexOf(code) >= 0) {
            return 2;
        }
        if (code == '`' || code == 'l') {
            return 3;
        }
        if (code < 128 && "\"()*I[]t{}".indexOf(code) >= 0) {
            return 4;
        }
        if (code < 128 && "<>fk".indexOf(code) >= 0) {
            return 5;
        }
        if (code == '@' || code == '~') {
            return 7;
        }
        return code < 256 ? 6 : 9;
    }

    public static boolean isEnglishWordCharacter(int value) {
        return value >= 'A' && value <= 'Z'
            || value >= 'a' && value <= 'z'
            || value >= '0' && value <= '9'
            || value == '\''
            || value == '\u2019'
            || value == '-'
            || value == '_';
    }

    public static int getTextWidth(String value) {
        int width = 0;
        int maximum = 0;
        boolean bold = false;
        Matcher matcher = WRAP_COLOR_TOKEN.matcher(value);
        for (int i = 0; i < value.length();) {
            if (matcher.region(i, value.length()).lookingAt()) {
                String token = matcher.group();
                bold = isWrapFormatToken(token)
                    && (bold || Character.toLowerCase(token.charAt(1)) == 'l');
                i = matcher.end();
                continue;
            }
            int code = value.codePointAt(i);
            i += Character.charCount(code);
            if (code == '\n') {
                maximum = Math.max(maximum, width);
                width = 0;
            } else {
                width += getGlyphWidth(code) + (bold && code != ' ' ? 1 : 0);
            }
        }
        return Math.max(maximum, width);
    }

    // Wrap legacy text by visible code points; nonpositive limits leave the input unchanged.
    public static List<String> wrapKeepColor(String text, int maxLength) {
        return wrapText(text, maxLength, false);
    }

    // Wrap legacy text by estimated default-font pixels, including the active bold style.
    public static List<String> wrapByWidth(String text, int maxWidth) {
        return wrapText(text, maxWidth, true);
    }

    private static List<String> wrapText(String text, int maximum, boolean pixels) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty() || maximum <= 0) {
            result.add(text == null ? "" : text);
            return result;
        }
        if (GRADIENT_PATTERN.matcher(text).find()) {
            text = applyGradients(text, CommonUtil.getMajorVersion(16));
        }
        text = text.replace("\r\n", "\n").replace('\r', '\n');

        // Keep raw offsets and the style before each code point so trimmed spaces cannot lose formatting.
        int[] offsets = new int[text.length() + 1];
        int[] values = new int[text.length()];
        int[] widths = new int[text.length()];
        String[] prefixes = new String[text.length()];
        String style = "";
        String styleBeforeRaw = "";
        boolean bold = false;
        int rawStart = 0;
        int count = 0;
        Matcher matcher = WRAP_COLOR_TOKEN.matcher(text);
        for (int index = 0; index < text.length();) {
            if (matcher.region(index, text.length()).lookingAt()) {
                String token = matcher.group();
                if (isWrapFormatToken(token)) {
                    if (!style.toLowerCase(Locale.ROOT).contains(token.toLowerCase(Locale.ROOT))) {
                        style += token;
                    }
                    bold = bold || Character.toLowerCase(token.charAt(1)) == 'l';
                } else {
                    style = token;
                    bold = false;
                }
                index = matcher.end();
                continue;
            }
            int code = text.codePointAt(index);
            offsets[count] = rawStart;
            values[count] = code;
            widths[count] = pixels ? getGlyphWidth(code) + (bold && code != ' ' ? 1 : 0) : 1;
            prefixes[count] = styleBeforeRaw;
            count++;
            index += Character.charCount(code);
            rawStart = index;
            styleBeforeRaw = style;
        }
        offsets[count] = text.length();
        if (count == 0) {
            result.add(text);
            return result;
        }

        int paragraphStart = 0;
        for (int end = 0; end <= count; end++) {
            if (end < count && values[end] != '\n') {
                continue;
            }
            if (paragraphStart == end) {
                result.add("");
            }
            int start = paragraphStart;
            while (start < end) {
                int lineEnd = findWrapEnd(values, widths, start, end, maximum);
                int visibleEnd = lineEnd;
                if (lineEnd < end) {
                    while (visibleEnd > start && Character.isWhitespace(values[visibleEnd - 1])) {
                        visibleEnd--;
                    }
                }
                String prefix = start == 0 ? "" : prefixes[start];
                result.add(prefix + text.substring(offsets[start], offsets[visibleEnd]));
                start = lineEnd;
                if (lineEnd < end) {
                    while (start < end && Character.isWhitespace(values[start])) {
                        start++;
                    }
                }
            }
            paragraphStart = end + 1;
        }
        return result;
    }

    private static int findWrapEnd(int[] values, int[] widths, int start, int end, int maximum) {
        long used = 0;
        int index = start;
        while (index < end) {
            int tokenEnd = index + 1;
            long tokenWidth = widths[index];
            if (isEnglishWordCharacter(values[index])) {
                while (tokenEnd < end && isEnglishWordCharacter(values[tokenEnd])) {
                    tokenWidth += widths[tokenEnd];
                    tokenEnd++;
                }
            }
            if (index > start && used + tokenWidth > maximum) {
                return index;
            }
            used += tokenWidth;
            index = tokenEnd;
        }
        return end;
    }

    private static boolean isWrapFormatToken(String token) {
        if (token.length() != 2) {
            return false;
        }
        char code = Character.toLowerCase(token.charAt(1));
        return code >= 'k' && code <= 'o';
    }

    public static String clear(String text) {
        text = SINGLE_HEX_PATTERN.matcher(text).replaceAll("");
        text = GRADIENT_PATTERN.matcher(text).replaceAll("");
        text = LEGACY_COLOR_PATTERN.matcher(text).replaceAll("");
        return text;
    }

    public static String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String normalized = TextUtil.clear(text);
        if (normalized == null) {
            return "";
        }
        return normalized.trim().toLowerCase(Locale.ROOT);
    }

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

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(gradientText));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String applyGradient(String startHex, String endHex, String text) {
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);

        int[] characters = text.codePoints().toArray();
        int length = characters.length;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("%02x%02x%02x", red, green, blue);
            builder.append("§x").append(toMinecraftHex(hex)).appendCodePoint(characters[i]);
        }

        return builder.toString();
    }

    private static String applyLegacyGradient(String startHex, String endHex, String text) {
        Color start = Color.decode("#" + startHex);
        Color end = Color.decode("#" + endHex);

        int[] characters = text.codePoints().toArray();
        int length = characters.length;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            String hex = String.format("%02x%02x%02x", red, green, blue);
            char legacyColor = getClosestLegacyColor(hex);
            builder.append("§").append(legacyColor).appendCodePoint(characters[i]);
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

    public static String parseLocalizedText(Player player, String text) {
        return parse(localizeVanillaNames(withPAPI(text, player), player));
    }

    public static String localizeVanillaNames(String text, Player player) {
        MinecraftResourceManager resources = MinecraftResourceManager.minecraftResourceManager;
        if (text == null || player == null || resources == null) {
            return text;
        }
        Matcher translations = VANILLA_TRANSLATION.matcher(text);
        StringBuilder localized = new StringBuilder();
        while (translations.find()) {
            String name = resources.getLocalizedName(translations.group(1), player.getLocale());
            translations.appendReplacement(
                localized,
                Matcher.quoteReplacement(name == null ? translations.group() : name));
        }
        translations.appendTail(localized);
        return localized.toString();
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

    public static Pattern pattern1 = Pattern.compile("\\{conditional_(.*?)}");
    public static Pattern pattern2 = Pattern.compile("\\{random_(.*?)}");
    public static Pattern pattern3 = Pattern.compile("\\{random-times_(.*?)}");
    public static Pattern pattern4 = Pattern.compile("\\{compare_([\\d.]+)_([\\d.]+)}");
    public static Pattern pattern5 = Pattern.compile("\\{math_(.*?)}");
    public static Pattern pattern6 = Pattern.compile("\\{random-next_(.*?)}");
    public static Pattern pattern7 = Pattern.compile("\\{cron_\"([^\"]+)\"\\}");
    public static Pattern pattern8 = Pattern.compile("\\{custom_(.*?)}");
    public static Pattern pattern9 = Pattern.compile("\\{random-last-reset_(.*?)}");

    public static String parseBuiltInPlaceholder(String text, Player player) {
        text = text.replace("{discount_", "{conditional_");
        if (player != null) {
            Matcher matcher1 = pattern1.matcher(text);
            while (matcher1.find()) {
                String discount = matcher1.group(1);
                text = text.replace("{conditional_" + discount + "}",
                    String.valueOf(ObjectConditionalPlaceholder.getNowValue(discount, player)));
            }
        }
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
                ObjectRandomPlaceholder.getNowValue(player, placeholder, number));
        }
        Matcher matcher3 = pattern3.matcher(text);
        while (matcher3.find()) {
            String placeholder = matcher3.group(1);
            text = text.replace("{random-times_" + placeholder + "}",
                ObjectRandomPlaceholder.getRefreshDoneTime(player, placeholder));
        }
        Matcher matcher4 = pattern4.matcher(text);
        while (matcher4.find()) {
            String compareNumber = matcher4.group(1);
            String baseNumber = matcher4.group(2);
            text = text.replace("{compare_" + compareNumber + "_" + baseNumber + "}",
                StaticPlaceholder.getCompareValue(player, new BigDecimal(baseNumber), new BigDecimal(compareNumber)));
        }
        Matcher matcher5 = pattern5.matcher(text);
        while (matcher5.find()) {
            String placeholder = matcher5.group(1);
            text = text.replace("{math_" + placeholder + "}",
                MathUtil.doCalculate(placeholder, ConfigManager.configManager.getInt("placeholder.math.scale", 0)).toString());
        }
        Matcher matcher6 = pattern6.matcher(text);
        while (matcher6.find()) {
            String placeholder = matcher6.group(1);
            text = text.replace("{random-next_" + placeholder + "}",
                ObjectRandomPlaceholder.getNextTime(player, placeholder));
        }
        Matcher matcher7 = pattern7.matcher(text);
        while (matcher7.find()) {
            String cronExpression = matcher7.group(1);

            // 使用 Quartz 格式解析
            CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
            Cron cron = parser.parse(cronExpression);
            cron.validate(); // 检查合法性

            // 当前时间
            ZonedDateTime now = ZonedDateTime.now();

            // 获取下一次执行时间
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(now);

            String time = "";

            if (nextExecution.isPresent()) {
                time = CommonUtil.timeToString(nextExecution.get().toLocalDateTime(), ConfigManager.configManager.getStringWithLang(player, "placeholder.cron.format"));
            }
            text = text.replace("{cron_\"" + cronExpression + "\"}", time);
        }
        Matcher matcher8 = pattern8.matcher(text);
        while (matcher8.find()) {
            String placeholder = matcher8.group(1);
            text = text.replace("{custom_" + placeholder + "}",
                ObjectCustomPlaceholder.getNowValue(player, placeholder));
        }
        Matcher matcher9 = pattern9.matcher(text);
        while (matcher9.find()) {
            String placeholder = matcher9.group(1);
            text = text.replace("{random-last-reset_" + placeholder + "}",
                ObjectRandomPlaceholder.getLastResetTime(player, placeholder));
        }
        text = CommonUtil.parseLang(player, text);
        return text;
    }

    public static void sendMessage(Player player, String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return;
        }

        if (UltimateShop.methodUtil == null) {
            Bukkit.getConsoleSender().sendMessage(rawText);
            return;
        }

        if (!rawText.contains("[") || UltimateShop.freeVersion) {
            UltimateShop.methodUtil.sendChat(player, rawText);
            return;
        }

        boolean sentAny = false;

        // message
        for (String msg : parseSimpleTag(rawText, "message")) {
            UltimateShop.methodUtil.sendChat(player, msg);
            sentAny = true;
        }

        // title
        for (TagResult tag : parseArgTag(rawText, "title")) {
            TitleData data = parseTitle(tag);
            UltimateShop.methodUtil.sendTitle(
                player,
                data.title,
                data.subTitle,
                data.fadeIn,
                data.stay,
                data.fadeOut
                );
            sentAny = true;
        }

        // actionbar
        for (String msg : parseSimpleTag(rawText, "actionbar")) {
            UltimateShop.methodUtil.sendActionBar(player, msg);
            sentAny = true;
        }

        // bossbar
        for (TagResult tag : parseArgTag(rawText, "bossbar")) {
            BossBarData data = parseBossBar(tag);
            UltimateShop.methodUtil.sendBossBar(
                player,
                data.title,
                data.progress,
                data.color,
                data.style
                );
            sentAny = true;
        }

        // sound
        for (TagResult tag : parseArgTag(rawText, "sound")) {
            SoundData data = parseSound(tag);
            if (data.sound != null) {
                player.playSound(player.getLocation(), data.sound, data.volume, data.pitch);
                sentAny = true;
            }
        }

        // 兜底
        if (!sentAny) {
            UltimateShop.methodUtil.sendChat(player, rawText);
        }
    }

    /* ================= 标签解析 ================= */

    private static List<String> parseSimpleTag(String text, String tag) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(
            "\\[" + tag + "]([\\s\\S]*?)\\[/" + tag + "]",
            Pattern.CASE_INSENSITIVE
            );
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(m.group(1).trim());
        }
        return list;
    }

    private static List<TagResult> parseArgTag(String text, String tag) {
        List<TagResult> list = new ArrayList<>();
        Pattern p = Pattern.compile(
            "\\[" + tag + "(?:=([^\\]]+))?]([\\s\\S]*?)\\[/" + tag + "]",
            Pattern.CASE_INSENSITIVE
            );
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(new TagResult(
                m.group(1),
                m.group(2).trim()
                ));
        }
        return list;
    }

    private static TitleData parseTitle(TagResult tag) {
        int fadeIn = 10;
        int stay = 70;
        int fadeOut = 20;

        if (tag.args != null) {
            String[] t = tag.args.split(",");
            if (t.length == 3) {
                fadeIn = parseInt(t[0], fadeIn);
                stay = parseInt(t[1], stay);
                fadeOut = parseInt(t[2], fadeOut);
            }
        }

        String[] parts = tag.content.split(";;", 2);
        String title = parts[0];
        String sub = parts.length > 1 ? parts[1] : "";

        return new TitleData(title, sub, fadeIn, stay, fadeOut);
    }

    /* ================= BossBar 解析 ================= */

    private static BossBarData parseBossBar(TagResult tag) {
        String color = "WHITE";
        String style = "SOLID";
        float progress = 1.0f;

        if (tag.args != null) {
            String[] a = tag.args.split(",");
            if (a.length > 0) {
                color = a[0];
            }
            if (a.length > 1) {
                style = a[1];
            }
            if (a.length > 2) {
                progress = parseFloat(a[2], progress);
            }
        }

        return new BossBarData(tag.content, progress, color, style);
    }

    /* ================= 工具 ================= */

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static float parseFloat(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    /* ================= 内部数据类 ================= */

    private static class TagResult {
        final String args;
        final String content;

        TagResult(String args, String content) {
            this.args = args;
            this.content = content;
        }
    }

    private static class TitleData {
        final String title;
        final String subTitle;
        final int fadeIn, stay, fadeOut;

        TitleData(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
            this.title = title;
            this.subTitle = subTitle;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }
    }

    private static class BossBarData {
        final String title;
        final float progress;
        final String color;
        final String style;

        BossBarData(String title, float progress, String color, String style) {
            this.title = title;
            this.progress = progress;
            this.color = color;
            this.style = style;
        }
    }

    private static SoundData parseSound(TagResult tag) {
        Sound sound = null;
        float volume = 1f;
        float pitch = 1f;

        if (tag.args != null) {
            String[] args = tag.args.split(",");
            if (args.length > 0) {
                try {
                    sound = Sound.valueOf(args[0].trim().toUpperCase());
                } catch (Exception e) {
                    sound = null; // 无效音效忽略
                }
            }
            if (args.length > 1) {
                volume = parseFloat(args[1], 1f);
            }
            if (args.length > 2) {
                pitch = parseFloat(args[2], 1f);
            }
        }

        return new SoundData(sound, volume, pitch);
    }

    private static class SoundData {
        final Sound sound;
        final float volume;
        final float pitch;

        SoundData(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }
    }
}
