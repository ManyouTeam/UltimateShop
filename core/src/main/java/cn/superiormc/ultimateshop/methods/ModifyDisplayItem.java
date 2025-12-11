package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            UltimateShop.methodUtil.setItemName(tempVal2, item.getDisplayName(player), player);
        }
        if (tempVal2.hasDisplayName()) {
            UltimateShop.methodUtil.setItemName(tempVal2, CommonUtil.modifyString(UltimateShop.methodUtil.getItemName(tempVal2), "amount", String.valueOf(multi),
                    "item-name", item.getDisplayName(player)), player);
        }
        List<String> addLore = new ArrayList<>();
        if (tempVal2.hasLore()) {
            addLore.addAll(CommonUtil.modifyList(player,
                    UltimateShop.methodUtil.getItemLore(tempVal2),
                    "amount", String.valueOf(multi),
                    "item-name", item.getDisplayName(player)));
        }
        addLore.addAll(getModifiedLore(player, multi, item, buyMore, false, clickType));
        if (!addLore.isEmpty()) {
            UltimateShop.methodUtil.setItemLore(tempVal2, addLore, player);
        }
        addLoreDisplayItem.setItemMeta(tempVal2);
        return addLoreDisplayItem;
    }

    public static List<String> getModifiedLore(
            Player player,
            int multi,
            ObjectItem item,
            boolean buyMore,
            boolean bedrock,
            String clickType
    ) {

        List<String> resultLore = new ArrayList<>();

        ObjectUseTimesCache playerCache =
                CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
        ObjectUseTimesCache serverCache =
                CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);

        if (playerCache == null) {
            playerCache = CacheManager.cacheManager.getPlayerCache(player).createUseTimesCache(item);
        }
        if (serverCache == null) {
            serverCache = CacheManager.cacheManager.serverCache.createUseTimesCache(item);
        }

        Map<Character, Boolean> prefixConditions =
                buildPrefixMap(player, item, clickType, buyMore, bedrock, playerCache, serverCache);

        for (String rawLine : item.getAddLore()) {

            if (rawLine.endsWith("-i") || rawLine.endsWith("-m") || rawLine.endsWith("-b")) {
                ErrorManager.errorManager.sendErrorMessage("§cYour display item add lore config is not updated, please reconfigure your " +
                        "display item add lore configs at config.yml file! You can get latest config at plugin Wiki: https://ultimateshop.superiormc.cn/info/configuration-files .");
            }

            ParsedLine parsed = ParsedLine.parse(rawLine);

            if (!parsed.isConditional()) {
                resultLore.add(parsed.getPureText());
                continue;
            }

            boolean condition = parsed.conditions.stream().allMatch(c -> {
                Boolean val = prefixConditions.get(c.key);
                if (val == null) {
                    val = false;
                }
                return c.negate ? !val : val;
            });

            if (condition) {
                resultLore.add(parsed.getPureText());
            }
        }

        if (!resultLore.isEmpty()) {
            resultLore = CommonUtil.modifyList(player, resultLore,
                    "buy-price",
                    ObjectPrices.getDisplayNameInLine(player, multi,
                            item.getBuyPrice().take(player.getInventory(), player,
                                    playerCache.getBuyUseTimes(), multi, true).getResultMap(),
                            item.getBuyPrice().getMode(), false),

                    "sell-price",
                    ObjectPrices.getDisplayNameInLine(player, multi,
                            item.getSellPrice().give(player, playerCache.getBuyUseTimes(), multi).getResultMap(),
                            item.getSellPrice().getMode(), false),

                    "buy-limit-player", String.valueOf(item.getPlayerBuyLimit(player)),
                    "sell-limit-player", String.valueOf(item.getPlayerSellLimit(player)),
                    "buy-limit-server", String.valueOf(item.getServerBuyLimit(player)),
                    "sell-limit-server", String.valueOf(item.getServerSellLimit(player)),

                    "buy-total-player", String.valueOf(playerCache.getTotalBuyUseTimes()),
                    "sell-total-player", String.valueOf(playerCache.getTotalSellUseTimes()),
                    "buy-total-server", String.valueOf(serverCache.getTotalBuyUseTimes()),
                    "sell-total-server", String.valueOf(serverCache.getTotalSellUseTimes()),

                    "buy-times-player", String.valueOf(playerCache.getBuyUseTimes()),
                    "sell-times-player", String.valueOf(playerCache.getSellUseTimes()),

                    "buy-refresh-player", playerCache.getBuyRefreshTimeDisplayName(),
                    "sell-refresh-player", playerCache.getSellRefreshTimeDisplayName(),
                    "buy-next-player", playerCache.getBuyRefreshTimeNextName(),
                    "sell-next-player", playerCache.getSellRefreshTimeNextName(),

                    "buy-times-server", String.valueOf(serverCache.getBuyUseTimes()),
                    "sell-times-server", String.valueOf(serverCache.getSellUseTimes()),

                    "buy-refresh-server", serverCache.getBuyRefreshTimeDisplayName(),
                    "sell-refresh-server", serverCache.getSellRefreshTimeDisplayName(),
                    "buy-next-server", serverCache.getBuyRefreshTimeNextName(),
                    "sell-next-server", serverCache.getSellRefreshTimeNextName(),

                    "last-buy-player", playerCache.getBuyLastTimeName(),
                    "last-sell-player", playerCache.getSellLastTimeName(),
                    "last-buy-server", serverCache.getBuyLastTimeName(),
                    "last-sell-server", serverCache.getSellLastTimeName(),

                    "last-reset-buy-player", playerCache.getBuyLastResetTimeName(),
                    "last-reset-sell-player", playerCache.getSellLastResetTimeName(),
                    "last-reset-buy-server", serverCache.getBuyLastResetTimeName(),
                    "last-reset-sell-server", serverCache.getSellLastResetTimeName(),

                    "buy-click", getBuyClickPlaceholder(player, multi, item, clickType),
                    "sell-click", getSellClickPlaceholder(player, multi, item, clickType),
                    "amount", String.valueOf(multi),
                    "item-name", item.getDisplayName(player)
            );
        }

        return resultLore;
    }

    private static class ConditionElement {
        final char key;
        final boolean negate;

        ConditionElement(char key, boolean negate) {
            this.key = key;
            this.negate = negate;
        }
    }

    private static class ParsedLine {

        final List<ConditionElement> conditions;
        final boolean conditional;
        final String text;

        ParsedLine(List<ConditionElement> conditions, String text) {
            this.conditions = conditions;
            this.conditional = !conditions.isEmpty();
            this.text = text;
        }

        boolean isConditional() { return conditional; }
        String getPureText() { return text; }

        static ParsedLine parse(String line) {
            if (line == null) line = "";

            List<ConditionElement> list = new ArrayList<>();
            StringBuilder out = new StringBuilder();

            int idx = 0;
            int len = line.length();

            while (idx < len) {
                char ch = line.charAt(idx);

                if (ch == '(' && idx + 2 < len && line.charAt(idx + 1) == '@') {
                    int end = line.indexOf(')', idx);
                    if (end != -1) {
                        String inside = line.substring(idx + 2, end);
                        extractConditions(inside, list, true);
                        idx = end + 1;
                        continue;
                    }
                }

                if (ch == '@' && idx + 1 < len) {
                    char key = line.charAt(idx + 1);
                    if (Character.isLetter(key)) {
                        list.add(new ConditionElement(key, false));
                        idx += 2;
                        continue;
                    }
                }

                out.append(ch);
                idx++;
            }

            if (out.toString().trim().isEmpty() && !list.isEmpty()) {
                return new ParsedLine(list, "");
            }

            return new ParsedLine(list, out.toString());
        }

        private static void extractConditions(String inside, List<ConditionElement> list, boolean negate) {
            int i = 0;
            while (i < inside.length()) {
                char c = inside.charAt(i);

                if (Character.isLetter(c)) {
                    list.add(new ConditionElement(c, negate));
                    i++;
                    continue;
                }

                if (c == '@' && i + 1 < inside.length()) {
                    char k = inside.charAt(i + 1);
                    if (Character.isLetter(k)) {
                        list.add(new ConditionElement(k, negate));
                        i += 2;
                        continue;
                    }
                }

                i++;
            }
        }
    }

    private static Map<Character, Boolean> buildPrefixMap(
            Player player,
            ObjectItem item,
            String clickType,
            boolean buyMore,
            boolean bedrock,
            ObjectUseTimesCache p,
            ObjectUseTimesCache s
    ) {
        Map<Character, Boolean> map = new HashMap<>();

        map.put('a', !item.getBuyPrice().empty);
        map.put('b', !item.getSellPrice().empty);
        map.put('c', item.getPlayerBuyLimit(player) != -1);
        map.put('d', item.getServerBuyLimit(player) != -1);
        map.put('e', item.getPlayerSellLimit(player) != -1);
        map.put('f', item.getServerSellLimit(player) != -1);

        map.put('g', item.getPlayerBuyLimit(player) > 0 && p.getBuyUseTimes() >= item.getPlayerBuyLimit(player));
        map.put('h', item.getPlayerSellLimit(player) > 0 && p.getSellUseTimes() >= item.getPlayerSellLimit(player));

        map.put('i', item.getServerBuyLimit(player) > 0 && s.getBuyUseTimes() >= item.getServerBuyLimit(player));
        map.put('j', item.getServerSellLimit(player) > 0 && s.getSellUseTimes() >= item.getServerSellLimit(player));

        map.put('k', item.getBuyMore());
        map.put('m', !item.getSellPrice().empty && item.isEnableSellAll());
        map.put('n', (!item.getBuyPrice().empty && parseClickType(item, clickType, true)) ||
                (!item.getSellPrice().empty && parseClickType(item, clickType, false)));

        map.put('p', buyMore);
        map.put('q', !buyMore);

        map.put('x', bedrock);
        map.put('y', !bedrock);

        map.put('u', parseClickType(item, clickType, true));
        map.put('v', parseClickType(item, clickType, false));

        return map;
    }

    private static String getBuyClickPlaceholder(Player player, int multi, ObjectItem item, String clickType) {
        if (!ConfigManager.configManager.getBoolean("placeholder.click.enabled")) {
            if (item.getSellPrice().empty || clickType.equals("buy")) {
                return ConfigManager.configManager.getString("placeholder.click.buy-with-no-sell", "", "amount", String.valueOf(multi));
            } else {
                return ConfigManager.configManager.getString("placeholder.click.buy", "", "amount", String.valueOf(multi));
            }
        }
        String s = "";
        switch (BuyProductMethod.startBuy(item, player, false, true, multi).getStatus()) {
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
                } else {
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
            } else {
                return ConfigManager.configManager.getString("placeholder.click.sell", "",  "amount", String.valueOf(multi));
            }
        }
        String s;
        switch (SellProductMethod.startSell(item, player, false, true, multi).getStatus()) {
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
                } else {
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
                return !buyOrSell;
            case "sell-all" :
                return !buyOrSell && item.isEnableSellAll();
            case "buy-or-sell" :
                if (item.getBuyPrice().empty && !item.getSellPrice().empty) {
                    return !buyOrSell;
                } else {
                    return buyOrSell;
                }
            default:
                return false;
        }
    }
}
