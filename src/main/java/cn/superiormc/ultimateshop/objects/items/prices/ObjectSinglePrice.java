package cn.superiormc.ultimateshop.objects.items.prices;

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
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.*;

public class ObjectSinglePrice extends AbstractSingleThing {

    private Map<Integer, Double> applyCostMap = new HashMap<>();

    private boolean priceMode;


    private ObjectItem item;


    public ObjectSinglePrice() {
        super();
    }

    public ObjectSinglePrice(ConfigurationSection singleSection) {
        super(singleSection);
        initCustomMode();
        initApplyCostMap();
    }

    public ObjectSinglePrice(ConfigurationSection singleSection, ObjectItem item) {
        super(singleSection);
        this.item = item;
        initCustomMode();
        initApplyCostMap();
    }

    private void initCustomMode() {
        priceMode = !singleSection.getString("custom-type", "none").equals("none");
    }

    private void initApplyCostMap() {
        List<Integer> apply = new ArrayList<>();
        if (Objects.isNull(singleSection) || singleSection.getInt("start-apply", -1) != -1) {
            return;
        }
        List<Integer> integers = singleSection.getIntegerList("apply");
        apply = integers;
        List<Double> cost = singleSection.getDoubleList("cost");
        while (apply.size() > cost.size()) {
            if (cost.size() > 0) {
                cost.add(cost.get(cost.size() - 1));
            }
            else {
                cost.add(-1.0);
            }
        }
        Map<Integer, Double> applyCostMap = new HashMap<>();
        for (int i = 0 ; i < apply.size() ; i++) {
            applyCostMap.put(apply.get(i), cost.get(i));
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
        if (priceMode) {
            return super.playerHasEnough(inventory,
                    ConfigManager.configManager.config.
                            getConfigurationSection("prices." + singleSection.getString("custom-type")),
                    player,
                    take,
                    cost);
        }
        return super.playerHasEnough(inventory, singleSection, player, take, cost);
    }

    @Override
    public double getAmount(Player player, int times) {
        if (singleSection == null) {
            return -1;
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
        if (!applyCostMap.isEmpty() && applyCostMap.containsKey(times)) {
            if (applyCostMap.get(times) != -1) {
                cost = applyCostMap.get(times);
            }
        }
        return cost;
    }

    public String getDisplayName(double amount) {
        if (empty) {
            return ConfigManager.configManager.getString("placeholder.price.empty");
        }
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        if (priceMode) {
            return CommonUtil.modifyString(ConfigManager.configManager.getString("prices." +
                            singleSection.getString("custom-type") + ".placeholder", tempVal1),
                    "amount",
                    String.valueOf(new BigDecimal(amount)));
        }
        return CommonUtil.modifyString(tempVal1,
                        "amount",
                        String.valueOf(new BigDecimal(amount)));
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

    public Map<Integer, Double> getApplyCostMap() {
        return applyCostMap;
    }

    @Override
    public String toString() {
        if (singleSection == null) {
            return "Empty Price";
        }
        return "Named Price: " + singleSection.getName();
    }

}
