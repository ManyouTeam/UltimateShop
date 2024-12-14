package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.libs.easyplugin.ColorParser;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.StaticPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
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
        if (player != null) {
            Pattern pattern1 = Pattern.compile("\\{discount_(.*?)\\}");
            Matcher matcher1 = pattern1.matcher(text);
            while (matcher1.find()) {
                String discount = matcher1.group(1);
                text = text.replace("{discount_" + discount + "}",
                        String.valueOf(StaticPlaceholder.getDiscountValue(discount, player)));
            }
        }
        Pattern pattern2 = Pattern.compile("\\{random_(.*?)\\}");
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
        Pattern pattern3 = Pattern.compile("\\{random-times_(.*?)\\}");
        Matcher matcher3 = pattern3.matcher(text);
        while (matcher3.find()) {
            String placeholder = matcher3.group(1);
            text = text.replace("{random-times_" + placeholder + "}",
                    ObjectRandomPlaceholder.getRefreshDoneTime(placeholder));
        }
        Pattern pattern4 = Pattern.compile("\\{compare_([\\d.]+)_([\\d.]+)\\}");
        Matcher matcher4 = pattern4.matcher(text);
        while (matcher4.find()) {
            String compareNumber = matcher4.group(1);
            String baseNumber = matcher4.group(2);
            text = text.replace("{compare_" + compareNumber + "_" + baseNumber + "}",
                    StaticPlaceholder.getCompareValue(new BigDecimal(baseNumber), new BigDecimal(compareNumber)));
        }
        Pattern pattern5 = Pattern.compile("\\{math_(.*?)\\}");
        Matcher matcher5 = pattern5.matcher(text);
        while (matcher5.find()) {
            String placeholder = matcher5.group(1);
            text = text.replace("{math_" + placeholder + "}",
                    MathUtil.doCalculate(placeholder, ConfigManager.configManager.getInt("placeholder.math.scale", 0)).toString());
        }
        if (text.contains("%") && CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            return text;
        }
    }
}
