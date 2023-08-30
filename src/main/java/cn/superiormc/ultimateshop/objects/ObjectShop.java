package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectShop {

    private YamlConfiguration config;

    private Map<String, ObjectItem> items = new HashMap<>();

    private String shopName;

    public ObjectShop(String fileName, YamlConfiguration config) {
        this.shopName = fileName.replace(".yml", "");
        this.config = config;
        initProducts();
        if (config.getString("settings.menu") != null) {
            initMenus();
        }
    }

    private void initProducts() {
        if (config.getConfigurationSection("items") == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not get items section in your shop config!!");
            return;
        }
        for (String s : config.getConfigurationSection("items").getKeys(false)) {
            items.put(s, new ObjectItem(shopName, config.getConfigurationSection("items." + s)));
        }
    }

    private void initMenus() {
        new MenuManager(getShopMenu(), this);
    }

    public YamlConfiguration getShopConfig() {
        return config;
    }

    public ObjectItem getProduct(String productID) {
        return items.get(productID);
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
        return config.getString("settings.menu", "default");
    }

}
