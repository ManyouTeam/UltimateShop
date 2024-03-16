package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjectSinglePrice extends AbstractSingleThing {

    private Map<Integer, BigDecimal> applyCostMap = new HashMap<>();

    private boolean customPrice;

    private ObjectItem item;

    private PriceMode priceMode;

    public ObjectSinglePrice() {
        super();
    }

    public ObjectSinglePrice(String id, ObjectPrices prices) {
        super(id, prices);
        this.item = prices.getItem();
        this.priceMode = prices.getPriceMode();
        initCustomMode();
        initApplyCostMap();
    }

    private void initCustomMode() {
        customPrice = !singleSection.getString("custom-type", "none").equals("none");
    }

    private void initApplyCostMap() {
        if (Objects.isNull(singleSection) || singleSection.getInt("start-apply", -1) != -1) {
            return;
        }
        List<Integer> apply = singleSection.getIntegerList("apply");
        List<Double> cost = singleSection.getDoubleList("cost");
        while (apply.size() > cost.size()) {
            if (!cost.isEmpty()) {
                cost.add(cost.get(cost.size() - 1));
            }
            else {
                cost.add(-1.0);
            }
        }
        Map<Integer, BigDecimal> applyCostMap = new HashMap<>();
        for (int i = 0 ; i < apply.size() ; i++) {
            applyCostMap.put(apply.get(i), BigDecimal.valueOf(cost.get(i)));
        }
        this.applyCostMap = applyCostMap;
    }

    @Override
    public boolean playerHasEnough(Inventory inventory,
                                   Player player,
                                   boolean take,
                                   double cost) {
        if (singleSection == null) {
            return false;
        }
        if (customPrice) {
            return super.playerHasEnough(inventory,
                    ConfigManager.configManager.config.
                            getConfigurationSection("prices." + singleSection.getString("custom-type")),
                    player,
                    take,
                    cost);
        }
        return super.playerHasEnough(inventory, singleSection, player, take, cost);
    }

    public BigDecimal getAmount(Player player, int times, int offsetAmount) {
        if (singleSection == null) {
            return new BigDecimal(-1);
        }
        String tempVal1 = singleSection.getString("amount", "1");
        String tempVal2 = ConfigManager.configManager.getString("prices." + singleSection.getString("custom-type") + ".amount", "1");
        if (singleSection.getString("custom-type") != null && tempVal2 != null) {
            tempVal1 = tempVal2;
        }
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
                    replacePlaceholder(playerBuyTimes, offsetAmount, true),
                    "sell-times-player",
                    replacePlaceholder(playerSellTimes, offsetAmount, false),
                    "buy-times-server",
                    replacePlaceholder(serverBuyTimes, offsetAmount, true),
                    "sell-times-server",
                    replacePlaceholder(serverSellTimes, offsetAmount, false));
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
        if (!applyCostMap.isEmpty() && applyCostMap.containsKey(times)) {
            if (applyCostMap.get(times).compareTo(new BigDecimal(-1)) == 0) {
                cost = applyCostMap.get(times);
            }
        }
        return cost.setScale(MathUtil.scale, RoundingMode.HALF_UP);
    }

    public String getDisplayName(BigDecimal amount) {
        if (empty) {
            return ConfigManager.configManager.getString("placeholder.price.empty");
        }
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        if (customPrice) {
            return CommonUtil.modifyString(ConfigManager.configManager.getString("prices." +
                            singleSection.getString("custom-type") + ".placeholder", tempVal1),
                    "amount",
                    String.valueOf(amount));
        }
        return CommonUtil.modifyString(tempVal1,
                        "amount",
                        String.valueOf(amount));
    }

    public int getStartApply() {
        if (singleSection == null) {
            return -1;
        }
        else {
            return singleSection.getInt("start-apply", -1);
        }
    }

    public int getEndApply() {
        if (singleSection == null) {
            return Integer.MAX_VALUE;
        }
        else {
            return singleSection.getInt("end-apply", Integer.MAX_VALUE);
        }
    }

    public Map<Integer, BigDecimal> getApplyCostMap() {
        return applyCostMap;
    }

    public boolean getCustomPrice() {
        return customPrice;
    }

    private String replacePlaceholder(int baseAmount, int offsetAmount, boolean buyOrSell) {
        // 如果是buy
        if ((buyOrSell && priceMode == PriceMode.BUY) || (!buyOrSell && priceMode == PriceMode.SELL)) {
            return String.valueOf(baseAmount + offsetAmount);
        }
        return String.valueOf(baseAmount);
    }

    @Override
    public String toString() {
        if (singleSection == null) {
            return "Empty Price";
        }
        return "Named Price: " + singleSection.getName();
    }

}
