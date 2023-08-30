package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.*;

public class ConfigManager {

    public static ConfigManager configManager;

    public FileConfiguration config;

    private Map<String, ObjectShop> shopConfigs = new HashMap<>();

    public ConfigManager() {
        configManager = this;
        UltimateShop.instance.saveDefaultConfig();
        this.config = UltimateShop.instance.getConfig();
        initShopConfigs();
    }

    private void initShopConfigs() {
        this.shopConfigs = new HashMap<>();
        File dir = new File(UltimateShop.instance.getDataFolder(), "shops");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if (!Objects.nonNull(files) && files.length != 0) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                shopConfigs.put(fileName.substring(0, fileName.length() - 4),
                        new ObjectShop(fileName, YamlConfiguration.loadConfiguration(file)));
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded shop: " +
                        fileName + "!");
            }
        }
    }

    public ObjectShop getShop(String fileName) {
        return shopConfigs.get(fileName);
    }

    public List<ObjectShop> getShopList() {
        List<ObjectShop> resultShops = new ArrayList<>();
        for (String key : shopConfigs.keySet()) {
            resultShops.add(shopConfigs.get(key));
        }
        return resultShops;
    }

    public List<String> getListWithColor(String... args) {
        List<String> resultList = new ArrayList<>();
        for (String s : config.getStringList(args[0])) {
            for (int i = 1 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    s = s.replace(var, "");
                }
                else {
                    s = s.replace(var, args[i + 1]);
                }
            }
            resultList.add(TextUtil.parse(s));
        }
        return resultList;
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }

    public String getString(String path, String... args) {
        String s = config.getString(path);
        if (s == null) {
            s = args[0];
        }
        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                s = s.replace(var, "");
            }
            else {
                s = s.replace(var, args[i + 1]);
            }
        }
        return s.replace("{plugin_folder}", String.valueOf(UltimateShop.instance.getDataFolder()));
    }

    public ConfigurationSection getPrice(String priceName) {
        return config.getConfigurationSection("prices." + priceName);
    }

    public String getClickAction(ClickType type) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("menu.click-event");
        if (tempVal1 == null) {
            return "none";
        }
        for (String s : tempVal1.getKeys(false)) {
            if (type.isLeftClick() && tempVal1.getString(s).equals("LEFT")) {
                return s;
            }
            if (type.isRightClick() && tempVal1.getString(s).equals("RIGHT")) {
                return s;
            }
            if (type.isShiftClick() && type.isLeftClick() && tempVal1.getString(s).equals("SHIFT-LEFT")) {
                return s;
            }
            if (type.isShiftClick() && type.isRightClick() && tempVal1.getString(s).equals("SHIFT-RIGHT")) {
                return s;
            }
        }
        return "none";
    }

}
