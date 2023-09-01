package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class YamlDatabase {

    public static void checkData(Player player) {
        ServerCache cache = null;
        File dir = new File(UltimateShop.instance.getDataFolder() + "/datas");
        File file = null;
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (player != null) {
            file = new File(dir, player.getUniqueId() + ".yml");
            if (!file.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                Map<String, Object> data = new HashMap<>();
                try {
                    data.put("playerName", player.getName());
                    for (String key : data.keySet()) {
                        config.set(key, data.get(key));
                    }
                    config.save(file);
                } catch (IOException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: " +
                            "Can not create new data file: " + player.getUniqueId() + ".yml!");
                }
            }
            if (CacheManager.cacheManager.playerCacheMap.containsKey(player)) {
                cache = CacheManager.cacheManager.playerCacheMap.get(player);
            }
        } else {
            // 新建文件
            file = new File(dir, "global.yml");
            if (!file.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                Map<String, Object> data = new HashMap<>();
                try {
                    data.put("playerName", "global");
                    for (String key : data.keySet()) {
                        config.set(key, data.get(key));
                    }
                    config.save(file);
                } catch (IOException e) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: " +
                            "Can not create new data file: global.yml!");
                }
            }
            // 获取对象
            cache = ServerCache.serverCache;
            if (cache == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found server cache object," +
                        " there maybe some issues...");
                return;
            }
        }
        // 次数储存系统
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection useTimeSection = config.getConfigurationSection("useTimes");
        if (useTimeSection != null) {
            for (String shopID : useTimeSection.getKeys(false)) {
                ConfigurationSection tempVal3 = useTimeSection.getConfigurationSection(shopID);
                for (String productID : useTimeSection.getKeys(false)) {
                    ConfigurationSection tempVal4 = tempVal3.getConfigurationSection(productID);
                    String shop = useTimeSection.getString("shop");
                    String product = useTimeSection.getString("product");
                    int buyUseTimes = useTimeSection.getInt("buyUseTimes");
                    int sellUseTimes = useTimeSection.getInt("sellUseTimes");
                    if (useTimeSection.getString("lastBuyTime") != null) {
                        String lastPurchaseTime = useTimeSection.getString("lastBuyTime");
                        String lastSellTime = useTimeSection.getString("lastSellTime");
                        cache.setUseTimesCache(shop, product, buyUseTimes, sellUseTimes, lastPurchaseTime, lastSellTime);
                    }
                }
            }
            // 动态价格储存系统
            // TODO...
        }
    }

    public static void updateData(Player player) {
        ServerCache cache = null;
        File dir = new File(UltimateShop.instance.getDataFolder()+"/datas");
        File file = null;
        if (!dir.exists()) {
            dir.mkdir();
        }
        Map<String, Object> data = new HashMap<>();
        if (player == null) {
            data.put("playerName", "global");
            cache = ServerCache.serverCache;
            if (cache == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found server cache object," +
                        " there maybe some issues...");
                return;
            }
            file = new File(dir, "global.yml");
            if (file.exists()){
                file.delete();
            }
        }
        else {
            data.put("playerName", player.getName());
            file = new File(dir, player.getUniqueId() + ".yml");
            if (file.exists()){
                file.delete();
            }
            cache = CacheManager.cacheManager.playerCacheMap.get(player);
        }
        YamlConfiguration config = new YamlConfiguration();
        // 储存购买次数
        ConfigurationSection useTimesSection = config.createSection("useTimes");
        Map<ObjectItem, ObjectUseTimesCache> tempVal1 = cache.getUseTimesCache();
        for (ObjectItem tempVal4 : tempVal1.keySet()) {
            ConfigurationSection tempVal5 = useTimesSection.getConfigurationSection(tempVal4.getShop());
            if (tempVal5 == null) {
                tempVal5 = useTimesSection.createSection(tempVal4.getShop());
            }
            ConfigurationSection tempVal6 = tempVal5.getConfigurationSection(tempVal4.getProduct());
            if (tempVal6 == null) {
                tempVal6 = tempVal5.createSection(tempVal4.getProduct());
            }
            data.put("buyUseTime", tempVal1.get(tempVal4).getBuyUseTimes());
            data.put("sellUseTimes", tempVal1.get(tempVal4).getSellUseTimes());
            if (tempVal1.get(tempVal4).getBuyRefreshTime() != null) {
                data.put("lastBuyTime", tempVal1.get(tempVal4).getLastBuyTime());
            }
            if (tempVal1.get(tempVal4).getSellRefreshTime() != null) {
                data.put("lastSellTime", tempVal1.get(tempVal4).getLastSellTime());
            }
            for (String key : data.keySet()) {
                tempVal6.set(key, data.get(key));
            }
            try {
                config.save(file);
            } catch (IOException e) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: " +
                        "Can not save data file: " + file.getName() + "!");
            }
        }
    }

}
