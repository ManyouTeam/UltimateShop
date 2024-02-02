package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;

import java.math.BigDecimal;
import java.util.Map;

public class TakeResult {

    private final Map<AbstractSingleThing, BigDecimal> resultMap;

    private boolean resultBoolean;

    public TakeResult(Map<AbstractSingleThing, BigDecimal> resultMap) {
        this.resultMap = resultMap;
        this.resultBoolean = false;
    }

    public void setResultBoolean() {
        this.resultBoolean = true;
    }

    public void addResultMapElement(AbstractSingleThing thing, BigDecimal amount) {
        resultMap.put(thing, amount);
    }

    public Map<AbstractSingleThing, BigDecimal> getResultMap() {
        return resultMap;
    }

    public boolean getResultBoolean() {
        return resultBoolean;
    }
}
