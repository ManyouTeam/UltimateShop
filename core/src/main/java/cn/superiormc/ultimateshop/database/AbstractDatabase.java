package cn.superiormc.ultimateshop.database;

import cn.superiormc.ultimateshop.objects.caches.ObjectCache;

public abstract class AbstractDatabase {

    public void onInit() {
        // Empty...
    }

    public void onClose() {
        // Empty...
    }

    public abstract void checkData(ObjectCache cache);

    public abstract void updateData(ObjectCache cache, boolean quitServer);

    public void updateDataOnDisable(ObjectCache cache, boolean disable) {
        updateData(cache, true);
        if (disable) {
            DatabaseExecutor.EXECUTOR.shutdownNow();
        }
    }
}
