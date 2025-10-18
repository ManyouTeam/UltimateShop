package cn.superiormc.ultimateshop.utils;


import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import redempt.crunch.Crunch;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static int scale = ConfigManager.configManager.getInt("math.scale", 2);

    public static BigDecimal doCalculate(String mathStr) {
        return doCalculate(mathStr, scale);
    }

    public static BigDecimal doCalculate(String mathStr, int scale) {
        try {
            if (!ConfigManager.configManager.getBoolean("math.enabled")) {
                return new BigDecimal(mathStr);
            }
            return BigDecimal.valueOf(Crunch.evaluateExpression(mathStr)).setScale(scale, RoundingMode.HALF_UP);
        }
        catch (NumberFormatException ep) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                ep.printStackTrace();
            }
            ErrorManager.errorManager.sendErrorMessage("§cError: Your number option value " +
                    mathStr + " can not be read as a number, maybe" +
                    "set math.enabled to false in config.yml maybe solve this problem!");
            return BigDecimal.ZERO;
        }
    }
}
