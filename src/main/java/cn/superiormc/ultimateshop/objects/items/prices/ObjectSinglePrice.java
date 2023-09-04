package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ObjectSinglePrice extends AbstractSingleThing {

    private Map<Integer, Double> applyCostMap = new HashMap<>();


    public ObjectSinglePrice() {
        super();
    }

    public ObjectSinglePrice(ConfigurationSection singleSection) {
        super(singleSection);
        initApplyCostMap();
    }

    private void initApplyCostMap() {
        List<Integer> apply = new ArrayList<>();
        if (Objects.isNull(singleSection) || singleSection.getInt("start-apply", -1) != -1) {
            return;
        }
        List<Integer> integers = singleSection.getIntegerList("apply");
        if (integers.size() == 0) {
            integers = new ArrayList<>();
            integers.add(Integer.valueOf(singleSection.getName()));
        }
        apply = integers;
        List<Double> cost = new ArrayList<>();
        List<Double> doubles = singleSection.getDoubleList("cost");
        if (doubles.size() == 0) {
            doubles = new ArrayList<>();
            doubles.add(singleSection.getDouble("amount", 1));
        }
        cost = doubles;
        while (apply.size() > cost.size()) {
            if (cost.size() > 0) {
                cost.add(cost.get(cost.size() - 1));
            }
            else {
                cost.add(0.0);
            }
        }
        Map<Integer, Double> applyCostMap = new HashMap<>();
        for (int i = 0 ; i < apply.size() ; i++) {
            applyCostMap.put(apply.get(i), cost.get(i));
        }
        this.applyCostMap = applyCostMap;
    }

    @Override
    public boolean playerHasEnough(Player player, boolean take, int times, int amount) {
        if (singleSection == null) {
            return false;
        }
        switch (type) {
            default:
                return checkHasEnough(player, take, times, amount);
            case "custom":
                return checkHasEnough(ConfigManager.configManager.getPrice(singleSection.getString("type")),
                        player,
                        take,
                        times,
                        amount);
        }
    }

    @Override
    public double getAmount(Player player, int times) {
        if (singleSection == null) {
            return -1;
        }
        double cost = singleSection.getDouble("amount", 1);
        if (applyCostMap != null && applyCostMap.containsKey(times)) {
            cost = applyCostMap.get(times);
        }
        return cost;
    }

    public String getDisplayName(double amount) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        switch (type) {
            default:
                return CommonUtil.modifyString(singleSection.getString("placeholder",
                                ConfigManager.configManager.getString("placeholder.price.unknown")),
                        "amount",
                        String.valueOf(amount));
            case "custom" :
                return CommonUtil.modifyString(ConfigManager.configManager.getString("prices." +
                                singleSection.getString("type") + ".placeholder"),
                        "amount",
                        String.valueOf(amount));
        }
    }

    public int getStartApply() {
        if (singleSection == null) {
            return -1;
        }
        else {
            return singleSection.getInt("start-apply", -1);
        }
    }

    public Map<Integer, Double> getApplyCostMap() {
        return applyCostMap;
    }


}
