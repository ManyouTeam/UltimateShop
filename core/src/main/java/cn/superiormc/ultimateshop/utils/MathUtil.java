package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import redempt.crunch.Crunch;
import redempt.crunch.functional.ExpressionEnv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static int scale;

    public static DecimalFormat integerFormat;

    public static DecimalFormat decimalFormat;

    public static void init() {
        scale =  ConfigManager.configManager.getInt("math.scale", 2);
        String integerPattern = ConfigManager.configManager.getString("number-display.format.integer", "#,##0");
        String decimalPattern = ConfigManager.configManager.getString("number-display.format.decimal", "#,##0.00##########");
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        integerFormat = new DecimalFormat(integerPattern, symbols);
        decimalFormat = new DecimalFormat(decimalPattern, symbols);
    }

    public static double multiply(double left, double right) {
        return BigDecimal.valueOf(left).multiply(BigDecimal.valueOf(right)).doubleValue();
    }

    public static String toDisplayString(double value) {
        return toDisplayString(BigDecimal.valueOf(value));
    }

    public static String toDisplayString(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        BigDecimal working = ConfigManager.configManager.getBoolean("number-display.strip-trailing-zeros.enabled") ? value.stripTrailingZeros() : value;
        if (!ConfigManager.configManager.getBoolean("number-display.format.enabled")) {
            return value.setScale(Math.max(0, value.scale()), RoundingMode.HALF_UP).toPlainString();
        }
        return working.scale() <= 0
                ? integerFormat.format(working)
                : decimalFormat.format(working);
    }

    public static BigDecimal doCalculate(String mathStr) {
        return doCalculate(mathStr, scale);
    }

    public static BigDecimal doCalculate(String mathStr, int scale) {
        try {
            if (!ConfigManager.configManager.getBoolean("math.enabled")) {
                return new BigDecimal(mathStr);
            }
            if (ConfigManager.configManager.getBoolean("math.enable-function")) {
                ExpressionEnv env = new ExpressionEnv();
                env.addFunction("sum", d -> {
                    double s = 0;
                    for (double v : d) {
                        s += v;
                    }
                    return s;
                });
                env.addFunction("max", d -> {
                    if (d.length == 0) {
                        return 0;
                    }
                    double m = d[0];
                    for (int i = 1; i < d.length; i++) {
                        m = Math.max(m, d[i]);
                    }
                    return m;
                });
                env.addFunction("min", d -> {
                    if (d.length == 0) return 0;
                    double m = d[0];
                    for (int i = 1; i < d.length; i++) {
                        m = Math.min(m, d[i]);
                    }
                    return m;
                });
                env.addFunction("avg", d -> {
                    if (d.length == 0) {
                        return 0;
                    }
                    double s = 0;
                    for (double v : d) {
                        s += v;
                    }
                    return s / d.length;
                });
                env.addFunction("random", d -> {
                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    // random()：0 到 1
                    if (d.length == 0) {
                        return random.nextDouble();
                    }

                    // random(max)：0 到 max
                    if (d.length == 1) {
                        double min = Math.min(0, d[0]);
                        double max = Math.max(0, d[0]);
                        return min == max ? min : random.nextDouble(min, max);
                    }

                    // random(min, max)
                    double min = Math.min(d[0], d[1]);
                    double max = Math.max(d[0], d[1]);
                    return min == max ? min : random.nextDouble(min, max);
                });
                return BigDecimal.valueOf(Crunch.compileExpression(mathStr, env).evaluate());
            }
            return BigDecimal.valueOf(Crunch.evaluateExpression(mathStr)).setScale(scale, RoundingMode.HALF_UP);
        }
        catch (Throwable throwable) {
            if (ConfigManager.configManager.getBoolean("debug")) {
                throwable.printStackTrace();
            }
            ErrorManager.errorManager.sendErrorMessage("§cError: Your number option value " +
                    mathStr + " can not be read as a number, maybe " +
                    "set math.enabled to false in config.yml maybe solve this problem!");
            return BigDecimal.ZERO;
        }
    }
}
