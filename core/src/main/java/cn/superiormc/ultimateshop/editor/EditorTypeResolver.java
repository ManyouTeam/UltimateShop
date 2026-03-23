package cn.superiormc.ultimateshop.editor;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Locale;

public class EditorTypeResolver {

    public static EditorValueKind resolve(ConfigurationSection parent, String key) {
        Object value = parent.get(key);
        String path = buildPath(parent.getCurrentPath(), key);

        if (value instanceof ConfigurationSection section) {
            if (isActionCollection(path)) {
                return EditorValueKind.ACTION_COLLECTION;
            }
            if (isConditionCollection(path)) {
                return EditorValueKind.CONDITION_COLLECTION;
            }
            if (isEconomySection(path, section)) {
                return EditorValueKind.ECONOMY_SECTION;
            }
            if (isItemSection(path, section)) {
                return EditorValueKind.ITEM_SECTION;
            }
            return EditorValueKind.SECTION;
        }

        if (value instanceof Boolean) {
            return EditorValueKind.BOOLEAN;
        }
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return EditorValueKind.INTEGER;
        }
        if (value instanceof Float || value instanceof Double) {
            return EditorValueKind.DOUBLE;
        }
        if (value instanceof List<?> list) {
            if (list.isEmpty()) {
                return EditorValueKind.STRING_LIST;
            }
            boolean allNumbers = true;
            for (Object object : list) {
                if (!(object instanceof Number)) {
                    allNumbers = false;
                    break;
                }
            }
            return allNumbers ? EditorValueKind.INTEGER_LIST : EditorValueKind.STRING_LIST;
        }
        if (value instanceof String) {
            if ("type".equalsIgnoreCase(key) && isActionEntry(parent.getCurrentPath())) {
                return EditorValueKind.ACTION_TYPE;
            }
            if ("type".equalsIgnoreCase(key) && isConditionEntry(parent.getCurrentPath())) {
                return EditorValueKind.CONDITION_TYPE;
            }
            return EditorValueKind.STRING;
        }

        return EditorValueKind.UNKNOWN;
    }

    public static boolean isActionCollection(String path) {
        String last = lastSegment(path);
        return last.endsWith("actions");
    }

    public static boolean isConditionCollection(String path) {
        String last = lastSegment(path);
        if (last.endsWith("conditions")) {
            return true;
        }
        String parent = parentSectionPath(path);
        return isLimitConditionRoot(parent) && !last.matches("\\d+");
    }

    public static boolean isThingCollection(String path) {
        String last = lastSegment(path);
        return last.equals("products") || last.equals("buy-prices") || last.equals("sell-prices");
    }

    public static boolean isActionEntry(String parentPath) {
        return isActionCollection(parentSectionPath(parentPath));
    }

    public static boolean isConditionEntry(String parentPath) {
        return isConditionCollection(parentSectionPath(parentPath));
    }

    public static boolean isLimitSection(String path) {
        String last = lastSegment(path);
        return last.equals("limits") || last.equals("buy-limits") || last.equals("sell-limits");
    }

    public static boolean isLimitConditionRoot(String path) {
        String last = lastSegment(path);
        return last.equals("limits-conditions")
                || last.equals("buy-limits-conditions")
                || last.equals("sell-limits-conditions");
    }

    public static boolean isClickEventSection(String path) {
        return lastSegment(path).equals("click-event");
    }

    public static boolean isResetTimeKey(String key) {
        if (key == null) {
            return false;
        }
        String normalized = key.toLowerCase(Locale.ENGLISH);
        return normalized.equals("buy-times-reset-time") || normalized.equals("sell-times-reset-time");
    }

    public static boolean isEconomySection(String path, ConfigurationSection section) {
        return section.contains("economy-plugin")
                || section.contains("economy-type")
                || section.contains("match-placeholder")
                || path.toLowerCase(Locale.ENGLISH).contains("prices.");
    }

    public static boolean isItemSection(String path, ConfigurationSection section) {
        String lowerPath = path.toLowerCase(Locale.ENGLISH);
        if (lowerPath.contains("match-item")) {
            return false;
        }
        return lowerPath.endsWith("display-item")
                || lowerPath.endsWith("content")
                || lowerPath.endsWith("convert")
                || lowerPath.endsWith("item")
                || section.contains("material")
                || section.contains("hook-plugin")
                || section.contains("hook-item")
                || section.contains("component");
    }

    public static String buildPath(String parentPath, String key) {
        if (parentPath == null || parentPath.isEmpty()) {
            return key;
        }
        return parentPath + "." + key;
    }

    public static String lastSegment(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        String[] split = path.split("\\.");
        return split[split.length - 1].toLowerCase(Locale.ENGLISH);
    }

    private static String parentSectionPath(String path) {
        if (path == null || path.isEmpty() || !path.contains(".")) {
            return "";
        }
        return path.substring(0, path.lastIndexOf('.'));
    }
}
