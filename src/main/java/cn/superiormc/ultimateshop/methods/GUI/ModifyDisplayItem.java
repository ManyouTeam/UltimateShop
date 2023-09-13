package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import io.lumine.mythic.bukkit.utils.lib.http.util.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModifyDisplayItem {

    public static ItemStack modifyItem (Player player,
                                        int multi,
                                        ItemStack addLoreDisplayItem,
                                        ObjectItem item) {
        if (multi > 1) {
            // 修改物品名称
            String itemName = item.getDisplayName();
            itemName = TextUtil.parse("&f" + itemName + ConfigManager.configManager.getString("display-item.add-displayname").
                    replace("{amount}", String.valueOf(multi)));
            ItemMeta meta = addLoreDisplayItem.getItemMeta();
            meta.setDisplayName(itemName);
            addLoreDisplayItem.setItemMeta(meta);
        }
        int buyTimes = 0;
        int sellTimes = 0;
        ObjectUseTimesCache tempVal9 = CacheManager.cacheManager.playerCacheMap.get(player).getUseTimesCache().get(item);
        ObjectUseTimesCache tempVal10 = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
        if (tempVal9 != null) {
            buyTimes = CacheManager.cacheManager.playerCacheMap.get(player).
                    getUseTimesCache().get(item).getBuyUseTimes();
            sellTimes = CacheManager.cacheManager.playerCacheMap.get(player).
                    getUseTimesCache().get(item).getSellUseTimes();
        }
        else {
            CacheManager.cacheManager.playerCacheMap.get(player).setUseTimesCache(item.getShop(),
                    item.getProduct(),
                    0,
                    0,
                    null,
                    null);
            tempVal9 = CacheManager.cacheManager.playerCacheMap.get(player).getUseTimesCache().get(item);
        }
        ItemMeta tempVal2 = addLoreDisplayItem.getItemMeta();
        List<String> addLore = new ArrayList<>();
        addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.top"));
        if (!item.getBuyPrice().empty) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-price"));
        }
        if (!item.getSellPrice().empty) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-price"));
        }
        if (item.getPlayerBuyLimit(player) != -1) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-limit"));
        }
        if (item.getPlayerSellLimit(player) != -1) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-limit"));
        }
        if (tempVal9 != null &&
                item.getPlayerBuyLimit(player) > 0 &&
                tempVal9.getBuyUseTimes() >= item.getPlayerBuyLimit(player)) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-refresh-player"));
        }
        if (tempVal9 != null &&
                item.getPlayerSellLimit(player) > 0 &&
                tempVal9.getSellUseTimes() >= item.getPlayerSellLimit(player)) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-refresh-player"));
        }
        if (tempVal10 != null &&
                item.getServerBuyLimit(player) > 0 &&
                tempVal10.getBuyUseTimes() >= item.getServerBuyLimit(player)) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-refresh-server"));
        }
        if (tempVal10 != null &&
                item.getServerSellLimit(player) > 0 &&
                tempVal10.getSellUseTimes() >= item.getServerSellLimit(player)) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-refresh-server"));
        }
        if (multi == 1 && !item.getBuyPrice().empty) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-click"));
        }
        if (multi == 1 && !item.getSellPrice().empty) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-click"));
        }
        addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.below"));
        if (!addLore.isEmpty()) {
            tempVal2.setLore(CommonUtil.modifyList(addLore,
                    "buy-price",
                    item.getBuyPrice().getDisplayNameWithOneLine(player,
                            buyTimes,
                            multi),
                    "sell-price",
                    item.getSellPrice().getDisplayNameWithOneLine(player,
                            sellTimes,
                            multi),
                    "buy-limit-player",
                    String.valueOf(item.getPlayerBuyLimit(player)),
                    "sell-limit-player",
                    String.valueOf(item.getPlayerSellLimit(player)),
                    "buy-limit-server",
                    String.valueOf(item.getServerSellLimit(player)),
                    "sell-limit-server",
                    String.valueOf(item.getServerBuyLimit(player)),
                    "buy-times-player",
                    String.valueOf(tempVal9 == null ? "0" : tempVal9.getBuyUseTimes()),
                    "sell-times-player",
                    String.valueOf(tempVal9 == null ? "0" : tempVal9.getSellUseTimes()),
                    "buy-refresh-player",
                    String.valueOf(tempVal9 == null ? "" : tempVal9.getBuyRefreshTimeDisplayName()),
                    "sell-refresh-player",
                    String.valueOf(tempVal9 == null ? "" : tempVal9.getSellRefreshTimeDisplayName()),
                    "buy-times-server",
                    String.valueOf(tempVal10 == null ? "0" : tempVal10.getBuyUseTimes()),
                    "sell-times-server",
                    String.valueOf(tempVal10 == null ? "0" : tempVal10.getSellUseTimes()),
                    "buy-refresh-server",
                    String.valueOf(tempVal10 == null ? "" : tempVal10.getBuyRefreshTimeDisplayName()),
                    "sell-refresh-server",
                    String.valueOf(tempVal10 == null ? "" : tempVal10.getSellRefreshTimeDisplayName()),
                    "buy-click",
                    getBuyClickPlaceholder(player, multi, item),
                    "sell-click",
                    getSellClickPlaceholder(player, multi, item),
                    "amount",
                    String.valueOf(multi)
            ));
            addLoreDisplayItem.setItemMeta(tempVal2);
        }
        return addLoreDisplayItem;
    }

    private static String getBuyClickPlaceholder(Player player, int multi, ObjectItem item) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            return "";
        }
        String s = "";
        switch(BuyProductMethod.startBuy(item.getShop(), item.getProduct(), player, false, true, multi)) {
            case ERROR:
                s = ConfigManager.configManager.getString("placeholder.click.error");
                break;
            case PLAYER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.buy-max-limit-player");
                break;
            case SERVER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.buy-max-limit-server");
                break;
            case NOT_ENOUGH :
                s = ConfigManager.configManager.getString("placeholder.click.buy-price-not-enough");
                break;
            case DONE :
                s = ConfigManager.configManager.getString("placeholder.click.buy");
                break;
        }
        return s;
    }

    private static String getSellClickPlaceholder(Player player, int multi, ObjectItem item) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            return "";
        }
        String s = "";
        switch(SellProductMethod.startSell(item.getShop(), item.getProduct(), player, false, true, multi)) {
            case ERROR :
                s = ConfigManager.configManager.getString("placeholder.click.error");
                break;
            case PLAYER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.sell-max-limit-player");
                break;
            case SERVER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.sell-max-limit-server");
                break;
            case NOT_ENOUGH :
                s = ConfigManager.configManager.getString("placeholder.click.sell-price-not-enough");
                break;
            case DONE :
                s = ConfigManager.configManager.getString("placeholder.click.sell");
                break;
            default :
                s = "Unknown";
                break;
        }
        return s;
    }
}
