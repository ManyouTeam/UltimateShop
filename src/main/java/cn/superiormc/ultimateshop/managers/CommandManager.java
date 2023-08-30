package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.commands.MainCommand;
import cn.superiormc.ultimateshop.commands.MainCommandTab;
import org.bukkit.Bukkit;

import java.util.Objects;

public class CommandManager {

    public static CommandManager commandManager;

    public CommandManager(){
        commandManager = this;
        registerCommands();
    }

    private void registerCommands(){
        Objects.requireNonNull(Bukkit.getPluginCommand("ultimateshop")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("ultimateshop")).setTabCompleter(new MainCommandTab());
    }
}
