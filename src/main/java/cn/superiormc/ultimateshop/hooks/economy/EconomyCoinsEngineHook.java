package cn.superiormc.ultimateshop.hooks.economy;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public class EconomyCoinsEngineHook extends AbstractEconomyHook {

    public EconomyCoinsEngineHook() {
        super("CoinsEngine");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        Currency currency = CoinsEngineAPI.getCurrency(currencyID);
        if (currency == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in CoinsEngine plugin!");
            return 0;
        }
        return CoinsEngineAPI.getBalance(player, currency);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        Currency currency = CoinsEngineAPI.getCurrency(currencyID);
        if (currency == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in CoinsEngine plugin!");
            return;
        }
        CoinsEngineAPI.removeBalance(player, currency, value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        Currency currency = CoinsEngineAPI.getCurrency(currencyID);
        if (currency == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in CoinsEngine plugin!");
            return;
        }
        CoinsEngineAPI.addBalance(player, currency, value);
    }
}
