package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                noCommand(sender);
                return true;
            case 1:
                if (args[0].equals("reload")) {
                    reloadCommand(sender);
                }
                else if (args[0].equals("help")) {
                    helpCommand(sender);
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
            case 4:
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

    private void noCommand(CommandSender sender) {
        if (sender instanceof Player) {
            if (ConfigManager.configManager.getBoolean("menu.auto-open.enabled")) {
                String tempVal1 = ConfigManager.configManager.getString("menu.auto-open.menu");
                ObjectMenu menu = ObjectMenu.commonMenus.get(tempVal1);
                if (menu == null) {
                    ObjectShop shop = ConfigManager.configManager.getShop(tempVal1);
                    if (shop == null) {
                        LanguageManager.languageManager.sendStringText(sender, "error.args");
                    }
                    else {
                        OpenGUI.openShopGUI((Player) sender, shop);
                    }
                }
                else {
                    OpenGUI.openCommonGUI((Player) sender, tempVal1);
                }
            }
            else {
                LanguageManager.languageManager.sendStringText(sender, "error.args");
            }
        }
        else {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
        }
    }

    private void helpCommand(CommandSender sender) {
        if (sender instanceof Player) {
            if (sender.hasPermission("spintowin.admin.help")) {
                LanguageManager.languageManager.sendStringText((Player) sender, "help.main-admin");
                return;
            }
            LanguageManager.languageManager.sendStringText((Player) sender, "help.main");
        }
        else {
            LanguageManager.languageManager.sendStringText((Player) sender, "help.main-console");
        }
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
            if (sender.hasPermission("ultimateshop.menu") &&
                    (sender.hasPermission("ultimateshop.menu.*") ||
                            sender.hasPermission("ultimateshop.menu." + args[1]))) {
                ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
                if (tempVal1 == null) {
                    if (args[1].equals(ConfigManager.configManager.getString("menu.select-more.menu"))) {
                        return;
                    }
                    OpenGUI.openCommonGUI((Player) sender, args[1]);
                }
                else {
                    OpenGUI.openShopGUI((Player) sender, tempVal1);
                }
            }
            else {
                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
            }
        }
    }

    private void quickBuyCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ultimateshop.quickbuy")) {
                switch (args.length) {
                    case 3:
                        BuyProductMethod.startBuy(args[1], args[2], ((Player) sender).getPlayer(), true);
                        break;
                    case 4:
                        BuyProductMethod.startBuy(args[1],
                                args[2],
                                ((Player) sender).getPlayer(),
                                true,
                                false,
                                Integer.parseInt(args[3]));
                        break;
                }
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
                switch (args.length) {
                    case 3:
                        SellProductMethod.startSell(args[1], args[2], ((Player) sender).getPlayer(), true);
                        break;
                    case 4:
                        SellProductMethod.startSell(args[1],
                                args[2],
                                ((Player) sender).getPlayer(),
                                true,
                                false,
                                Integer.parseInt(args[3]));
                        break;
                }
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
