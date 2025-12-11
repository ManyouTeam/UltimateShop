package cn.superiormc.ultimateshop.cache;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.entity.Player;

public class PlayerCache extends ServerCache {

    private final Player player;

    public PlayerCache(Player player) {
        super(player);
        this.player = player;
    }

    public void initPlayerCache() {
        SchedulerUtil.runTaskAsynchronously(() -> CacheManager.cacheManager.database.checkData(this));
    }

    public void shutPlayerCache(boolean quitServer) {
        SchedulerUtil.runTaskAsynchronously(() -> CacheManager.cacheManager.database.updateData(this, quitServer));
    }

    public void shutPlayerCacheOnDisable(boolean disable) {
        CacheManager.cacheManager.database.updateDataOnDisable(this, disable);
    }

    public Player getPlayer() {
        return player;
    }
}
