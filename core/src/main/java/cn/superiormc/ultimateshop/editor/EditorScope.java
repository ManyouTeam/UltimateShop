package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum EditorScope {

    SHOP("shops", "Shop"),
    MENU("menus", "Menu");

    private final String folderName;

    private final String displayName;

    EditorScope(String folderName, String displayName) {
        this.folderName = folderName;
        this.displayName = displayName;
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

    public List<String> listIds() {
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
        String normalized = raw.toLowerCase();
        if (normalized.equals("shop") || normalized.equals("shops")) {
            return SHOP;
        }
        if (normalized.equals("menu") || normalized.equals("menus")) {
            return MENU;
        }
        return null;
    }
}
