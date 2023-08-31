package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.entity.Player;

public class PlayerCache extends ServerCache {

    private Player player;

    public PlayerCache(Player player) {
        super(player);
        this.player = player;
        initPlayerCache();
    }

    private void initPlayerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.checkData(player);
        }
    }

    public void shutPlayerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateData(player);
        }
    }

    public void setPlayerDynamicPriceCache(String shop, String product, double price) {
        // TODO...
    }
}
