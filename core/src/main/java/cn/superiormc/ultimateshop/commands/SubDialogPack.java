package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.DialogPackManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubDialogPack extends AbstractCommand {

    public SubDialogPack() {
        this.id = "dialogpack";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = false;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[] {
            1, 2
        };
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        generate(args, player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        generate(args, Bukkit.getConsoleSender());
    }

    private void generate(String[] args, CommandSender sender) {
        try {
            Integer format = args.length > 1 ? Integer.valueOf(args[1]) : null;
            if (format != null && (format < 69 || format > 10000)) {
                String message = "§cError: Resource format must be in 69..10000";
                ErrorManager.errorManager.sendErrorMessage(message);
                if (sender instanceof Player player) {
                    TextUtil.sendMessage(player, TextUtil.pluginPrefix() + " " + message);
                }
                return;
            }
            DialogPackManager.dialogPackManager.regenerate(sender, format);
        } catch (IllegalArgumentException exception) {
            String message = "§cError: " + exception.getMessage();
            ErrorManager.errorManager.sendErrorMessage(message);
            if (sender instanceof Player player) {
                TextUtil.sendMessage(player, TextUtil.pluginPrefix() + " " + message);
            }
        }
    }
}
