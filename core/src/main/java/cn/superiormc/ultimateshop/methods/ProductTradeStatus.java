package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ProductTradeStatus {

    public static ProductTradeStatus ERROR = new ProductTradeStatus(Status.ERROR);

    public static ProductTradeStatus SERVER_MAX = new ProductTradeStatus(Status.SERVER_MAX);

    public static ProductTradeStatus PLAYER_MAX = new ProductTradeStatus(Status.PLAYER_MAX);

    public static ProductTradeStatus PERMISSION = new ProductTradeStatus(Status.PERMISSION);

    public static ProductTradeStatus NOT_ENOUGH = new ProductTradeStatus(Status.NOT_ENOUGH);

    public static ProductTradeStatus INVENTORY_FULL = new ProductTradeStatus(Status.INVENTORY_FULL);

    private GiveResult giveResult = null;

    private TakeResult takeResult = null;

    private int multi;

    private final Status status;

    public ProductTradeStatus(Status status,
                              TakeResult takeResult) {
        this.status = status;
        this.takeResult = takeResult;
    }

    public ProductTradeStatus(Status status,
                              TakeResult takeResult,
                              GiveResult giveResult,
                              int multi) {
        this.status = status;
        this.takeResult = takeResult;
        this.giveResult = giveResult;
        this.multi = multi;
    }

    public ProductTradeStatus(Status status) {
        this.status = status;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    @NotNull
    public TakeResult getTakeResult() {
        if (takeResult == null) {
            return new TakeResult(new HashMap<>());
        }
        return takeResult;
    }

    @Nullable
    public GiveResult getGiveResult() {
        return giveResult;
    }

    public int getAmount() {
        return multi;
    }

    public enum Status {
        ERROR,
        PERMISSION,
        PLAYER_MAX,
        SERVER_MAX,
        NOT_ENOUGH,
        INVENTORY_FULL,
        DONE
    }
}
