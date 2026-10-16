package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.ProductUIModel;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DialogInfoGUI extends DialogGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private final int amount;

    private final int buyAmount;

    private final int sellAmount;

    private final boolean enableBuyMore;

    private final Runnable backAction;

    private final List<ProductUIModel.Action> extraActions;

    public DialogInfoGUI(Player player, ObjectItem item) {
        this(player, item, null);
    }

    public DialogInfoGUI(Player player, ObjectItem item, String amount) {
        this(player, item, amount, null);
    }

    public DialogInfoGUI(Player player, ObjectItem item, String amount, Runnable backAction) {
        this(player, item, amount, backAction, null);
    }

    public DialogInfoGUI(
        Player player,
        ObjectItem item,
        String amount,
        Runnable backAction,
        List<ProductUIModel.Action> extraActions) {
        super(player);
        this.extraActions = extraActions == null ? null : List.copyOf(extraActions);
        this.backAction = backAction;
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.enableBuyMore =
            item.getBuyMore()
            && menu != null
            && ConfigManager.configManager.containsClickAction(
            "select-amount", item, player);
        this.buyAmount = amount == null ? item.getDefaultBuyAmount() : getAmount(amount);
        this.sellAmount = amount == null ? item.getDefaultSellAmount() : getAmount(amount);
        this.amount = item.getBuyPrice().empty ? sellAmount : buyAmount;
    }

    @Override
    public void constructGUI() {
        ObjectMenu sourceMenu = item.getShopObject().getShopMenuObject();
        if (CacheManager.cacheManager.getObjectCache(player) == null) {
            LanguageManager.languageManager.sendStringText(
                player, "error.player-not-found", "player", player.getName());
            return;
        }
        String itemName = item.getDisplayName(player);
        String title =
            ConfigManager.configManager.getMenuRendererText(
                sourceMenu == null ? null : sourceMenu.getConfig(),
                player, "dialog", "info.title", "", "item-name", itemName, "amount", String.valueOf(amount));
        DialogView.Builder builder = DialogView.builder(title);
        builder.keepOpenAfterAction(
            ConfigManager.configManager.getMenuRendererBoolean(
                sourceMenu == null ? null : sourceMenu.getConfig(),
                "dialog", "not-auto-close", true));
        ItemStack displayItem = item.getDisplayItem(player);
        if (ConfigManager.configManager.getMenuRendererBoolean(
            sourceMenu == null ? null : sourceMenu.getConfig(),
            "dialog", "info.display-item", true)
            && !displayItem.getType().isAir()) {
            builder.item(displayItem);
        }
        List<String> content =
            ShopHelper.getProductInfoContent(player, item, buyAmount, sellAmount, "dialog");
        if (!content.isEmpty()) {
            builder.body(String.join("\n", content));
        }
        ProductUIModel model = new ProductUIModel(item.getShopObject().getShopMenuObject(), player, "dialog");
        List<ProductUIModel.Action> actions = new ArrayList<>();
        if (extraActions == null) {
            actions.addAll(model.getPrimaryActions(item));
            actions.addAll(model.getExtraActions(item));
        } else {
            actions.addAll(extraActions);
        }
        for (ProductUIModel.Action action : actions) {
            builder.action(
                DialogAction.of(
                "product_" + action.id(),
                action.label(),
                response -> {
                    if (item.isVisibleInMenu("dialog")
                        && item.canDisplay(MenuSender.of(player, "dialog"))) {
                        action.execute(item, player, buyAmount, sellAmount);
                    }
                }));
        }
        builder.action(
            DialogAction.of(
            "back",
            ConfigManager.configManager.getMenuRendererText(
                sourceMenu == null ? null : sourceMenu.getConfig(),
                player, "dialog", "info.buttons.back", ""),
            response -> {
                if (backAction != null) {
                    backAction.run();
                } else {
                    ShopGUI.openGUI(player, item.getShopObject(), false, true);
                }
            }));
        dialog = builder.build();
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }

    public int getAmount(String amountString) {
        if (!enableBuyMore) {
            return 1;
        }
        int realAmount;
        try {
            realAmount = Integer.parseInt(amountString);
            if (realAmount < 1) {
                realAmount = 1;
            } else if (realAmount > menu.getSection().getInt("max-amount", 64)) {
                realAmount = Math.max(1, menu.getSection().getInt("max-amount", 64));
            }
        } catch (Throwable e) {
            realAmount = 1;
        }
        return realAmount;
    }
}
