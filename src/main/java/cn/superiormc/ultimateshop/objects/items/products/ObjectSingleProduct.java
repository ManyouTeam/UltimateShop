package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ObjectSingleProduct extends AbstractSingleThing {

    private ObjectItem item;

    public ObjectSingleProduct() {
        super();
    }

    public ObjectSingleProduct(String id, ConfigurationSection singleSection) {
        super(id, singleSection);
    }

    public ObjectSingleProduct(String id, ConfigurationSection singleSection, ObjectItem item) {
        super(id, singleSection);
        this.item = item;
    }

    public String getDisplayName(BigDecimal amount) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        return CommonUtil.modifyString(tempVal1,
                "amount",
                String.valueOf(amount));
    }

    @Override
    public BigDecimal getAmount(Player player, int times) {
        String tempVal1 = singleSection.getString("amount", "1");
        if (item != null && ConfigManager.configManager.getBoolean("placeholder.data.can-used-in-amount")) {
            int playerBuyTimes = 0;
            int playerSellTimes = 0;
            int serverBuyTimes = 0;
            int serverSellTimes = 0;
            ObjectUseTimesCache tempVal3 = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
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
        BigDecimal cost = MathUtil.doCalculate(TextUtil.withPAPI(tempVal1, player));
        if (singleSection.getString("max-amount") != null) {
            BigDecimal maxAmount = new BigDecimal(TextUtil.withPAPI(singleSection.getString("max-amount"), player));
            if (cost.compareTo(maxAmount) > 0) {
                cost = maxAmount;
            }
        }
        if (singleSection.getString("min-amount") != null) {
            BigDecimal minAmount = new BigDecimal(TextUtil.withPAPI(singleSection.getString("min-amount"), player));
            if (cost.compareTo(minAmount) < 0) {
                cost = minAmount;
            }
        }
        return cost.setScale(2, RoundingMode.HALF_UP);
    }

}
