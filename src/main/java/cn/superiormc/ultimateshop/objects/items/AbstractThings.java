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

    public void giveThing(Player player, int times) {
        giveThing(player, times, 1);
    }

    public void giveThing(Player player, int times, int multi) {
        return;
    }

    public boolean takeThing(Player player, boolean take, int times) {
        return takeThing(player, take, times, 1);
    }

    public boolean takeThing(Player player, boolean take, int times, int multi) {
        return false;
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
