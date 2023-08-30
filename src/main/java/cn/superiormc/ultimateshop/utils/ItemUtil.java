package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import com.cryptomorin.xseries.XItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    
    public static ItemStack buildItemStack(ConfigurationSection section) {
        ItemStack resultItem;
        if (section.contains("hook-item")) {
            resultItem = ItemsHook.getHookItem(section.getString("hook-plugin"), section.getString("hook-item"));
            if (resultItem == null) {
                return new ItemStack(Material.BEDROCK);
            }
            else {
                return resultItem;
            }
        }
        else {
            if (section.getString("material") == null) {
                return null;
            }
            if (section.getString("name") != null) {
                section.set("name", TextUtil.parse(section.getString("name")));
            }
            List<String> loreList = new ArrayList<>();
            for (String s : section.getStringList("lore")) {
                loreList.add(TextUtil.parse(s));
            }
            if (loreList.isEmpty() && section.getString("lore") != null) {
                loreList.add(section.getString("lore"));
            }
            if (!loreList.isEmpty()) {
                section.set("lore", loreList);
            }
            resultItem = XItemStack.deserialize(section);
            return resultItem;
        }
    }

}
