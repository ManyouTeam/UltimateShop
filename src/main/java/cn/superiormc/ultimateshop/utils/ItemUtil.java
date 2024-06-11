package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Items.DebuildItem;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.utils.ItemUtils;

import java.util.Map;

public class ItemUtil {

    public static String getItemName(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            return ItemUtils.getItemName(displayItem);
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
        return result.toString();
    }

    public static boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (ConfigManager.configManager.getStringOrDefault("sell-mode", "sell.sell-method", "Bukkit").equals("Bukkit")) {
            return item1.equals(item2);
        }
        Map<String, Object> item1Result = DebuildItem.debuildItem(item1, new MemoryConfiguration()).getValues(true);
        Map<String, Object> item2Result = DebuildItem.debuildItem(item2, new MemoryConfiguration()).getValues(true);
        for (String key : item1Result.keySet()) {
            if (!item2Result.get(key).equals(item1Result.get(key))) {
                return false;
            }
        }
        return true;
    }

}
