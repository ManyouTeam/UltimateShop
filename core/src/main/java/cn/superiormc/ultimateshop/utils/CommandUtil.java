package cn.superiormc.ultimateshop.utils;

import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Player, SchedulerUtil> guiUpdateTask = new HashMap<>();

    public static void updateGUI(Player player) {
        if (player != null) {
            GUIStatus guiStatus = AbstractGUI.playerList.get(player);
            if (guiStatus != null && guiStatus.getGUI() != null) {
                SchedulerUtil task = guiUpdateTask.get(player);
                if (task != null) {
                    task.cancel();
                    guiUpdateTask.remove(player);
                }
                guiUpdateTask.put(player,
                        SchedulerUtil.runTaskLater(() -> guiStatus.getGUI().constructGUI(), 20L));
            }
        }
    }
}
