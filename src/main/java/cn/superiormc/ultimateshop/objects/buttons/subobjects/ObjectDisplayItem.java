package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectDisplayItem{

    private ConfigurationSection section;

    private ObjectItem item;

    public ObjectDisplayItem(ConfigurationSection section, ObjectItem item) {
        this.section = section;
        this.item = item;
    }

    public ItemStack getDisplayItem(Player player) {
        ItemStack addLoreDisplayItem = null;
        if (section == null && ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
            addLoreDisplayItem = item.getReward().getDisplayItem(section, player, false, 0, 1);
            if (addLoreDisplayItem == null) {
                addLoreDisplayItem = new ItemStack(Material.STONE);
            }
        }
        else {
            // 显示物品
            PlayerCache playerCache = CacheManager.cacheManager.playerCacheMap.get(player);
            int value = 0;
            if (playerCache != null && playerCache.getUseTimesCache().get(item) != null) {
                if (item.getBuyPrice().empty && !item.getSellPrice().empty) {
                    value = playerCache.getUseTimesCache().get(item).getSellUseTimes();
                }
                else {
                    value = playerCache.getUseTimesCache().get(item).getBuyUseTimes();
                }
            }
            ConfigurationSection tempVal3 = section.getConfigurationSection(
                    String.valueOf(value));
            if (section.getName().equals("display-item") && tempVal3 != null) {
                ItemStack displayItem = ItemUtil.buildItemStack(player, tempVal3, (int) Double.parseDouble(
                        (TextUtil.withPAPI(tempVal3.getString("amount", "1"), player))));
                addLoreDisplayItem = displayItem.clone();
            }
            else {
                ItemStack displayItem = ItemUtil.buildItemStack(player, section, (int) Double.parseDouble(
                        (TextUtil.withPAPI(section.getString("amount", "1"), player))));
                addLoreDisplayItem = displayItem.clone();
            }
        }
        return addLoreDisplayItem;
    }

    public ItemStack getDisplayItem(Player player, int multi) {
        ItemStack addLoreDisplayItem = getDisplayItem(player);
        if (section.getBoolean("modify-lore", true)) {
            return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item, !item.getBuyMore());
        }
        return addLoreDisplayItem;
    }

}
