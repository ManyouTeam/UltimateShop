package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
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
                tempVal1.add("help");
                if (sender.hasPermission("ultimateshop.editor") && !UltimateShop.freeVersion) {
                    tempVal1.add("editor");
                }
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
                if (sender.hasPermission("ultimateshop.sellall")) {
                    tempVal1.add("sellall");
                }
                if (sender.hasPermission("ultimateshop.givesellstick") && !UltimateShop.freeVersion) {
                    tempVal1.add("givesellstick");
                }
                if (sender.hasPermission("ultimateshop.setbuytimes")) {
                    tempVal1.add("setbuytimes");
                }
                if (sender.hasPermission("ultimateshop.setselltimes")) {
                    tempVal1.add("setselltimes");
                }
                if (sender.hasPermission("ultimateshop.saveitem")) {
                    tempVal1.add("saveitem");
                }
                break;
            case 2:
                switch (args[0]) {
                    case "quickbuy" : case "quicksell" : case "setbuytimes": case "setselltimes":
                        for (ObjectShop tempVal2: ConfigManager.configManager.getShopList()) {
                            tempVal1.add(tempVal2.getShopName());
                        }
                        break;
                    case "menu":
                        for (ObjectShop tempVal2: ConfigManager.configManager.getShopList()) {
                            tempVal1.add(tempVal2.getShopName());
                        }
                        for (String tempVal4 : ObjectMenu.commonMenus.keySet()) {
                            if (tempVal4.equals(ConfigManager.configManager.getString("menu.select-more.menu"))) {
                                    continue;
                            }
                            tempVal1.add(tempVal4);
                        }
                        break;
                }
                break;
            case 3:
                switch (args[0]) {
                    case "quickbuy": case "quicksell": case "setbuytimes": case "setselltimes":
                        ObjectShop tempVal3 = ConfigManager.configManager.getShop(args[1]);
                        if (tempVal3 == null) {
                            tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.unknown-shop"));
                            break;
                        }
                        for (ObjectItem tempVal4 : tempVal3.getProductList()) {
                            tempVal1.add(tempVal4.getItemConfig().getName());
                        }
                        break;
                }
                break;
        }
        return tempVal1;
    }
}
