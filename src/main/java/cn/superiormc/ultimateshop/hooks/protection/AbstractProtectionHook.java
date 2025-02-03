package cn.superiormc.ultimateshop.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AbstractProtectionHook {

    protected String pluginName;

    public AbstractProtectionHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract boolean canUse(Player player, Location location);
}
