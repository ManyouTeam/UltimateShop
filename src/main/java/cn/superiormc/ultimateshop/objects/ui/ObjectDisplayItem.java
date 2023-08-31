package cn.superiormc.ultimateshop.objects.ui;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ObjectDisplayItem{

    private ConfigurationSection section;

    private ItemStack displayItem;

    private ObjectItem item;

    public ObjectDisplayItem(ConfigurationSection section, ObjectItem item) {
        this.section = section;
        this.item = item;
        initDisplayItem();
    }

    private void initDisplayItem() {
        // 显示物品
        if (section.contains("hook-item")) {
            displayItem = ItemsHook.getHookItem(section.getString("hook-plugin"),
                    section.getString("hook-item"));
        }
        else {
            displayItem = ItemUtil.buildItemStack(section);
        }
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStack getDisplayItem(Player player) {
        int buyTimes = 0;
        int sellTimes = 0;
        ObjectUseTimesCache tempVal9 = CacheManager.cacheManager.playerCacheMap.get(player).getUseTimesCache().get(item);
        if (tempVal9 != null) {
            buyTimes = CacheManager.cacheManager.playerCacheMap.get(player).
                    getUseTimesCache().get(item).getBuyUseTimes();
            sellTimes = CacheManager.cacheManager.playerCacheMap.get(player).
                    getUseTimesCache().get(item).getSellUseTimes();
        }
        ItemStack addLoreDisplayItem = null;
        if (section == null || ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
            addLoreDisplayItem = item.getReward().getDisplayItem();
            if (addLoreDisplayItem == null) {
                addLoreDisplayItem = item.getReward().getDisplayItem();
            }
        }
        else {
            addLoreDisplayItem = displayItem.clone();
        }
        ItemMeta tempVal2 = addLoreDisplayItem.getItemMeta();
        List<String> addLore = new ArrayList<>();
        addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.top"));
        if (tempVal9 != null) {
            if (item.getPlayerBuyLimit(player) != -1) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-limit"));
            }
            if (item.getPlayerSellLimit(player) != -1) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-limit"));
            }
            if (tempVal9.getBuyUseTimes()
                    == item.getPlayerBuyLimit(player)) {
                addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-refresh"));
            }
            if (tempVal9.getSellUseTimes()
                    == item.getPlayerSellLimit(player)) {
                addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-refresh"));
            }
        }
        if (!item.getBuyPrice().empty) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.buy-click"));
        }
        if (!item.getSellPrice().empty) {
            addLore.addAll(ConfigManager.configManager.getListWithColor("display-item.add-lore.sell-click"));
        }
        if (!addLore.isEmpty()) {
            tempVal2.setLore(CommonUtil.modifyList(addLore,
                    "buy-price",
                    item.getBuyPrice().getDisplayNameWithOneLine(
                            buyTimes,
                            ConfigManager.configManager.getString("placeholder.price.split-symbol")),
                    "sell-price",
                    item.getSellPrice().getDisplayNameWithOneLine(
                            sellTimes,
                            ConfigManager.configManager.getString("placeholder.price.split-symbol")),
                    "buy-limit-player",
                    String.valueOf(item.getPlayerBuyLimit(player)),
                    "sell-limit-player",
                    String.valueOf(item.getPlayerSellLimit(player)),
                    "buy-limit-server",
                    String.valueOf(item.getServerSellLimit(player)),
                    "sell-limit-server",
                    String.valueOf(item.getServerBuyLimit(player)),
                    "buy-times",
                    String.valueOf(tempVal9 == null ? "" : tempVal9.getBuyUseTimes()),
                    "sell-times",
                    String.valueOf(tempVal9 == null ? "" : tempVal9.getSellUseTimes()),
                    "buy-refresh",
                    String.valueOf(tempVal9 == null ? "" : tempVal9.getBuyRefreshTimeDisplayName()),
                    "sell-refresh",
                    String.valueOf(tempVal9 == null ? "" : tempVal9.getSellRefreshTimeDisplayName()),
                    "buy-click",
                    getBuyClickPlaceholder(player),
                    "sell-click",
                    getSellClickPlaceholder(player)
            ));
            addLoreDisplayItem.setItemMeta(tempVal2);
        }
        return addLoreDisplayItem;
    }

    private String getBuyClickPlaceholder(Player player) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            return "";
        }
        String s = "";
        switch(BuyProductMethod.startBuy(item.getShop(), item.getProduct(), player, false, true)) {
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

    private String getSellClickPlaceholder(Player player) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            return "";
        }
        String s = "";
        switch(SellProductMethod.startSell(item.getShop(), item.getProduct(), player, false, true)) {
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
