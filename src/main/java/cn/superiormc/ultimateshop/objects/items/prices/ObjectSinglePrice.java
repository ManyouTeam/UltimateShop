package cn.superiormc.ultimateshop.objects.items.prices;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import io.lumine.mythic.bukkit.utils.lib.http.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ObjectSinglePrice extends AbstractSingleThing {

    private Map<Integer, Double> applyCostMap = new HashMap<>();

    private boolean priceMode;


    public ObjectSinglePrice() {
        super();
    }

    public ObjectSinglePrice(ConfigurationSection singleSection) {
        super(singleSection);
        initCustomMode();
        initApplyCostMap();
    }

    private void initCustomMode() {
        if (!singleSection.getString("custom-type", "none").equals("none")) {
            priceMode = true;
        }
        else {
            priceMode = false;
        }
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
    public boolean checkHasEnough(Player player,
                                  boolean take,
                                  double cost) {
        if (singleSection == null) {
            return false;
        }
        if (priceMode) {
            return super.checkHasEnough(ConfigManager.configManager.config.
                            getConfigurationSection("prices." + singleSection.getString("custom-type")),
                    player,
                    take,
                    cost);
        }
        return super.checkHasEnough(singleSection, player, take, cost);
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
        double cost = Double.parseDouble(TextUtil.withPAPI(tempVal1, player));
        if (applyCostMap != null && applyCostMap.containsKey(times)) {
            cost = applyCostMap.get(times);
        }
        return cost;
    }

    public String getDisplayName(double amount) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        if (priceMode) {
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

    public Map<Integer, Double> getApplyCostMap() {
        return applyCostMap;
    }


}
