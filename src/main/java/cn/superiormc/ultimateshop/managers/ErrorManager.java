package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
            try {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        getError = false;
                    }
                }.runTaskLater(UltimateShop.instance, 100);
            } catch (Exception ignored) {
            }
        }
    }
}
