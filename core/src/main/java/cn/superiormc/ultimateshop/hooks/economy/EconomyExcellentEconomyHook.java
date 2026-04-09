package cn.superiormc.ultimateshop.hooks.economy;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

public class EconomyExcellentEconomyHook extends AbstractEconomyHook {

    private ExcellentEconomyAPI eeAPI = null;

    public EconomyExcellentEconomyHook() {
        super("ExcellentEconomy");
        RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
        if (provider != null) {
            this.eeAPI = provider.getProvider();
        }
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        return eeAPI.getBalance(player, currencyID);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        eeAPI.withdraw(player, currencyID, value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        eeAPI.deposit(player, currencyID, value);
    }

    @Override
    public boolean isEnabled() {
        if (eeAPI == null) {
            ErrorManager.errorManager.sendErrorMessage("§cCan not hook into ExcellentEconomy plugin!");
            return false;
        }
        return true;
    }
}
