package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.UUID;

public class ObjectThingRun {

    private final UUID uuid;

    private final int times;

    private final double amount;

    private final int multi;

    private final boolean sellAll;

    private final ClickType type;

    private final boolean reopen;

    private ProductTradeStatus.Status status;

    public ObjectThingRun(Player player) {
        this.uuid = player.getUniqueId();
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = null;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, int multi) {
        this.uuid = player.getUniqueId();
        this.times = 1;
        this.amount = multi;
        this.multi = multi;
        this.sellAll = false;
        this.type = null;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, boolean reopen) {
        this.uuid = player.getUniqueId();
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = null;
        this.reopen = reopen;
    }

    public ObjectThingRun(Player player, ClickType type) {
        this.uuid = player.getUniqueId();
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = type;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, ClickType type, ProductTradeStatus.Status status) {
        this.uuid = player.getUniqueId();
        this.times = 1;
        this.amount = 1;
        this.multi = 1;
        this.sellAll = false;
        this.type = type;
        this.reopen = false;
        this.status = status;
    }

    public ObjectThingRun(Player player, int times, int multi, double amount) {
        this.uuid = player.getUniqueId();
        this.times = times;
        this.amount = amount;
        this.multi = multi;
        this.sellAll = false;
        this.type = null;
        this.reopen = false;
    }

    public ObjectThingRun(Player player, int times, int multi, double amount, boolean sellAll) {
        this.uuid = player.getUniqueId();
        this.times = times;
        this.amount = amount;
        this.multi = multi;
        this.sellAll = sellAll;
        this.type = null;
        this.reopen = false;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
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
        if (multi < 1) {
            return 1;
        }
        return multi;
    }

    public ProductTradeStatus.Status getStatus() {
        return status;
    }

    public boolean isReopen() {
        return reopen;
    }
}
