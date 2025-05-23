package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectSellStick;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectConditionalPlaceholder;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

import static cn.superiormc.ultimateshop.objects.ObjectSellStick.SELL_STICK_ID;

public class ConfigManager {

    public static ConfigManager configManager;

    public FileConfiguration config;

    public Map<String, ObjectShop> shopConfigs = new HashMap<>();

    public Map<String, ObjectRandomPlaceholder> randomPlaceholders = new HashMap<>();

    public Map<String, ObjectConditionalPlaceholder> conditionalPlaceholders = new HashMap<>();

    public Map<String, ObjectSellStick> sellStickMap = new HashMap<>();

    public ConfigManager() {
        configManager = this;
        UltimateShop.instance.saveDefaultConfig();
        this.config = UltimateShop.instance.getConfig();
        initShopConfigs();
        loadShopConfigs();
        initMenuConfigs();
        if (!UltimateShop.freeVersion) {
            initRandomPlaceholder();
            initConditionalPlaceholder();
            initSellStickConfigs();
        }
    }

    private void initShopConfigs() {
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
                String substring = fileName.substring(0, fileName.length() - 4);
                shopConfigs.put(substring,
                        new ObjectShop(substring, YamlConfiguration.loadConfiguration(file)));
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded shop: " + fileName + "!");
            }
        }
    }

    private void loadShopConfigs() {
        for (ObjectShop shop : shopConfigs.values()) {
            shop.initCopyProducts();
            shop.initMenus();
        }
    }

    private void initMenuConfigs() {
        File dir = new File(UltimateShop.instance.getDataFolder(), "menus");
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
                String substring = fileName.substring(0, fileName.length() - 4);
                if (ObjectMenu.notCommonMenuNames.contains(substring)) {
                    continue;
                }
                if (ObjectMenu.commonMenus.containsKey(substring)) {
                    continue;
                }
                new ObjectMenu(substring);
            }
        }
    }

    private void initSellStickConfigs() {
        File dir = new File(UltimateShop.instance.getDataFolder(), "sell_sticks");
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
                String substring = fileName.substring(0, fileName.length() - 4);
                sellStickMap.put(substring, new ObjectSellStick(substring, YamlConfiguration.loadConfiguration(file)));
            }
        }
    }

    private void initRandomPlaceholder() {
        File dir = new File(UltimateShop.instance.getDataFolder(), "random_placeholders");
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
                String substring = fileName.substring(0, fileName.length() - 4);
                randomPlaceholders.put(substring, new ObjectRandomPlaceholder(substring, YamlConfiguration.loadConfiguration(file)));
            }
        }

        // Legacy Support
        if (randomPlaceholders.isEmpty()) {
            ConfigurationSection tempVal1 = config.getConfigurationSection("placeholder.random");
            if (tempVal1 == null) {
                return;
            }
            for (String key : tempVal1.getKeys(false)) {
                if (!randomPlaceholders.containsKey(key)) {
                    randomPlaceholders.put(key, new ObjectRandomPlaceholder(key, tempVal1.getConfigurationSection(key)));
                }
            }
        }
    }

    private void initConditionalPlaceholder() {
        File dir = new File(UltimateShop.instance.getDataFolder(), "conditional_placeholders");
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
                String substring = fileName.substring(0, fileName.length() - 4);
                conditionalPlaceholders.put(substring, new ObjectConditionalPlaceholder(substring, YamlConfiguration.loadConfiguration(file)));
            }
        }
    }

    public ObjectShop getShop(String fileName) {
        return shopConfigs.get(fileName);
    }

    public Collection<ObjectShop> getShops() {
        return shopConfigs.values();
    }

    public List<ObjectShop> getShopList() {
        List<ObjectShop> resultShops = new ArrayList<>();
        for (String key : shopConfigs.keySet()) {
            resultShops.add(shopConfigs.get(key));
        }
        return resultShops;
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public List<String> getStringListOrDefault(String originalPath, String newPath) {
        if (config.getStringList(originalPath).isEmpty()) {
            return config.getStringList(newPath);
        }
        return config.getStringList(originalPath);
    }

    public List<Integer> getIntList(String path) {
        return config.getIntegerList(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBooleanOrDefault(String originalPath, String newPath) {
        return config.getBoolean(originalPath, config.getBoolean(newPath, false));
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public int getIntWithPAPI(Player player, String path, String defaultValue) {
        return Integer.parseInt(TextUtil.withPAPI(config.getString(path, defaultValue), player));
    }

    public int getIntOrDefault(String originalPath, String newPath, int defaultValue) {
        return config.getInt(originalPath, config.getInt(newPath, defaultValue));
    }

    public long getLong(String path, long defaultValue) {
        return config.getLong(path, defaultValue);
    }

    public double getDouble(String path, double defaultValue) {
        return config.getDouble(path, defaultValue);
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public ConfigurationSection getSectionOrDefault(String originalPath, String newPath) {
        ConfigurationSection tempVal1 = config.getConfigurationSection(originalPath);
        if (tempVal1 == null) {
            return config.getConfigurationSection(newPath);
        }
        return tempVal1;
    }

    public String getString(String path, String... args) {
        String s = config.getString(path);
        if (s == null) {
            if (args.length == 0) {
                return null;
            }
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

    public String getStringOrDefault(String originalPath, String newPath, String defaultValue) {
        return config.getString(originalPath, config.getString(newPath, defaultValue));
    }

    public String getClickAction(ClickType type) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("menu.click-event");
        if (tempVal1 == null) {
            return "none";
        }
        for (String s : tempVal1.getKeys(false)) {
            for (String t : tempVal1.getString(s).split(";;")) {
                if (t.equals(type.name())){
                    return s;
                }
            }
        }
        return "none";
    }

    public boolean containsClickAction(String clickEvent) {
        ConfigurationSection clickEventSection = ConfigManager.configManager.getSection("menu.click-event");
        if (clickEventSection == null) {
            return false;
        }
        return clickEventSection.contains(clickEvent);
    }

    public ObjectRandomPlaceholder getRandomPlaceholder(String id) {
        return randomPlaceholders.get(id);
    }

    public Collection<ObjectRandomPlaceholder> getRandomPlaceholders() {
        return randomPlaceholders.values();
    }

    public ObjectConditionalPlaceholder getConditionalPlaceholder(String id) {
        return conditionalPlaceholders.get(id);
    }

    public Collection<ObjectConditionalPlaceholder> getConditionalPlaceholders() {
        return conditionalPlaceholders.values();
    }

    public ObjectSellStick getSellStick(String id) {
        return sellStickMap.get(id);
    }

    public ObjectSellStick getSellStickID(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(SELL_STICK_ID, PersistentDataType.STRING)) {
            return null;
        }
        String id = meta.getPersistentDataContainer().get(SELL_STICK_ID, PersistentDataType.STRING);
        return getSellStick(id);
    }

    public Collection<ObjectSellStick> getSellSticks() {
        return sellStickMap.values();
    }

}
