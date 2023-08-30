package cn.superiormc.ultimateshop.gui;

import org.bukkit.entity.Player;

public abstract class AbstractGUI {
    protected Player owner;

    public AbstractGUI(Player owner) {
        this.owner = owner;
    }

    protected abstract void constructGUI();

    public abstract void openGUI();

    public Player getOwner() {
        return owner;
    }
}