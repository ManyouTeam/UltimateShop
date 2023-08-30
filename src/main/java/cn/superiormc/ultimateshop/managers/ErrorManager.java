package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.commands.MainCommand;
import cn.superiormc.ultimateshop.commands.MainCommandTab;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ErrorManager {

    public static ErrorManager errorManager;

    public boolean getError = false;

    private String lastErrorMessage = "";

    public ErrorManager(){
        errorManager = this;
    }

    public void sendErrorMessage(String message){
        if (!getError || !message.equals(lastErrorMessage)) {
            Bukkit.getConsoleSender().sendMessage(message);
            lastErrorMessage = message;
            getError = true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    getError = false;
                }
            }.runTaskLater(UltimateShop.instance, 100);
        }
    }
}
