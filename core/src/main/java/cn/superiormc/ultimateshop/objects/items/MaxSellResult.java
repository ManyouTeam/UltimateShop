package cn.superiormc.ultimateshop.objects.items;

import java.util.HashMap;

public class MaxSellResult {

    public static MaxSellResult empty = new MaxSellResult();

    private int maxAmount;

    private TakeResult takeResult;

    public MaxSellResult() {
        this.maxAmount = 0;
        this.takeResult = new TakeResult(new HashMap<>());
    }

    public TakeResult getTakeResult() {
        return takeResult;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
}
