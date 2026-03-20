package cn.superiormc.ultimateshop.api;

import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemPreTransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private final boolean buyOrSell;

    private final ObjectItem item;

    private final Player player;

    private final int amount;

    private final GiveResult giveResult;

    private final TakeResult takeResult;

    public ItemPreTransactionEvent(boolean buyOrSell,
                                   Player player,
                                   int amount,
                                   ObjectItem item,
                                   GiveResult giveResult,
                                   TakeResult takeResult) {
        this.buyOrSell = buyOrSell;
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.giveResult = giveResult;
        this.takeResult = takeResult;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public ObjectShop getShop() {
        return item.getShopObject();
    }

    public ObjectItem getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public GiveResult getGiveResult() {
        return giveResult;
    }

    public TakeResult getTakeResult() {
        return takeResult;
    }

    public boolean isBuyOrSell() {
        return buyOrSell;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
