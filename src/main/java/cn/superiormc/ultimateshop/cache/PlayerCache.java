package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerCache extends ServerCache {

    private Player player;

    public PlayerCache(Player player) {
        super(player);
        this.player = player;
    }

    public void initPlayerCache() {
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.checkData(this);
            }
            else {
                YamlDatabase.checkData(this);
            }
        });
    }

    public void shutPlayerCache() {
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.updateData(this);
            }
            else {
                YamlDatabase.updateData(this);
            }
        });
    }

    public void shutPlayerCacheOnDisable() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataNoAsync(this);
        }
        else {
            YamlDatabase.updateData(this);
        }
    }
}
