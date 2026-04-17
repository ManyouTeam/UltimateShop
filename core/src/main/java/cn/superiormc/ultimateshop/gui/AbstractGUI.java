package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class AbstractGUI {

    private boolean inClickCooldown = false;

    protected Player player;

    public AbstractGUI(Player owner) {
        this.player = owner;
    }

    public abstract void constructGUI();

    public void updateGUI() {
        constructGUI();
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
