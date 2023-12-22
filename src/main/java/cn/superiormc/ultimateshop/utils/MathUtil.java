package cn.superiormc.ultimateshop.utils;


import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.Bukkit;
import redempt.crunch.Crunch;

import java.text.DecimalFormat;

public class MathUtil {

    public static double doCalculate(String mathStr) {
        try {
            if (!ConfigManager.configManager.getBoolean("math.enabled")) {
                return Double.parseDouble(mathStr);
            }
            return Double.parseDouble(String.format("%.2f", Crunch.evaluateExpression(mathStr)));
        }
        catch (NumberFormatException ep) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                ep.printStackTrace();
            }
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your number option value " +
                    mathStr + " can not be read as a number, maybe" +
                    "set math.enabled to false in config.yml maybe solve this problem!");
            return 0D;
        }
    }
}
