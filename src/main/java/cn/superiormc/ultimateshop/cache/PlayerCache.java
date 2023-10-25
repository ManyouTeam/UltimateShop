package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.entity.Player;

public class PlayerCache extends ServerCache {

    private Player player;

    public PlayerCache(Player player) {
        super(player);
        this.player = player;
    }

    public void initPlayerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.checkData(player);
        }
        else {
            YamlDatabase.checkData(player);
        }
    }

    public void shutPlayerCache() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateData(player);
        }
        else {
            YamlDatabase.updateData(player);
        }
    }

    public void shutPlayerCacheOnDisable() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataNoAsync(player);
        }
        else {
            YamlDatabase.updateData(player);
        }
    }
}
