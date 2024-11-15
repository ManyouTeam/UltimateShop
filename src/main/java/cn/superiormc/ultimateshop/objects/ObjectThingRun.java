package cn.superiormc.ultimateshop.objects;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ObjectThingRun {

    private final Player player;

    private final int times;

    private final double amount;

    private final int multi;

    private final boolean sellAll;

    private final ClickType type;

    public ObjectThingRun(Player player) {
        this.player = player;
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = null;
    }

    public ObjectThingRun(Player player, ClickType type) {
        this.player = player;
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = type;
    }

    public ObjectThingRun(Player player, int times, double amount) {
        this.player = player;
        this.times = times;
        this.amount = amount;
        this.multi = 1;
        this.sellAll = false;
        this.type = null;
    }

    public ObjectThingRun(Player player, int times, double amount, boolean sellAll) {
        this.player = player;
        this.times = times;
        this.amount = amount;
        this.multi = (int) amount;
        this.sellAll = sellAll;
        this.type = null;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTimes() {
        if (times < 0) {
            return 0;
        }
        return times;
    }

    public boolean getSellAll() {
        return sellAll;
    }

    public ClickType getType() {
        return type;
    }

    public double getAmount() {
        if (amount < 0) {
            return 0;
        }
        return amount;
    }

    public int getMulti() {
        if (multi < 1) {
            return 1;
        }
        return multi;
    }
}
