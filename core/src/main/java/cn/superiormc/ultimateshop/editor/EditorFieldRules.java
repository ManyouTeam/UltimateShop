package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class EditorFieldRules {

    private static final Pattern PLACEHOLDER = Pattern.compile("%[^%]+%");
    private static final Pattern COLOR = Pattern.compile("#(?:[a-fA-F0-9]{6}|[a-fA-F0-9]{8})");
    private static final Set<String> THEME_COLORS = Set.of("background", "sidebar", "card", "border", "accent",
            "text", "hover", "color", "corner-color", "shadow", "top", "left", "bottom");

    private EditorFieldRules() {
    }

    public static String resetMode(EditorTarget target, String path) {
        String modePath = path.substring(0, path.length() - "time".length()) + "mode";
        ConfigurationSection config = target.getConfig();
        if (target.getScope() == EditorScope.CONFIG) {
            return config.getString(modePath, "NEVER").toUpperCase(Locale.ROOT);
        }
        String legacyPath = modePath.replace("-times-", "-limits-");
        String mode;
        if (target.getScope() == EditorScope.SHOP && path.startsWith("items.")) {
            String key = EditorTypeResolver.lastSegment(modePath);
            mode = config.getString(legacyPath, config.getString("general-configs." + key.replace("-times-", "-limits-")));
            if (mode == null) {
                mode = config.getString(modePath, config.getString("general-configs." + key));
            }
        } else if (target.getScope() == EditorScope.SHARED_USE_TIMES) {
            mode = config.getString(modePath, config.getString(legacyPath));
        } else {
            mode = config.getString(legacyPath, config.getString(modePath));
        }
        if (mode == null) {
            mode = ConfigManager.configManager.getString("use-times.default-reset-mode", "NEVER");
        }
        return mode.toUpperCase(Locale.ROOT);
    }

    public static boolean validate(Player player, EditorTarget target, String path, Object value) {
        String requirement = invalidRequirement(target, path, value);
        if (requirement == null) {
            return true;
        }
        EditorLang.send(player, "editor.message.invalid-field", "&c{path} requires {requirement}.",
                "path", path, "requirement",
                EditorLang.text(player, "editor.validation." + requirement, requirement));
        return false;
    }

    private static String invalidRequirement(EditorTarget target, String path, Object value) {
        String key = EditorTypeResolver.lastSegment(path);
        String parent = EditorUtil.parentPath(path);
        if (isResetValue(target, path, key)) {
            return validateReset(target, path, value);
        }
        if ((target.getScope() == EditorScope.SHOP || target.getScope() == EditorScope.SHARED_USE_TIMES
                || target.getScope() == EditorScope.CONFIG)
                && key.endsWith("reset-time-format")) {
            try {
                if (value instanceof String text && !text.isBlank()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(text);
                    LocalDateTime.parse(formatter.format(LocalDateTime.of(2026, 1, 2, 13, 14, 15)), formatter);
                    return null;
                }
            } catch (IllegalArgumentException | DateTimeException exception) {
                return "date-format";
            }
            return "date-format";
        }
        if (target.getScope() == EditorScope.SHOP && path.matches("(?:items\\.[^.]+|general-configs)\\.default-(?:buy|sell)-amount")) {
            return integerInRange(value, 1, 9999) ? null : "trade-amount";
        }
        if (target.getScope().isShopOrMenu() && (parent.isEmpty() && target.getScope() != EditorScope.SHOP
                || parent.equals("settings.menu-settings"))) {
            if (key.equals("size")) {
                return integerInRange(value, 9, 54) && Integer.parseInt(value.toString()) % 9 == 0
                        ? null : "inventory-size";
            }
            if (key.equals("layout")) {
                return validateLayout(target, parent, value);
            }
        }
        if (EditorTypeResolver.isActionEntry(parent)) {
            return validateAction(target.getConfig().getString(parent + ".type", ""), key, value);
        }
        if ((EditorTypeResolver.isUiPath(path) || target.getScope() == EditorScope.UI_STYLE)
                && (path.startsWith("theme.") || path.contains(".theme.")) && THEME_COLORS.contains(key)) {
            return value instanceof Number || value instanceof String text && COLOR.matcher(text).matches()
                    ? null : "hex-color";
        }
        return null;
    }

    private static boolean isResetValue(EditorTarget target, String path, String key) {
        if (target.getScope() == EditorScope.CONFIG) {
            return path.equals("use-times.default-reset-time");
        }
        return (target.getScope() == EditorScope.SHOP || target.getScope() == EditorScope.SHARED_USE_TIMES)
                && (EditorTypeResolver.isResetTimeKey(key) || key.matches("(buy|sell)-limits-reset-time"));
    }

    private static String validateReset(EditorTarget target, String path, Object value) {
        String mode = resetMode(target, path);
        if (mode.equals("NEVER")) {
            return null;
        }
        if (!(value instanceof String text)) {
            return "reset-value";
        }
        if (mode.equals("RANDOM_PLACEHOLDER")) {
            return EditorScope.RANDOM_PLACEHOLDER.listIds().contains(text) ? null : "random-placeholder";
        }
        if (mode.endsWith("CUSTOM")) {
            return PLACEHOLDER.matcher(text).matches() ? null : "papi-placeholder";
        }
        if (mode.endsWith("TIMED")) {
            for (String entry : text.split(";;", -1)) {
                if (!validClock(entry, true)) {
                    return "timed-reset";
                }
            }
        } else if (mode.endsWith("TIMER") && !validClock(text, false)) {
            return "timer-reset";
        }
        return null;
    }

    private static boolean validClock(String text, boolean timeOfDay) {
        String[] parts = text.split(":", -1);
        if (parts.length < 3 || parts.length > 5) {
            return false;
        }
        try {
            int[] numbers = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                numbers[i] = Integer.parseInt(parts[i]);
            }
            if (timeOfDay) {
                LocalTime.of(numbers[parts.length - 3], numbers[parts.length - 2], numbers[parts.length - 1]);
            }
            return true;
        } catch (IllegalArgumentException | DateTimeException exception) {
            return false;
        }
    }

    private static String validateLayout(EditorTarget target, String parent, Object value) {
        if (!(value instanceof List<?> rows)) {
            return "layout-list";
        }
        int slots = 0;
        for (Object row : rows) {
            if (!(row instanceof String) && !(row instanceof Number)) {
                return "layout-list";
            }
            String line = row.toString();
            for (int index = 0; index < line.length(); index++) {
                if (line.charAt(index) == '`') {
                    int end = line.indexOf('`', index + 1);
                    if (end <= index + 1) {
                        return "layout-token";
                    }
                    index = end;
                }
                slots++;
            }
        }
        int size = target.getConfig().getInt(EditorTypeResolver.buildPath(parent, "size"), 54);
        return size > 0 && slots > size ? "layout-capacity" : null;
    }

    private static String validateAction(String action, String key, Object value) {
        String text = value.toString();
        if (action.equals("particle") && key.equals("particle") && !text.contains("%") && !text.contains("{")) {
            try {
                Particle.valueOf(text.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                return "particle-name";
            }
        }
        if (action.equals("effect") && key.equals("potion")
                && !isNamespacedKey(text.contains(":") ? text : text.toLowerCase(Locale.ROOT))) {
            return "potion-key";
        }
        if (action.equals("sound") && key.equals("sound") && !isNamespacedKey(text)) {
            return "sound-key";
        }
        if (action.equals("sound") && (key.equals("volume") || key.equals("pitch"))) {
            try {
                if (Float.isFinite(Float.parseFloat(text))) {
                    return null;
                }
            } catch (NumberFormatException exception) {
                return "finite-number";
            }
            return "finite-number";
        }
        return null;
    }

    private static boolean isNamespacedKey(String text) {
        try {
            return NamespacedKey.fromString(text) != null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean integerInRange(Object value, int min, int max) {
        try {
            int number = Integer.parseInt(value.toString());
            return number >= min && number <= max;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
