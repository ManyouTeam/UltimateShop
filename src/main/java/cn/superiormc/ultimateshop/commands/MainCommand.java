package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.methods.SellStickItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import com.cryptomorin.xserieschanged.XItemStack;
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
                    case "editor":
                        if (sender instanceof Player) {
                            if (sender.hasPermission("ultimateshop.editor")) {
                                OpenGUI.openEditorGUI((Player) sender);
                                break;
                            }
                            else {
                                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
                            }
                        }
                        else {
                            LanguageManager.languageManager.sendStringText("error.in-game");
                        }
                    default:
                        LanguageManager.languageManager.sendStringText(sender, "error.args");
                        break;
                }
                return true;
            case 2:
                if (args[0].equals("menu")) {
                    menuCommand(sender, args);
                }
                else if (args[0].equals("saveitem")) {
                    saveItemCommand(sender, args);
                }
                else {
                    LanguageManager.languageManager.sendStringText(sender, "error.args");
                }
                return true;
            case 3:
                if (args[0].equals("menu")) {
                    menuCommand(sender, args);
                } else if (args[0].equals("quickbuy")) {
                    quickBuyCommand(sender, args);
                }
                else if (args[0].equals("quicksell")) {
                    quickSellCommand(sender, args);
                }
                else if (args[0].equals("givesellstick") && !UltimateShop.freeVersion) {
                    giveSellStickCommand(sender, args);
                } else {
                    LanguageManager.languageManager.sendStringText(sender, "error.args");
                }
                return true;
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
                else if (args[0].equals("setselltimes")) {
                    setSellTimesCommand(sender, args);
                }
                else if (args[0].equals("setbuytimes")) {
                    setBuyTimesCommand(sender, args);
                } else {
                    LanguageManager.languageManager.sendStringText(sender, "error.args");
                }
                return true;
            case 5:
                if (args[0].equals("setselltimes")) {
                    setSellTimesCommand(sender, args);
                }
                else if (args[0].equals("setbuytimes")) {
                    setBuyTimesCommand(sender, args);
                } else {
                    LanguageManager.languageManager.sendStringText(sender, "error.args");
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
            LanguageManager.languageManager.sendStringText(sender, "help.main-console");
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
            if (sender.hasPermission("ultimateshop.menu")) {
                if (args.length < 2) {
                    LanguageManager.languageManager.sendStringText(sender,
                            "error.args");
                    return;
                }
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
            if (args.length < 3) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
                return;
            }
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                LanguageManager.languageManager.sendStringText
                        (sender,
                                "error.player-not-found",
                                "player",
                                args[2]);
                return;
            }
            ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
            if (tempVal1 == null) {
                if (args[1].equals(ConfigManager.configManager.getString("menu.select-more.menu"))) {
                    return;
                }
                OpenGUI.openCommonGUI(player, args[1]);
            }
            else {
                OpenGUI.openShopGUI(player, tempVal1);
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
                    default:
                        LanguageManager.languageManager.sendStringText(sender,
                                "error.args");
                        break;
                }
            }
            else {
                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
            }
        }
        else {
            if (args.length < 4) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
                return;
            }
            Player player = Bukkit.getPlayer(args[args.length - 1]);
            if (player == null) {
                LanguageManager.languageManager.sendStringText
                        (sender,
                                "error.player-not-found",
                                "player",
                                args[args.length - 1]);
                return;
            }
            switch (args.length) {
                case 4:
                    BuyProductMethod.startBuy(args[1], args[2], player, true);
                    break;
                case 5:
                    BuyProductMethod.startBuy(args[1],
                            args[2],
                            player,
                            true,
                            false,
                            Integer.parseInt(args[3]));
                    break;
                default:
                    LanguageManager.languageManager.sendStringText(sender,
                            "error.args");
                    break;
            }
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
                    default:
                        LanguageManager.languageManager.sendStringText(sender,
                                "error.args");
                        break;
                }
            }
            else {
                LanguageManager.languageManager.sendStringText((Player) sender, "error.miss-permission");
            }
        }
        else {
            if (args.length < 4) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
                return;
            }
            Player player = Bukkit.getPlayer(args[args.length - 1]);
            if (player == null) {
                LanguageManager.languageManager.sendStringText
                        (sender,
                                "error.player-not-found",
                                "player",
                                args[args.length - 1]);
                return;
            }
            switch (args.length) {
                case 4:
                    SellProductMethod.startSell(args[1], args[2], player, true);
                    break;
                case 5:
                    SellProductMethod.startSell(args[1],
                            args[2],
                            player,
                            true,
                            false,
                            Integer.parseInt(args[3]));
                    break;
                default:
                    LanguageManager.languageManager.sendStringText(sender,
                            "error.args");
                    break;
            }
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

    private void saveItemCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 2) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
            }
            else if (sender.hasPermission("ultimateshop.saveitem")) {
                ItemManager.itemManager.saveMainHandItem((Player) sender, args[1]);
                LanguageManager.languageManager.sendStringText((Player) sender, "plugin.saved");
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
            if (args.length < 3) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
                return;
            }
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
                default:
                    LanguageManager.languageManager.sendStringText(sender,
                            "error.args");
                    break;
            }
        } else {
            LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
        }
    }
    private void setBuyTimesCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("ultimateshop.setbuytimes")) {
            if (args.length < 4) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
                return;
            }
            ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
            if (tempVal1 == null) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.shop-not-found",
                        "shop",
                        args[1]);
                return;
            }
            ObjectItem tempVal2 = tempVal1.getProduct(args[2]);
            if (tempVal2 == null) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.product-not-found",
                        "product",
                        args[2]);
                return;
            }
            ServerCache tempVal3 = null;
            if (args[3].equals("global")) {
                tempVal3 = ServerCache.serverCache;
            }
            else {
                Player player = Bukkit.getPlayer(args[3]);
                if (player == null) {
                    LanguageManager.languageManager.sendStringText
                            (sender,
                                    "error.player-not-found",
                                    "player",
                                    args[3]);
                    return;
                }
                tempVal3 = CacheManager.cacheManager.getPlayerCache(player);
            }
            if (tempVal3 == null) {
                LanguageManager.languageManager.sendStringText
                        (sender,
                                "error.player-not-found",
                                "player",
                                args[3]);
                return;
            }
            ObjectUseTimesCache tempVal4 = tempVal3.getUseTimesCache().get(tempVal2);
            switch (args.length) {
                case 4:
                    if (tempVal4 == null) {
                        tempVal3.setUseTimesCache(tempVal1.getShopName(),
                                tempVal2.getProduct(),
                                0,
                                0,
                                null,
                                null,
                                null,
                                null);
                    }
                    else {
                        tempVal4.setBuyUseTimes(0);
                    }
                    LanguageManager.languageManager.sendStringText(sender,
                            "set-times",
                            "player",
                            args[3],
                            "item",
                            args[2],
                            "times",
                            "0");
                    break;
                case 5:
                    if (tempVal4 == null) {
                        tempVal3.setUseTimesCache(tempVal1.getShopName(),
                                tempVal2.getProduct(),
                                Integer.parseInt(args[4]),
                                0,
                                null,
                                null,
                                null,
                                null);
                    }
                    else {
                        tempVal4.setBuyUseTimes(Integer.parseInt(args[4]));
                    }
                    LanguageManager.languageManager.sendStringText(sender,
                            "set-times",
                            "player",
                            args[3],
                            "item",
                            args[2],
                            "times",
                            args[4]);
                    break;
                default:
                    LanguageManager.languageManager.sendStringText(sender,
                            "error.args");
                    break;
            }
        } else {
            LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
        }
    }
    private void setSellTimesCommand(CommandSender sender, String[] args) {
        // /shop setusetimes 商店名称 物品名称 玩家名称 次数
        if (sender.hasPermission("ultimateshop.setselltimes")) {
            if (args.length < 4) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.args");
                return;
            }
            ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
            if (tempVal1 == null) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.shop-not-found",
                        "shop",
                        args[1]);
                return;
            }
            ObjectItem tempVal2 = tempVal1.getProduct(args[2]);
            if (tempVal2 == null) {
                LanguageManager.languageManager.sendStringText(sender,
                        "error.product-not-found",
                        "product",
                        args[2]);
                return;
            }
            ServerCache tempVal3 = null;
            if (args[3].equals("global")) {
                tempVal3 = ServerCache.serverCache;
            }
            else {
                Player player = Bukkit.getPlayer(args[3]);
                if (player == null) {
                    LanguageManager.languageManager.sendStringText
                            (sender,
                                    "error.player-not-found",
                                    "player",
                                    args[3]);
                    return;
                }
                tempVal3 = CacheManager.cacheManager.getPlayerCache(player);
            }
            if (tempVal3 == null) {
                LanguageManager.languageManager.sendStringText
                        (sender,
                                "error.player-not-found",
                                "player",
                                args[3]);
                return;
            }
            ObjectUseTimesCache tempVal4 = tempVal3.getUseTimesCache().get(tempVal2);
            switch (args.length) {
                case 4:
                    if (tempVal4 == null) {
                        tempVal3.setUseTimesCache(tempVal1.getShopName(),
                                tempVal2.getProduct(),
                                0,
                                0,
                                null,
                                null,
                                null,
                                null);
                    }
                    else {
                        tempVal4.setSellUseTimes(0);
                    }
                    LanguageManager.languageManager.sendStringText(sender,
                            "set-times",
                            "player",
                            args[3],
                            "item",
                            args[2],
                            "times",
                            "0");
                    break;
                case 5:
                    if (tempVal4 == null) {
                        tempVal3.setUseTimesCache(tempVal1.getShopName(),
                                tempVal2.getProduct(),
                                0,
                                Integer.parseInt(args[4]),
                                null,
                                null,
                                null,
                                null);
                    }
                    else {
                        tempVal4.setSellUseTimes(Integer.parseInt(args[4]));
                    }
                    LanguageManager.languageManager.sendStringText(sender,
                            "set-times",
                            "player",
                            args[3],
                            "item",
                            args[2],
                            "times",
                            args[4]);
                    break;
                default:
                    LanguageManager.languageManager.sendStringText(sender,
                            "error.args");
                    break;
            }
        } else {
            LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
        }
    }
}
