package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectItemConfig {

    private final ObjectItem item;

    private final ConfigurationSection section;

    private final ConfigurationSection shopSection;

    public ObjectItemConfig(ObjectItem objectItem, ConfigurationSection section) {
        this.item = objectItem;
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

    public Map<String, ConfigurationSection> mergeCache;

    public ConfigurationSection getActionOrConditionSection(String path) {
        ConfigurationSection tempVal1 = section.getConfigurationSection(path);
        if (shopSection == null) {
            return tempVal1;
        }
        ConfigurationSection tempVal2 = shopSection.getConfigurationSection(path);
        if (tempVal1 != null) {
            if (tempVal2 == null) {
                return tempVal1;
            }
            if (mergeCache.containsKey(path)) {
                return mergeCache.get(path);
            }
            for (String key : tempVal1.getKeys(true)) {
                tempVal2.set(key, tempVal1.get(key));
            }
            mergeCache.put(path, tempVal2);
        }
        return tempVal2;
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

    public int getInt(String path, int defaultValue) {
        if (shopSection == null) {
            return section.getInt(path, defaultValue);
        }
        if (section.contains(path)) {
            return section.getInt(path);
        }
        return shopSection.getInt(path, defaultValue);
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public ObjectItem getItem() {
        return item;
    }

    public Set<String> getKeys(boolean deep) {
        return section.getKeys(deep);
    }

}
