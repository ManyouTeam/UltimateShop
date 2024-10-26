package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ObjectRandomPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    private final List<String> elements = new ArrayList<>();

    private final Collection<ObjectRandomPlaceholder> notSameAs = new ArrayList<>();

    public ObjectRandomPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        initElements(true);
    }

    public void initElements(boolean firstLoad) {
        elements.addAll(section.getStringList("elements"));
        for (int i = 0; i < elements.size(); i++) {
            ObjectRandomPlaceholder tempVal2 = ConfigManager.configManager.getRandomPlaceholder(elements.get(i));
            if (tempVal2 != null && !tempVal2.equals(this)) {
                if (!tempVal2.getElements().isEmpty()) {
                    elements.remove(elements.get(i));
                    elements.addAll(tempVal2.getElements());
                }
            }
        }
        if (!firstLoad) {
            for (String removeElement : section.getStringList("not-same-as")) {
                ObjectRandomPlaceholder tempVal2 = ConfigManager.configManager.getRandomPlaceholder(removeElement);
                if (tempVal2 != null && !tempVal2.equals(this)) {
                    elements.remove(tempVal2.getNowValue());
                    notSameAs.add(tempVal2);
                }
            }
        }
    }

    public Collection<ObjectRandomPlaceholder> getNotSameAs() {
        return notSameAs;
    }

    public String getID() {
        return id;
    }

    public List<String> getElements() {
        return elements;
    }

    public String getNewValue() {
        if (elements.isEmpty()) {
            return "ERROR: Value Empty";
        }
        String[] element = RandomUtil.getRandomElement(elements).split("~");
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

    public String getNowValue() {
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

    public static String getNowValue(String id) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return "ERROR: Unknown Placeholder";
        }
        ObjectRandomPlaceholderCache tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        }
        String tempVal3 = tempVal2.getNowValue();
        if (tempVal3 == null) {
            return "";
        }
        return tempVal3;
    }

    public static String getRefreshDoneTime(String id) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return "ERROR: Unknown Placeholder";
        }
        ObjectRandomPlaceholderCache tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = CacheManager.cacheManager.serverCache.getRandomPlaceholderCache().get(tempVal1);
        }
        LocalDateTime tempVal3 = tempVal2.getRefreshDoneTime();
        if (tempVal3 == null || tempVal3.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.refresh.never");
        }
        return CommonUtil.timeToString(tempVal3, ConfigManager.configManager.getString("placeholder.cooldown.format"));
    }
}
