package cn.superiormc.ultimateshop.utils;


import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.Bukkit;
import redempt.crunch.Crunch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

public class MathUtil {

    public static double doCalculate(String mathStr) {
        try {
            if (!ConfigManager.configManager.getBoolean("math.enabled")) {
                return Double.parseDouble(mathStr);
            }
            return Double.parseDouble(String.format("%.2f", Crunch.evaluateExpression(mathStr)));
        }
        catch (NumberFormatException | EmptyStackException ep) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your number option can not be read, maybe" +
                    " you forgot install PlaceholderAPI plugin in your server, or you didn't enable math.enabled option in config.yml!");
            return 0D;
        }
    }

    private static boolean compare(char curr, char stackTop) {
        if (stackTop == '(') {
            return true;
        }
        if (curr == '*' || curr == '/') {
            return stackTop == '+' || stackTop == '-';
        }
        return false;
    }
}
