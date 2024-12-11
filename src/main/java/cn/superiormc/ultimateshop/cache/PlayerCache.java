package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.entity.Player;

public class PlayerCache extends ServerCache {

    private final Player player;

    public PlayerCache(Player player) {
        super(player);
        this.player = player;
    }

    public void initPlayerCache() {
        SchedulerUtil.runTaskAsynchronously(() -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.checkData(this);
            } else {
                YamlDatabase.checkData(this);
            }
        });
    }

    public void shutPlayerCache(boolean quitServer) {
        SchedulerUtil.runTaskAsynchronously(() -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.updateData(this, quitServer);
            } else {
                YamlDatabase.updateData(this, quitServer);
            }
        });
    }

    public void shutPlayerCacheOnDisable(boolean disable) {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataOnDisable(this, disable);
        } else {
            YamlDatabase.updateData(this, true);
        }
    }
}
