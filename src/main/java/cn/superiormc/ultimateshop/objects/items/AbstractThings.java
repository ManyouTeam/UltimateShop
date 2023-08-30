package cn.superiormc.ultimateshop.objects.items;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;


public abstract class AbstractThings {

    public String mode;

    public ConfigurationSection section;

    public boolean empty;

    public AbstractThings() {
        this.mode = "UNKNOWN";
        this.empty = true;
    }

    public AbstractThings(ConfigurationSection section, String mode) {
        this.section = section;
        this.mode = mode;
        this.empty = false;
    }

    public String getMode() {
        return mode;
    }

    public void giveThing(Player player, int times) {
        return;
    }

    public boolean takeThing(Player player, boolean take, int times) {
        return false;
    }
}
