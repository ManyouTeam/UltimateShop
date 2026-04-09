package cn.superiormc.ultimateshop.hooks.economy;

import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

public class EconomyCoinsEngineHook extends AbstractEconomyHook {

    public EconomyCoinsEngineHook() {
        super("CoinsEngine");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        return CoinsEngineAPI.getBalance(player.getUniqueId(), currencyID);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        CoinsEngineAPI.removeBalance(player.getUniqueId(), currencyID, value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        CoinsEngineAPI.addBalance(player.getUniqueId(), currencyID, value);
    }
}
