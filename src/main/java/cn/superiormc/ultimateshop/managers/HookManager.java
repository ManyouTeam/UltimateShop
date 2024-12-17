package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.hooks.economy.*;
import cn.superiormc.ultimateshop.hooks.items.*;
import cn.superiormc.ultimateshop.papi.PlaceholderAPIExpansion;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class HookManager {

    public static HookManager hookManager;

    private Map<String, AbstractEconomyHook> economyHooks;

    private Map<String, AbstractItemHook> itemHooks;

    public HookManager() {
        hookManager = this;
        initNormalHook();
        initEconomyHook();
        initItemHook();
    }

    private void initNormalHook() {
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            PlaceholderAPIExpansion.papi = new PlaceholderAPIExpansion(UltimateShop.instance);
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fHooking into PlaceholderAPI...");
            if (PlaceholderAPIExpansion.papi.register()){
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fFinished hook!");
            }
        }
        if (!UltimateShop.freeVersion && CommonUtil.getClass("org.geysermc.floodgate.api.FloodgateApi")) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fHooking into Floodgate...");
            UltimateShop.useGeyser = true;
        }
    }

    private void initEconomyHook() {
        economyHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("Vault")) {
            registerNewEconomyHook("Vault", new EconomyVaultHook());
        }
        if (CommonUtil.checkPluginLoad("PlayerPoints")) {
            registerNewEconomyHook("PlayerPoints", new EconomyPlayerPointsHook());
        }
        if (CommonUtil.checkPluginLoad("CoinsEngine")) {
            registerNewEconomyHook("CoinsEngine", new EconomyCoinsEngineHook());
        }
        if (CommonUtil.checkPluginLoad("UltraEconomy")) {
            registerNewEconomyHook("UltraEconomy", new EconomyUltraEconomyHook());
        }
        if (CommonUtil.checkPluginLoad("EcoBits")) {
            registerNewEconomyHook("EcoBits", new EconomyEcoBitsHook());
        }
        if (CommonUtil.checkPluginLoad("PEconomy")) {
            registerNewEconomyHook("PEconomy", new EconomyPEconomyHook());
        }
        if (CommonUtil.checkPluginLoad("RedisEconomy")) {
            registerNewEconomyHook("RedisEconomy", new EconomyRedisEconomyHook());
        }
        if (CommonUtil.checkPluginLoad("RoyaleEconomy")) {
            registerNewEconomyHook("RoyaleEconomy", new EconomyRoyaleEconomyHook());
        }
        if (CommonUtil.checkPluginLoad("VotingPlugin")) {
            registerNewEconomyHook("VotingPlugin", new EconomyVotingPluginHook());
        }
    }

    private void initItemHook() {
        itemHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            registerNewItemHook("ItemsAdder", new ItemItemsAdderHook());
        }
        if (CommonUtil.checkPluginLoad("Oraxen")) {
            registerNewItemHook("Oraxen", new ItemOraxenHook());
        }
        if (CommonUtil.checkPluginLoad("MMOItems")) {
            registerNewItemHook("MMOItems", new ItemMMOItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoItems")) {
            registerNewItemHook("EcoItems", new ItemEcoItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoArmor")) {
            registerNewItemHook("EcoArmor", new ItemEcoArmorHook());
        }
        if (CommonUtil.checkPluginLoad("MythicMobs")) {
            registerNewItemHook("MythicMobs", new ItemMythicMobsHook());
        }
        if (CommonUtil.checkPluginLoad("eco")) {
            registerNewItemHook("eco", new ItemecoHook());
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            registerNewItemHook("NeigeItems", new ItemNeigeItemsHook());
        }
        if (CommonUtil.checkPluginLoad("ExecutableItems")) {
            registerNewItemHook("ExecutableItems", new ItemExecutableItemsHook());
        }
        if (CommonUtil.checkPluginLoad("Nexo")) {
            registerNewItemHook("Nexo", new ItemNexoHook());
        }
    }

    public void registerNewEconomyHook(String pluginName,
                                       AbstractEconomyHook economyHook) {
        if (!economyHooks.containsKey(pluginName)) {
            economyHooks.put(pluginName, economyHook);
        }
    }

    public void registerNewItemHook(String pluginName,
                                    AbstractItemHook itemHook) {
        if (!itemHooks.containsKey(pluginName)) {
            itemHooks.put(pluginName, itemHook);
        }
    }

    public double getEconomyAmount(Player player, String pluginName, String currencyID) {
        if (!economyHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return 0;
        }
        AbstractEconomyHook economyHook = economyHooks.get(pluginName);
        if (!economyHook.isEnabled()) {
            return 0;
        }
        return economyHook.getEconomy(player, currencyID);
    }

    public int getEconomyAmount(Player player, String vanillaType) {
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

    public boolean getPrice(Player player,
                            String pluginName,
                            String currencyID,
                            double value,
                            boolean take) {
        if (value < 0) {
            return false;
        }
        if (!economyHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return false;
        }
        AbstractEconomyHook economyHook = economyHooks.get(pluginName);
        if (player.hasPermission("ultimateshop.bypassprice")) {
            return true;
        }
        return economyHook.isEnabled() && economyHook.checkEconomy(player, value, take, currencyID);
    }

    public boolean getPrice(Player player, String vanillaType, int value, boolean take) {
        vanillaType = vanillaType.toLowerCase();
        if (vanillaType.equals("exp")) {
            if (player.getTotalExperience() >= value) {
                if (take) {
                    player.giveExp(-value);
                }
                return true;
            }
            return false;
        }
        else if (vanillaType.equals("levels")) {
            if (player.getLevel() >= value) {
                if (take) {
                    player.giveExpLevels(-value);
                }
                return true;
            }
            return false;
        }
        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: You set economy type to "
                + vanillaType + " in shop config, however for now UltimateShop does not support it!");
        return false;
    }

    public ItemStack getHookItem(Player player, String pluginName, String itemID) {
        if (!itemHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return null;
        }
        AbstractItemHook itemHook = itemHooks.get(pluginName);
        return itemHook.getHookItemByID(player, itemID);
    }

    public void giveEconomy(String pluginName, String currencyName, Player player, double value) {
        if (!economyHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return;
        }
        AbstractEconomyHook economyHook = economyHooks.get(pluginName);
        if (!economyHook.isEnabled()) {
            return;
        }
        economyHook.giveEconomy(player, value, currencyName);
    }

    public void giveEconomy(String vanillaType, Player player, int value) {
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
    }

    public void takeEconomy(String pluginName, String currencyName, Player player, double value) {
        if (!economyHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return;
        }
        AbstractEconomyHook economyHook = economyHooks.get(pluginName);
        if (!economyHook.isEnabled()) {
            return;
        }
        economyHook.takeEconomy(player, value, currencyName);
    }

    public String getHookItemID(String pluginName, ItemStack hookItem) {
        if (!hookItem.hasItemMeta()) {
            return null;
        }
        if (!itemHooks.containsKey(pluginName)) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return null;
        }
        AbstractItemHook itemHook = itemHooks.get(pluginName);
        return itemHook.getIDByItemStack(hookItem);
    }

    public String[] getHookItemPluginAndID(ItemStack hookItem) {
        for (AbstractItemHook itemHook : itemHooks.values()) {
            String itemID = itemHook.getIDByItemStack(hookItem);
            if (itemID != null) {
                return new String[]{itemHook.getPluginName(), itemHook.getIDByItemStack(hookItem)};
            }
        }
        return null;
    }
}
