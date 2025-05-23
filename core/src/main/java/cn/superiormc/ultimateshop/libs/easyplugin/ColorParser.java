package cn.superiormc.ultimateshop.libs.easyplugin;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ColorParser {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#([\\da-fA-F]{6})");
    public static final Pattern GRADIENT_PATTERN = Pattern.compile("&<&?#([\\da-fA-F]{6})>");
    public static final Pattern COLOR_PATTERN = Pattern.compile("([&§][0-9a-fA-FrRxX])+"); // 会影响颜色的代码
    public static final Pattern FORMAT_PATTERN = Pattern.compile("([&§][0-9a-fA-Fk-oK-OrRxX])+"); // MC可用的格式化代码

    /**
     * 清除一条消息中的全部颜色代码 (包括RGB颜色代码与渐变颜色代码)
     *
     * @param text 源消息内容
     * @return 清理颜色后的消息
     */
    public static String clear(String text) {
        text = HEX_PATTERN.matcher(text).replaceAll("");
        text = GRADIENT_PATTERN.matcher(text).replaceAll("");
        text = COLOR_PATTERN.matcher(text).replaceAll("");
        return text;
    }

    /**
     * 对一条消息进行颜色解析，包括普通颜色代码、RGB颜色代码与RBG渐变代码。
     *
     * @param text 源消息内容
     * @return 解析后的消息
     */
    public static String parse(String text) {
        return parseBaseColor(parseGradientColor(parseHexColor(text)));
    }

    /**
     * 解析消息中的基本颜色代码格式 {@code &+颜色代码 }，如 {@literal &c} 、{@literal &a}
     *
     * @param text 消息内容
     * @return RGB处理后的消息
     * @see net.md_5.bungee.api.ChatColor
     */
    public static String parseBaseColor(final String text) {
        return text.replaceAll("&", "§").replace("§§", "&");
    }

    /**
     * 解析消息中的RGB颜色代码(版本需要≥1.14) 格式 {@code &(#XXXXXX) }，如 {@literal &(#aaaaaa)}
     *
     * @param text 消息内容
     * @return RGB处理后的消息
     */
    public static String parseHexColor(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            text = matcher.replaceFirst(buildHexColor(matcher.group(1)).toLowerCase());
            matcher.reset(text);
        }
        return text;
    }

    /**
     * 对一条消息进行RGB渐变处理(版本需要≥1.14)，格式 {@code &<#XXXXXX>FOOBAR&<#XXXXXX> }。
     *
     * @param text 消息内容
     * @return RGB渐变处理后的消息
     */
    public static String parseGradientColor(String text) {
        List<String> colors = new ArrayList<>();

        Matcher matcher = ColorParser.GRADIENT_PATTERN.matcher(text);
        while (matcher.find()) colors.add(matcher.group(1));

        if (colors.isEmpty()) return text; // 无渐变颜色，直接跳出

        String[] parts = ColorParser.GRADIENT_PATTERN.split(text);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String startHex = i - 1 >= 0 && colors.size() > i - 1 ? colors.get(i - 1) : null; // 本条消息的起始颜色
            String endHex = colors.size() > i ? colors.get(i) : null; // 本条消息的结束颜色
            builder.append(gradientText(parts[i], startHex, endHex));
        }

        return builder.toString();
    }

    public static String gradientText(String text,
                                      Color startColor, Color endColor) {
        Objects.requireNonNull(text, "Text to be gradient should not be null!");
        if (startColor == null || endColor == null || text.isEmpty()) {
            // 起始颜色有任一为空，则不进行渐变上色。
            // 若有起始颜色，则代表其跟在某个渐变之后，应当添加"&r"阻断前面的渐变。
            return (startColor != null ? "&r" : "") + text;
        }

        // 用于记录消息中特殊格式的位置
        // 在渐变中，允许使用格式字符与颜色字符来改变其中某个字的颜色/格式，以支持更多形式内容。
        LinkedHashMap<Integer, String> extraFormats = new LinkedHashMap<>();
        Matcher matcher = ColorParser.FORMAT_PATTERN.matcher(text);
        while (matcher.find()) {
            extraFormats.put(matcher.start(), matcher.group());
            text = matcher.replaceFirst("");
            matcher.reset(text);
        }

        if (text.length() == 1) {
            // 当只有一个实际字符时，无需进行渐变计算，直接返回 中间颜色+起始格式(如果有)+消息 即可。
            return colorText(text, extraFormats.get(0), buildHexColor(mediumHex(startColor, endColor)));
        }

        String[] characters = text.split("");
        int step = characters.length; // 变换次数

        // 决定每种颜色变换的方向
        int rDirection = startColor.getRed() < endColor.getRed() ? 1 : -1;
        int gDirection = startColor.getGreen() < endColor.getGreen() ? 1 : -1;
        int bDirection = startColor.getBlue() < endColor.getBlue() ? 1 : -1;

        // 决定每种颜色每次变换的度
        int rStep = Math.abs(startColor.getRed() - endColor.getRed()) / (step - 1);
        int gStep = Math.abs(startColor.getGreen() - endColor.getGreen()) / (step - 1);
        int bStep = Math.abs(startColor.getBlue() - endColor.getBlue()) / (step - 1);

        String[] hexes = IntStream.range(0, step).mapToObj(i -> colorToHex(
                startColor.getRed() + rStep * i * rDirection,
                startColor.getGreen() + gStep * i * gDirection,
                startColor.getBlue() + bStep * i * bDirection
        )).toArray(String[]::new);

        return IntStream.range(0, characters.length)
                .mapToObj(i -> colorText(characters[i], extraFormats.get(i), buildHexColor(hexes[i])))
                .collect(Collectors.joining());
    }

    protected static String gradientText(String text, String startHex, String endHex) {
        return gradientText(text,
                startHex == null ? null : Color.decode("0x" + startHex),
                endHex == null ? null : Color.decode("0x" + endHex)
        );
    }

    private static String mediumHex(Color start, Color end) {
        return colorToHex(
                Math.abs(start.getRed() - end.getRed()) / 2,
                Math.abs(start.getGreen() - end.getGreen()) / 2,
                Math.abs(start.getBlue() - end.getBlue()) / 2
        );
    }

    private static String colorText(String message, String format, String color) {
        if (format != null && COLOR_PATTERN.matcher(format).find()) {
            // format中存在影响颜色的内容，则当前消息的颜色会被覆盖。
            // 为了减少最终消息的长度，故直接返回键入的FORMAT和对应消息的内容。
            return format + message;
        }
        return (color == null ? "" : color) + (format == null ? "" : parseBaseColor(format)) + message;
    }

    protected static String colorToHex(Color color) {
        return colorToHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    protected static String colorToHex(int r, int g, int b) {
        // 将R、G、B转换为16进制(若非2位则补0)输出
        return String.format("%02X%02X%02X", r, g, b);
    }

    protected static String buildHexColor(String hexCode) {
        return Arrays.stream(hexCode.split("")).map(s -> '§' + s)
                .collect(Collectors.joining("", '§' + "x", ""));
    }
}