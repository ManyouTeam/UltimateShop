package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTab implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 1 :
                if (sender.hasPermission("ultimateshop.quickbuy")) {
                    tempVal1.add("quickbuy");
                }
                if (sender.hasPermission("ultimateshop.quicksell")) {
                    tempVal1.add("quicksell");
                }
                if (sender.hasPermission("ultimateshop.menu")) {
                    tempVal1.add("menu");
                }
                if (sender.hasPermission("ultimateshop.reload")) {
                    tempVal1.add("reload");
                }
                if (sender.hasPermission("ultimateshop.admin")) {
                    tempVal1.add("createshop");
                    tempVal1.add("createproduct");
                    tempVal1.add("setproductbuyprice");
                    tempVal1.add("setproductsellprice");
                }
                break;
            case 2:
                switch (args[0]) {
                    case "quickbuy" : case "quicksell" : case "createproduct" :
                    case "setproductbuyprice" : case "setproductsellprice" :
                        for (ObjectShop tempVal2: ConfigManager.configManager.getShopList()) {
                            tempVal1.add(tempVal2.getShopName());
                        }
                        break;
                    case "menu":
                        for (ObjectShop tempVal2: ConfigManager.configManager.getShopList()) {
                            if (sender.hasPermission("ultimateshop.menu.*") ||
                            sender.hasPermission("ultimateshop.menu." + tempVal2.getShopName())) {
                                tempVal1.add(tempVal2.getShopName());
                            }
                        }
                        for (String tempVal4 : ObjectMenu.commonMenus.keySet()) {
                            if (sender.hasPermission("ultimateshop.menu.*") ||
                                    sender.hasPermission("ultimateshop.menu." + tempVal4)) {
                                if (tempVal4.equals(ConfigManager.configManager.getString("menu.select-more.menu"))) {
                                    continue;
                                }
                                tempVal1.add(tempVal4);
                            }
                        }
                        break;
                    case "createshop":
                        tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-shop-id"));
                        break;
                }
                break;
            case 3:
                switch (args[0]) {
                    case "quickbuy" : case "quicksell" :
                    case "setproductbuyprice" : case "setproductsellprice" :
                        ObjectShop tempVal3 = ConfigManager.configManager.getShop(args[1]);
                        if (tempVal3 == null) {
                            tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.unknown-shop"));
                            break;
                        }
                        for (ObjectItem tempVal4 : tempVal3.getProductList()) {
                            tempVal1.add(tempVal4.getItemConfig().getName());
                        }
                        break;
                    case "createshop" :
                        tempVal1.addAll(ObjectMenu.commonMenus.keySet());
                        tempVal1.addAll(ObjectMenu.shopMenuNames);
                        break;
                    case "createproduct" :
                        tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-product-id"));
                        break;
                }
                break;
            case 4:
                switch (args[0]) {
                    case "createshop" :
                        tempVal1.add("true");
                        tempVal1.add("false");
                        break;
                    case "createproduct" :
                        tempVal1.add("CLASSIC_ALL");
                        tempVal1.add("CLASSIC_ANY");
                        tempVal1.add("ALL");
                        tempVal1.add("ANY");
                        break;
                    case "setproductbuyprice" : case "setproductsellprice" :
                        tempVal1.add("handItem");
                        tempVal1.add("levels");
                        tempVal1.add("exp");
                        tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-economy-plugin"));
                        tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-hook-plugin"));
                }
            case 5:
                switch (args[0]) {
                    case "createproduct" :
                        tempVal1.add("CLASSIC_ALL");
                        tempVal1.add("CLASSIC_ANY");
                        tempVal1.add("ALL");
                        tempVal1.add("ANY");
                        break;
                    case "setproductbuyprice" : case "setproductsellprice" :
                        switch (args[3]) {
                            case ("handItem") :
                            case ("levels") :
                            case ("exps") :
                                tempVal1.add("default");
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.must-select-default"));
                                break;
                            case ("Vault") :
                            case ("PlayerPoints") :
                            case ("GamePoints") :
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-placeholder"));
                                break;
                            case ("UltraEconomy") :
                            case ("PEconomy") :
                            case ("CoinsEngine") :
                            case ("EcoBits") :
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-currency"));
                                break;
                            case ("ItemsAdder") :
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-hook-item-namespace-id"));
                                break;
                            case ("Oraxen") :
                            case ("EcoItems") :
                            case ("eco") :
                            case ("MythicMobs") :
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-hook-item-id"));
                                break;
                            case ("EcoArmor") :
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-hook-item-slot-id"));
                                break;
                            case ("MMOItems") :
                                tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-hook-item-type-id"));
                                break;
                        }
                        break;
                }
            case 6:
                switch (args[0]) {
                    case "setproductbuyprice" :
                    case "setproductsellprice" :
                        tempVal1.add("0");
                        tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.enter-apply-number"));
                        break;
                }
        }
        return tempVal1;
    }
}
