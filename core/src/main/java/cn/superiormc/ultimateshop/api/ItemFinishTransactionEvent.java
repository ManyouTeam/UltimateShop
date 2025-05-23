package cn.superiormc.ultimateshop.api;

import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.GiveResult;
import cn.superiormc.ultimateshop.objects.items.TakeResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemFinishTransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final boolean buyOrSell;

    private final ObjectItem item;

    private final Player player;

    private final int amount;

    public ItemFinishTransactionEvent(boolean buyOrSell,
                                      Player player,
                                      int amount,
                                      ObjectItem item) {
        this.buyOrSell = buyOrSell;
        this.amount = amount;
        this.player = player;
        this.item = item;
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
