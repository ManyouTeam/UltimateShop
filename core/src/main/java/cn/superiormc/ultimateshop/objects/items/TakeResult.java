package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.Map;

public class TakeResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;

    private boolean resultBoolean;

    private boolean conditionBoolean;

    public boolean empty;

    public TakeResult(Map<AbstractSingleThing, BigDecimal> resultMap) {
        this.resultMap = resultMap;
        this.resultBoolean = false;
        this.conditionBoolean = true;
        this.empty = resultMap.isEmpty();
    }

    public void setResultBoolean() {
        this.resultBoolean = true;
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

    public AbstractThings getThings() {
        for (AbstractSingleThing singleThing : resultMap.keySet()) {
            return singleThing.getThings();
        }
        return null;
    }

    public boolean getResultBoolean() {
        return resultBoolean;
    }

    public boolean getConditionBoolean() {
        return conditionBoolean;
    }

    public void take(int times, int amount, Inventory inventory, Player player) {
        take(times, amount, ItemStorage.of(inventory), player);
    }

    public void take(int times, int amount, ItemStorage storage, Player player) {
        for (AbstractSingleThing singleThing : resultMap.keySet()) {
            double cost = resultMap.get(singleThing).doubleValue();
            singleThing.playerHasEnough(storage, player, true, cost);
            singleThing.takeAction.runAllActions(new ObjectThingRun(player, times, amount, cost));
        }
    }
}
