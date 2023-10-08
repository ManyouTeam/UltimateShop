package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectSingleProduct extends AbstractSingleThing {

    private ObjectItem item;

    public ObjectSingleProduct() {
        super();
    }

    public ObjectSingleProduct(ConfigurationSection singleSection) {
        super(singleSection);
    }

    public ObjectSingleProduct(ConfigurationSection singleSection, ObjectItem item) {
        super(singleSection);
        this.item = item;
    }

    public String getDisplayName(double amount) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        return CommonUtil.modifyString(tempVal1,
                "amount",
                String.valueOf(amount));
    }

    public double getAmount(Player player, int times) {
        String tempVal1 = singleSection.getString("amount", "1");
        if (item != null && ConfigManager.configManager.getBoolean("placeholder.data.can-used-in-amount")) {
            int playerBuyTimes = 0;
            int playerSellTimes = 0;
            int serverBuyTimes = 0;
            int serverSellTimes = 0;
            ObjectUseTimesCache tempVal3 = CacheManager.cacheManager.playerCacheMap.get(player).getUseTimesCache().get(item);
            ObjectUseTimesCache tempVal4 = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
            if (tempVal3 != null) {
                playerBuyTimes = tempVal3.getBuyUseTimes();
                playerSellTimes = tempVal3.getSellUseTimes();
            }
            if (tempVal4 != null) {
                serverBuyTimes = tempVal4.getBuyUseTimes();
                serverSellTimes = tempVal4.getSellUseTimes();
            }
            tempVal1 = CommonUtil.modifyString(tempVal1,
                    "buy-times-player",
                    String.valueOf(playerBuyTimes),
                    "sell-times-player",
                    String.valueOf(playerSellTimes),
                    "buy-times-server",
                    String.valueOf(serverBuyTimes),
                    "sell-times-server",
                    String.valueOf(serverSellTimes));
        }
        double cost = MathUtil.doCalculate(TextUtil.withPAPI(tempVal1, player)).doubleValue();
        if (singleSection.getString("max-amount") != null) {
            double maxAmount = Double.parseDouble(TextUtil.withPAPI(singleSection.getString("max-amount"), player));
            if (cost > maxAmount) {
                cost = maxAmount;
            }
        }
        if (singleSection.getString("min-amount") != null) {
            double minAmount = Double.parseDouble(TextUtil.withPAPI(singleSection.getString("min-amount"), player));
            if (cost < minAmount) {
                cost = minAmount;
            }
        }
        return cost;
    }

}
