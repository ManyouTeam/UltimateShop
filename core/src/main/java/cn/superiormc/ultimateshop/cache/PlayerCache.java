package cn.superiormc.ultimateshop.cache;

import org.bukkit.entity.Player;

public class PlayerCache extends ServerCache {

    public PlayerCache(Player player) {
        super(player);
    }

    public Player getPlayer() {
        return player;
    }
}
