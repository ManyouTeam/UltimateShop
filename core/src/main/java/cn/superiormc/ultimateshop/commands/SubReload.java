package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubReload extends AbstractCommand {

    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        executeReload(args, player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        executeReload(args, Bukkit.getConsoleSender());
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        if (args.length == 2) {
            return List.of("all");
        }
        return List.of();
    }

    private void executeReload(String[] args, CommandSender sender) {
        if (args.length == 2 && !"all".equalsIgnoreCase(args[1])) {
            LanguageManager.languageManager.sendStringText(sender, "error.args");
            return;
        }
        ReloadPlugin.reload(sender, args.length == 2);
    }
}
