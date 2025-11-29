package cn.superiormc.ultimateshop.objects.menus;

import org.bukkit.entity.Player;

public class MenuSender {

    public static final MenuSender empty = new MenuSender();

    private Player player;

    private final boolean isStatic;

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
        return player == null ? empty : new MenuSender(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuSender)) return false;

        MenuSender that = (MenuSender) o;

        if (isStatic && that.isStatic) return true;

        return !isStatic && !that.isStatic &&
                player.getUniqueId().equals(that.player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return isStatic ? 0 : player.getUniqueId().hashCode();
    }
}