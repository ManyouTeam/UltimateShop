package cn.superiormc.ultimateshop.hooks.economy;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import dev.unnm3d.rediseconomy.currency.Currency;
import org.bukkit.entity.Player;

public class EconomyRedisEconomyHook extends AbstractEconomyHook {

    private final RedisEconomyAPI api;

    public EconomyRedisEconomyHook() {
        super("RedisEconomy");
        api = RedisEconomyAPI.getAPI();
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        Currency redisCurrency = api.getCurrencyByName(currencyID);
        if (redisCurrency == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in RedisEconomy plugin!");
            return 0;
        }
        return redisCurrency.getBalance(player);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        Currency redisCurrency = api.getCurrencyByName(currencyID);
        if (redisCurrency == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in RedisEconomy plugin!");
            return;
        }
        redisCurrency.withdrawPlayer(player, value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        Currency redisCurrency = api.getCurrencyByName(currencyID);
        if (redisCurrency == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                    currencyID + " in RedisEconomy plugin!");
            return;
        }
        redisCurrency.depositPlayer(player, value);
    }

    @Override
    public boolean isEnabled() {
        if (api == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into RedisEconomy plugin!");
            return true;
        }
        return true;
    }
}
