package cn.superiormc.ultimateshop.hooks;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.bencodez.votingplugin.VotingPluginMain;
import com.bencodez.votingplugin.user.VotingPluginUser;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.CurrencyUtils;
import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;
import me.qKing12.RoyaleEconomy.API.MultiCurrencyHandler;
import me.qKing12.RoyaleEconomy.RoyaleEconomy;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.soknight.peconomy.api.PEconomyAPI;
import ru.soknight.peconomy.database.model.WalletModel;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.math.BigDecimal;

public class EconomyHook {

    public static void giveEconomy(String pluginName, String currencyName, Player player, double value) {
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your server don't have " + pluginName +
                    " plugin, but your shop config try use its hook!");
            return;
        }
        switch (pluginName) {
            case "PlayerPoints":
                PlayerPoints playerPoints = PlayerPoints.getInstance();
                if (playerPoints == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into PlayerPoints plugin, " +
                            "maybe your are using old version, please try update it to newer version!");
                    return;
                }
                playerPoints.getAPI().give(player.getUniqueId(), (int) value);
                return;
            case "Vault":
                RegisteredServiceProvider<Economy> rsp = UltimateShop.instance.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into Vault plugin, " +
                            "Vault is a API plugin, maybe you didn't install a Vault-based economy plugin in your server!");
                    return;
                }
                Economy eco = rsp.getProvider();
                eco.depositPlayer(player, value);
                return;
            case "CoinsEngine":
                Currency currency = CoinsEngineAPI.getCurrency(currencyName);
                if (currency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in CoinsEngine plugin!");
                    return;
                }
                CoinsEngineAPI.addBalance(player, currency, value);
                return;
            case "UltraEconomy":
                UltraEconomyAPI ueAPI = UltraEconomy.getAPI();
                if (ueAPI == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into UltraEconomy plugin!");
                    return;
                }
                if (!UltraEconomy.getAPI().getCurrencies().name(currencyName).isPresent()) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in UltraEconomy plugin!");
                    return;
                }
                UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).get().getBalance(UltraEconomy.getAPI().getCurrencies().name(currencyName).get()).addHand((float) value);
                return;
            case "EcoBits":
                if (Currencies.getByID(currencyName) == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in EcoBits plugin!");
                    return;
                }
                if (CurrencyUtils.getBalance(player, Currencies.getByID(currencyName)).doubleValue() >= value) {
                    CurrencyUtils.adjustBalance(player, Currencies.getByID(currencyName), BigDecimal.valueOf(value));
                    return;
                }
                return;
            case "PEconomy":
                PEconomyAPI peAPI = PEconomyAPI.get();
                if (peAPI == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into PEconomy plugin!");
                    return;
                }
                WalletModel wallet = peAPI.getWallet(player.getName());
                wallet.addAmount(currencyName, (int) value);
                peAPI.updateWallet(wallet);
                return;
            case "RedisEconomy":
                RedisEconomyAPI api = RedisEconomyAPI.getAPI();
                if (api == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into RedisEconomy plugin!");
                    return;
                }
                dev.unnm3d.rediseconomy.currency.Currency redisCurrency = api.getCurrencyByName("vault");
                if (redisCurrency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in RedisEconomy plugin!");
                    return;
                }
                redisCurrency.depositPlayer(player, value);
                return;
            case "RoyaleEconomy":
                me.qKing12.RoyaleEconomy.API.Currency reCurrency = MultiCurrencyHandler.findCurrencyById(currencyName);
                if (reCurrency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in RoyaleEconomy plugin!");
                    return;
                }
                reCurrency.addAmount(player.getUniqueId().toString(), value);
            case "VotingPlugin":
                VotingPluginUser user = VotingPluginMain.getPlugin().getVotingPluginUserManager().getVotingPluginUser(player);
                if (user == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find find user data " +
                            player.getName() + " in VotingPlugin plugin!");
                    return;
                }
                user.addPoints((int) value);
                return;
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set hook plugin to "
                + pluginName + " in shop config, however for now UltimateShop does not support it!");
    }

    public static void giveEconomy(String vanillaType, Player player, int value) {
        vanillaType = vanillaType.toLowerCase();
        if (vanillaType.equals("exp")) {
            player.giveExp(value);
            return;
        } else if (vanillaType.equals("levels")) {
            player.giveExpLevels(value);
            return;
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set economy type to "
                + vanillaType + " in shop config, however for now UltimateShop does not support it!");
        return;
    }
}