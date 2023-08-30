package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.SellProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equals("reload")) {
                    reloadCommand(sender);
                }
                else {
                    LanguageManager.languageManager.sendStringText(sender, "error.args");
                }
                return true;
            case 2:
                if (args[0].equals("menu")) {
                    menuCommand(sender, args);
                }
                return true;
            case 3:
                if (args[0].equals("quickbuy")) {
                    quickBuyCommand(sender, args);
                }
                else if (args[0].equals("quicksell")) {
                    quickSellCommand(sender, args);
                }
                return true;
        }
        LanguageManager.languageManager.sendStringText(sender, "error.args");
        return true;
    }

    private void reloadCommand(CommandSender sender) {
        if (sender.hasPermission("ultimateshop.reload")) {
            new ConfigManager();
            new LanguageManager();
            LanguageManager.languageManager.sendStringText(sender, "plugin.reloaded");
        }
        else {
            LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
        }
    }

    private void menuCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ultimateshop.menu")) {
                ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
                if (tempVal1 == null) {
                    LanguageManager.languageManager.sendStringText((Player) sender,
                            "error.shop-not-found",
                            "shop",
                             args[1]);
                    return;
                }
                if (sender.hasPermission("ultimateshop.menu.*") ||
                sender.hasPermission("ultimateshop.menu." + args[1])) {
                    ShopGUI gui = new ShopGUI((Player) sender, tempVal1);
                    Listener guiListener = new GUIListener(gui);
                    Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
                    gui.openGUI();
                }
                else {
                    LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
                }
            }
        }
    }

    private void quickBuyCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ultimateshop.quickbuy")) {
                BuyProductMethod.startBuy(args[1], args[2], ((Player) sender).getPlayer(), true);
            }
            else {
                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
            }
        }
        else {
            LanguageManager.languageManager.sendStringText("error.in-game");
        }
    }

    private void quickSellCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ultimateshop.quicksell")) {
                SellProductMethod.startSell(args[1], args[2], ((Player) sender).getPlayer(), true);
            }
            else {
                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
            }
        }
        else {
            LanguageManager.languageManager.sendStringText("error.in-game");
        }
    }
}
