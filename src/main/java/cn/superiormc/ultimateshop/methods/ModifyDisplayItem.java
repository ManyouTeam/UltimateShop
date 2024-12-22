package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModifyDisplayItem {

    public static ObjectDisplayItemStack modifyItem(Player player,
                                                    int multi,
                                                    ObjectDisplayItemStack addLoreDisplayItem,
                                                    ObjectItem item,
                                                    boolean buyMore) {
        return modifyItem(player, multi, addLoreDisplayItem, item, buyMore, "general");
    }

    public static ObjectDisplayItemStack modifyItem(Player player,
                                                    int multi,
                                                    ObjectDisplayItemStack addLoreDisplayItem,
                                                    ObjectItem item,
                                                    boolean buyMore,
                                                    String clickType) {
        if (clickType == null) {
            clickType = "general";
        }
        ItemMeta tempVal2 = addLoreDisplayItem.getMeta();
        if (tempVal2 == null) {
            return addLoreDisplayItem;
        }
        // 修改物品名称
        if (item.getItemConfig().getString("display-name") != null) {
            tempVal2.setDisplayName(item.getDisplayName(player));
        }
        if (tempVal2.hasDisplayName()) {
            tempVal2.setDisplayName(CommonUtil.modifyString(tempVal2.getDisplayName(), "amount", String.valueOf(multi),
                    "item-name", item.getDisplayName(player)));
        }
        List<String> addLore = new ArrayList<>();
        if (tempVal2.hasLore()) {
            addLore.addAll(tempVal2.getLore());
        }
        addLore.addAll(getModifiedLore(player, multi, item, buyMore, false, clickType));
        if (!addLore.isEmpty()) {
            tempVal2.setLore(addLore);
        }
        addLoreDisplayItem.setItemMeta(tempVal2);
        return addLoreDisplayItem;
    }
    
    public static List<String> getModifiedLore(Player player,
                                                int multi,
                                                ObjectItem item,
                                                boolean buyMore,
                                                boolean bedrock,
                                                String clickType) {
        List<String> addLore = new ArrayList<>();
        int buyTimes = 0;
        int sellTimes = 0;
        ObjectUseTimesCache tempVal9 = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        ObjectUseTimesCache tempVal10 = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
        if (tempVal9 != null) {
            buyTimes = tempVal9.getBuyUseTimes();
            sellTimes = tempVal9.getSellUseTimes();
        } else {
            tempVal9 = CacheManager.cacheManager.getPlayerCache(player).createUseTimesCache(item);
        }
        if (tempVal10 == null) {
            tempVal10 = CacheManager.cacheManager.serverCache.createUseTimesCache(item);
        }
        for (String tempVal3 : item.getAddLore()) {
            String tempVal4 = tempVal3;
            boolean not = false;
            if (tempVal4.endsWith("-b")) {
                if (bedrock) {
                    continue;
                }
                else {
                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                }
            }
            if (tempVal4.endsWith("-m")) {
                if (!buyMore) {
                    continue;
                }
                else {
                    tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
                }
            }
            if (tempVal4.endsWith("-i")) {
                not = true;
                tempVal4 = tempVal4.substring(0, tempVal4.length() - 2);
            }
            if (tempVal3.startsWith("@") && tempVal4.length() >= 2) {
                tempVal4 = tempVal4.substring(2);
                switch (tempVal3.charAt(1)) {
                    case 'a':
                        if (!parseClickType(item, clickType, true)) {
                            continue;
                        }
                        if (!item.getBuyPrice().empty) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'b':
                        if (!parseClickType(item, clickType, false)) {
                            continue;
                        }
                        if (!item.getSellPrice().empty) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'c':
                        if (item.getPlayerBuyLimit(player) != -1) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'd':
                        if (item.getServerBuyLimit(player) != -1) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'e':
                        if (item.getPlayerSellLimit(player) != -1) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'f':
                        if (item.getServerSellLimit(player) != -1) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'g':
                        if (tempVal9 != null &&
                                item.getPlayerBuyLimit(player) > 0 &&
                                tempVal9.getBuyUseTimes() >= item.getPlayerBuyLimit(player)) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'h':
                        if (tempVal9 != null &&
                                item.getPlayerSellLimit(player) > 0 &&
                                tempVal9.getSellUseTimes() >= item.getPlayerSellLimit(player)) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'i':
                        if (tempVal10 != null &&
                                item.getServerBuyLimit(player) > 0 &&
                                tempVal10.getBuyUseTimes() >= item.getServerBuyLimit(player)) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'j':
                        if (tempVal10 != null &&
                                item.getServerSellLimit(player) > 0 &&
                                tempVal10.getSellUseTimes() >= item.getServerSellLimit(player)) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'k':
                        if (!buyMore && item.getBuyMore()) {
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                    case 'n':
                        if ((!item.getBuyPrice().empty && parseClickType(item, clickType, true)) ||
                                (!item.getSellPrice().empty && parseClickType(item, clickType, false))){
                            if (not) {
                                continue;
                            }
                            addLore.add(tempVal4);
                        } else if (not) {
                            addLore.add(tempVal4);
                        }
                        break;
                }
            }
            else {
                addLore.add(tempVal3);
            }
        }
        if (!addLore.isEmpty()) {
            if (tempVal9 != null && tempVal10 != null) {
                addLore = CommonUtil.modifyList(player, addLore,
                        "buy-price",
                        ObjectPrices.getDisplayNameInLine(player,
                                multi,
                                item.getBuyPrice().takeSingleThing(player.getInventory(), player, tempVal9.getBuyUseTimes(), multi, true).getResultMap(),
                                item.getBuyPrice().getMode(),
                                false),
                        "sell-price",
                        ObjectPrices.getDisplayNameInLine(player,
                                multi,
                                item.getSellPrice().giveSingleThing(player, tempVal9.getBuyUseTimes(), multi).getResultMap(),
                                item.getSellPrice().getMode(),
                                false),
                        "buy-limit-player",
                        String.valueOf(item.getPlayerBuyLimit(player)),
                        "sell-limit-player",
                        String.valueOf(item.getPlayerSellLimit(player)),
                        "buy-limit-server",
                        String.valueOf(item.getServerBuyLimit(player)),
                        "sell-limit-server",
                        String.valueOf(item.getServerSellLimit(player)),
                        "buy-times-player",
                        String.valueOf(buyTimes),
                        "sell-times-player",
                        String.valueOf(sellTimes),
                        "buy-refresh-player",
                        String.valueOf(tempVal9.getBuyRefreshTimeDisplayName()),
                        "sell-refresh-player",
                        String.valueOf(tempVal9.getSellRefreshTimeDisplayName()),
                        "buy-times-server",
                        String.valueOf(tempVal10.getBuyUseTimes()),
                        "sell-times-server",
                        String.valueOf(tempVal10.getSellUseTimes()),
                        "buy-refresh-server",
                        String.valueOf(tempVal10.getBuyRefreshTimeDisplayName()),
                        "sell-refresh-server",
                        String.valueOf(tempVal10.getSellRefreshTimeDisplayName()),
                        "buy-click",
                        getBuyClickPlaceholder(player, multi, item, clickType),
                        "sell-click",
                        getSellClickPlaceholder(player, multi, item, clickType),
                        "amount",
                        String.valueOf(multi),
                        "item-name",
                        item.getDisplayName(player)
                );
            } else if (player.getOpenInventory().getType() == InventoryType.CHEST) {
                player.closeInventory();
                LanguageManager.languageManager.sendStringText(player, "plugin.reload-close-gui");
            }
        }
        return addLore;
    }

    private static String getBuyClickPlaceholder(Player player, int multi, ObjectItem item, String clickType) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            if (item.getSellPrice().empty || clickType.equals("buy")) {
                return ConfigManager.configManager.getString("placeholder.click.buy-with-no-sell", "", "amount", String.valueOf(multi));
            }
            else {
                return ConfigManager.configManager.getString("placeholder.click.buy", "", "amount", String.valueOf(multi));
            }
        }
        String s = "";
        switch (BuyProductMethod.startBuy(item.getShop(), item.getProduct(), player, false, true, multi).getStatus()) {
            case ERROR:
                s = ConfigManager.configManager.getString("placeholder.click.error", "",  "amount", String.valueOf(multi));
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
                if (item.getSellPrice().empty || clickType.equals("buy")) {
                    s = ConfigManager.configManager.getString("placeholder.click.buy-with-no-sell", "", "amount", String.valueOf(multi));
                }
                else {
                    s = ConfigManager.configManager.getString("placeholder.click.buy", "", "amount", String.valueOf(multi));
                }
                break;
        }
        return s;
    }

    private static String getSellClickPlaceholder(Player player, int multi, ObjectItem item, String clickType) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            if (item.getBuyPrice().empty || clickType.equals("sell")) {
                return ConfigManager.configManager.getString("placeholder.click.sell-with-no-buy", "",  "amount", String.valueOf(multi));
            }
            else {
                return ConfigManager.configManager.getString("placeholder.click.sell", "",  "amount", String.valueOf(multi));
            }
        }
        String s;
        switch (SellProductMethod.startSell(item.getShop(), item.getProduct(), player, false, true, multi).getStatus()) {
            case ERROR :
                s = ConfigManager.configManager.getString("placeholder.click.error", "",  "amount", String.valueOf(multi));
                break;
            case PERMISSION:
                s = ConfigManager.configManager.getString("placeholder.click.sell-condition-not-meet", "",  "amount", String.valueOf(multi));
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
                if (item.getBuyPrice().empty || clickType.equals("sell")) {
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

    private static boolean parseClickType(ObjectItem item, String clickType, boolean buyOrSell) {
        if (clickType == null || clickType.equals("general")) {
            return true;
        }
        switch (clickType) {
            case "buy" :
                return buyOrSell;
            case "sell" :
            case "sell-all" :
                return !buyOrSell;
            case "buy-or-sell" :
                if (item.getBuyPrice().empty && !item.getSellPrice().empty) {
                    return !buyOrSell;
                }
                else {
                    return buyOrSell;
                }
            default:
                return false;
        }
    }
}
