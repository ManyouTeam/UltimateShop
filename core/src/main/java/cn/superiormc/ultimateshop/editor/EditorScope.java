package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public enum EditorScope {

    SHOP("shops", "Shop"),
    MENU("menus/common_menus", "Menu"),
    SHOP_MENU_TEMPLATE("menus/shop_menu_templates", "Shop Menu Template"),
    BUY_MORE_MENU("menus/buy_more_menus", "Buy More Menu"),
    FAVOURITE_MENU("menus/favourite_menus", "Favourite Menu"),
    SEARCH_MENU("menus/search_menus", "Search Menu"),
    ITEM_SELL_MENU("menus/item_sell_menus", "Item Sell Menu"),
    CONFIG("", "Global Configuration", "config"),
    UI_STYLE("ui", "UI Styles"),
    ITEM("items", "Saved Items"),
    LANGUAGE("languages", "Languages"),
    RANDOM_PLACEHOLDER("placeholders/random_placeholders", "Random Placeholders"),
    CONDITIONAL_PLACEHOLDER("placeholders/conditional_placeholders", "Conditional Placeholders"),
    CUSTOM_PLACEHOLDER("placeholders/custom_placeholders", "Custom Placeholders"),
    SHARED_USE_TIMES("shared_use_times", "Shared Use Times"),
    SELL_STICK("sell_sticks", "Sell Sticks"),
    SELL_CHEST("sell_chests", "Sell Chests");

    private final String folderName;

    private final String displayName;

    private final String singleFileId;

    EditorScope(String folderName, String displayName) {
        this(folderName, displayName, null);
    }

    EditorScope(String folderName, String displayName, String singleFileId) {
        this.folderName = folderName;
        this.displayName = displayName;
        this.singleFileId = singleFileId;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public File getFolder() {
        return new File(UltimateShop.instance.getDataFolder(), folderName);
    }

    public File getFile(String id) {
        return new File(getFolder(), id + ".yml");
    }

    public String getFilePath(String id) {
        if (folderName.isEmpty()) {
            return id + ".yml";
        }
        return folderName + "/" + id + ".yml";
    }

    public String getCommandName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isShopOrMenu() {
        return this == SHOP || folderName.startsWith("menus/");
    }

    public boolean acceptsId(String id) {
        return id != null && !id.isBlank() && id.indexOf('/') < 0 && id.indexOf('\\') < 0
                && id.indexOf(':') < 0 && (singleFileId == null || singleFileId.equals(id));
    }

    public List<String> listIds() {
        if (singleFileId != null) {
            return getFile(singleFileId).isFile() ? List.of(singleFileId) : List.of();
        }
        File folder = getFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        List<String> ids = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            ids.add(name.substring(0, name.length() - 4));
        }
        return ids;
    }

    public static EditorScope ofName(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.toLowerCase(Locale.ROOT);
        for (EditorScope scope : values()) {
            String name = scope.getCommandName();
            if (normalized.equals(name) || normalized.equals(name + "s")) {
                return scope;
            }
        }
        return null;
    }
}
