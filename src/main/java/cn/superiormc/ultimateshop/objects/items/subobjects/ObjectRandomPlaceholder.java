package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.util.*;

public class ObjectRandomPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    private final List<String> configElements = new ArrayList<>();

    private final Collection<ObjectRandomPlaceholder> notSameAs = new ArrayList<>();

    private final int elementAmount;

    public ObjectRandomPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.elementAmount = section.getInt("element-amount", 1);
        this.configElements.addAll(section.getStringList("elements"));
    }

    public Collection<ObjectRandomPlaceholder> getNotSameAs() {
        return notSameAs;
    }

    public String getID() {
        return id;
    }

    public List<String> getConfigElements() {
        return configElements;
    }

    public List<String> getNewValue() {
        List<String> result = new ArrayList<>();
        if (configElements.isEmpty()) {
            result.add("ERROR: Value Empty");
        }
        Collections.shuffle(configElements);
        for (int i = 0; i < Math.min(elementAmount, configElements.size()); i++) {
            String tempVal1 = configElements.get(i);
            String[] element = tempVal1.split("~");
            if (element.length == 1) {
                result.add(tempVal1);
                continue;
            }
            int min = Integer.parseInt(element[0]);
            int max = Integer.parseInt(element[1]);
            Random random = new Random();
            result.add(String.valueOf(random.nextInt(max - min + 1) + min));
        }
        return result;
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

    public List<String> getNowValue() {
        ObjectRandomPlaceholderCache tempVal1 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(this);
        if (tempVal1 == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(this);
            tempVal1 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(this);
        }
        return tempVal1.getNowValue(false, false);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ObjectRandomPlaceholder) {
            return ((ObjectRandomPlaceholder) object).getID().equals(getID());
        }
        return false;
    }

    public static String getNowValue(String id, int x) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return "Error: Unknown Placeholder";
        }
        ObjectRandomPlaceholderCache tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        }
        List<String> tempVal3 = tempVal2.getNowValue();
        if (x > tempVal3.size()) {
            x = tempVal3.size();
        }
        return tempVal3.get(x - 1);
    }

    public static LocalDateTime getRefreshDoneTimeObject(String id) {
        if (UltimateShop.freeVersion) {
            return LocalDateTime.now();
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return LocalDateTime.now();
        }
        ObjectRandomPlaceholderCache tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        }
        return tempVal2.getRefreshDoneTime();
    }

    public static String getRefreshDoneTime(String id) {
        return CommonUtil.timeToString(getRefreshDoneTimeObject(id), ConfigManager.configManager.getString("placeholder.cooldown.format"));
    }
}
