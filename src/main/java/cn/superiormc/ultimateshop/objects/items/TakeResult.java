package cn.superiormc.ultimateshop.objects.items;

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

    public boolean getResultBoolean() {
        return resultBoolean;
    }
}
