package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.cache.ServerCache;

public abstract class AbstractDatabase {

    public void onInit() {
        // Empty...
    }

    public void onClose() {
        // Empty...
    }

    public abstract void checkData(ServerCache cache);

    public abstract void updateData(ServerCache cache, boolean quitServer);

    public void updateDataOnDisable(ServerCache cache, boolean disable) {
        updateData(cache, true);
    }
}
