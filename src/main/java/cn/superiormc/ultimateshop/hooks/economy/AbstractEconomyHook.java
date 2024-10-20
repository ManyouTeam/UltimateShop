package cn.superiormc.ultimateshop.hooks.economy;

import org.bukkit.entity.Player;

public abstract class AbstractEconomyHook {

    protected String pluginName;

    public AbstractEconomyHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public boolean hasEnoughEconomy(Player player, double value, String currencyID) {
        return getEconomy(player, currencyID) >= value;
    }

    public boolean checkEconomy(Player player, double amount, boolean take, String currencyID) {
        boolean result = hasEnoughEconomy(player, amount, currencyID);
        if (take) {
            takeEconomy(player, amount, currencyID);
        }
        return result;
    }

    public abstract double getEconomy(Player player, String currencyID);

    public abstract void takeEconomy(Player player, double value, String currencyID);

    public abstract void giveEconomy(Player player, double value, String currencyID);

    public boolean isEnabled() {
        return true;
    }

    public String getPluginName() {
        return pluginName;
    }
}
