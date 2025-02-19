package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ObjectSellStick {

    public static final NamespacedKey SELL_STICK_TIMES = new NamespacedKey(UltimateShop.instance, "sell_stick_usage");

    public static final NamespacedKey SELL_STICK_ID = new NamespacedKey(UltimateShop.instance, "sell_stick_id");

    private final String id;

    private final ConfigurationSection section;

    private final ObjectAction action;

    private final ObjectCondition condition;

    private final int usageTimes;

    private final boolean infinite;

    private final double multiplier;

    public ObjectSellStick(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.action = new ObjectAction(section.getConfigurationSection("actions"));
        this.condition = new ObjectCondition(section.getConfigurationSection("conditions"));
        this.usageTimes = section.getInt("usage-times");
        this.multiplier = section.getDouble("multiplier");
        this.infinite = usageTimes < 0 || section.getBoolean("infinite");
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded sell stick: " + id + ".yml!");
    }

    public String getID() {
        return id;
    }

    public ItemStack getNewItem(Player player, int amount) {
        return getItemWithUsageTimes(player, amount, usageTimes);
    }

    public ItemStack getItemWithUsageTimes(Player player, int amount, int times) {
        ItemStack resultItem;
        if (section.contains("display-item")) {
            resultItem = BuildItem.buildItemStack(player, section.getConfigurationSection("display-item"), 1);
        } else {
            resultItem = BuildItem.buildItemStack(player, section, 1);
        }
        resultItem.setAmount(amount);
        if (!resultItem.hasItemMeta()) {
            ItemMeta tempMeta = Bukkit.getItemFactory().getItemMeta(resultItem.getType());
            resultItem.setItemMeta(tempMeta);
        }
        ItemMeta meta = resultItem.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta.hasLore()) {
            for (String str : meta.getLore()) {
                if (!infinite) {
                    str = CommonUtil.modifyString(str, "times", String.valueOf(times));
                } else {
                    str = CommonUtil.modifyString(str, "times", TextUtil.parse(player, ConfigManager.configManager.getString("placeholder.sell-stick.infinite")));
                }
                newLore.add(str);
            }
            meta.setLore(newLore);
        }
        meta.getPersistentDataContainer().remove(SELL_STICK_ID);
        meta.getPersistentDataContainer().remove(SELL_STICK_TIMES);
        meta.getPersistentDataContainer().set(SELL_STICK_ID,
                PersistentDataType.STRING,
                id);
        if (!infinite) {
            meta.getPersistentDataContainer().set(SELL_STICK_TIMES,
                    PersistentDataType.INTEGER,
                    times);
        }
        resultItem.setItemMeta(meta);
        return resultItem;
    }

    public void takeUsageTimes(Player player, ItemStack item) {
        if (item == null) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        if (infinite) {
            return;
        }
        int nowValue = getSellStickValue(item);
        item.setAmount(item.getAmount() - 1);
        if (nowValue - 1 > 0) {
            ItemStack tempItem = getItemWithUsageTimes(player, 1, nowValue - 1);
            if (tempItem != null) {
                CommonUtil.giveOrDrop(player, tempItem);
            }
        }
    }

    public static int getSellStickValue(ItemStack item) {
        if (item == null) {
            return 0;
        }
        if (!item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(SELL_STICK_TIMES, PersistentDataType.INTEGER)) {
            return 0;
        }
        return meta.getPersistentDataContainer().get(SELL_STICK_TIMES, PersistentDataType.INTEGER);
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
}
