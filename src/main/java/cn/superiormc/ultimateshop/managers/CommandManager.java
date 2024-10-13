package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.commands.*;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandManager {

    public static CommandManager commandManager;

    private Map<String, AbstractCommand> registeredCommands = new HashMap<>();

    public CommandManager(){
        commandManager = this;
        registerBukkitCommands();
        registerObjectCommand();
    }

    private void registerBukkitCommands(){
        Objects.requireNonNull(Bukkit.getPluginCommand("ultimateshop")).setExecutor(new MainCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("ultimateshop")).setTabCompleter(new MainCommandTab());
    }

    private void registerObjectCommand() {
       registerNewSubCommand(new SubSaveItem());
       registerNewSubCommand(new SubMenu());
       registerNewSubCommand(new SubQuickSell());
       registerNewSubCommand(new SubQuickBuy());
       registerNewSubCommand(new SubReload());
       registerNewSubCommand(new SubGiveSellStick());
       registerNewSubCommand(new SubGiveSaveItem());
       registerNewSubCommand(new SubSellAll());
       registerNewSubCommand(new SubAddBuyTimes());
       registerNewSubCommand(new SubAddSellTimes());
       registerNewSubCommand(new SubSetSellTimes());
       registerNewSubCommand(new SubSetBuyTimes());
       registerNewSubCommand(new SubHelp());
       registerNewSubCommand(new SubGenerateItemFormat());
       registerNewSubCommand(new SubGetPlaceholderValue());
       registerNewSubCommand(new SubResetRandomPlaceholder());
       registerNewSubCommand(new SubSetRandomPlaceholder());
    }

    public Map<String, AbstractCommand> getSubCommandsMap() {
        return registeredCommands;
    }

    public void registerNewSubCommand(AbstractCommand command) {
        registeredCommands.put(command.getId(), command);
    }

}
