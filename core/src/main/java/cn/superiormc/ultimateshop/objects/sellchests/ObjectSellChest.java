package cn.superiormc.ultimateshop.objects.sellchests;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static cn.superiormc.ultimateshop.managers.SellChestManager.SELL_CHEST_ID;
import static cn.superiormc.ultimateshop.managers.SellChestManager.SELL_CHEST_TIMES;

public class ObjectSellChest {

    private final String id;

    private final ConfigurationSection section;

    private final ObjectAction action;

    private final ObjectCondition condition;

    private final int usageTimes;

    private final boolean infinite;

    private final double multiplier;

    private final List<String> holograms;

    public ObjectSellChest(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.action = new ObjectAction(section.getConfigurationSection("actions"));
        this.condition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.usageTimes = section.getInt("usage-times");
        this.multiplier = section.getDouble("multiplier");
        this.infinite = usageTimes < 0 || section.getBoolean("infinite");
        this.holograms = section.getStringList("holograms");
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded sell stick: " + id + ".yml!");
    }

    public String getID() {
        return id;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

    public ObjectAction getAction() {
        return action;
    }

    public int getUsageTimes() {
        return usageTimes;
    }

    public List<String> getHolograms() {
        return holograms;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public ItemStack getNewItem(int amount) {
        return getItemWithUsageTimes(amount, usageTimes);
    }

    public ItemStack getItemWithUsageTimes(int amount, int times) {
        ItemStack resultItem;
        if (section.contains("display-item")) {
            resultItem = BuildItem.buildItemStack(null, section.getConfigurationSection("display-item"), 1);
        } else {
            resultItem = BuildItem.buildItemStack(null, section, 1);
        }
        if (resultItem.getType() != Material.CHEST) {
            resultItem.setType(Material.CHEST);
        }
        resultItem.setAmount(amount);
        if (!resultItem.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = resultItem.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta.hasLore()) {
            for (String str : UltimateShop.methodUtil.getItemLore(meta)) {
                if (!infinite) {
                    str = CommonUtil.modifyString(null, str, "times", String.valueOf(times));
                } else {
                    str = CommonUtil.modifyString(null, str, "times", ConfigManager.configManager.getString("placeholder.sell-chest.infinite"));
                }
                newLore.add(str);
            }
            UltimateShop.methodUtil.setItemLore(meta, newLore, null);
        }
        meta.getPersistentDataContainer().remove(SELL_CHEST_ID);
        meta.getPersistentDataContainer().remove(SELL_CHEST_TIMES);
        meta.getPersistentDataContainer().set(SELL_CHEST_ID,
                PersistentDataType.STRING,
                id);
        if (!infinite) {
            meta.getPersistentDataContainer().set(SELL_CHEST_TIMES,
                    PersistentDataType.INTEGER,
                    times);
        }
        resultItem.setItemMeta(meta);
        return resultItem;
    }

    public static int getSellChestValue(ItemStack item) {
        if (item == null) {
            return 0;
        }
        if (!item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(SELL_CHEST_TIMES, PersistentDataType.INTEGER)) {
            return 0;
        }
        return meta.getPersistentDataContainer().get(SELL_CHEST_TIMES, PersistentDataType.INTEGER);
    }

    public double getYOffset() {
        return section.getDouble("y-offset", 2.25);
    }
}
