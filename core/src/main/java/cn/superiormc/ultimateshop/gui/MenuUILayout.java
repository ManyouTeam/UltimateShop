package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.ConfigurationUtil;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MenuUILayout {

    private final ConfigurationSection ui;

    private final Map<String, Area> areas = new LinkedHashMap<>();

    private final int width;

    private final int height;

    private boolean valid = true;

    public MenuUILayout(ConfigurationSection menu) {
        ConfigurationSection configured = menu.getConfigurationSection("ui");
        ui = configured == null ? new MemoryConfiguration() : configured;
        width = ui.getInt("layout.canvas.width", 648);
        height = ui.getInt("layout.canvas.height", 396);
        if (configured == null) {
            invalidate("Menu requires ui configuration");
            return;
        }
        if (width < 144 || width > 900 || height < 144 || height > 540 || height % 9 != 0) {
            invalidate("Invalid ui.layout.canvas dimensions");
            return;
        }
        ConfigurationSection definitions = ui.getConfigurationSection("layout.areas");
        if (definitions == null) {
            invalidate("Menu requires ui.layout.areas");
            return;
        }
        for (String name : definitions.getKeys(false)) {
            ConfigurationSection definition = definitions.getConfigurationSection(name);
            if (definition == null) {
                invalidate("Invalid layout area: " + name);
                return;
            }
            Box bounds =
                new Box(
                definition.getInt("x"),
                definition.getInt("y"),
                definition.getInt("width"),
                definition.getInt("height"));
            if (!validate(bounds, new Box(0, 0, width, height), "area " + name)) {
                return;
            }
            String type = definition.getString("type", "free");
            if (!List.of("grid", "stack", "free").contains(type)) {
                invalidate("Unknown layout type: " + type);
                return;
            }
            int columns = definition.getInt("columns", 1);
            int rows = definition.getInt("rows", 1);
            int gapX = definition.getInt("gap-x", definition.getInt("gap", 0));
            int gapY = definition.getInt("gap-y", definition.getInt("gap", 0));
            if (columns < 1
                || columns > 36
                || rows < 1
                || rows > 60
                || gapX < 0
                || gapY < 0
                || gapY % 9 != 0) {
                invalidate("Invalid grid or gap in area " + name);
                return;
            }
            int cellWidth = (bounds.width - (columns - 1) * gapX) / columns;
            int cellHeight =
                definition.getInt(
                type.equals("stack") ? "item-height" : "cell-height",
                type.equals("stack")
                ? 36
                : (bounds.height - (rows - 1) * gapY) / rows / 9 * 9);
            if (!type.equals("free")
                && (cellWidth < 24
                || cellHeight < 27
                || cellHeight % 9 != 0
                || type.equals("grid")
                && rows * cellHeight + (rows - 1) * gapY > bounds.height)) {
                invalidate("Invalid cell dimensions in area " + name);
                return;
            }
            areas.put(
                name,
                new Area(name, type, bounds, columns, rows, gapX, gapY, cellWidth, cellHeight));
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<String, Area> getAreas() {
        return Map.copyOf(areas);
    }

    public ConfigurationSection getUi() {
        return ui;
    }

    public List<Element> getHeaderElements() {
        ConfigurationSection header = ui.getConfigurationSection("layout.header");
        if (header == null) {
            return List.of();
        }
        List<Element> elements = new ArrayList<>();
        int index = -200;
        for (String key : header.getKeys(false)) {
            ConfigurationSection settings = header.getConfigurationSection(key);
            if (settings == null || !settings.getBoolean("enabled", true)) {
                continue;
            }
            Box bounds =
                new Box(
                settings.getInt("x"),
                settings.getInt("y"),
                settings.getInt("width", 108),
                27);
            if (!validate(bounds, new Box(0, 0, width, height), "header " + key)) {
                return List.of();
            }
            elements.add(new Element(index--, "header:" + key, "button", bounds, -1));
        }
        return List.copyOf(elements);
    }

    public List<Element> getPaginationElements() {
        ConfigurationSection pagination = ui.getConfigurationSection("layout.pagination");
        if (pagination == null || !pagination.getBoolean("enabled", true)) {
            return List.of();
        }
        List<Element> elements = new ArrayList<>();
        int index = -100;
        for (String key : List.of("previous", "indicator", "next")) {
            ConfigurationSection settings = pagination.getConfigurationSection(key);
            if (settings == null || !settings.getBoolean("enabled", true)) {
                continue;
            }
            Box bounds =
                new Box(
                settings.getInt("x"),
                settings.getInt("y"),
                settings.getInt("width", 36),
                settings.getInt("height", 27));
            if (!validate(bounds, new Box(0, 0, width, height), "pagination " + key)) {
                return List.of();
            }
            elements.add(new Element(index--, "pagination:" + key, "button", bounds, -1));
        }
        return List.copyOf(elements);
    }

    public boolean isValid() {
        return valid;
    }

    public List<Element> arrange(List<Entry> entries) {
        if (!valid) {
            return List.of();
        }
        List<Entry> ordered = new ArrayList<>(entries);
        ordered.sort(Comparator.comparingInt(this::order).thenComparingInt(Entry::slot));
        List<Element> result = new ArrayList<>();
        Map<String, Map<Integer, Entry>> cells = new HashMap<>();
        Map<String, Integer> stackIndices = new HashMap<>();
        for (Entry entry : ordered) {
            ConfigurationSection placement = placement(entry);
            if (placement == null || !placement.getBoolean("enabled", true)) {
                continue;
            }
            String name = placement.getString("area");
            Area area = areas.get(name);
            if (area == null) {
                invalidate("Unknown placement area " + name + " for slot " + entry.slot);
                return List.of();
            }
            if (area.type.equals("grid") && placement.contains("row")) {
                int row = placement.getInt("row");
                int column = placement.getInt("column", 0);
                if (row < 0 || column < 0 || column >= area.columns) {
                    invalidate("Invalid grid cell for slot " + entry.slot);
                    return List.of();
                }
                int cell = row * area.columns + column;
                Entry previous =
                    cells.computeIfAbsent(name, ignored -> new HashMap<>())
                    .putIfAbsent(cell, entry);
                if (previous != null) {
                    invalidate(
                        "Duplicate grid cell: slots " + previous.slot + " and " + entry.slot);
                    return List.of();
                }
            }
        }
        for (Entry entry : ordered) {
            ConfigurationSection placement = placement(entry);
            if (placement == null || !placement.getBoolean("enabled", true)) {
                continue;
            }
            Area area = areas.get(placement.getString("area"));
            int x;
            int y;
            int w;
            int h;
            int page = -1;
            if (area.type.equals("grid")) {
                Map<Integer, Entry> occupied =
                    cells.computeIfAbsent(area.name, ignored -> new HashMap<>());
                int cell;
                if (placement.contains("row")) {
                    cell = placement.getInt("row") * area.columns + placement.getInt("column", 0);
                } else {
                    cell = 0;
                    while (occupied.containsKey(cell)) {
                        cell++;
                    }
                    occupied.put(cell, entry);
                }
                page = cell / (area.columns * area.rows);
                int local = cell % (area.columns * area.rows);
                x = area.bounds.x + local % area.columns * (area.cellWidth + area.gapX);
                y = area.bounds.y + local / area.columns * (area.cellHeight + area.gapY);
                w = area.cellWidth;
                h = area.cellHeight;
            } else if (area.type.equals("stack")) {
                int index = stackIndices.merge(area.name, 1, Integer::sum) - 1;
                x = area.bounds.x;
                y = area.bounds.y + index * (area.cellHeight + area.gapY);
                w = area.bounds.width;
                h = area.cellHeight;
            } else {
                if (!placement.contains("x") || !placement.contains("y")) {
                    invalidate("Free placement requires x and y: slot " + entry.slot);
                    return List.of();
                }
                x = area.bounds.x + placement.getInt("x");
                y = area.bounds.y + placement.getInt("y");
                w = placement.getInt("width", 108);
                h = placement.getInt("height", 27);
            }
            Box bounds = new Box(x, y, placement.getInt("width", w), placement.getInt("height", h));
            if (!validate(bounds, area.bounds, "slot " + entry.slot)) {
                return List.of();
            }
            String appearance =
                entry.config == null
            ? "card"
            : entry.config.getString(
                "ui.appearance", entry.product ? "card" : "button");
            if (!List.of("card", "button").contains(appearance)) {
                invalidate("Unknown ui.appearance: " + appearance);
                return List.of();
            }
            if (appearance.equals("card") && bounds.height < 99) {
                invalidate("Cards require at least 99 pixels of height: slot " + entry.slot);
                return List.of();
            }
            Element element =
                new Element(entry.slot, area.name, appearance, bounds, page, entry.product);
            for (Element previous : result) {
                if ((previous.page < 0 || page < 0 || previous.page == page)
                    && previous.bounds.intersects(bounds)) {
                    invalidate(
                        "Overlapping UI elements: slots "
                        + previous.slot
                        + " and "
                        + entry.slot);
                    return List.of();
                }
            }
            result.add(element);
        }
        List<Element> decorations = new ArrayList<>(getHeaderElements());
        if (result.stream().anyMatch(element -> element.page > 0)) {
            decorations.addAll(getPaginationElements());
        }
        for (Element decoration : decorations) {
            for (Element element : result) {
                if (decoration.bounds.intersects(element.bounds)) {
                    invalidate("UI element overlaps " + decoration.area + ": slot " + element.slot);
                    return List.of();
                }
            }
        }
        return valid ? List.copyOf(result) : List.of();
    }

    public ConfigurationSection placement(Entry entry) {
        ConfigurationSection own =
            entry.config == null ? null : entry.config.getConfigurationSection("ui.placement");
        ConfigurationSection defaults =
            entry.product ? ui.getConfigurationSection("products.placement") : null;
        if (own == null || defaults == null) {
            return own == null ? defaults : own;
        }
        return ConfigurationUtil.merged(defaults, own);
    }

    private int order(Entry entry) {
        ConfigurationSection placement = placement(entry);
        return placement == null ? entry.slot : placement.getInt("order", entry.slot);
    }

    private boolean validate(Box box, Box parent, String subject) {
        if (box.width < 24
            || box.height < 9
            || box.y % 9 != 0
            || box.height % 9 != 0
            || box.x < parent.x
            || box.y < parent.y
            || box.x + box.width > parent.x + parent.width
            || box.y + box.height > parent.y + parent.height) {
            invalidate(
                "Invalid bounds for "
                + subject
                + "; y/height must be multiples of 9 and fit the area");
            return false;
        }
        return true;
    }

    private void invalidate(String message) {
        valid = false;
        ErrorManager.errorManager.sendErrorMessage("§cInvalid menu UI layout: " + message);
    }

    public record Entry(int slot, ConfigurationSection config, boolean product) {

    }

    public record Element(
        int slot, String area, String appearance, Box bounds, int page, boolean product) {
        public Element(int slot, String area, String appearance, Box bounds, int page) {
            this(slot, area, appearance, bounds, page, false);
        }
    }

    public record Area(
        String name,
        String type,
        Box bounds,
        int columns,
        int rows,
        int gapX,
        int gapY,
        int cellWidth,
        int cellHeight) {

    }

    public record Box(int x, int y, int width, int height) {
        public boolean intersects(Box other) {
            return x < other.x + other.width
                && x + width > other.x
                && y < other.y + other.height
                && y + height > other.y;
        }
    }
}
