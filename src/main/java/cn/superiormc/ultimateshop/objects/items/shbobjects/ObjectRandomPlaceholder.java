package cn.superiormc.ultimateshop.objects.items.shbobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.utils.RandomUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

public class ObjectRandomPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    public ObjectRandomPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
    }

    public String getID() {
        return id;
    }

    public String getNewValue() {
        String[] element = RandomUtil.getRandomElement(section.getStringList("elements")).split("~");
        if (element.length == 1) {
            return element[0];
        }
        int min = Integer.parseInt(element[0]);
        int max = Integer.parseInt(element[1]);
        Random random = new Random();
        return String.valueOf(random.nextInt(max - min + 1) + min);
    }

    public ConfigurationSection getConfig() {
        return section;
    }

    public String getMode() {
        String tempVal1 = section.getString("reset-mode");
        if (tempVal1 == null) {
            return "NEVER";
        }
        return tempVal1.toUpperCase();
    }

    public static String getNowValue(String id) {
        if (UltimateShop.freeVersion) {
            return "";
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return "";
        }
        ObjectRandomPlaceholderCache tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        }
        return tempVal2.getNowValue();
    }
}
