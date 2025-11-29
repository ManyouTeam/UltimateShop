package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ObjectRandomPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    private final List<RandomElement> elements = new ArrayList<>();

    private final Collection<ObjectRandomPlaceholder> notSameAs = new ArrayList<>();

    private final int elementAmount;

    private final boolean perPlayer;

    public ObjectRandomPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.perPlayer = section.getBoolean("per-player-element", false);
        this.elementAmount = section.getInt("element-amount", 1);
        if (section.isList("elements")) {
            // 简单模式
            for (String str : section.getStringList("elements")) {
                elements.add(new RandomElement(str, 1, new ObjectCondition()));
            }
        } else if (section.isConfigurationSection("elements")) {
            // 高级模式
            ConfigurationSection elemSec = section.getConfigurationSection("elements");
            for (String key : elemSec.getKeys(false)) {
                ConfigurationSection single = elemSec.getConfigurationSection(key);

                if (single == null) {
                    continue;
                }

                int rate = single.getInt("rate", 1);
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fAdded element " + key + " for random placeholder: " + id + ".yml!");

                elements.add(new RandomElement(key, rate, new ObjectCondition(single.getConfigurationSection("conditions"))));
            }
        }
        UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded random placeholder: " + id + ".yml!");
    }

    public Collection<ObjectRandomPlaceholder> getNotSameAs() {
        return notSameAs;
    }

    public String getID() {
        return id;
    }

    public List<String> getConfigElements() {
        List<String> result = new ArrayList<>();
        for (RandomElement randomElement : elements) {
            result.add(randomElement.getValue());
        }
        return result;
    }

    public List<RandomElement> getElements() {
        return elements;
    }

    public List<String> getNewValue(ServerCache cache) {
        List<String> result = new ArrayList<>();
        if (elements.isEmpty()) {
            result.add("ERROR: Value Empty");
            return result;
        }

        List<RandomElement> available = new ArrayList<>();
        for (RandomElement elem : elements) {
            if (cache.server || elem.isAvailable(cache.player)) {
                available.add(elem);
            }
        }

        if (available.isEmpty()) {
            result.add("ERROR: No Available Value");
            return result;
        }

        List<RandomElement> chosenList = new ArrayList<>();
        int size = available.size();
        for (int i = 0; i < Math.min(elementAmount, size); i++) {
            RandomElement chosen = getWeightedRandom(available);
            chosenList.add(chosen);
            available.remove(chosen);
        }

        boolean sortByOriginal = section.getBoolean("element-sort", true);
        if (sortByOriginal) {
            Set<RandomElement> chosenSet = new HashSet<>(chosenList); // O(1) 查找
            for (RandomElement elem : elements) {
                if (chosenSet.contains(elem)) {
                    result.add(elem.parseValue());
                }
            }
        } else {
            for (RandomElement elem : chosenList) {
                result.add(elem.parseValue());
            }
        }

        return result;
    }

    private RandomElement getWeightedRandom(List<RandomElement> elements) {
        int totalWeight = elements.stream().mapToInt(RandomElement::getRate).sum();
        int rnd = new Random().nextInt(totalWeight);
        int cur = 0;
        for (RandomElement elem : elements) {
            cur += elem.getRate();
            if (rnd < cur) {
                return elem;
            }
        }
        return elements.get(0); // fallback
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

    public List<String> getNowValue(ServerCache cache) {
        ObjectRandomPlaceholderCache tempVal1 = cache.getRandomPlaceholderCache().get(this);
        if (tempVal1 == null) {
            cache.addRandomPlaceholderCache(this);
            tempVal1 = cache.getRandomPlaceholderCache().get(this);
        }
        if (tempVal1 == null) {
            return new ArrayList<>();
        }
        return tempVal1.getNowValue(false, false);
    }

    public boolean isPerPlayer() {
        return perPlayer;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ObjectRandomPlaceholder) {
            return ((ObjectRandomPlaceholder) object).getID().equals(getID());
        }
        return false;
    }

    public static String getNowValue(Player player, String id, int x) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return "Error: Unknown Placeholder";
        }
        ServerCache cache;
        if (tempVal1.perPlayer) {
            if (player == null) {
                ErrorManager.errorManager.sendErrorMessage("§cThe random placeholder is per player and can not sync data with server cache.");
                return "Error: Sync Error";
            }
            cache = CacheManager.cacheManager.getPlayerCache(player);
        } else {
            cache = CacheManager.cacheManager.serverCache;
        }
        ObjectRandomPlaceholderCache tempVal2 = cache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            cache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = cache.getRandomPlaceholderCache().get(tempVal1);
        }
        if (tempVal2 == null) {
            return "";
        }
        List<String> tempVal3 = tempVal2.getNowValue();
        if (x > tempVal3.size()) {
            x = tempVal3.size();
        }
        return tempVal3.get(x - 1);
    }

    public static LocalDateTime getRefreshDoneTimeObject(Player player, String id) {
        if (UltimateShop.freeVersion) {
            return CommonUtil.getNowTime().withYear(2999);
        }
        ObjectRandomPlaceholder tempVal1 = ConfigManager.configManager.getRandomPlaceholder(id);
        if (tempVal1 == null) {
            return CommonUtil.getNowTime().withYear(2999);
        }
        ServerCache cache;
        if (tempVal1.perPlayer) {
            if (player == null) {
                ErrorManager.errorManager.sendErrorMessage("§cThe random placeholder is per player and can not sync data with server cache.");
                return CommonUtil.getNowTime().withYear(2999);
            }
            cache = CacheManager.cacheManager.getPlayerCache(player);
        } else {
            cache = CacheManager.cacheManager.serverCache;
        }
        ObjectRandomPlaceholderCache tempVal2 = cache.getRandomPlaceholderCache().get(tempVal1);
        if (tempVal2 == null) {
            cache.addRandomPlaceholderCache(tempVal1);
            tempVal2 = cache.getRandomPlaceholderCache().get(tempVal1);
        }
        if (tempVal2 == null) {
            return CommonUtil.getNowTime().withYear(2999);
        }
        return tempVal2.getRefreshDoneTime();
    }

    public static String getRefreshDoneTime(Player player, String id) {
        return CommonUtil.timeToString(getRefreshDoneTimeObject(player, id), ConfigManager.configManager.getString("placeholder.refresh.format"));
    }

    public static String getNextTime(Player player, String id) {
        if (UltimateShop.freeVersion) {
            return "";
        }
        LocalDateTime tempVal1 = getRefreshDoneTimeObject(player, id);
        if (tempVal1 == null || tempVal1.getYear() == 2999) {
            return ConfigManager.configManager.getString("placeholder.next.never");
        }
        Duration duration = Duration.between(CommonUtil.getNowTime(), tempVal1);
        long totalSeconds = duration.getSeconds();
        if (totalSeconds < 0) {
            return ConfigManager.configManager.getString("placeholder.next.never");
        }
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (days > 0) {
            return ConfigManager.configManager.getString("placeholder.next.with-day-format").replace("{d}", String.valueOf(days))
                    .replace("{h}", String.format("%02d", hours))
                    .replace("{m}", String.format("%02d", minutes))
                    .replace("{s}", String.format("%02d", seconds));
        }
        return ConfigManager.configManager.getString("placeholder.next.without-day-format").replace("{h}", String.valueOf(hours))
                .replace("{m}", String.format("%02d", minutes))
                .replace("{s}", String.format("%02d", seconds));
    }
}
