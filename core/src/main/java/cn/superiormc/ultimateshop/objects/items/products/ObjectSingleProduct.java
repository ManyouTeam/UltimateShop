package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.StaticPlaceholder;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectSingleProduct extends AbstractSingleThing {

    private ObjectItem item;

    private boolean isStatic;

    private BigDecimal baseAmount;

    public ObjectSingleProduct() {
        super();
    }

    public ObjectSingleProduct(String id, ObjectProducts products) {
        super(id, products);
        this.item = products.getItem();
        this.things = products;
        this.isStatic = singleSection.getString("amount", "1").matches("-?\\d+(\\.\\d+)?");
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(singleSection.getString("amount", "1"));
        if (matcher.find()) {
            this.baseAmount = new BigDecimal(matcher.group());
        }
        initCondition();
        initAction();
    }

    @Override
    public String getDisplayName(int multi, BigDecimal amount, boolean alwaysStatic) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        return CommonUtil.modifyString(tempVal1,
                "amount",
                String.valueOf(amount),
                "status",
                alwaysStatic ? "" : StaticPlaceholder.getCompareValue(baseAmount.multiply(new BigDecimal(multi)), amount));
    }

    public boolean isStatic() {
        return isStatic;
    }

    public BigDecimal getAmount(Player player, int offsetAmount, boolean buyOrSell) {
        String tempVal1 = singleSection.getString("amount", "1");
        BigDecimal cost;
        if (isStatic()) {
            cost = baseAmount;
        } else {
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
                        replacePlaceholder(playerBuyTimes, offsetAmount, buyOrSell, true),
                        "sell-times-player",
                        replacePlaceholder(playerSellTimes, offsetAmount, buyOrSell, false),
                        "buy-times-server",
                        replacePlaceholder(serverBuyTimes, offsetAmount, buyOrSell, true),
                        "sell-times-server",
                        replacePlaceholder(serverSellTimes, offsetAmount, buyOrSell, false),
                        "last-buy-player", tempVal3 != null ? tempVal3.getBuyLastTimeName() : "",
                        "last-sell-player", tempVal3 != null ? tempVal3.getSellLastTimeName() : "",
                        "last-buy-server", tempVal4 != null ? tempVal4.getBuyLastTimeName() : "",
                        "last-sell-server", tempVal4 != null ? tempVal4.getSellLastTimeName() : "");
            }
            cost = MathUtil.doCalculate(TextUtil.withPAPI(tempVal1, player));
        }
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
        return cost.setScale(MathUtil.scale, RoundingMode.HALF_UP);
    }

    protected String replacePlaceholder(int baseAmount, int offsetAmount, boolean buyOrSell, boolean placeholderBuyOrSell) {
        // 如果是buy
        if ((buyOrSell && placeholderBuyOrSell) || (!buyOrSell && !placeholderBuyOrSell)) {
            return String.valueOf(baseAmount + offsetAmount);
        }
        return String.valueOf(baseAmount);
    }

}
