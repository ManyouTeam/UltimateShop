package cn.superiormc.ultimateshop.utils;


import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import redempt.crunch.Crunch;

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
                    " you forgot install PlaceholderAPI plugin in your server, or you didn't enable math.enabled option in config.yml!");
            return 0D;
        }
    }
}
