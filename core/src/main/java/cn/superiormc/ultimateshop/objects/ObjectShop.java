package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectCopyItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectShop {

    private final YamlConfiguration config;

    private final Map<String, ObjectItem> items = new HashMap<>();

    private final Map<String, ObjectCopyItem> copyItems = new HashMap<>();

    public Map<String, AbstractButton> buttonItems = new HashMap<>();

    private final String shopName;

    private ObjectMenu menu;

    public ObjectShop(String fileName, YamlConfiguration config) {
        this.shopName = fileName;
        this.config = config;
        initProducts();
        initButtonItems();
        if (!UltimateShop.freeVersion) {
            initCustomCommand();
        }
    }

    private void initProducts() {
        if (config.getConfigurationSection("items") == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get items section in your shop config!!");
            return;
        }
        for (String s : config.getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection itemSection = config.getConfigurationSection("items." + s);
            if (itemSection == null) {
                continue;
            }
            String tempVal1 = itemSection.getString("as-sub-button");
            if (tempVal1 != null) {
                copyItems.put(s, null);
                continue;
            }
            items.put(s, new ObjectItem(this, itemSection));
        }
    }

    public void initCopyProducts() {
        for (String s : copyItems.keySet()) {
            if (items.containsKey(s)) {
                copyItems.remove(s);
                continue;
            }
            ConfigurationSection itemSection = config.getConfigurationSection("items." + s);
            if (itemSection == null) {
                continue;
            }
            String tempVal1 = itemSection.getString("as-sub-button");
            if (tempVal1 != null) {
                String[] tempVal2 = tempVal1.split(";;");
                if (tempVal2.length >= 2) {
                    ObjectShop shop = ConfigManager.configManager.getShop(tempVal2[0]);
                    if (shop != null) {
                        ObjectItem item = shop.getProduct(tempVal2[1]);
                        if (item != null) {
                            items.put(s, item);
                            copyItems.put(s, new ObjectCopyItem(itemSection, item));
                        }
                    }
                } else {
                    ObjectItem item = items.get(tempVal1);
                    if (item != null) {
                        items.put(s, item);
                        copyItems.put(s, new ObjectCopyItem(itemSection, item));
                    }
                }
            }
        }
    }

    public void initMenus() {
        if (config.getString("settings.menu") != null) {
            this.menu = new ObjectMenu(config.getString("settings.menu"), this);
        }
    }

    private void initButtonItems() {
        ConfigurationSection tempVal1 = config.getConfigurationSection("buttons");
        if (tempVal1 == null) {
            return;
        }
        for (String button : tempVal1.getKeys(false)) {
            buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button), this));
        }
    }

    private void initCustomCommand() {
        String commandName = config.getString("settings.custom-command.name");
        if (commandName != null && !commandName.isEmpty()) {
            ObjectShop shop = this;
            BukkitCommand command = new BukkitCommand(commandName) {
                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    if (!(sender instanceof Player)) {
                        LanguageManager.languageManager.sendStringText("error.in-game");
                        return true;
                    }
                    ShopGUI.openGUI((Player) sender, shop, false, false);
                    return true;
                }
            };
            command.setDescription(config.getString("settings.custom-command.description", "UltimateShop Custom Command for " + commandName));
            CommandUtil.registerCustomCommand(command);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fRegistered custom command for shop: " + shopName + ".");
        }
    }

    public YamlConfiguration getShopConfig() {
        return config;
    }

    public AbstractButton getButton(String buttonID) {
        if (buttonID == null) {
            return null;
        }
        return buttonItems.get(buttonID);
    }

    public ObjectItem getProduct(String productID) {
        if (productID == null) {
            return null;
        }
        return items.get(productID);
    }

    public ObjectCopyItem getCopyItem(String itemID) {
        if (itemID == null) {
            return null;
        }
        return copyItems.get(itemID);
    }

    public List<ObjectItem> getProductList() {
        List<ObjectItem> resultItems = new ArrayList<>();
        for (String key : items.keySet()) {
            resultItems.add(items.get(key));
        }
        return resultItems;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopMenu() {
        if (menu == null) {
            return "";
        }
        return menu.getName();
    }

    @Nullable
    public ObjectMenu getShopMenuObject() {
        return menu;
    }

    public String getShopDisplayName() {
        return config.getString("settings.shop-name", menu == null ? "" : menu.getName());
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return getShopName();
    }

}
