package cn.superiormc.ultimateshop.objects.items;

import java.math.BigDecimal;
import java.util.Map;

public class GiveResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;


    public GiveResult(Map<AbstractSingleThing, BigDecimal> resultMap) {
        this.resultMap = resultMap;
    }

    public void addResultMapElement(AbstractSingleThing thing, BigDecimal amount) {
        resultMap.put(thing, amount);
    }

    public Map<AbstractSingleThing, BigDecimal> getResultMap() {
        return resultMap;
    }

}
