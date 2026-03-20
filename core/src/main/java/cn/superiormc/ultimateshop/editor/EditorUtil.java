package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditorUtil {

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
        lore.add(EditorLang.text(player, "editor.summary.value", "&7Value: &f{value}",
                "value", trim(String.valueOf(value), 48)));
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
}
