package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.libs.easyplugin.ColorParser;
import cn.superiormc.ultimateshop.methods.GetDiscountValue;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.items.shbobjects.ObjectRandomPlaceholder;
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
        return parse(withPAPI(text, player));
    }

    public static String withPAPI(String text, Player player) {
        if (text.matches("[0-9]+")) {
            return text;
        }
        Pattern pattern1 = Pattern.compile("\\{discount_(.*?)\\}");
        Matcher matcher1 = pattern1.matcher(text);
        while (matcher1.find()) {
            String discount = matcher1.group(1);
            text = text.replace("{discount_" + discount + "}",
                    String.valueOf(GetDiscountValue.getDiscountLimits(discount, player)));
        }
        Pattern pattern2 = Pattern.compile("\\{random_(.*?)\\}");
        Matcher matcher2 = pattern2.matcher(text);
        while (matcher2.find()) {
            String placeholder = matcher2.group(1);
            text = text.replace("{random_" + placeholder + "}",
                    ObjectRandomPlaceholder.getNowValue(placeholder));
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
