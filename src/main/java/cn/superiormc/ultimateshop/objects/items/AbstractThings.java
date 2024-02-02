package cn.superiormc.ultimateshop.objects.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.math.BigDecimal;
import java.util.Map;


public abstract class AbstractThings {

    public ThingMode mode;

    public ConfigurationSection section;

    public boolean empty;

    public AbstractThings() {
        this.mode = ThingMode.UNKNOWN;
        this.empty = true;
    }

    public AbstractThings(ConfigurationSection section, String mode) {
        initThingMode(mode);
        this.section = section;
        this.empty = false;
    }

    public ThingMode getMode() {
        return mode;
    }

    public abstract Map<AbstractSingleThing, BigDecimal> getAmount(Player player,
                                                                   int times,
                                                                   int amount);

    public abstract GiveResult giveSingleThing(Player player, int times, int amount);

    public void giveThing(Player player, Map<AbstractSingleThing, BigDecimal> result) {
        for (AbstractSingleThing singleThing: result.keySet()) {
            singleThing.playerGive(player, result.get(singleThing).doubleValue());
        }
    }

    public abstract TakeResult takeSingleThing(Inventory inventory, Player player, int times, int amount);


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
}
