package cn.superiormc.ultimateshop.objects.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

    public abstract Map<AbstractSingleThing, Double> getAmount(Player player,
                                                               int times,
                                                               int amount);

    public abstract void giveSingleThing(Player player, int times, int amount);

    public void giveThing(Player player, int times, int multi) {
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0; i < multi; i++) {
                    giveSingleThing(player, times + i, 1);
                }
                break;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                giveSingleThing(player, times, multi);
                break;
        }
    }

    public abstract boolean takeSingleThing(Inventory inventory, Player player, boolean take, int times, int amount);


    public boolean takeThing(Inventory inventory, Player player, boolean take, int times, int multi) {
        if (!takeSingleThing(inventory, player, take, times, multi)) {
            if (!take) {
                return false;
            }
        }
        return true;
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
