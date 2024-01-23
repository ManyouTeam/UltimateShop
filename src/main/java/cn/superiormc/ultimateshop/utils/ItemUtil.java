package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.hooks.CheckValidHook;
import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.libs.xserieschanged.XItemStack;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemUtil {
    
    public static ItemStack buildItemStack(Player player, ConfigurationSection section, int amount) {
        ItemStack resultItem = new ItemStack(Material.STONE);
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
                XItemStack.edit(resultItem, section, player);
                resultItem.setAmount(amount);
            }
        }
        if (section.getString("material") != null &&
                ItemManager.itemManager.getItemByKey(section.getString("material")) != null) {
            resultItem = ItemManager.itemManager.getItemByKey(section.getString("material"));
        }
        resultItem = XItemStack.edit(resultItem, section, player);
        resultItem.setAmount(amount);
        ConfigurationSection tempVal1 = section.getConfigurationSection("plugin-enchants");
        if (!UltimateShop.freeVersion && tempVal1 != null) {
            if (CommonUtil.checkPluginLoad("AdvancedEnchantments")) {
                for (String enchantName : tempVal1.getKeys(false)) {
                    resultItem = AEAPI.applyEnchant(enchantName, tempVal1.getInt(enchantName), resultItem);
                }
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
