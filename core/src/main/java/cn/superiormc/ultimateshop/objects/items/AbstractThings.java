package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractThings {

    public ThingMode mode;

    public ConfigurationSection section;

    public boolean empty;

    protected ObjectItem item;

    public AbstractThings() {
        this.mode = ThingMode.UNKNOWN;
        this.empty = true;
    }

    public AbstractThings(ConfigurationSection section, String mode, ObjectItem item) {
        initThingMode(mode);
        this.section = section;
        this.item = item;
    }

    public ThingMode getMode() {
        return mode;
    }

    public abstract GiveResult giveSingleThing(Player player,
                                               int times,
                                               int amount);

    public boolean giveThing(int times, Player player, double multiplier, Map<AbstractSingleThing, BigDecimal> result) {
        boolean resultBoolean = true;
        Collection<GiveItemStack> giveItemStacks = new ArrayList<>();
        for (AbstractSingleThing singleThing: result.keySet()) {
            GiveItemStack giveItemStack = singleThing.playerCanGive(player, result.get(singleThing).doubleValue());
            giveItemStacks.add(giveItemStack);
            if (!giveItemStack.isCanGive()) {
                resultBoolean = false;
            }
        }
        if (!resultBoolean) {
            return false;
        }
        for (GiveItemStack giveItemStack : giveItemStacks) {
            giveItemStack.giveToPlayer(times, multiplier, player);
        }
        return true;
    }

    public abstract TakeResult takeSingleThing(Inventory inventory,
                                               Player player,
                                               int times,
                                               int amount,
                                               boolean test);


    public void takeThing(Inventory inventory, Player player, Map<AbstractSingleThing, BigDecimal> result) {
        for (AbstractSingleThing singleThing : result.keySet()) {
            singleThing.playerHasEnough(inventory, player, true, result.get(singleThing).doubleValue());
        }
    }

    private void initThingMode(String mode) {
        switch(mode.toUpperCase()) {
            case ("ANY") :
                this.mode = ThingMode.ANY;
                break;
            case ("ALL") :
                this.mode = ThingMode.ALL;
                break;
            case ("CLASSIC") :
            case ("CLASSIC_ALL"):
                this.mode = ThingMode.CLASSIC_ALL;
                break;
            case ("CLASSIC_ANY"):
                this.mode = ThingMode.CLASSIC_ANY;
                break;
            default:
                this.mode = ThingMode.UNKNOWN;
                break;
        }
    }

    public ObjectItem getItem() {
        return item;
    }
}
