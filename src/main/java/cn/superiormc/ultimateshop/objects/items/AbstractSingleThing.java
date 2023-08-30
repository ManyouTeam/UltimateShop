package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.hooks.EconomyHook;
import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.hooks.PriceHook;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
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
        initType();
        this.empty = true;
    }

    public AbstractSingleThing(ConfigurationSection singleSection) {
        this.singleSection = singleSection;
        initType();
        this.empty = false;
    }

    private void initType() {
        if (singleSection == null) {
            type = "free";
        } else if (singleSection.contains("hook-plugin") && singleSection.contains("hook-item")) {
            type = "hook";
        } else if (singleSection.contains("material")) {
            type = "vanilla";
        } else if (singleSection.contains("economy-plugin")) {
            type = "economy";
        } else if (singleSection.contains("economy-type") && !singleSection.contains("economy-plugin")) {
            type = "exp";
        } else if (singleSection.contains("type")) {
            type = "custom";
        } else {
            type = "free";
        }
    }

    public boolean playerHasEnough(Player player, boolean take, int times) {
        return false;
    }

    public void playerGive(Player player, int times) {
        if (singleSection == null) {
            return;
        }
        double cost = getAmount(times);
        switch (type) {
            case "vanilla":  case "hook":
                if (getItemThing(singleSection, player, true, times) == null) {
                    return;
                }
                return;
            case "economy" :
                EconomyHook.giveEconomy(singleSection.getString("economy-plugin"),
                        singleSection.getString("economy-type", "Unkown"),
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

    public double getAmount(int times) {
        return singleSection.getDouble("amount", 1);
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

    public boolean checkHasEnough(Player player, boolean take, int times) {
        return checkHasEnough(singleSection, player, take, times);
    }

    public boolean checkHasEnough(ConfigurationSection singleSection, Player player, boolean take, int times) {
        ItemStack itemStack;
        double cost = getAmount(times);
        if (cost == -1) {
            return false;
        }
        switch (type) {
            case "hook":
                String pluginName = singleSection.getString("hook-plugin", "");
                String itemID = singleSection.getString("hook-item", "");
                if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                    itemID = singleSection.getString("hook-item-type") + ";;" + itemID;
                } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                    itemID = itemID + ";;" + singleSection.getString("hook-item-type");
                }
                itemStack = ItemsHook.getHookItem(pluginName, itemID);
                return PriceHook.getPrice(player,
                        itemStack,
                        (int) cost, take);
            case "vanilla":
                itemStack = ItemUtil.buildItemStack(singleSection);
                if (itemStack == null) {
                    return false;
                }
                itemStack.setAmount(1);
                return PriceHook.getPrice(player, itemStack, (int) cost, take);
            case "economy":
                return PriceHook.getPrice(singleSection.getString("economy-plugin"),
                        singleSection.getString("economy-type", "default"),
                        player,
                        cost, take);
            case "exp":
                return PriceHook.getPrice(singleSection.getString("economy-type"),
                        player,
                        (int) cost, take);
            case "unknwon":
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §c" +
                        "There is something wrong in your shop configs!");
                return false;
        }
        return false;
    }

    public ItemStack getItemThing(ConfigurationSection section, Player player, boolean give,
                                  int times) {
        if (singleSection == null) {
            return null;
        }
        double cost = getAmount(times);
        ItemStack itemStack;
        switch (type) {
            case "vanilla":
                itemStack = ItemUtil.buildItemStack(singleSection);
                itemStack.setAmount((int) cost);
                if (give) {
                    if (player.getInventory().addItem(itemStack).size() != 0) {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                        LanguageManager.languageManager.sendStringText(player,
                                "plugin.item-dropped",
                                "item",
                                itemStack.getItemMeta().hasDisplayName() ?
                                        itemStack.getItemMeta().getDisplayName() :
                                        itemStack.getType().name());
                    }
                }
                return itemStack;

            case "hook":
                String pluginName = singleSection.getString("hook-plugin");
                String itemID = singleSection.getString("hook-item");
                if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                    itemID = singleSection.getString("hook-item-type") + ";;" + itemID;
                } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                    itemID = itemID + ";;" + singleSection.getString("hook-item-type");
                }
                itemStack = ItemsHook.getHookItem(pluginName,
                        itemID);
                if (itemStack == null) {
                    return null;
                }
                itemStack.setAmount((int) cost);
                if (give) {
                    if (player.getInventory().addItem(itemStack).size() != 0) {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                        LanguageManager.languageManager.sendStringText(player, "plugin.item-dropped");
                    }
                }
                return itemStack;
        }
        return null;
    }

}
