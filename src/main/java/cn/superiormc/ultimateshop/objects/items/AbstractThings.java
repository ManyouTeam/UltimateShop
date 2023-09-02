package cn.superiormc.ultimateshop.objects.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


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

    public abstract boolean takeSingleThing(Player player, boolean take, int times, int amount);


    public boolean takeThing(Player player, boolean take, int times, int multi) {
        switch (mode) {
            case ALL:
            case ANY:
                for (int i = 0 ; i < multi ; i ++) {
                    if (!takeSingleThing(player, take, times + i, 1)) {
                        if (!take) {
                            return false;
                        }
                    }
                }
                return true;
            case CLASSIC_ALL:
            case CLASSIC_ANY:
                if (!takeSingleThing(player, take, times, multi)) {
                    if (!take) {
                        return false;
                    }
                }
                return true;
            default:
                return false;
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
            default:
                this.mode = ThingMode.UNKNOWN;
                break;
        }
    }
}
