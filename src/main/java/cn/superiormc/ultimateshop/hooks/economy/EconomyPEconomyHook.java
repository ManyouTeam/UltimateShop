package cn.superiormc.ultimateshop.hooks.economy;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.entity.Player;
import ru.soknight.peconomy.api.PEconomyAPI;
import ru.soknight.peconomy.database.model.WalletModel;

public class EconomyPEconomyHook extends AbstractEconomyHook {

    private final PEconomyAPI peAPI;

    public EconomyPEconomyHook() {
        super("PEconomy");
        peAPI = PEconomyAPI.get();
    }

    @Override
    public boolean hasEnoughEconomy(Player player, double value, String currencyID) {
        return peAPI.hasAmount(player.getName(), currencyID, (int) value);
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        return peAPI.getAmount(player.getName(), currencyID);
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        WalletModel wallet = peAPI.getWallet(player.getName());
        wallet.takeAmount(currencyID, (int) value);
        peAPI.updateWallet(wallet);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        WalletModel wallet = peAPI.getWallet(player.getName());
        wallet.addAmount(currencyID, (int) value);
        peAPI.updateWallet(wallet);
    }

    @Override
    public boolean isEnabled() {
        if (peAPI == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into Vault plugin, " +
                    "Vault is a API plugin, maybe you didn't install a Vault-based economy plugin in your server!");
            return false;
        }
        return true;
    }
}
