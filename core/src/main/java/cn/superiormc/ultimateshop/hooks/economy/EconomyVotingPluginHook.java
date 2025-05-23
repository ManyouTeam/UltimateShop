package cn.superiormc.ultimateshop.hooks.economy;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import com.bencodez.votingplugin.VotingPluginMain;
import com.bencodez.votingplugin.user.VotingPluginUser;
import org.bukkit.entity.Player;

public class EconomyVotingPluginHook extends AbstractEconomyHook {

    public EconomyVotingPluginHook() {
        super("VotingPlugin");
    }

    @Override
    public double getEconomy(Player player, String currencyID) {
        VotingPluginUser user = VotingPluginMain.getPlugin().getVotingPluginUserManager().getVotingPluginUser(player);
        if (user == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find find user data " +
                    player.getName() + " in VotingPlugin plugin!");
            return 0;
        }
        return user.getPoints();
    }

    @Override
    public void takeEconomy(Player player, double value, String currencyID) {
        VotingPluginUser user = VotingPluginMain.getPlugin().getVotingPluginUserManager().getVotingPluginUser(player);
        if (user == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find find user data " +
                    player.getName() + " in VotingPlugin plugin!");
            return;
        }
        user.removePoints((int) value);
    }

    @Override
    public void giveEconomy(Player player, double value, String currencyID) {
        VotingPluginUser user = VotingPluginMain.getPlugin().getVotingPluginUserManager().getVotingPluginUser(player);
        if (user == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find find user data " +
                    player.getName() + " in VotingPlugin plugin!");
            return;
        }
        user.addPoints((int) value);
    }
}
