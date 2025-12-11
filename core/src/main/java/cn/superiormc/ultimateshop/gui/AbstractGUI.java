package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
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

    public abstract void constructGUI();

    public boolean canOpenGUI(boolean reopen) {
        boolean result = true;
        if (playerList.containsKey(player)) {
            if (reopen && playerList.get(player).getStatus() != GUIStatus.Status.ACTION_OPEN_MENU) {
                playerList.replace(player, GUIStatus.of(this, GUIStatus.Status.ACTION_OPEN_MENU));
            } else if (ConfigManager.configManager.getLong("menu.cooldown.reopen", -1L) > 0L) {
                result = false;
            }
        } else {
            playerList.put(player, GUIStatus.of(this, GUIStatus.Status.CAN_REOPEN));
        }
        return result;
    }

    public void removeOpenGUIStatus() {
        long time = ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L);
        if (time > 0L && playerList.containsKey(player) && playerList.get(player).getStatus() != GUIStatus.Status.ALREADY_IN_COOLDOWN) {
            playerList.replace(player, GUIStatus.of(this, GUIStatus.Status.ALREADY_IN_COOLDOWN));
            SchedulerUtil.runTaskLater(() -> playerList.remove(player), time);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public ConfigurationSection getSection() {
        return null;
    }

    public ObjectMenu getMenu() {
        return null;
    }

    public void addCooldown() {
        long setValue = ConfigManager.configManager.getLong("menu.cooldown.click", 5L);
        if (!inClickCooldown && setValue > 0L) {
            inClickCooldown = true;
            SchedulerUtil.runTaskLater(() -> inClickCooldown = false, setValue);
        }
    }

    public boolean getCooldown() {
        return inClickCooldown;
    }
}

