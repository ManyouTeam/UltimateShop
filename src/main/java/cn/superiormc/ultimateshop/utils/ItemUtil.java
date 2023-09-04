package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import com.cryptomorin.xseries.XItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {
    
    public static ItemStack buildItemStack(ConfigurationSection section, int amount) {
        ItemStack resultItem;
        section.set("amount", amount);
        if (section.contains("hook-item")) {
            String pluginName = section.getString("hook-plugin");
            String itemID = section.getString("hook-item");
            if (pluginName == null || itemID == null) {
                return new ItemStack(Material.STONE);
            }
            if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                itemID = section.getString("hook-item-type") + ";;" + itemID;
            } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                itemID = itemID + ";;" + section.getString("hook-item-type");
            }
            resultItem = ItemsHook.getHookItem(pluginName,
                    itemID);
            if (resultItem == null) {
                return new ItemStack(Material.STONE);
            }
            else {
                resultItem.setAmount(amount);
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
            resultItem.setAmount(amount);
            return resultItem;
        }
    }

}
