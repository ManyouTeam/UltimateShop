package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LocateManager;
import cn.superiormc.ultimateshop.methods.Items.DebuildItem;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemUtil {

    public static String getItemName(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        if (LocateManager.enableThis() && LocateManager.locateManager != null) {
            return LocateManager.locateManager.getLocateName(displayItem);
        }
        return getItemNameWithoutVanilla(displayItem);
    }

    public static String getItemNameWithoutVanilla(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        StringBuilder result = new StringBuilder();
        for (String word : displayItem.getType().name().toLowerCase().split("_")) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                result.append(firstChar).append(restOfWord).append(" ");
            }
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (ConfigManager.configManager.getStringOrDefault("sell-mode", "sell.sell-method", "Bukkit").equals("Bukkit")) {
            return item1.isSimilar(item2);
        }
        Map<String, Object> item1Result = DebuildItem.debuildItem(item1, new MemoryConfiguration()).getValues(true);
        Map<String, Object> item2Result = DebuildItem.debuildItem(item2, new MemoryConfiguration()).getValues(true);
        if (ConfigManager.configManager.getBoolean("sell.item-format.require-same-key")) {
            for (String key : item1Result.keySet()) {
                if (canIgnore(key)) {
                    continue;
                }
                if (!item2Result.containsKey(key)) {
                    return false;
                }
            }
        }
        for (String key : item2Result.keySet()) {
            if (canIgnore(key)) {
                continue;
            }
            Object object = item1Result.get(key);
            if (object == null) {
                return false;
            }
            if (object instanceof MemorySection) {
                continue;
            }
            if (!object.equals(item2Result.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static boolean canIgnore(String key) {
        if (key == null) {
            return true;
        }
        if (key.equals("amount")) {
            return true;
        }
        for (String tempVal1 : ConfigManager.configManager.getStringListOrDefault("sell.ignore-item-format-key", "sell.item-format.ignore-key")) {
            if (tempVal1.equals(key) || key.startsWith(tempVal1 + ".")) {
                return true;
            }
        }
        return false;
    }
}
