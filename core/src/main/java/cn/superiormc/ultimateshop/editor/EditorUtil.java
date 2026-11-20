package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.methods.Dupe;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditorUtil {

    static final Object SECTION_SENTINEL = new Object();

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            UltimateShop.methodUtil.setItemName(meta, name, null);
            if (lore != null && !lore.isEmpty()) {
                UltimateShop.methodUtil.setItemLore(meta, lore, null);
            }
            itemStack.setItemMeta(meta);
        }
        itemStack = Dupe.markGuiDisplayItem(itemStack);
        return itemStack;
    }

    public static List<String> summarize(Player player, Object value) {
        List<String> lore = new ArrayList<>();
        if (value instanceof ConfigurationSection section) {
            lore.add(EditorLang.text(player, "editor.summary.type", "&7Type: &f{value}",
                    "value", EditorLang.text(player, "editor.summary.section", "Section")));
            lore.add(EditorLang.text(player, "editor.summary.keys", "&7Keys: &f{value}",
                    "value", String.valueOf(section.getKeys(false).size())));
            return lore;
        }
        if (value instanceof List<?> list) {
            lore.add(EditorLang.text(player, "editor.summary.type", "&7Type: &f{value}",
                    "value", EditorLang.text(player, "editor.summary.list", "List")));
            lore.add(EditorLang.text(player, "editor.summary.size", "&7Size: &f{value}",
                    "value", String.valueOf(list.size())));
            addPreview(lore, list);
            return lore;
        }
        String display = trim(String.valueOf(value), 48);
        if (value instanceof Boolean enabled) {
            display = enabled ? EditorLang.text(player, "editor.common.enabled", "Enabled")
                    : EditorLang.text(player, "editor.common.disabled", "Disabled");
        }
        lore.add(EditorLang.text(player, "editor.summary.value", "&7Value: &f{value}", "value", display));
        return lore;
    }

    private static void addPreview(List<String> lore, Collection<?> values) {
        int index = 0;
        for (Object value : values) {
            lore.add("&8- &7" + trim(String.valueOf(value), 42));
            index++;
            if (index >= 3) {
                break;
            }
        }
    }

    public static String trim(String value, int limit) {
        if (value == null) {
            return "null";
        }
        if (value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit - 3) + "...";
    }

    public static String displayPath(Player player, String path) {
        return EditorLang.displayPath(player, path);
    }

    public static String parentPath(String path) {
        if (path == null || path.isEmpty() || !path.contains(".")) {
            return "";
        }
        return path.substring(0, path.lastIndexOf('.'));
    }

    public static String limitConditionsPath(String limitsPath) {
        if (limitsPath == null || limitsPath.isEmpty()) {
            return "limits-conditions";
        }
        return limitsPath + "-conditions";
    }

    public static String limitsPathFromConditionsRoot(String path) {
        if (path == null || !path.endsWith("limits-conditions")) {
            return null;
        }
        return path.substring(0, path.length() - "-conditions".length());
    }
    public static ConfigurationSection getSection(EditorTarget target, String path) {
        if (path == null || path.isEmpty()) {
            return target.getConfig();
        }
        return target.getConfig().getConfigurationSection(path);
    }

    static int nextNumericKey(ConfigurationSection section) {
        int max = 0;
        for (String key : section.getKeys(false)) {
            if (key.matches("\\d+")) {
                max = Math.max(max, Integer.parseInt(key));
            }
        }
        return max + 1;
    }

    static Object parseNewChildValue(String input, EditorPresetField field) {
        if (field == null) {
            return parseNewChildValue(input);
        }
        try {
            return switch (field.getType()) {
                case STRING, STRING_CHOICE, RESET_TIME, CLICK_TYPE_BINDING -> {
                    yield input;
                }
                case STRING_LIST -> {
                    yield input.equals("[]") ? List.of() : CommonUtil.translateString(input);
                }
                case INTEGER_LIST -> {
                    List<Integer> numbers = new ArrayList<>();
                    if (!input.equals("[]")) {
                        for (String part : input.split(";;", -1)) {
                            numbers.add(Integer.parseInt(part.trim()));
                        }
                    }
                    yield numbers;
                }
                case INTEGER -> {
                    yield Integer.parseInt(input.trim());
                }
                case DOUBLE -> {
                    double number = Double.parseDouble(input.trim());
                    yield Double.isFinite(number) ? number : null;
                }
                case BOOLEAN -> {
                    yield input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")
                            ? Boolean.parseBoolean(input) : null;
                }
                default -> {
                    yield parseNewChildValue(input);
                }
            };
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static Object parseNewChildValue(String input) {
        if (input == null) {
            return "";
        }
        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("section") || trimmed.equals("{}")) {
            return SECTION_SENTINEL;
        }
        if (trimmed.equals("[]")) {
            return List.of();
        }
        if (trimmed.equalsIgnoreCase("true") || trimmed.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(trimmed);
        }
        if (trimmed.contains(";;")) {
            String[] split = trimmed.split(";;");
            List<Integer> integerValues = new ArrayList<>();
            List<String> stringValues = new ArrayList<>();
            boolean integerList = true;
            for (String part : split) {
                String element = part.trim();
                if (element.isEmpty()) {
                    stringValues.add("");
                    integerList = false;
                    continue;
                }
                stringValues.add(element);
                if (integerList) {
                    try {
                        integerValues.add(Integer.parseInt(element));
                    } catch (NumberFormatException exception) {
                        integerList = false;
                    }
                }
            }
            return integerList ? integerValues : stringValues;
        }
        if (trimmed.matches("-?\\d+")) {
            try {
                return Integer.parseInt(trimmed);
            } catch (NumberFormatException ignored) {
            }
        }
        if (trimmed.matches("-?\\d+\\.\\d+")) {
            try {
                return Double.parseDouble(trimmed);
            } catch (NumberFormatException ignored) {
            }
        }
        return input;
    }
}
