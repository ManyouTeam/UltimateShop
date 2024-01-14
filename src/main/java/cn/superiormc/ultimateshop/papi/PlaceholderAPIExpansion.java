package cn.superiormc.ultimateshop.papi;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    public static PlaceholderAPIExpansion papi = null;

    private final UltimateShop plugin;
    @Override
    public boolean canRegister() {
        return true;
    }

    public PlaceholderAPIExpansion(UltimateShop plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "PQguanfang";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "ultimateshop";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return null;
        }
        String[] args = params.split("_");
        if (args.length < 3) {
            return null;
        }
        ObjectShop shop = ConfigManager.configManager.getShop(args[0]);
        if (shop == null) {
            return LanguageManager.languageManager.getStringText("placeholderapi.unknown-shop");
        }
        ObjectItem item = shop.getProduct(args[1]);
        if (item == null) {
            return LanguageManager.languageManager.getStringText("placeholderapi.unknown-product");
        }
        ObjectUseTimesCache playerTimesCache = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        ObjectUseTimesCache serverTimesCache = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
        switch (args[2]) {
            case "{buy-limit-player}":
                return String.valueOf(item.getPlayerBuyLimit(player));
            case "{sell-limit-player}":
                return String.valueOf(item.getPlayerSellLimit(player));
            case "{buy-limit-server}" :
                return String.valueOf(item.getServerSellLimit(player));
            case "{sell-limit-server}" :
                return String.valueOf(item.getServerBuyLimit(player));
            case "{buy-times-player}":
                return String.valueOf(playerTimesCache == null ? "0" :
                        playerTimesCache.getBuyUseTimes());
            case "{sell-times-player}":
                return String.valueOf(playerTimesCache == null ? "0" :
                        playerTimesCache.getSellUseTimes());
            case "{buy-refresh-player}":
                return String.valueOf(playerTimesCache == null ? "" :
                        playerTimesCache.getBuyRefreshTimeDisplayName());
            case "{sell-refresh-player}":
                return String.valueOf(playerTimesCache == null ? "" :
                        playerTimesCache.getSellRefreshTimeDisplayName());
            case "{buy-cooldown-player}":
                return String.valueOf(playerTimesCache == null ? ConfigManager.configManager.
                        getString("placeholder.cooldown.now") :
                        playerTimesCache.getBuyCooldownTimeDisplayName());
            case "{sell-cooldown-player}":
                return String.valueOf(playerTimesCache == null ? ConfigManager.configManager.
                        getString("placeholder.cooldown.now") :
                        playerTimesCache.getSellCooldownTimeDisplayName());
            case "{buy-times-server}":
                return String.valueOf(serverTimesCache  == null ? "0" :
                        serverTimesCache.getBuyUseTimes());
            case "{sell-times-server}":
                return String.valueOf(serverTimesCache  == null ? "0" :
                        serverTimesCache.getSellUseTimes());
            case "{buy-refresh-server}":
                return String.valueOf(serverTimesCache  == null ? "" :
                        serverTimesCache.getBuyRefreshTimeDisplayName());
            case "{sell-refresh-server}":
                return String.valueOf(serverTimesCache  == null ? "" :
                        serverTimesCache.getSellRefreshTimeDisplayName());
            case "{buy-cooldown-server}":
                return String.valueOf(serverTimesCache == null ? ConfigManager.configManager.
                        getString("placeholder.cooldown.now") :
                        serverTimesCache.getBuyCooldownTimeDisplayName());
            case "{sell-cooldown-server}":
                return String.valueOf(serverTimesCache == null ? ConfigManager.configManager.
                        getString("placeholder.cooldown.now") :
                        serverTimesCache.getSellCooldownTimeDisplayName());

        }
        return args[2];
    }
}
