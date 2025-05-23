package cn.superiormc.ultimateshop.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;

public class CommandUtil {

    public static void registerCustomCommand(BukkitCommand command) {
        CommandMap commandMap;
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (commandMap == null) {
            return;
        }
        commandMap.register("ultimateshop", command);
    }
}
