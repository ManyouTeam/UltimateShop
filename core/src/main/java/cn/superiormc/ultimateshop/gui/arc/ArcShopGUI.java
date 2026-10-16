package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;

final class ArcShopGUI extends ArcCommonGUI {

    private final ObjectShop shop;

    ArcShopGUI(Player player, ObjectMenu menu, ObjectShop shop, boolean bypass, ArcMenuRuntime runtime) {
        super(player, menu, bypass, runtime);
        this.shop = shop;
    }

    @Override
    protected void prepareMenu() {
        ObjectCache playerCache = CacheManager.cacheManager.getObjectCache(player);
        ObjectCache serverCache = CacheManager.cacheManager.serverCache;
        if (playerCache != null && serverCache != null) {
            for (ObjectItem item : shop.getProductListNotHidden(player)) {
                ObjectUseTimesCache cache = playerCache.getUseTimesCache().get(item);
                if (cache != null) {
                    cache.refreshTimes();
                }
                cache = serverCache.getUseTimesCache().get(item);
                if (cache != null) {
                    cache.refreshTimes();
                }
            }
        }
        super.prepareMenu();
        title = menu.getString("title", shop.getShopDisplayName())
            .replace("{shop-name}", shop.getShopDisplayName())
            .replace("{shop-id}", shop.getShopName().replace("_", "-"));
    }
}
