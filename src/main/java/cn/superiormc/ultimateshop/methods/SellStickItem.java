package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SellStickItem {

    public static final NamespacedKey SELL_STICK_TIMES = new NamespacedKey(UltimateShop.instance, "sell_stick_usage");
    public static final NamespacedKey SELL_STICK_ID = new NamespacedKey(UltimateShop.instance, "sell_stick_id");

    public static ItemStack getExtraSlotItem(Player player, String itemID, int amount) {
        ConfigurationSection section = ConfigManager.configManager.config.getConfigurationSection(
                "sell-stick-items." + itemID
        );
        if (section == null) {
            LanguageManager.languageManager.sendStringText("error-item-not-found",
                    "item",
                    itemID);
            return null;
        }
        return getExtraSlotItem(player, itemID, amount, section.getInt("usage-times"));
    }

    public static ItemStack getExtraSlotItem(Player player, String itemID, int amount, int times) {
        ConfigurationSection section = ConfigManager.configManager.config.getConfigurationSection(
                "sell-stick-items." + itemID
        );
        if (section == null) {
            LanguageManager.languageManager.sendStringText("error-item-not-found",
                    "item",
                    itemID);
            return null;
        }
        ItemStack resultItem = ItemUtil.buildItemStack(player, section, 1);
        resultItem.setAmount(amount);
        if (!resultItem.hasItemMeta()) {
            ItemMeta tempMeta = Bukkit.getItemFactory().getItemMeta(resultItem.getType());
            resultItem.setItemMeta(tempMeta);
        }
        ItemMeta meta = resultItem.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta.hasLore()) {
            for (String str : meta.getLore()) {
                str = str.replace("{times}", String.valueOf(times));
                newLore.add(str);
            }
            meta.setLore(newLore);
        }
        meta.getPersistentDataContainer().remove(SELL_STICK_ID);
        meta.getPersistentDataContainer().remove(SELL_STICK_TIMES);
        meta.getPersistentDataContainer().set(SELL_STICK_ID,
                PersistentDataType.STRING,
                itemID);
        meta.getPersistentDataContainer().set(SELL_STICK_TIMES,
                PersistentDataType.INTEGER,
                times);
        resultItem.setItemMeta(meta);
        return resultItem;
    }

    public static String getExtraSlotItemID(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(SELL_STICK_ID, PersistentDataType.STRING)) {
            return null;
        }
        return meta.getPersistentDataContainer().get(SELL_STICK_ID, PersistentDataType.STRING);
    }

    public static int getExtraSlotItemValue(ItemStack item) {
        if (!item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(SELL_STICK_TIMES, PersistentDataType.INTEGER)) {
            return 0;
        }
        return meta.getPersistentDataContainer().get(SELL_STICK_TIMES, PersistentDataType.INTEGER);
    }

    public static void removeExtraSlotItemValue(Player player, ItemStack item) {
        if (!item.hasItemMeta()) {
            return;
        }
        int nowValue = getExtraSlotItemValue(item);
        String id = getExtraSlotItemID(item);
        if (id == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not found sell stick item ID");
        }
        else {
            item.setAmount(item.getAmount() - 1);
            if (nowValue - 1 > 0) {
                ItemStack tempItem = getExtraSlotItem(player, id, 1, nowValue - 1);
                if (tempItem != null) {
                    player.getInventory().addItem(tempItem);
                }
            }
        }
    }
}
