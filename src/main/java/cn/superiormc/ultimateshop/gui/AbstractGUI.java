package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractGUI {

    public static Map<Player, Boolean> playerList = new HashMap<>();

    protected Player player;

    private boolean inClickCooldown = false;

    public AbstractGUI(Player owner) {
        this.player = owner;
    }

    protected abstract void constructGUI();

    public boolean canOpenGUI() {
        if (ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L) <= 0L) {
            return true;
        }
        if (playerList.containsKey(player)) {
            return false;
        }
        playerList.put(player, false);
        return true;
    }

    public static boolean canReopenGUI(Player player) {
        long time = ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L);
        if (time <= 0L) {
            return true;
        }
        if (playerList.containsKey(player)) {
            // 虽然包括了某个玩家，但是只要其不是二次触发，就允许通过
            if (playerList.get(player)) {
                return false;
            }
            playerList.replace(player, true);
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                playerList.remove(player);
            }, time);
        }
        return true;
    }

    public void removeOpenGUIStatus() {
        long time = ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L);
        if (playerList.containsKey(player) && !playerList.get(player)) {
            playerList.replace(player, true);
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                playerList.remove(player);
            }, time);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void addCooldown() {
        long setValue = ConfigManager.configManager.getLong("menu.cooldown.click", 5L);
        if (!inClickCooldown && setValue > 0L) {
            inClickCooldown = true;
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                inClickCooldown = false;
            }, setValue);
        }
    }

    public boolean getCooldown() {
        return inClickCooldown;
    }
}