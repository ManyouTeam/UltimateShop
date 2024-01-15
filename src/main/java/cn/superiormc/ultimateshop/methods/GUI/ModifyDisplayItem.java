package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModifyDisplayItem {

    public static ItemStack modifyItem(Player player,
                                        int multi,
                                        ItemStack addLoreDisplayItem,
                                        ObjectItem item,
                                        boolean buyMore) {
        ItemMeta tempVal2 = addLoreDisplayItem.getItemMeta();
        if (tempVal2 == null) {
            return addLoreDisplayItem;
        }
        // 修改物品名称
        if (buyMore || item.getItemConfig().getString("display-name") != null) {
            String itemName = item.getDisplayName(player);
            if (buyMore) {
                itemName = itemName + TextUtil.parse(ConfigManager.configManager.getString("display-item.add-displayname").
                        replace("{amount}", String.valueOf(multi)));
            }
            tempVal2.setDisplayName(itemName);
            addLoreDisplayItem.setItemMeta(tempVal2);
        }
        List<String> addLore = new ArrayList<>();
        if (tempVal2.hasLore()) {
            addLore.addAll(tempVal2.getLore());
        }
        addLore.addAll(getModifiedLore(player, multi, item, buyMore, false));
        if (!addLore.isEmpty()) {
            tempVal2.setLore(addLore);
            addLoreDisplayItem.setItemMeta(tempVal2);
        }
        return addLoreDisplayItem;
    }
    
    public static List<String> getModifiedLore(Player player,
                                                int multi,
                                                ObjectItem item,
                                                boolean buyMore,
                                                boolean bedrock) {
        List<String> addLore = new ArrayList<>();
        int buyTimes = 0;
        int sellTimes = 0;
        ObjectUseTimesCache tempVal9 = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        ObjectUseTimesCache tempVal10 = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
        if (tempVal9 != null) {
            buyTimes = tempVal9.getBuyUseTimes();
            sellTimes = tempVal9.getSellUseTimes();
        }
        else {
            CacheManager.cacheManager.getPlayerCache(player).setUseTimesCache(item.getShop(),
                    item.getProduct(),
                    0,
                    0,
                    null,
                    null,
                    null,
                    null);
            tempVal9 = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        }
        if (tempVal10 == null) {
            CacheManager.cacheManager.serverCache.setUseTimesCache(item.getShop(),
                    item.getProduct(),
                    0,
                    0,
                    null,
                    null,
                    null,
                    null);
            tempVal10 = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
        }
        for (String tempVal3 : ConfigManager.configManager.getListWithColor("display-item.add-lore")) {
            if (tempVal3.startsWith("@") && tempVal3.length() >= 2) {
                String tempVal4 = tempVal3.substring(2);
                switch (tempVal3.charAt(1)) {
                    case 'a':
                        if (!item.getBuyPrice().empty) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'b':
                        if (!item.getSellPrice().empty) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'c':
                        if (item.getPlayerBuyLimit(player) != -1) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'd':
                        if (item.getServerBuyLimit(player) != -1) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'e':
                        if (item.getPlayerSellLimit(player) != -1) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'f':
                        if (item.getServerSellLimit(player) != -1) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'g':
                        if (tempVal9 != null &&
                                item.getPlayerBuyLimit(player) > 0 &&
                                tempVal9.getBuyUseTimes() >= item.getPlayerBuyLimit(player)) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'h':
                        if (tempVal9 != null &&
                                item.getPlayerSellLimit(player) > 0 &&
                                tempVal9.getSellUseTimes() >= item.getPlayerSellLimit(player)) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'i':
                        if (tempVal10 != null &&
                                item.getServerBuyLimit(player) > 0 &&
                                tempVal10.getBuyUseTimes() >= item.getServerBuyLimit(player)) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'j':
                        if (tempVal10 != null &&
                                item.getServerSellLimit(player) > 0 &&
                                tempVal10.getSellUseTimes() >= item.getServerSellLimit(player)) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'k':
                        if (!buyMore) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'l':
                        if (tempVal9 != null &&
                                tempVal9.getCooldownBuyRefreshTime() != null &&
                                tempVal9.getCooldownBuyRefreshTime().isAfter(LocalDateTime.now())) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'm':
                        if (tempVal10 != null &&
                                tempVal10.getCooldownBuyRefreshTime() != null &&
                                tempVal10.getCooldownBuyRefreshTime().isAfter(LocalDateTime.now())) {
                            if (tempVal3.endsWith("-b")) {
                                if (bedrock) {
                                    continue;
                                }
                                else {
                                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                                }
                            }
                            addLore.add(tempVal4);
                        }
                        break;
                }
            }
            else {
                addLore.add(TextUtil.parse(tempVal3, player));
            }
        }
        if (!addLore.isEmpty()) {
            addLore = CommonUtil.modifyList(addLore,
                    "buy-price",
                    item.getBuyPrice().getDisplayNameInGUI(item.getBuyPrice().
                            getDisplayName(player,
                                    buyTimes,
                                    multi)),
                    "sell-price",
                    item.getSellPrice().getDisplayNameInGUI(item.getSellPrice().
                            getDisplayName(player,
                                    sellTimes,
                                    multi)),
                    "buy-limit-player",
                    String.valueOf(item.getPlayerBuyLimit(player)),
                    "sell-limit-player",
                    String.valueOf(item.getPlayerSellLimit(player)),
                    "buy-limit-server",
                    String.valueOf(item.getServerBuyLimit(player)),
                    "sell-limit-server",
                    String.valueOf(item.getServerSellLimit(player)),
                    "buy-times-player",
                    String.valueOf(tempVal9 == null ? "-" : buyTimes),
                    "sell-times-player",
                    String.valueOf(tempVal9 == null ? "-" : sellTimes),
                    "buy-refresh-player",
                    String.valueOf(tempVal9 == null ? ConfigManager.configManager.getString("placeholder.refresh.never") : tempVal9.getBuyRefreshTimeDisplayName()),
                    "sell-refresh-player",
                    String.valueOf(tempVal9 == null ? ConfigManager.configManager.getString("placeholder.refresh.never") : tempVal9.getSellRefreshTimeDisplayName()),
                    "buy-cooldown-player",
                    String.valueOf(tempVal9 == null ? ConfigManager.configManager.getString("placeholder.cooldown.now") : tempVal9.getBuyCooldownTimeDisplayName()),
                    "sell-cooldown-player",
                    String.valueOf(tempVal9 == null ? ConfigManager.configManager.getString("placeholder.cooldown.now") : tempVal9.getSellCooldownTimeDisplayName()),
                    "buy-times-server",
                    String.valueOf(tempVal10 == null ? "-" : tempVal10.getBuyUseTimes()),
                    "sell-times-server",
                    String.valueOf(tempVal10 == null ? "-" : tempVal10.getSellUseTimes()),
                    "buy-refresh-server",
                    String.valueOf(tempVal10 == null ? ConfigManager.configManager.getString("placeholder.refresh.never") : tempVal10.getBuyRefreshTimeDisplayName()),
                    "sell-refresh-server",
                    String.valueOf(tempVal10 == null ? ConfigManager.configManager.getString("placeholder.refresh.never") : tempVal10.getSellRefreshTimeDisplayName()),
                    "buy-cooldown-server",
                    String.valueOf(tempVal10 == null ? ConfigManager.configManager.getString("placeholder.cooldown.now") : tempVal10.getBuyCooldownTimeDisplayName()),
                    "sell-cooldown-server",
                    String.valueOf(tempVal10 == null ? ConfigManager.configManager.getString("placeholder.cooldown.now") : tempVal10.getSellCooldownTimeDisplayName()),
                    "buy-click",
                    getBuyClickPlaceholder(player, multi, item),
                    "sell-click",
                    getSellClickPlaceholder(player, multi, item),
                    "amount",
                    String.valueOf(multi)
            );
        }
        return addLore;
    }

    private static String getBuyClickPlaceholder(Player player, int multi, ObjectItem item) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            return "";
        }
        String s = "";
        switch(BuyProductMethod.startBuy(item.getShop(), item.getProduct(), player, false, true, multi)) {
            case ERROR:
                s = ConfigManager.configManager.getString("placeholder.click.error", "",  "amount", String.valueOf(multi));
                break;
            case IN_COOLDOWN:
                s = ConfigManager.configManager.getString("placeholder.click.buy-in-cooldown", "",  "amount", String.valueOf(multi));
                break;
            case PERMISSION:
                s = ConfigManager.configManager.getString("placeholder.click.buy-condition-not-meet", "",  "amount", String.valueOf(multi));
                break;
            case PLAYER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.buy-max-limit-player", "", "amount", String.valueOf(multi));
                break;
            case SERVER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.buy-max-limit-server", "", "amount", String.valueOf(multi));
                break;
            case NOT_ENOUGH :
                s = ConfigManager.configManager.getString("placeholder.click.buy-price-not-enough", "", "amount", String.valueOf(multi));
                break;
            case DONE :
                if (item.getSellPrice().empty) {
                    s = ConfigManager.configManager.getString("placeholder.click.buy-with-no-sell", "", "amount", String.valueOf(multi));
                }
                else {
                    s = ConfigManager.configManager.getString("placeholder.click.buy", "", "amount", String.valueOf(multi));
                }
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
                s = ConfigManager.configManager.getString("placeholder.click.error", "",  "amount", String.valueOf(multi));
                break;
            case PERMISSION:
                s = ConfigManager.configManager.getString("placeholder.click.sell-condition-not-meet", "",  "amount", String.valueOf(multi));
                break;
            case IN_COOLDOWN:
                s = ConfigManager.configManager.getString("placeholder.click.sell-in-cooldown", "",  "amount", String.valueOf(multi));
                break;
            case PLAYER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.sell-max-limit-player", "",  "amount", String.valueOf(multi));
                break;
            case SERVER_MAX:
                s = ConfigManager.configManager.getString("placeholder.click.sell-max-limit-server", "",  "amount", String.valueOf(multi));
                break;
            case NOT_ENOUGH :
                s = ConfigManager.configManager.getString("placeholder.click.sell-price-not-enough", "",  "amount", String.valueOf(multi));
                break;
            case DONE :
                if (item.getBuyPrice().empty) {
                    s = ConfigManager.configManager.getString("placeholder.click.sell-with-no-buy", "",  "amount", String.valueOf(multi));
                }
                else {
                    s = ConfigManager.configManager.getString("placeholder.click.sell", "",  "amount", String.valueOf(multi));
                }
                break;
            default :
                s = "Unknown";
                break;
        }
        return s;
    }
}
