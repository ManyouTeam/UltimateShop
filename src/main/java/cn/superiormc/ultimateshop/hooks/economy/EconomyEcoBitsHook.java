package cn.superiormc.ultimateshop.hooks.economy;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;
import com.willfp.ecobits.currencies.CurrencyUtils;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class EconomyEcoBitsHook extends AbstractEconomyHook {

    public EconomyEcoBitsHook() {
        super("EcoBits");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        Currency currencies = Currencies.getByID(currencyID);
        if (currencies == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in EcoBits plugin!");
            return 0;
        }
        return CurrencyUtils.getBalance(player, currencies).doubleValue();
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        Currency currencies = Currencies.getByID(currencyID);
        if (currencies == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in EcoBits plugin!");
            return;
        }
        CurrencyUtils.adjustBalance(player, currencies, BigDecimal.valueOf(-value));
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        Currency currencies = Currencies.getByID(currencyID);
        if (currencies == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in EcoBits plugin!");
            return;
        }
        CurrencyUtils.adjustBalance(player, currencies, BigDecimal.valueOf(value));
    }
}
