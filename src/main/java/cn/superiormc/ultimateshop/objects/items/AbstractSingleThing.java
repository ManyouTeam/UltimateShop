package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.hooks.EconomyHook;
import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.hooks.PriceHook;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import com.cryptomorin.xseries.XItemStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractSingleThing {

    public String type;

    public ConfigurationSection singleSection;

    public ObjectCondition condition;

    public boolean empty;

    public AbstractSingleThing() {
        initType(null);
        this.empty = true;
    }

    public AbstractSingleThing(ConfigurationSection singleSection) {
        this.singleSection = singleSection;
        if (singleSection.contains("custom-type")) {
            initType(ConfigManager.configManager.config.getConfigurationSection("prices." +
                    singleSection.getString("custom-type")));
        }
        else {
            initType(singleSection);
        }
        this.empty = false;
    }

    private void initType(ConfigurationSection section) {
        if (section == null) {
            type = "unknown";
        } else if (section.contains("hook-plugin") && section.contains("hook-item")) {
            type = "hook";
        } else if (section.contains("material")) {
            type = "vanilla";
        } else if (section.contains("economy-plugin")) {
            type = "economy";
        } else if (section.contains("economy-type") && !section.contains("economy-plugin")) {
            type = "exp";
        } else {
            type = "free";
        }
    }

    public void playerGive(Player player,
                           int times,
                           int classic_multi) {
        if (singleSection == null) {
            return;
        }
        double cost = getAmount(player, times);
        switch (type) {
            case "vanilla":  case "hook":
                if (getItemThing(singleSection,
                        player,
                        true,
                        times,
                        classic_multi) == null) {
                    return;
                }
                return;
            case "economy" :
                EconomyHook.giveEconomy(singleSection.getString("economy-plugin"),
                        singleSection.getString("economy-type", "Unknown"),
                        player,
                        cost);

                return;
            case "exp" :
                EconomyHook.giveEconomy(singleSection.getString("economy-type"),
                        player,
                        (int) cost);
                return;
        }
    }

    public double getAmount(Player player, int times) {
        String tempVal1 = singleSection.getString("amount", "1");
        return Double.parseDouble(TextUtil.withPAPI(tempVal1, player));
    }

    public boolean getCondition(Player player) {
        List<String> conditions = singleSection.getStringList("conditions");
        if (conditions.isEmpty()) {
            condition = new ObjectCondition();
        }
        else {
            condition = new ObjectCondition(conditions);
        }
        if (!condition.getBoolean(player)) {
            return false;
        }
        return true;
    }

    public boolean checkHasEnough(Player player,
                                  boolean take,
                                  int times,
                                  int classic_multi) {
        return checkHasEnough(singleSection, player, take, times, classic_multi);
    }

    public boolean checkHasEnough(ConfigurationSection section,
                                  Player player,
                                  boolean take,
                                  int times,
                                  int classic_multi) {
        if (section == null) {
            return false;
        }
        double cost = getAmount(player, times) * classic_multi;
        if (cost < 0) {
            return false;
        }
        switch (type) {
            case "hook":
                String pluginName = section.getString("hook-plugin", "");
                String itemID = section.getString("hook-item", "");
                if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                    itemID = section.getString("hook-item-type") + ";;" + itemID;
                } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                    itemID = itemID + ";;" + section.getString("hook-item-type");
                }
                return PriceHook.getPrice(pluginName,
                        itemID,
                        player,
                        (int) cost, take);
            case "vanilla":
                ItemStack itemStack = getItemThing(section, player, false, times, classic_multi);
                if (itemStack == null) {
                    return false;
                }
                itemStack.setAmount(1);
                return PriceHook.getPrice(player, itemStack, (int) cost, take);
            case "economy":
                return PriceHook.getPrice(section.getString("economy-plugin"),
                        section.getString("economy-type", "default"),
                        player,
                        cost, take);
            case "exp":
                return PriceHook.getPrice(section.getString("economy-type"),
                        player,
                        (int) cost, take);
            case "unknwon":
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §c" +
                        "There is something wrong in your shop configs!");
                return false;
        }
        return false;
    }

    public ItemStack getItemThing(ConfigurationSection section,
                                  Player player,
                                  boolean give,
                                  int times,
                                  int classic_multi) {
        if (singleSection == null) {
            return null;
        }
        double cost = getAmount(player, times) * classic_multi;
        ItemStack itemStack;
        itemStack = ItemUtil.buildItemStack(singleSection, (int) cost);
        if (itemStack == null) {
            return null;
        }
        if (give) {
            XItemStack.giveOrDrop(player, itemStack);
        }
        return itemStack;
    }

}
