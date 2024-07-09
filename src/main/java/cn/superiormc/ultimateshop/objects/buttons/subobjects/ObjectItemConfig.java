package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ObjectItemConfig {

    private ObjectItem item;

    private ConfigurationSection section;

    private ConfigurationSection shopSection;

    public ObjectItemConfig(ObjectItem objectItem, ConfigurationSection section) {
        this.item = item;
        this.section = section;
        this.shopSection = objectItem.getShopObject().getShopConfig().getConfigurationSection("general-configs");
    }

    public ConfigurationSection getConfigurationSection(String key) {
        if (shopSection == null) {
            return section.getConfigurationSection(key);
        }
        ConfigurationSection tempVal1 = section.getConfigurationSection(key);
        if (tempVal1 == null) {
            return shopSection.getConfigurationSection(key);
        }
        return tempVal1;
    }

    public String getString(String key) {
        if (shopSection == null) {
            return section.getString(key);
        }
        String tempVal1 = section.getString(key);
        if (tempVal1 == null) {
            return shopSection.getString(key);
        }
        return tempVal1;
    }

    public String getString(String key, String defaultValue) {
        if (shopSection == null) {
            return section.getString(key, defaultValue);
        }
        String tempVal1 = section.getString(key);
        if (tempVal1 == null) {
            return shopSection.getString(key, defaultValue);
        }
        return tempVal1;
    }

    public List<String> getStringList(String path) {
        if (shopSection == null) {
            return section.getStringList(path);
        }
        List<String> tempVal1 = section.getStringList(path);
        if (tempVal1.isEmpty()) {
            return shopSection.getStringList(path);
        }
        return tempVal1;
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        if (shopSection == null) {
            return section.getBoolean(path, defaultValue);
        }
        if (section.contains(path)) {
            return section.getBoolean(path);
        }
        return shopSection.getBoolean(path, defaultValue);
    }

    public ConfigurationSection getSection() {
        return section;
    }

}
