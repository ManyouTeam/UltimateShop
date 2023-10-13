package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Create.CreateProduct;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.methods.SellStickItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import com.cryptomorin.xseries.XItemStack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            noCommand(sender);
            return true;
        }
        else if (args[0].equals("createshop") ||
                args[0].equals("createproduct")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("ultimateshop.admin")) {
                    String[] newArray = new String[args.length - 1];
                    System.arraycopy(args, 1, newArray, 0, args.length - 1);
                    switch (args[0]) {
                        case "createshop" :
                            CreateProduct.createShop((Player) sender, newArray);
                            break;
                        case "createproduct" :
                            CreateProduct.createProduct((Player) sender, newArray);
                            break;
                        case "setproductbuyprice" :
                            CreateProduct.setProductPrice((Player) sender, true, newArray);
                            break;
                        case "setproductsellprice" :
                            CreateProduct.setProductPrice((Player) sender, false, newArray);
                            break;
                    }
                } else {
                    LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
                }
            } else {
                LanguageManager.languageManager.sendStringText("error.in-game");
            }
            return true;
        }
        switch (args.length) {
            case 1:
                switch (args[0]) {
                    case "reload":
                        reloadCommand(sender);
                        break;
                    case "help":
                        helpCommand(sender);
                        break;
                    case "sellall":
                        sellAllCommand(sender);
                        break;
                    default:
                        LanguageManager.languageManager.sendStringText(sender, "error.args");
                        break;
                }
                return true;
            case 2:
                if (args[0].equals("menu")) {
                    menuCommand(sender, args);
                }
                return true;
            case 3:
            case 4:
                if (args[0].equals("quickbuy")) {
                    quickBuyCommand(sender, args);
                }
                else if (args[0].equals("quicksell")) {
                    quickSellCommand(sender, args);
                }
                else if (args[0].equals("givesellstick") && !UltimateShop.freeVersion) {
                    giveSellStickCommand(sender, args);
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
            if (sender.hasPermission("ultimateshop.admin")) {
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
            ReloadPlugin.reload(sender);
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
        else {
            LanguageManager.languageManager.sendStringText("error.in-game");
        }
    }

    private void quickBuyCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ultimateshop.quickbuy")) {
                switch (args.length) {
                    case 3:
                        if (sender.hasPermission("ultimateshop.quickbuy." + args[1] + "." + args[2])) {
                            BuyProductMethod.startBuy(args[1], args[2], ((Player) sender).getPlayer(), true);
                        }
                        else {
                            LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
                        }
                        break;
                    case 4:
                        if (sender.hasPermission("ultimateshop.quickbuy." + args[1] + "." + args[2])) {
                            BuyProductMethod.startBuy(args[1],
                                    args[2],
                                    ((Player) sender).getPlayer(),
                                    true,
                                    false,
                                    Integer.parseInt(args[3]));
                        }
                        else {
                            LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
                        }
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
                        if (sender.hasPermission("ultimateshop.quicksell." + args[1] + "." + args[2])) {
                            SellProductMethod.startSell(args[1], args[2], ((Player) sender).getPlayer(), true);
                        }
                        else {
                            LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
                        }
                        break;
                    case 4:
                        if (sender.hasPermission("ultimateshop.quicksell." + args[1] + "." + args[2])) {
                            SellProductMethod.startSell(args[1],
                                args[2],
                                ((Player) sender).getPlayer(),
                                true,
                                false,
                                Integer.parseInt(args[3]));
                        }
                        else {
                            LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
                        }
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

    private void sellAllCommand(CommandSender sender) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ultimateshop.sellall")) {
                OpenGUI.openSellAllGUI((Player) sender);
            }
            else {
                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
            }
        }
        else {
            LanguageManager.languageManager.sendStringText("error.in-game");
        }
    }

    private void giveSellStickCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("ultimateshop.givesellstick")) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                LanguageManager.languageManager.sendStringText
                        (sender,
                                "error.player-not-found",
                                "player",
                                args[2]);
                return;
            }
            switch (args.length) {
                // /shop givestick 物品名称 玩家名称 数量
                case 3:
                    XItemStack.giveOrDrop(player, SellStickItem.getExtraSlotItem(player, args[1], 1));
                    LanguageManager.languageManager.sendStringText(sender,
                            "give-sell-stick",
                            "player",
                            player.getName(),
                            "item",
                            args[1],
                            "amount",
                            "1");
                    break;
                case 4:
                    XItemStack.giveOrDrop(player, SellStickItem.getExtraSlotItem(player, args[1], Integer.parseInt(args[3])));
                    LanguageManager.languageManager.sendStringText(sender,
                            "give-sell-stick",
                            "player",
                            player.getName(),
                            "item",
                            args[1],
                            "amount",
                            args[3]);
                    break;
            }
        }
        else {
            LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
        }
    }
}
