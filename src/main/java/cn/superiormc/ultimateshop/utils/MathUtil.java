package cn.superiormc.ultimateshop.utils;


import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import redempt.crunch.Crunch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

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
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your number option value " +
                    mathStr + " can not be read as a number, maybe" +
                    "set math.enabled to false in config.yml maybe solve this problem!");
            return BigDecimal.ZERO;
        }
    }

    public static List<BigDecimal> transferMathList(List<String> stringList) {
        List<BigDecimal> tempVal1 = new ArrayList<>();
        for (String tempVa2 : stringList) {
            try {
                tempVal1.add(new BigDecimal(tempVa2));
            } catch (NumberFormatException ep) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your number option value " +
                        tempVa2 + " can not be read as a number, maybe" +
                        "set math.enabled to false in config.yml maybe solve this problem!");
                tempVal1.add(new BigDecimal(-1));
            }
        }
        return tempVal1;
    }
}
