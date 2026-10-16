package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.form.FormBuyMoreGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ProductUIModel {

    private final ObjectMenu menu;

    private final Player player;

    private final String presentation;

    public ProductUIModel(ObjectMenu menu, Player player, String presentation) {
        this.menu = Objects.requireNonNull(menu);
        this.player = Objects.requireNonNull(player);
        this.presentation = presentation;
    }

    private Object getGraphicSetting(ObjectItem item, String path, Object fallback) {
        if (!presentation.equals("ore") && !presentation.equals("arcmenu")) {
            return fallback;
        }
        return ConfigManager.configManager.getProductUISetting(
            menu.getConfig(), item.getButtonConfig(), path, fallback);
    }

    public record Action(String id, String label, ClickType click) {
        public void execute(ObjectItem item, Player player) {
            execute(item, player, item.getDefaultBuyAmount(), item.getDefaultSellAmount());
        }

        public void execute(ObjectItem item, Player player, int buyAmount, int sellAmount) {
            if (id.equals("select-amount") && CommonUtil.isBedrockPlayer(player)) {
                new FormBuyMoreGUI(player, item).openGUI(true);
                return;
            }
            item.executeClickAction(id, click, player, buyAmount, sellAmount);
        }
    }

    public List<Action> getAvailableActions(ObjectItem item) {
        Map<String, Action> actions = new LinkedHashMap<>();
        for (ClickType click : ClickType.values()) {
            String id = ConfigManager.configManager.getClickAction(click, item);
            if (id.equals("none") || actions.containsKey(id)) {
                continue;
            }
            boolean buy = !item.getBuyPrice().empty;
            boolean sell = !item.getRawSellPrice().empty;
            if (id.equals("buy") && !buy
                || id.equals("sell") && !sell
                || id.equals("buy-or-sell") && !buy && !sell
                || id.equals("sell-all") && (!sell || !item.isEnableSellAll())
                || id.equals("select-amount")
                && (!item.getBuyMore() || item.getBuyMoreMenu() == null)
                || id.equals("add-favourite") && !item.isAllowFavourite()) {
                continue;
            }
            var custom = ConfigManager.configManager.getSection("menu.click-event-actions." + id);
            if (custom != null
                && (custom.getBoolean("buy-only", false) && !buy
                || custom.getBoolean("sell-only", false) && !sell)) {
                continue;
            }
            String key =
                id.equals("buy-or-sell")
            ? (buy ? "buy" : "sell")
            : id.equals("select-amount") ? "buy-more" : id;
            String label =
                custom == null
            ? ConfigManager.configManager.getMenuRendererText(
                menu.getConfig(), player, presentation.equals("form") ? "form" : "dialog", "info.buttons." + key,
                id,
                "item-name",
                item.getDisplayName(player))
            : custom.getString("display-name", id);
            Object configuredLabel = getGraphicSetting(item, "actions.labels." + id, null);
            if (configuredLabel instanceof String text) {
                label =
                    CommonUtil.modifyString(
                    player, text, "item-name", item.getDisplayName(player));
            }
            actions.put(id, new Action(id, label, click));
        }
        return new ArrayList<>(actions.values());
    }

    public List<Action> getPrimaryActions(ObjectItem item) {
        Object setting =
            getGraphicSetting(item, "actions.primary", List.of("buy", "sell"));
        List<?> ids = setting instanceof List<?> list ? list : List.of("buy", "sell");
        List<Action> available = getAvailableActions(item);
        List<Action> result = new ArrayList<>();
        for (Object name : ids) {
            Action found =
                available.stream()
                .filter(a -> a.id().equals(String.valueOf(name)))
                .findFirst()
                .orElse(null);
            if (found == null
                && ("buy".equals(name) && !item.getBuyPrice().empty
                || "sell".equals(name)
                && item.getBuyPrice().empty
                && !item.getRawSellPrice().empty)) {
                found =
                    available.stream()
                    .filter(a -> a.id().equals("buy-or-sell"))
                    .findFirst()
                    .orElse(null);
            }
            if (found != null && !result.contains(found) && result.size() < 2) {
                result.add(found);
            }
        }
        return result;
    }

    public List<Action> getExtraActions(ObjectItem item) {
        List<Action> primary = getPrimaryActions(item);
        Object setting =
            getGraphicSetting(item, "actions.extra", "auto");
        List<Action> result = new ArrayList<>();
        for (Action action : getAvailableActions(item)) {
            if (primary.stream().anyMatch(a -> a.id().equals(action.id()))) {
                continue;
            }
            if (action.id().equals("buy-or-sell")
                && primary.stream()
                .anyMatch(
                a ->
                a.id().equals(
                item.getBuyPrice().empty
                ? "sell"
                : "buy"))) {
                continue;
            }
            if (setting instanceof List<?> list && !list.contains(action.id())) {
                continue;
            }
            if (!(setting instanceof List<?>) && !"auto".equals(setting)) {
                continue;
            }
            result.add(action);
        }
        return result;
    }

    public String getPriceLine(ObjectItem item) {
        Object enabled =
            getGraphicSetting(item, "price.enabled", true);
        if (Boolean.FALSE.equals(enabled)) {
            return "";
        }
        String kind =
            item.getBuyPrice().empty
        ? "only-sell"
        : item.getRawSellPrice().empty ? "only-buy" : "both";
        Object format =
            getGraphicSetting(item, "price.formats." + kind, null);
        return item.getDisplayItemObject()
            .getDisplayItem(player)
            .getPriceExtraLine(format instanceof String text ? text : null)
            .replace("\n", " | ");
    }

    public List<String> getLore(ObjectItem item, String presentation) {
        return ModifyDisplayItem.getModifiedLore(
            player,
            item,
            item.getDefaultBuyAmount(),
            item.getDefaultSellAmount(),
            presentation);
    }

    public List<String> getTooltip(ObjectItem item) {
        return getTooltip(item, "arcmenu-hover");
    }

    public List<String> getTooltip(ObjectItem item, String presentation) {
        List<String> result = new ArrayList<>();
        result.add(item.getDisplayName(player, false));
        result.addAll(getHoverLore(item, presentation));
        if (result.size() == 1) {
            String price = getPriceLine(item);
            if (!price.isEmpty()) {
                result.add(price);
            }
        }
        return result;
    }

    public List<String> getHoverLore(ObjectItem item, String presentation) {
        return getHoverLore(
            item, item.getDefaultBuyAmount(), item.getDefaultSellAmount(), presentation);
    }

    public List<String> getHoverLore(
        ObjectItem item, int buyAmount, int sellAmount, String presentation) {
        return ShopHelper.getProductInfoContent(
            player,
            item,
            buyAmount,
            sellAmount,
            presentation);
    }
}
