package cn.superiormc.ultimateshop.objects.menus;

import org.bukkit.entity.Player;

public class MenuSender {

    public static MenuSender empty = new MenuSender();

    private Player player;

    private boolean isStatic;

    private MenuSender(Player player) {
        this.isStatic = false;
        this.player = player;
    }

    private MenuSender() {
        this.isStatic = true;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public static MenuSender of(Player player) {
        return new MenuSender(player);
    }
}
