package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubReload extends AbstractCommand {

    public SubReload() {
        this.id = "reload";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ReloadPlugin.reload(player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        ReloadPlugin.reload(Bukkit.getConsoleSender());
    }
}
