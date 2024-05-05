package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.database.SQLDatabase;
import cn.superiormc.ultimateshop.database.YamlDatabase;
import cn.superiormc.ultimateshop.managers.CacheManager;
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
        if (UltimateShop.isFolia) {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.checkData(this);
            } else {
                YamlDatabase.checkData(this);
            }
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.checkData(this);
            } else {
                YamlDatabase.checkData(this);
            }
        });
    }

    public void shutPlayerCache(boolean quitServer) {
        if (UltimateShop.isFolia) {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.updateData(this, quitServer);
            }
            else {
                YamlDatabase.updateData(this, quitServer);
            }
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
            if (ConfigManager.configManager.getBoolean("database.enabled")) {
                SQLDatabase.updateData(this, quitServer);
            }
            else {
                YamlDatabase.updateData(this, quitServer);
            }
        });
    }

    public void shutPlayerCacheOnDisable() {
        if (ConfigManager.configManager.getBoolean("database.enabled")) {
            SQLDatabase.updateDataNoAsync(this);
        }
        else {
            YamlDatabase.updateData(this, true);
        }
    }
}
