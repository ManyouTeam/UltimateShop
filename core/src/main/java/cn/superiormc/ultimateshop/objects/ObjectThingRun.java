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

    private final boolean reopen;

    public ObjectThingRun(Player player) {
        this.player = player;
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = null;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, boolean reopen) {
        this.player = player;
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = null;
        this.reopen = reopen;
    }

    public ObjectThingRun(Player player, ClickType type) {
        this.player = player;
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = type;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, int times, double amount) {
        this.player = player;
        this.times = times;
        this.amount = amount;
        this.multi = (int) amount;
        this.sellAll = false;
        this.type = null;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, int times, double amount, boolean sellAll) {
        this.player = player;
        this.times = times;
        this.amount = amount;
        this.multi = (int) amount;
        this.sellAll = sellAll;
        this.type = null;
        this.reopen = false;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTimes() {
        return Math.max(times, 0);
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
        return Math.max(multi, 1);
    }

    public boolean isReopen() {
        return reopen;
    }
}
