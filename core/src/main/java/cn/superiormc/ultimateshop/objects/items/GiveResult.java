package cn.superiormc.ultimateshop.objects.items;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GiveResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;

    public boolean empty;

    public GiveResult(Map<AbstractSingleThing, BigDecimal> resultMap) {
        this.resultMap = resultMap;
        this.empty = resultMap.isEmpty();
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

    public boolean give(int times, int multi, Player player, double multiplier) {
        boolean resultBoolean = true;
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
            giveItemStack.giveToPlayer(times, multi, multiplier, player);
        }
        return true;
    }

}
