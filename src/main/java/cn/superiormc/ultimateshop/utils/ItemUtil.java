package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.hooks.CheckValidHook;
import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.managers.ItemManager;
import com.cryptomorin.xserieschanged.XItemStack;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Function;

public class ItemUtil {
    
    public static ItemStack buildItemStack(Player player, ConfigurationSection section, int amount) {
        if (section.getString("name") != null) {
            section.set("name", TextUtil.parse(TextUtil.withPAPI(section.getString("name"), player)));
        }
        List<String> loreList = new ArrayList<>();
        for (String s : section.getStringList("lore")) {
            loreList.add(TextUtil.parse(TextUtil.withPAPI(s, player), player));
        }
        if (loreList.isEmpty() && section.getString("lore") != null) {
            loreList.add(TextUtil.parse(TextUtil.withPAPI(section.getString("lore"), player)));
        }
        if (!loreList.isEmpty()) {
            section.set("lore", loreList);
        }
        ItemStack resultItem;
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
                XItemStack.edit(resultItem, section, Function.identity(), null);
                resultItem.setAmount(amount);
            }
        }
        else {
            if (section.getString("material") == null) {
                return new ItemStack(Material.STONE);
            }
            if (ItemManager.itemManager.getItemByKey(section.getString("material")) != null) {
                resultItem = ItemManager.itemManager.getItemByKey(section.getString("material"));
            }
            else {
                if (Material.getMaterial(section.getString("material").toUpperCase()) == null) {
                    return new ItemStack(Material.STONE);
                }
                else {
                    resultItem = XItemStack.deserialize(section);
                }
            }
            resultItem.setAmount(amount);
        }
        ConfigurationSection tempVal1 = section.getConfigurationSection("plugin-enchants");
        if (!UltimateShop.freeVersion && tempVal1 != null) {
            if (CommonUtil.checkPluginLoad("AdvancedEnchantments")) {
                for (String enchantName : tempVal1.getKeys(false)) {
                    resultItem = AEAPI.applyEnchant(enchantName, tempVal1.getInt(enchantName), resultItem);
                }
            }
        }
        if (!section.getStringList("add-lore").isEmpty()) {
            ItemMeta tempVal2 = resultItem.getItemMeta();
            if (tempVal2 != null) {
                List<String> tempVal3 = new ArrayList<>();
                if (tempVal2.hasLore()) {
                    tempVal3 = tempVal2.getLore();
                }
                tempVal3.addAll(TextUtil.getListWithColor(section.getStringList("add-lore")));
                tempVal2.setLore(tempVal3);
                resultItem.setItemMeta(tempVal2);
            }
        }
        return resultItem;
    }

    public static Map<String, Object> debuildItem(ItemStack itemStack) {
        Map<String, Object> resultMap = new HashMap<>();
        if (CheckValidHook.checkValid(itemStack) != null) {
            resultMap.put("hook-plugin", CheckValidHook.checkValid(itemStack)[0]);
            resultMap.put("hook-item", CheckValidHook.checkValid(itemStack)[1]);
        }
        resultMap.putAll(XItemStack.serialize(itemStack));
        if (resultMap.containsKey("hook-item")) {
            resultMap.remove("material");
        }
        return resultMap;
    }

}
