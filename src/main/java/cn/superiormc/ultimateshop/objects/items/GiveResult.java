package cn.superiormc.ultimateshop.objects.items;

import java.math.BigDecimal;
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

}
