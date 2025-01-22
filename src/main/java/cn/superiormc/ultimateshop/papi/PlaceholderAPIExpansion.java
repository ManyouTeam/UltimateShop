package cn.superiormc.ultimateshop.papi;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.utils.TextUtil;
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
        return "1.2.0";
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
        if (args.length < 1) {
            return null;
        } else if (args[0].startsWith("{")) {
            return TextUtil.parseBuiltInPlaceholder(params, player);
        } else {
            ObjectShop shop = ConfigManager.configManager.getShop(args[0]);
            if (shop == null) {
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-shop");
            }
            ObjectItem item = shop.getProduct(args[1]);
            if (item == null) {
                return LanguageManager.languageManager.getStringText("placeholderapi.unknown-product");
            }
            PlayerCache playerCache = CacheManager.cacheManager.getPlayerCache(player);
            ObjectUseTimesCache playerTimesCache = playerCache.getUseTimesCache().get(item);
            if (playerTimesCache == null) {
                playerTimesCache = playerCache.createUseTimesCache(item);
                if (playerTimesCache == null) {
                    return "ERROR: Can not found player cache.";
                }
            }
            ObjectUseTimesCache serverTimesCache = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
            if (serverTimesCache == null) {
                serverTimesCache = CacheManager.cacheManager.serverCache.createUseTimesCache(item);
                if (serverTimesCache == null) {
                    return "ERROR: Can not found server cache.";
                }
            }
            String tempVal1 = args[2];
            if (tempVal1.startsWith("{") && tempVal1.endsWith("}")) {
                tempVal1 = tempVal1.substring(1, tempVal1.length() - 1);
            }
            switch (tempVal1) {
                case "buy-price":
                    return TextUtil.parse(ObjectPrices.getDisplayNameInLine(player,
                            1,
                            item.getBuyPrice().takeSingleThing(player.getInventory(), player, playerTimesCache.getBuyUseTimes(), 1, true).getResultMap(),
                            item.getBuyPrice().getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")));
                case "sell-price":
                    return TextUtil.parse(ObjectPrices.getDisplayNameInLine(player,
                            1,
                            item.getSellPrice().giveSingleThing(player, playerTimesCache.getBuyUseTimes(), 1).getResultMap(),
                            item.getSellPrice().getMode(),
                            !ConfigManager.configManager.getBoolean("placeholder.status.can-used-everywhere")));
                case "buy-limit-player":
                    return String.valueOf(item.getPlayerBuyLimit(player));
                case "sell-limit-player":
                    return String.valueOf(item.getPlayerSellLimit(player));
                case "buy-limit-server":
                    return String.valueOf(item.getServerBuyLimit(player));
                case "sell-limit-server":
                    return String.valueOf(item.getServerSellLimit(player));
                case "buy-times-player":
                    return String.valueOf(playerTimesCache.getBuyUseTimes());
                case "sell-times-player":
                    return String.valueOf(playerTimesCache.getSellUseTimes());
                case "buy-refresh-player":
                    return String.valueOf(playerTimesCache.getBuyRefreshTimeDisplayName());
                case "sell-refresh-player":
                    return String.valueOf(playerTimesCache.getSellRefreshTimeDisplayName());
                case "buy-next-player":
                    return String.valueOf(playerTimesCache.getBuyRefreshTimeNextName());
                case "sell-next-player":
                    return String.valueOf(playerTimesCache.getSellRefreshTimeNextName());
                case "buy-times-server":
                    return String.valueOf(serverTimesCache.getBuyUseTimes());
                case "sell-times-server":
                    return String.valueOf(serverTimesCache.getSellUseTimes());
                case "buy-refresh-server":
                    return String.valueOf(serverTimesCache.getBuyRefreshTimeDisplayName());
                case "sell-refresh-server":
                    return String.valueOf(serverTimesCache.getSellRefreshTimeDisplayName());
                case "buy-next-server":
                    return String.valueOf(serverTimesCache.getBuyRefreshTimeNextName());
                case "sell-next-server":
                    return String.valueOf(serverTimesCache.getSellRefreshTimeNextName());
                case "last-buy-player":
                    return playerTimesCache.getBuyLastTimeName();
                case "last-sell-player":
                    return playerTimesCache.getSellLastTimeName();
                case "last-buy-server":
                    return serverTimesCache.getBuyLastTimeName();
                case "last-sell-server":
                    return serverTimesCache.getSellLastTimeName();
                case "item-name":
                    return item.getDisplayName(player);
            }
        }
        return null;
    }
}
