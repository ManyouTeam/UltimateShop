package cn.superiormc.ultimateshop.objects.dialog;

import cn.superiormc.ultimateshop.gui.MenuUILayout;
import cn.superiormc.ultimateshop.managers.ErrorManager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class ObjectDialogTemplate {

    public static ObjectDialogTemplate fromUi(
        ConfigurationSection menu, MenuUILayout.Element element) {
        return fromUi(menu, element, null);
    }

    public static ObjectDialogTemplate fromUi(
        ConfigurationSection menu, MenuUILayout.Element element, ConfigurationSection button) {
        MemoryConfiguration style = new MemoryConfiguration();
        boolean card = element.appearance().equals("card");
        ConfigurationSection configured =
            menu.getConfigurationSection(
            "ui.renderers.ore.templates." + (card ? "product" : "button"));
        if (configured != null) {
            configured.getValues(true).forEach(style::set);
        }
        style.set("type", card ? "product" : "button");
        style.set("width", element.bounds().width());
        style.set("height", element.bounds().height());
        style.set(
            "padding",
            card
            ? menu.getInt("ui.products.card.padding", 9)
            : Math.min(12, (element.bounds().width() - 1) / 2));
        style.set("icon-size", card ? menu.getInt("ui.products.card.icon-size", 32) : 0);
        style.set("name-y", 9);
        style.set("name-lines", card ? 1 : 2);
        style.set("lore-y", 27);
        style.set(
            "lore-lines",
            card
            ? Math.min(
            menu.getInt(
            element.product()
            ? "ui.products.card.lore-lines"
            : "ui.products.card.description-lines",
            element.product() ? 6 : 2),
            element.bounds().height() / 9 - 6)
            : 0);
        style.set("details-y", element.bounds().height() - 27);
        for (var entry :
            Map.of(
            "background",
            card ? "card" : "button.background",
            "border",
            card ? "border" : "button.border",
            "accent",
            "accent",
            "footer",
            "button.background")
            .entrySet()) {
            String color = menu.getString("ui.theme." + entry.getValue(), "#354861");
            if (color.length() == 9) {
                color = "#" + color.substring(3);
            }
            style.set(entry.getKey(), color);
        }
        if (element.area().startsWith("header:")) {
            String key = element.area().substring(7);
            String background = menu.getString("ui.theme.card", "#141E30");
            if (background.length() == 9) {
                background = "#" + background.substring(3);
            }
            for (String color : List.of("background", "border", "accent", "footer")) {
                style.set(color, background);
            }
        }
        return new ObjectDialogTemplate(card ? "product" : "button", style);
    }

    private boolean valid = true;

    private final boolean card;

    public boolean isCard() {
        return card;
    }

    public boolean isValid() {
        return valid;
    }

    private void invalidate(String message) {
        valid = false;
        ErrorManager.errorManager.sendErrorMessage("§c" + message);
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
        card = product;
        width = integer(config, "width", product ? 408 : 96);
        height = integer(config, "height", product ? 108 : 36);
        padding = integer(config, "padding", product ? 12 : 6);
        iconSize = integer(config, "icon-size", product ? 32 : 0);
        nameRow =
            integer(
            config,
            "name-y",
            product ? 9 : Math.max(0, Math.round((height - 3 - 7) / 18.0f)) * 9)
        / 9;
        nameLines = integer(config, "name-lines", product ? 1 : 2);
        autoNameY = !product && (config == null || !config.contains("name-y"));
        loreRow = integer(config, "lore-y", 27) / 9;
        loreLines = integer(config, "lore-lines", product ? 6 : 0);
        detailsRow = integer(config, "details-y", height - 27) / 9;
        background = color(config, "background", "#262c36");
        border = color(config, "border", "#596373");
        accent = color(config, "accent", "#83cfce");
        footer = color(config, "footer", "#294b54");
        texture = config == null ? "" : config.getString("texture", "");
        selectedBackground = color(config, "selected-background", "#244A32");
        selectedBorder = color(config, "selected-border", "#65D98B");
        selectedTexture = config == null ? "" : config.getString("selected-texture", "");
        for (String path : new String[] {
                texture, selectedTexture
            }
            ) {
            if (!path.isEmpty()
                && (!path.matches("[a-zA-Z0-9_/-]+\\.png")
                || path.contains("..")
                || path.startsWith("/"))) {
                invalidate(
                    "Dialog texture must be a relative PNG path inside the plugin textures"
                    + " folder");
            }
        }
        if (iconSize < 0
            || iconSize > 64
            || (product
            && iconSize > 0
            && (padding * 2 + iconSize + 8 >= width
            || loreRow * 9 + iconSize > detailsRow * 9))) {
            invalidate("Invalid product icon size: " + name);
        }
        if (width < 24
            || width > 900
            || height < 27
            || height > 540
            || height % 9 != 0
            || padding < 0
            || padding * 2 >= width
            || nameRow < 0
            || nameRow >= height / 9
            || nameLines < 1
            || nameLines > 60
            || getNameLines(product) < 1
            || detailsRow < 0
            || detailsRow + 2 > height / 9
            || loreRow < 0
            || loreLines < 0
            || loreLines > height / 9
            || (product && (loreRow + loreLines > detailsRow || nameRow >= detailsRow))
            || (loreLines > 0 && nameRow >= loreRow && nameRow < loreRow + loreLines)) {
            invalidate("Invalid Dialog template: " + name);
        }
        for (String key : new String[] {
                "name-y", "lore-y", "details-y"
            }
            ) {
            if (config != null && config.getInt(key, 0) % 9 != 0) {
                invalidate("Dialog template " + key + " must be a multiple of 9: " + name);
            }
        }
        String signature =
            "anchors-v8:"
            + width
        + ":"
            + height
        + ":"
            + background
        + ":"
            + border
        + ":"
            + accent
        + ":"
            + footer
        + ":"
            + detailsRow
        + ":"
            + iconSize
        + ":"
            + loreRow
        + ":"
            + texture
        + ":"
            + selectedBackground
        + ":"
            + selectedBorder
        + ":"
            + selectedTexture;
        String generatedId;
        try {
            byte[] digest =
                MessageDigest.getInstance("SHA-256")
                .digest(signature.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                hex.append(String.format("%02x", digest[i] & 255));
            }
            generatedId = "ore/" + hex;
        } catch (NoSuchAlgorithmException exception) {
            invalidate("Unable to calculate Ore resource ID: " + exception.getMessage());
            generatedId = "ore/" + Integer.toHexString(signature.hashCode());
        }
        resourceId = generatedId;
    }

    private int integer(ConfigurationSection config, String key, int fallback) {
        return config == null ? fallback : config.getInt(key, fallback);
    }

    private int color(ConfigurationSection config, String key, String fallback) {
        String value = config == null ? fallback : config.getString(key, fallback);
        if (!value.matches("#[a-fA-F0-9]{6}")) {
            invalidate("Dialog template color must be #RRGGBB: " + key);
            return Integer.parseInt(fallback.substring(1), 16);
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
