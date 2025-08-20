package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.Map;

public class TakeResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;

    private boolean resultBoolean;

    public boolean empty;

    public TakeResult(Map<AbstractSingleThing, BigDecimal> resultMap) {
        this.resultMap = resultMap;
        this.resultBoolean = false;
        this.empty = resultMap.isEmpty();
    }

    public void setResultBoolean() {
        this.resultBoolean = true;
    }

    public void addResultMapElement(AbstractSingleThing thing, BigDecimal amount) {
        resultMap.put(thing, amount);
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

    public void take(int times, int amount, Inventory inventory, Player player) {
        for (AbstractSingleThing singleThing : resultMap.keySet()) {
            double cost = resultMap.get(singleThing).doubleValue();
            singleThing.playerHasEnough(inventory, player, true, cost);
            singleThing.takeAction.runAllActions(new ObjectThingRun(player, times, amount, cost));
        }
    }
}
