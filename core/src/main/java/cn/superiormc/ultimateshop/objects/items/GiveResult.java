package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.MathUtil;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;

public class GiveResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;

    public boolean empty;

    private boolean conditionBoolean;

    private double multiplier;

    private BigDecimal originalTotal;

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
            AbstractThings things = getThings();
            ObjectItem item = things == null ? null : things.getItem();
            double multiplier = ShopHelper.getSellMultiplier(player, item);
            Map<AbstractSingleThing, BigDecimal> map = new HashMap<>();
            for (Map.Entry<AbstractSingleThing, BigDecimal> entry : resultMap.entrySet()) {
                map.put(entry.getKey(), MathUtil.applyConfiguredScale(
                        entry.getValue().multiply(BigDecimal.valueOf(multiplier))));
            }
            return map;
        }
        return resultMap;
    }

    public void setMultiplier(double multiplier) {
        setMultiplier(BigDecimal.valueOf(multiplier));
    }

    public void setMultiplier(BigDecimal multiplier) {
        if (originalTotal == null) {
            originalTotal = resultMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        this.multiplier = BigDecimal.valueOf(this.multiplier).multiply(multiplier).doubleValue();
        for (AbstractSingleThing singleThing : resultMap.keySet()) {
            resultMap.put(singleThing, MathUtil.applyConfiguredScale(
                    resultMap.get(singleThing).multiply(multiplier)));
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public BigDecimal getOriginalTotal() {
        if (originalTotal != null) {
            return originalTotal;
        }
        return resultMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
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
        return give(times, multi, player, BigDecimal.valueOf(multiplier));
    }

    public boolean give(int times, int multi, Player player, BigDecimal multiplier) {
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
