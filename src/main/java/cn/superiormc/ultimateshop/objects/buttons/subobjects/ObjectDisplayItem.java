package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ObjectDisplayItem{

    private ConfigurationSection section;

    private ItemStack displayItem;

    private ObjectItem item;

    public ObjectDisplayItem(ConfigurationSection section, ObjectItem item) {
        this.section = section;
        this.item = item;
        initDisplayItem();
    }

    private void initDisplayItem() {
        // 显示物品
        if (section.contains("hook-item")) {
            displayItem = ItemsHook.getHookItem(section.getString("hook-plugin"),
                    section.getString("hook-item"));
        }
        else {
            displayItem = ItemUtil.buildItemStack(section);
        }
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStack getDisplayItem(Player player) {
        return getDisplayItem(player, 1);
    }

    public ItemStack getDisplayItem(Player player, int multi) {
        ItemStack addLoreDisplayItem = null;
        if (section == null || ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
            addLoreDisplayItem = item.getReward().getDisplayItem();
            if (addLoreDisplayItem == null) {
                addLoreDisplayItem = item.getReward().getDisplayItem();
            }
        }
        else {
            addLoreDisplayItem = displayItem.clone();
        }
        return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item);
    }


}
