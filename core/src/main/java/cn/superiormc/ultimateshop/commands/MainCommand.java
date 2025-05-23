package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.CommandManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
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
                            ShopGUI.openGUI((Player) sender, shop, false, false);
                        }
                    }
                    else {
                        CommonGUI.openGUI((Player) sender, tempVal1, false, false);
                    }
                }
                else {
                    LanguageManager.languageManager.sendStringText(sender, "error.args");
                }
            }
            else {
                LanguageManager.languageManager.sendStringText(sender, "error.args");
            }
        } else if (CommandManager.commandManager.getSubCommandsMap().get(args[0]) != null) {
            AbstractCommand object = CommandManager.commandManager.getSubCommandsMap().get(args[0]);
            if (object.getOnlyInGame() && !(sender instanceof Player)) {
                LanguageManager.languageManager.sendStringText("error.in-game");
                return true;
            }
            if (object.getRequiredPermission() != null && !object.getRequiredPermission().isEmpty()
                    && !sender.hasPermission(object.getRequiredPermission())) {
                LanguageManager.languageManager.sendStringText(sender, "error.miss-permission");
                return true;
            }
            if ((object.premiumOnly && UltimateShop.freeVersion) || !object.getLengthCorrect(args.length, sender)) {
                LanguageManager.languageManager.sendStringText(sender, "error.args");
                return true;
            }
            if (sender instanceof Player) {
                object.executeCommandInGame(args, (Player) sender);
                return true;
            }
            object.executeCommandInConsole(args);
            return true;
        } else {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
            return true;
        }
        return true;
    }
}
