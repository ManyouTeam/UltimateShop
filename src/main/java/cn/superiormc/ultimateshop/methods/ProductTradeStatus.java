package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProductTradeStatus {

    public static ProductTradeStatus ERROR = new ProductTradeStatus(Status.ERROR);

    public static ProductTradeStatus SERVER_MAX = new ProductTradeStatus(Status.SERVER_MAX);

    public static ProductTradeStatus PLAYER_MAX = new ProductTradeStatus(Status.PLAYER_MAX);

    public static ProductTradeStatus PERMISSION = new ProductTradeStatus(Status.PERMISSION);

    public static ProductTradeStatus NOT_ENOUGH = new ProductTradeStatus(Status.NOT_ENOUGH);

    public static ProductTradeStatus IN_COOLDOWN = new ProductTradeStatus(Status.IN_COOLDOWN);

    private GiveResult giveResult = null;

    private TakeResult takeResult = null;

    private Status status;
    public ProductTradeStatus(Status status, TakeResult takeResult) {
        this.status = status;
        this.takeResult = takeResult;
    }

    public ProductTradeStatus(Status status, TakeResult takeResult, GiveResult giveResult) {
        this.status = status;
        this.takeResult = takeResult;
        this.giveResult = giveResult;
    }

    public ProductTradeStatus(Status status) {
        this.status = status;
    }

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

    public GiveResult getGiveResult() {
        return giveResult;
    }

    public enum Status {
        ERROR,
        PERMISSION,
        PLAYER_MAX,
        SERVER_MAX,
        NOT_ENOUGH,
        IN_COOLDOWN,
        DONE
    }
}
