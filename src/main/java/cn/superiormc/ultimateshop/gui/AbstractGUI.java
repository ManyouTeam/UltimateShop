package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractGUI {

    public static Map<Player, GUIStatus> playerList = new HashMap<>();

    private static boolean inClickCooldown = false;

    protected Player player;

    public AbstractGUI(Player owner) {
        this.player = owner;
    }

    protected abstract void constructGUI();

    public boolean canOpenGUI(boolean reopen) {
        if (ConfigManager.configManager.getLong("menu.cooldown.reopen", -1L) <= 0L) {
            return true;
        }
        if (playerList.containsKey(player)) {
            if (reopen && playerList.get(player) != GUIStatus.ACTION_OPEN_MENU) {
                playerList.replace(player, GUIStatus.ACTION_OPEN_MENU);
            } else {
                return false;
            }
        } else {
            playerList.put(player, GUIStatus.CAN_REOPEN);
            return true;
        }
        return true;
    }

    public void removeOpenGUIStatus() {
        long time = ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L);
        if (playerList.containsKey(player) && playerList.get(player) != GUIStatus.ALREADY_IN_COOLDOWN) {
            playerList.replace(player, GUIStatus.ALREADY_IN_COOLDOWN);
            if (UltimateShop.isFolia) {
                Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> playerList.remove(player), time);
                return;
            }
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> playerList.remove(player), time);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void addCooldown() {
        long setValue = ConfigManager.configManager.getLong("menu.cooldown.click", 5L);
        if (!inClickCooldown && setValue > 0L) {
            inClickCooldown = true;
            if (UltimateShop.isFolia) {
                Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> inClickCooldown = false, setValue);
                return;
            }
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> inClickCooldown = false, setValue);
        }
    }

    public boolean getCooldown() {
        return inClickCooldown;
    }
}

