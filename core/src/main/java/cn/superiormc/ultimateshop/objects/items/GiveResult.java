package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;

public class GiveResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;

    public boolean empty;

    private boolean conditionBoolean;

    private double multiplier;

    public GiveResult(Map<AbstractSingleThing, BigDecimal> resultMap) {
        this.resultMap = resultMap;
        this.empty = resultMap.isEmpty();
        this.conditionBoolean = true;
        this.multiplier = 1;
    }

    public void addResultMapElement(AbstractSingleThing thing, Player player, int times, int multi, BigDecimal amount) {
        resultMap.put(thing, amount);
        if (!thing.getRequireCondition(player, times, multi, amount.doubleValue())) {
            this.conditionBoolean = false;
        }
        this.empty = false;
    }

    public Map<AbstractSingleThing, BigDecimal> getResultMap() {
        return resultMap;
    }

    public Map<AbstractSingleThing, BigDecimal> getResultMapForSellMultiplierDisplay(Player player) {
        if (!ConfigManager.configManager.getBoolean("sell.multiplier.display-original-price")) {
            Map<AbstractSingleThing, BigDecimal> map = new HashMap<>();
            for (Map.Entry<AbstractSingleThing, BigDecimal> entry : resultMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue().multiply(BigDecimal.valueOf(ShopHelper.getSellMultiplier(player))));
            }
            return map;
        }
        return resultMap;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = this.multiplier * multiplier;
        for (AbstractSingleThing singleThing : resultMap.keySet()) {
            BigDecimal newValue = resultMap.get(singleThing).multiply(BigDecimal.valueOf(multiplier));
            resultMap.put(singleThing, newValue);
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public AbstractThings getThings() {
        for (AbstractSingleThing singleThing : resultMap.keySet()) {
            return singleThing.getThings();
        }
        return null;
    }

    public boolean getConditionBoolean() {
        return conditionBoolean;
    }

    public boolean give(int times, int multi, Player player, double multiplier) {
        boolean resultBoolean = true;
        setMultiplier(multiplier);
        Collection<GiveItemStack> giveItemStacks = new ArrayList<>();
        for (AbstractSingleThing singleThing: resultMap.keySet()) {
            GiveItemStack giveItemStack = singleThing.playerCanGive(player, resultMap.get(singleThing).doubleValue());
            giveItemStacks.add(giveItemStack);
            if (!giveItemStack.isCanGive()) {
                resultBoolean = false;
            }
        }
        if (!resultBoolean) {
            return false;
        }
        for (GiveItemStack giveItemStack : giveItemStacks) {
            giveItemStack.giveToPlayer(times, multi, player);
        }
        return true;
    }

}
