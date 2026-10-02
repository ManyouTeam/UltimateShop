package cn.superiormc.ultimateshop.objects.dialog;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogLayoutUtil {

    public static int getButtonY(ConfigurationSection menu, int index) {
        return 18 + index * (ObjectDialogTemplate.fromMenu(menu, "button").getHeight() + 9);
    }

    public static String getTemplateName(ConfigurationSection menu, int slot, ConfigurationSection button, String fallback) {
        String name = button == null ? fallback : button.getString("dialog.template", fallback);
        return menu.getString("dialog.slots." + slot + ".template", name);
    }

    public static int[] getPosition(ConfigurationSection menu, int slot, ConfigurationSection button, int x, int y) {
        ConfigurationSection position = menu.getConfigurationSection("dialog.slots." + slot + ".position");
        if (position == null && button != null) {
            position = button.getConfigurationSection("dialog.position");
        }
        return position == null ? new int[]{x, y} : new int[]{position.getInt("x", x), position.getInt("y", y)};
    }

    public static List<int[]> getConfiguredProductPositions(ConfigurationSection menu) {
        List<int[]> result = new ArrayList<>();
        for (Map<?, ?> position : menu.getMapList("dialog.products.positions")) {
            if (!(position.get("x") instanceof Number) || !(position.get("y") instanceof Number)) {
                throw new IllegalArgumentException("Each product position needs numeric x and y");
            }
            result.add(new int[]{((Number) position.get("x")).intValue(), ((Number) position.get("y")).intValue()});
        }
        return result;
    }

    public static int[] getGridProductPosition(ConfigurationSection menu, ObjectDialogTemplate template, int index) {
        int columns = menu.getInt("dialog.columns", 2);
        int x = menu.getInt("dialog.products.x", 144);
        int y = menu.getInt("dialog.products.y", 18);
        int gapX = menu.getInt("dialog.products.gap-x", 12);
        int gapY = menu.getInt("dialog.products.gap-y", 9);
        if (columns < 1 || x < 0 || y < 0 || gapX < 0 || gapY < 0 || y % 9 != 0 || gapY % 9 != 0) {
            throw new IllegalArgumentException("Invalid product grid origin or gap");
        }
        return new int[]{x + index % columns * (template.getWidth() + gapX), y + index / columns * (template.getHeight() + gapY)};
    }
}
