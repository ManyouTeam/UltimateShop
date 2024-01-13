package cn.superiormc.ultimateshop.objects.menus;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMenu {

    public MenuType type;

    public static Map<String, ObjectMenu> commonMenus = new HashMap<>();

    public static Map<ObjectShop, ObjectMenu> shopMenus = new HashMap<>();

    public static List<String> shopMenuNames = new ArrayList<>();

    public String fileName;

    private ObjectShop shop = null;

    private ObjectCondition condition;

    public Configuration menuConfigs;

    public Map<Integer, AbstractButton> menuItems = new HashMap<>();

    public Map<String, AbstractButton> buttonItems = new HashMap<>();

    public ObjectMenu(String fileName, ObjectShop shop) {
        this.fileName = fileName;
        this.shop = shop;
        this.type = MenuType.Shop;
        initMenu();
        initShopItems();
        initButtonItems();
    }

    public ObjectMenu(String fileName, ObjectItem item) {
        this.fileName = fileName;
        this.shop = item.getShopObject();
        this.type = MenuType.More;
        initMenu();
        initButtonItems();
    }

    public ObjectMenu(String fileName) {
        this.fileName = fileName;
        this.type = MenuType.Common;
        initMenu();
        initButtonItems();
    }

    public MenuType getType() {
        return type;
    }

    public void initMenu() {
        if (type == MenuType.Common) {
            commonMenus.put(fileName, this);
        }
        else {
            shopMenus.put(shop, this);
            shopMenuNames.add(fileName);
        }
        File file = new File(UltimateShop.instance.getDataFolder() + "/menus/" + fileName + ".yml");
        if (!file.exists()){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: We can not found your menu file: " +
                    fileName + ".yml!");
        }
        else {
            if (type == MenuType.Common) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded menu: " +
                        fileName + ".yml!");
            }
            this.menuConfigs = YamlConfiguration.loadConfiguration(file);
        }
        if (menuConfigs == null) {
            this.condition = new ObjectCondition();
        }
        else {
            this.condition = new ObjectCondition(menuConfigs.getStringList("conditions"));
        }
    }

    public void initShopItems() {
        int i = 0;
        if (menuConfigs == null) {
            return;
        }
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (shop.getProduct(String.valueOf(itemChar)) == null) {
                    if (shop.getButton(String.valueOf(itemChar)) == null) {
                        continue;
                    }
                    menuItems.put(slot, shop.getButton(String.valueOf(itemChar)));
                    continue;
                }
                menuItems.put(slot, shop.
                        getProduct(String.valueOf(itemChar)));
            }
        }
    }

    public void initButtonItems() {
        if (menuConfigs == null) {
            return;
        }
        ConfigurationSection tempVal1 = menuConfigs.getConfigurationSection("buttons");
        if (tempVal1 == null) {
            return;
        }
        for (String button : tempVal1.getKeys(false)) {
            if (shop == null) {
                buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button)));
            }
            else {
                buttonItems.put(button, new ObjectButton(tempVal1.getConfigurationSection(button),
                        shop));
            }
        }
        int i = 0;
        for (String singleLine : menuConfigs.getStringList("layout")) {
            for (int c = 0 ; c < singleLine.length() ; c ++) {
                char itemChar = singleLine.charAt(c);
                int slot = i;
                i ++;
                if (buttonItems.get(String.valueOf(itemChar)) == null) {
                    continue;
                }
                menuItems.put(slot, buttonItems.get(String.valueOf(itemChar)));
            }
        }
    }

    public String getString(String path, String defaultValue) {
        if (defaultValue == null) {
            return menuConfigs.getString(path);
        }
        return menuConfigs.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return menuConfigs.getInt(path, defaultValue);
    }

    public Map<Integer, AbstractButton> getMenu() {
        return menuItems;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

}