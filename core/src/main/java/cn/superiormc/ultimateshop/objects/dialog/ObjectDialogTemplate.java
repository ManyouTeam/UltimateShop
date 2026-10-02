package cn.superiormc.ultimateshop.objects.dialog;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class ObjectDialogTemplate {

    public static ObjectDialogTemplate fromMenu(ConfigurationSection menu, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dialog template name must not be empty");
        }
        ConfigurationSection config = menu.getConfigurationSection("dialog.templates." + name);
        if (config == null && !Set.of("product", "button").contains(name)) {
            throw new IllegalArgumentException("Unknown Dialog template: " + name);
        }
        String type = config == null ? name : config.getString("type", name);
        if (type.equals("product")) {
            int columns = menu.getInt("dialog.columns", 2);
            int gap = menu.getInt("dialog.products.gap-x", 12);
            int available = menu.getInt("dialog.width", 576) - menu.getInt("dialog.products.x", 144) - 12;
            if (columns < 1 || columns > 12 || gap < 0) {
                throw new IllegalArgumentException("Invalid product columns or horizontal gap");
            }
            int cardWidth = (available - (columns - 1) * gap) / columns;
            MemoryConfiguration fitted = new MemoryConfiguration();
            if (config != null) {
                config.getValues(true).forEach(fitted::set);
            }
            fitted.set("type", "product");
            fitted.set("width", Math.min(integerWidth(config, cardWidth), cardWidth));
            config = fitted;
        }
        return new ObjectDialogTemplate(name, config);
    }

    private static int integerWidth(ConfigurationSection config, int fallback) {
        return config == null ? fallback : config.getInt("width", fallback);
    }

    private final int width;

    private final int height;

    private final int padding;

    private final int iconSize;

    private final int nameRow;

    private final int nameLines;

    private final boolean autoNameY;

    private final int loreRow;

    private final int loreLines;

    private final int detailsRow;

    private final boolean centered;

    private final int background;

    private final int border;

    private final int accent;

    private final int footer;

    private final String texture;

    private final int selectedBackground;

    private final int selectedBorder;

    private final String selectedTexture;

    private final String resourceId;

    public ObjectDialogTemplate(String name, ConfigurationSection config) {
        String type = config == null ? name : config.getString("type", name);
        boolean product = type.equals("product");
        width = integer(config, "width", product ? 408 : 96);
        height = integer(config, "height", product ? 108 : 36);
        padding = integer(config, "padding", product ? 12 : 6);
        iconSize = integer(config, "icon-size", product ? 32 : 0);
        nameRow = integer(config, "name-y", product ? 9 : Math.max(0, Math.round((height - 3 - 7) / 18.0f)) * 9) / 9;
        nameLines = integer(config, "name-lines", product ? 1 : 2);
        autoNameY = !product && (config == null || !config.contains("name-y"));
        loreRow = integer(config, "lore-y", 27) / 9;
        loreLines = integer(config, "lore-lines", product ? 6 : 0);
        detailsRow = integer(config, "details-y", height - 27) / 9;
        centered = config == null ? !product : config.getString("text-align", product ? "left" : "center").equals("center");
        background = color(config, "background", "#262c36");
        border = color(config, "border", "#596373");
        accent = color(config, "accent", "#83cfce");
        footer = color(config, "footer", "#294b54");
        texture = config == null ? "" : config.getString("texture", "");
        selectedBackground = color(config, "selected-background", "#244A32");
        selectedBorder = color(config, "selected-border", "#65D98B");
        selectedTexture = config == null ? "" : config.getString("selected-texture", "");
        for (String path : new String[]{texture, selectedTexture}) {
            if (!path.isEmpty() && (!path.matches("[a-zA-Z0-9_/-]+\\.png") || path.contains("..") || path.startsWith("/"))) {
                throw new IllegalArgumentException("Dialog texture must be a relative PNG path inside the plugin textures folder");
            }
        }
        if (iconSize < 0 || iconSize > 64 || (product && iconSize > 0 && (padding * 2 + iconSize + 8 >= width || loreRow * 9 + iconSize > detailsRow * 9))) {
            throw new IllegalArgumentException("Invalid product icon size: " + name);
        }
        if (width < 24 || width > 900 || height < 27 || height > 540 || height % 9 != 0
                || padding < 0 || padding * 2 >= width || nameRow < 0 || nameRow >= height / 9
                || nameLines < 1 || nameLines > 60
                || getNameLines(product) < 1
                || detailsRow < 0 || detailsRow + 2 > height / 9 || loreRow < 0 || loreLines < 0
                || loreLines > height / 9 || (product && (loreRow + loreLines > detailsRow || nameRow >= detailsRow))
                || (loreLines > 0 && nameRow >= loreRow && nameRow < loreRow + loreLines)) {
            throw new IllegalArgumentException("Invalid Dialog template: " + name);
        }
        for (String key : new String[]{"name-y", "lore-y", "details-y"}) {
            if (config != null && config.getInt(key, 0) % 9 != 0) {
                throw new IllegalArgumentException("Dialog template " + key + " must be a multiple of 9: " + name);
            }
        }
        String signature = "anchors-v8:" + width + ":" + height + ":" + background + ":" + border + ":" + accent + ":" + footer + ":" + detailsRow + ":" + iconSize + ":" + loreRow + ":" + texture
                + ":" + selectedBackground + ":" + selectedBorder + ":" + selectedTexture;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(signature.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                hex.append(String.format("%02x", digest[i] & 255));
            }
            resourceId = "ore/" + hex;
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private int integer(ConfigurationSection config, String key, int fallback) {
        return config == null ? fallback : config.getInt(key, fallback);
    }

    private int color(ConfigurationSection config, String key, String fallback) {
        String value = config == null ? fallback : config.getString(key, fallback);
        if (!value.matches("#[a-fA-F0-9]{6}")) {
            throw new IllegalArgumentException("Dialog template color must be #RRGGBB: " + key);
        }
        return Integer.parseInt(value.substring(1), 16);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPadding() {
        return padding;
    }

    public int getIconSize() {
        return iconSize;
    }

    public int getNameRow() {
        return nameRow;
    }

    public int getNameRow(int lines) {
        if (!autoNameY) {
            return nameRow;
        }
        int areaHeight = loreLines > 0 ? Math.min(height - 3, loreRow * 9) : height - 3;
        return Math.max(0, Math.round((areaHeight - (lines - 1) * 9 - 7) / 18.0f));
    }

    public int getNameLines(boolean product) {
        int bottom = (height - 3 - 7) / 9 + 1;
        if (product) {
            bottom = Math.min(bottom, detailsRow);
        }
        if (loreLines > 0) {
            bottom = Math.min(bottom, loreRow);
        }
        return Math.min(nameLines, bottom - (autoNameY ? 0 : nameRow));
    }

    public int getLoreRow() {
        return loreRow;
    }

    public int getLoreLines() {
        return loreLines;
    }

    public int getDetailsRow() {
        return detailsRow;
    }

    public int getDetailsTextRow() {
        int footerHeight = height - 3 - detailsRow * 9;
        return detailsRow + Math.max(0, Math.round((footerHeight - 7) / 18.0f));
    }

    public boolean isCentered() {
        return centered;
    }

    public int getBackground() {
        return background;
    }

    public int getBorder() {
        return border;
    }

    public int getAccent() {
        return accent;
    }

    public int getFooter() {
        return footer;
    }

    public String getTexture() {
        return texture;
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getSelectedBackground() {
        return selectedBackground;
    }

    public int getSelectedBorder() {
        return selectedBorder;
    }

    public String getSelectedTexture() {
        return selectedTexture;
    }
}
