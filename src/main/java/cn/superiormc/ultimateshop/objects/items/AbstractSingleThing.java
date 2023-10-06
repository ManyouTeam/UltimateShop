package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.hooks.EconomyHook;
import cn.superiormc.ultimateshop.hooks.PriceHook;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
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
                           double cost) {
        if (singleSection == null) {
            return;
        }
        switch (type) {
            case "vanilla":  case "hook":
                if (getItemThing(singleSection,
                        player,
                        true,
                        cost) == null) {
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

    public abstract double getAmount(Player player, int times);

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

    public double playerHasAmount(Player player) {
        return playerHasAmount(singleSection, player);
    }

    public double playerHasAmount(ConfigurationSection section, Player player) {
        if (section == null) {
            return 0;
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
                return PriceHook.getItemAmount(player,
                        pluginName,
                        itemID);
            case "vanilla":
                ItemStack itemStack = getItemThing(section, player, false, 1);
                if (itemStack == null) {
                    return 0;
                }
                itemStack.setAmount(1);
                return PriceHook.getItemAmount(player, itemStack);
            case "economy":
                return PriceHook.getEconomyAmount(player, section.getString("economy-plugin"),
                        section.getString("economy-type", "default"));
            case "exp":
                return PriceHook.getEconomyAmount(player,
                        section.getString("economy-type"));
            case "unknwon":
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §c" +
                        "There is something wrong in your shop configs!");
                return 0;
        }
        return 0;
    }

    public boolean playerHasEnough(Player player,
                                   boolean take,
                                   double cost) {
        return playerHasEnough(singleSection,
                player,
                take,
                cost);
    }

    public boolean playerHasEnough(ConfigurationSection section,
                                   Player player,
                                   boolean take,
                                   double cost) {
        if (section == null) {
            return false;
        }

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
                return PriceHook.getPrice(player,
                        pluginName,
                        itemID,
                        (int) cost, take);
            case "vanilla":
                ItemStack itemStack = getItemThing(section, player, false, 1);
                if (itemStack == null) {
                    return false;
                }
                itemStack.setAmount(1);
                return PriceHook.getPrice(player, itemStack, (int) cost, take);
            case "economy":
                return PriceHook.getPrice(player,
                        section.getString("economy-plugin"),
                        section.getString("economy-type", "default"),
                        cost, take);
            case "exp":
                return PriceHook.getPrice(player,
                        section.getString("economy-type"),
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
                                  double cost) {
        if (section == null) {
            if (singleSection == null) {
                return null;
            }
            section = singleSection;
        }
        ItemStack itemStack;
        itemStack = ItemUtil.buildItemStack(section, (int) cost);
        if (itemStack == null) {
            return null;
        }
        if (give) {
            XItemStack.giveOrDrop(player, itemStack);
        }
        return itemStack;
    }

    public abstract String getDisplayName(double amount);

}
