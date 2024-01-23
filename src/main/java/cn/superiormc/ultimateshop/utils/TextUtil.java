package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.libs.easyplugin.ColorParser;
import cn.superiormc.ultimateshop.methods.GetDiscountValue;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static String parse(String text) {
        if (text == null)
            return "";
        return ColorParser.parse(text);
    }

    public static String parse(String text, Player player) {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, parse(text));
        }
        else {
            return parse(text);
        }
    }

    public static String withPAPI(String text, Player player) {
        if (text.matches("[0-9]+")) {
            return text;
        }
        Pattern pattern = Pattern.compile("\\{discount_(.*?)\\}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String discount = matcher.group(1);
            text = text.replace("{discount_" + discount + "}",
                    String.valueOf(GetDiscountValue.getDiscountLimits(discount, player)));
        }
        if (text.contains("%") && CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        else {
            return text;
        }
    }

    public static List<String> getListWithColor(List<String> inList) {
        List<String> resultList = new ArrayList<>();
        for (String s : inList) {
            resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }

    public static List<String> getListWithColorAndPAPI(List<String> inList, Player player) {
        List<String> resultList = new ArrayList<>();
        for (String s : inList) {
            if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
                PlaceholderAPI.setPlaceholders(player, s);
            }
            resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }
}
