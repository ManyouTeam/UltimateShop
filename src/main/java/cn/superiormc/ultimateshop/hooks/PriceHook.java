package cn.superiormc.ultimateshop.hooks;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.CurrencyUtils;
import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;
import me.qKing12.RoyaleEconomy.API.MultiCurrencyHandler;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.soknight.peconomy.api.PEconomyAPI;
import ru.soknight.peconomy.database.model.WalletModel;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.gamepoints.api.GamePointsAPI;
import su.nightexpress.gamepoints.data.PointUser;

import java.math.BigDecimal;

public class PriceHook {

    public static boolean getPrice(Player player, String pluginName, String currencyName, double value, boolean take) {
        if (value < 0) {
            return false;
        }
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your server don't have " + pluginName +
                    " plugin, but your shop config try use its hook!");
            return false;
        }
        switch (pluginName) {
            case "GamePoints":
                PointUser user = GamePointsAPI.getUserData(player);
                if (user.getBalance() >= value) {
                    if (take) {
                        user.takePoints((int) value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "PlayerPoints":
                PlayerPoints playerPoints = PlayerPoints.getInstance();
                if (playerPoints == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into PlayerPoints plugin, " +
                            "maybe your are using old version, please try update it to newer version!");
                    return false;
                }
                double balance = playerPoints.getAPI().look(player.getUniqueId());
                if (balance >= value) {
                    if (take) {
                        playerPoints.getAPI().take(player.getUniqueId(), (int) value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "Vault":
                RegisteredServiceProvider<Economy> rsp = UltimateShop.instance.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into Vault plugin, " +
                            "Vault is a API plugin, maybe you didn't install a Vault-based economy plugin in your server!");
                    return false;
                }
                Economy eco = rsp.getProvider();
                if (eco.has(player, value)) {
                    if (take) {
                        eco.withdrawPlayer(player, value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "CoinsEngine":
                Currency currency = CoinsEngineAPI.getCurrency(currencyName);
                if (currency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in CoinsEngine plugin!");
                    return false;
                }
                if (CoinsEngineAPI.getBalance(player, currency) >= value) {
                    if (take) {
                        CoinsEngineAPI.removeBalance(player, currency, value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "UltraEconomy":
                UltraEconomyAPI ueAPI = UltraEconomy.getAPI();
                if (ueAPI == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into UltraEconomy plugin!");
                    return false;
                }
                if (!UltraEconomy.getAPI().getCurrencies().name(currencyName).isPresent()) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in UltraEconomy plugin!");
                    return false;
                }
                if (UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).get().getBalance(UltraEconomy.getAPI().getCurrencies().name(currencyName).get()).getOnHand() >= value) {
                    if (take) {
                        UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).get().getBalance(UltraEconomy.getAPI().getCurrencies().name(currencyName).get()).removeHand((float) value);
                    }
                    return true;
                } else {
                    return false;
                }
            case "EcoBits":
                if (Currencies.getByID(currencyName) == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in EcoBits plugin!");
                    return false;
                }
                if (CurrencyUtils.getBalance(player, Currencies.getByID(currencyName)).doubleValue() >= value) {
                    if (take) {
                        CurrencyUtils.adjustBalance(player, Currencies.getByID(currencyName), BigDecimal.valueOf(-value));
                    }
                    return true;
                } else {
                    return false;
                }
            case "PEconomy":
                PEconomyAPI peAPI = PEconomyAPI.get();
                if (peAPI == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into PEconomy plugin!");
                    return false;
                }
                if (peAPI.hasAmount(player.getName(), currencyName, (int) value)) {
                    if (take) {
                        WalletModel wallet = peAPI.getWallet(player.getName());
                        wallet.takeAmount(currencyName, (int) value);
                        peAPI.updateWallet(wallet);
                    }
                    return true;
                }
                else {
                    return false;
                }
            case "RedisEconomy":
                RedisEconomyAPI api = RedisEconomyAPI.getAPI();
                if (api == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into RedisEconomy plugin!");
                    return false;
                }
                dev.unnm3d.rediseconomy.currency.Currency redisCurrency = api.getCurrencyByName("vault");
                if (redisCurrency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in RedisEconomy plugin!");
                    return false;
                }
                if (redisCurrency.getBalance(player) >= value) {
                    if (take) {
                        redisCurrency.withdrawPlayer(player, value);
                    }
                    return true;
                }
                else {
                    return false;
                }
            case "RoyaleEconomy":
                me.qKing12.RoyaleEconomy.API.Currency reCurrency = MultiCurrencyHandler.findCurrencyById(currencyName);
                if (reCurrency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in RoyaleEconomy plugin!");
                    return false;
                }
                if (reCurrency.getAmount(player.getUniqueId().toString()) >= value) {
                    if (take) {
                        reCurrency.removeAmount(player.getUniqueId().toString(), value);
                    }
                    return true;
                }
                else {
                    return false;
                }
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set hook plugin to "
                + pluginName + " in shop config, however for now UltimateShop does not support it!");
        return false;
    }

    public static double getEconomyAmount(Player player, String pluginName, String currencyName) {
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your server don't have " + pluginName +
                    " plugin, but your shop config try use its hook!");
            return 0;
        }
        switch (pluginName) {
            case "GamePoints":
                PointUser user = GamePointsAPI.getUserData(player);
                return user.getBalance();
            case "PlayerPoints":
                PlayerPoints playerPoints = PlayerPoints.getInstance();
                if (playerPoints == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into PlayerPoints plugin, " +
                            "maybe your are using old version, please try update it to newer version!");
                    return 0;
                }
                double balance = playerPoints.getAPI().look(player.getUniqueId());
                return balance;
            case "Vault":
                RegisteredServiceProvider<Economy> rsp = UltimateShop.instance.getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into Vault plugin, " +
                            "Vault is a API plugin, maybe you didn't install a Vault-based economy plugin in your server!");
                    return 0;
                }
                Economy eco = rsp.getProvider();
                return eco.getBalance(player);
            case "CoinsEngine":
                Currency currency = CoinsEngineAPI.getCurrency(currencyName);
                if (currency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in CoinsEngine plugin!");
                    return 0;
                }
                return CoinsEngineAPI.getBalance(player, currency);
            case "UltraEconomy":
                UltraEconomyAPI ueAPI = UltraEconomy.getAPI();
                if (ueAPI == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into UltraEconomy plugin!");
                    return 0;
                }
                if (!UltraEconomy.getAPI().getCurrencies().name(currencyName).isPresent()) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in UltraEconomy plugin!");
                    return 0;
                }
                return UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).get().
                        getBalance(UltraEconomy.getAPI().getCurrencies().name(currencyName).get()).getOnHand();
            case "EcoBits":
                if (Currencies.getByID(currencyName) == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in EcoBits plugin!");
                    return 0;
                }
                return CurrencyUtils.getBalance(player, Currencies.getByID(currencyName)).doubleValue();
            case "PEconomy":
                PEconomyAPI peAPI = PEconomyAPI.get();
                if (peAPI == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into PEconomy plugin!");
                    return 0;
                }
                return peAPI.getAmount(player.getName(), currencyName);
            case "RedisEconomy":
                RedisEconomyAPI api = RedisEconomyAPI.getAPI();
                if (api == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not hook into RedisEconomy plugin!");
                    return 0;
                }
                dev.unnm3d.rediseconomy.currency.Currency redisCurrency = api.getCurrencyByName("vault");
                if (redisCurrency == null) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not find currency " +
                            currencyName + " in RedisEconomy plugin!");
                    return 0;
                }
                return redisCurrency.getBalance(player);
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set hook plugin to "
                + pluginName + " in shop config, however for now UltimateShop does not support it!");
        return 0;
    }

    public static boolean getPrice(Player player, String vanillaType, int value, boolean take) {
        vanillaType = vanillaType.toLowerCase();
        if (vanillaType.equals("exp")) {
            if (player.getTotalExperience() >= value) {
                if (take) {
                    player.giveExp(-value);
                }
                return true;
            }
            else {
                return false;
            }
        }
        else if (vanillaType.equals("levels")) {
            if (player.getLevel() >= value) {
                if (take) {
                    player.giveExpLevels(-value);
                }
                return true;
            }
            else {
                return false;
            }
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set economy type to "
                + vanillaType + " in shop config, however for now UltimateShop does not support it!");
        return false;
    }

    public static int getEconomyAmount(Player player, String vanillaType) {
        vanillaType = vanillaType.toLowerCase();
        if (vanillaType.equals("exp")) {
            return player.getTotalExperience();
        }
        else if (vanillaType.equals("levels")) {
            return player.getLevel();
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set economy type to "
                + vanillaType + " in shop config, however for now UltimateShop does not support it!");
        return 0;
    }

    public static boolean getPrice(Inventory inventory, Player player, String pluginName, String item, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storage = inventory.getStorageContents();
        int amount = getItemAmount(inventory, player, pluginName, item);
        if (amount >= value) {
            if (take) {
                for (int i = 0 ; i < storage.length ; i++) {
                    if (storage[i] == null || storage[i].getType().isAir()) {
                        continue;
                    }
                    ItemStack temItem = storage[i].clone();
                    temItem.setAmount(1);
                    String tempVal10 = CheckValidHook.checkValid(pluginName, temItem);
                    if (tempVal10 != null && tempVal10.equals(item)) {
                        if (storage[i].getAmount() >= value) {
                            storage[i].setAmount(storage[i].getAmount() - value);
                            break;
                        } else {
                            value -= storage[i].getAmount();
                            storage[i].setAmount(0);
                        }
                    }
                }
                if (inventory instanceof PlayerInventory) {
                    player.getInventory().setStorageContents(storage);
                }
                else {
                    inventory.setStorageContents(storage);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    public static int getItemAmount(Inventory inventory, Player player, String pluginName, String item) {
        if (item == null) {
            return 0;
        }
        int amount = 0;
        ItemStack[] storage = inventory.getStorageContents();
        for (ItemStack tempVal1 : storage) {
            if (tempVal1 == null || tempVal1.getType().isAir()) {
                continue;
            }
            ItemStack temItem = tempVal1.clone();
            temItem.setAmount(1);
            String tempVal10 = CheckValidHook.checkValid(pluginName, temItem);
            if (tempVal10 != null && tempVal10.equals(item)) {
                amount += tempVal1.getAmount();
            }
            else if (temItem == ItemsHook.getHookItem(pluginName, item)) {
                amount += tempVal1.getAmount();
            }
        }
        return amount;
    }

    public static boolean getPrice(Inventory inventory, Player player, ItemStack item, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storage = inventory.getStorageContents();
        int amount = getItemAmount(inventory, player, item);
        if (amount >= value) {
            if (take) {
                for (int i = 0 ; i < storage.length ; i++) {
                    if (storage[i] == null || storage[i].getType().isAir()) {
                        continue;
                    }
                    ItemStack temItem = storage[i].clone();
                    temItem.setAmount(1);
                    if (temItem.equals(item)) {
                        if (storage[i].getAmount() >= value) {
                            storage[i].setAmount(storage[i].getAmount() - value);
                            break;
                        } else {
                            value -= storage[i].getAmount();
                            storage[i].setAmount(0);
                        }
                    }
                }
                if (inventory instanceof PlayerInventory) {
                    player.getInventory().setStorageContents(storage);
                }
                else {
                    inventory.setStorageContents(storage);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    public static int getItemAmount(Inventory inventory, Player player, ItemStack item) {
        if (item == null) {
            return 0;
        }
        ItemStack[] storage = inventory.getStorageContents();
        int amount = 0;
        for (ItemStack tempVal1 : storage) {
            if (tempVal1 == null || tempVal1.getType().isAir()) {
                continue;
            }
            ItemStack temItem = tempVal1.clone();
            temItem.setAmount(1);
            if (temItem.equals(item)) {
                amount += tempVal1.getAmount();
            }
        }
        return amount;
    }

}
