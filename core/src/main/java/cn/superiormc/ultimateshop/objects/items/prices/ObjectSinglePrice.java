package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.UltimateShop;
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
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectSinglePrice extends AbstractSingleThing {

    private Map<Integer, BigDecimal> applyCostMap = new HashMap<>();

    private boolean customPrice;

    private ObjectItem item;

    private PriceMode priceMode;

    private boolean isStatic;

    private boolean isAlwaysApply = false;

    private BigDecimal baseAmount;

    private String amountOption;

    public ObjectSinglePrice() {
        super();
    }

    public ObjectSinglePrice(String id, ObjectPrices prices) {
        super(id, prices);
        this.item = prices.getItem();
        this.priceMode = prices.getPriceMode();
        this.things = prices;
        this.amountOption = singleSection.getString("amount", "1");
        String tempVal1 = ConfigManager.configManager.getString("prices." + singleSection.getString("custom-type") + ".amount", "1");
        if (singleSection.getString("custom-type") != null && tempVal1 != null) {
            this.amountOption = tempVal1;
        }
        this.isStatic = amountOption.matches("-?\\d+(\\.\\d+)?");
        if (!amountOption.contains("{conditional_") &&
                ConfigManager.configManager.getBooleanOrDefault("placeholder.auto-settings.add-discount-in-all-price-amount.enabled",
                        "placeholder.auto-settings.add-conditional-in-all-price-amount.enabled") &&
                !ConfigManager.configManager.getStringListOrDefault("placeholder.auto-settings.add-discount-in-all-price-amount.black-shops",
                        "placeholder.auto-settings.add-conditional-in-all-price-amount.black-shops").contains(prices.getItem().getShop())) {
            if (!ConfigManager.configManager.getBooleanOrDefault("placeholder.auto-settings.add-discount-in-all-price-amount.black-dynamic-price",
                    "placeholder.auto-settings.add-conditional-in-all-price-amount.black-dynamic-price") ||
                    isStatic) {
                if (prices.getPriceMode() == PriceMode.BUY) {
                    this.amountOption = "{conditional_" + ConfigManager.configManager.getStringOrDefault("placeholder.auto-settings.add-discount-in-all-price-amount.buy-placeholder",
                            "placeholder.auto-settings.add-conditional-in-all-price-amount.black-dynamic-price", "") + "} * (" + amountOption + ")";
                } else {
                    this.amountOption = "{conditional_" + ConfigManager.configManager.getStringOrDefault("placeholder.auto-settings.add-discount-in-all-price-amount.sell-placeholder",
                            "placeholder.auto-settings.add-conditional-in-all-price-amount.sell-placeholder", "") +
                            "} * (" + amountOption + ")";
                }
                isStatic = false;
            }
        }
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(amountOption);
        if (matcher.find()) {
            this.baseAmount = new BigDecimal(matcher.group());
        }
        initCustomMode();
        initApplyCostMap();
        initCondition();
        initAction();
    }

    private void initCustomMode() {
        customPrice = !singleSection.getString("custom-type", "none").equals("none");
    }

    private void initApplyCostMap() {
        if (Objects.isNull(singleSection) || (singleSection.getInt("start-apply", -1) != -1
                && singleSection.getInt("end-apply", -1) != -1)) {
            isAlwaysApply = true;
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

    public boolean isStatic() {
        return isStatic;
    }

    public BigDecimal getAmount(Player player, int times, int offsetAmount) {
        if (singleSection == null) {
            return new BigDecimal(-1);
        }
        BigDecimal cost;
        if (isStatic()) {
            cost = baseAmount;
        } else {
            String tempVal1 = amountOption;
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
                        replacePlaceholder(serverSellTimes, offsetAmount, false),
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
        if (!applyCostMap.isEmpty() && applyCostMap.containsKey(times)) {
            if (applyCostMap.get(times).compareTo(new BigDecimal(-1)) == 0) {
                cost = applyCostMap.get(times);
            }
        }
        return cost.setScale(MathUtil.scale, RoundingMode.HALF_UP);
    }

    @Override
    public String getDisplayName(int multi, BigDecimal amount, boolean alwaysStatic) {
        if (empty) {
            return ConfigManager.configManager.getString("placeholder.price.empty");
        }
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        String tempVal2;
        if (customPrice) {
            tempVal2 = ConfigManager.configManager.getString("prices." +
                    singleSection.getString("custom-type") + ".placeholder", tempVal1);
        } else {
            tempVal2 = singleSection.getString("placeholder", tempVal1);
        }
        if (!alwaysStatic && !tempVal2.contains("{status}") && !isStatic && baseAmount != null && !UltimateShop.freeVersion &&
                ConfigManager.configManager.getBoolean("placeholder.auto-settings.add-status-in-dynamic-price-placeholder.enabled")) {
            tempVal2 = tempVal2 + " " + StaticPlaceholder.getCompareValue(baseAmount.multiply(new BigDecimal(multi)), amount);
        }
        if (tempVal2.contains("{amount}") && ConfigManager.configManager.getBoolean("placeholder.auto-settings.change-amount-in-all-price-placeholder.enabled")) {
            tempVal2 = tempVal2.replace("{amount}", ConfigManager.configManager.getString("placeholder.auto-settings.change-amount-in-all-price-placeholder.replace-value", "{amount}"));
        }
        return CommonUtil.modifyString(tempVal2,
                        "amount",
                        String.valueOf(amount),
                        "status",
                        alwaysStatic || baseAmount == null ? "" : StaticPlaceholder.getCompareValue(baseAmount.multiply(new BigDecimal(multi)), amount));
    }

    public int getStartApply() {
        if (isAlwaysApply || singleSection == null) {
            return -1;
        } else {
            return singleSection.getInt("start-apply", -1);
        }
    }

    public int getEndApply() {
        if (isAlwaysApply || singleSection == null) {
            return Integer.MAX_VALUE;
        } else {
            return singleSection.getInt("end-apply", Integer.MAX_VALUE);
        }
    }

    public boolean isAlwaysApply() {
        return isAlwaysApply;
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

    public ObjectItem getItem() {
        return item;
    }

    @Override
    public String toString() {
        if (singleSection == null) {
            return "Empty Price";
        }
        return "Named Price: " + singleSection.getName();
    }

}
