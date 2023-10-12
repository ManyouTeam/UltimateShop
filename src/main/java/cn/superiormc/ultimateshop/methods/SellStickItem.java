package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SellStickItem {

    public static final NamespacedKey SELL_STICK = new NamespacedKey(UltimateShop.instance, "sell_stick_usage");

    public static ItemStack getExtraSlotItem(String itemID, int amount) {
        ItemStack resultItem;
        ConfigurationSection section = ConfigManager.configManager.config.getConfigurationSection(
                "sell-stick-items." + itemID
        );
        if (section == null) {
            return null;
        }
        resultItem = ItemUtil.buildItemStack(section, 1);
        if (!resultItem.hasItemMeta()) {
            ItemMeta tempMeta = Bukkit.getItemFactory().getItemMeta(resultItem.getType());
            resultItem.setItemMeta(tempMeta);
        }
        ItemMeta meta = resultItem.getItemMeta();
        meta.getPersistentDataContainer().set(SELL_STICK,
                PersistentDataType.INTEGER,
                section.getInt("usage-times", amount));
        resultItem.setItemMeta(meta);
        return resultItem;
    }

    public static int getExtraSlotItemValue(ItemStack item) {
        if (!item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(SELL_STICK, PersistentDataType.INTEGER)) {
            return 0;
        }
        return meta.getPersistentDataContainer().get(SELL_STICK, PersistentDataType.INTEGER);
    }

    public static void removeExtraSlotItemValue(ItemStack item) {
        if (!item.hasItemMeta()) {
            return;
        }
        int nowValue = getExtraSlotItemValue(item);
        if (nowValue - 1 <= 0) {
            item.setAmount(0);
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(SELL_STICK, PersistentDataType.INTEGER)) {
            meta.getPersistentDataContainer().set(SELL_STICK, PersistentDataType.INTEGER,
                    nowValue - 1);
        }
    }
}
