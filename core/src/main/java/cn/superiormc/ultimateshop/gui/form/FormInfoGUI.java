package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.gui.ProductUIModel;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.List;

public class FormInfoGUI extends FormGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private final String amount;

    private final boolean enableBuyMore;

    private final Runnable backAction;

    public FormInfoGUI(Player owner, ObjectItem item) {
        this(owner, item, null);
    }

    public FormInfoGUI(Player owner, ObjectItem item, String amount) {
        this(owner, item, amount, null);
    }

    public FormInfoGUI(Player owner, ObjectItem item, String amount, Runnable backAction) {
        super(owner);
        this.backAction = backAction;
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.amount = amount;
        this.enableBuyMore =
            item.getBuyMore()
            && menu != null
            && ConfigManager.configManager.containsClickAction(
            "select-amount", item, player);
        constructGUI();
    }

    @Override
    public void constructGUI() {
        ObjectCache tempVal1 = CacheManager.cacheManager.getObjectCache(player);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(
                player, "error.player-not-found", "player", player.getName());
            return;
        }
        constructUnifiedForm();
    }

    private String bedrockTransfer(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    private void constructUnifiedForm() {
        var sourceMenu = item.getShopObject().getShopMenuObject();
        var model = new ProductUIModel(sourceMenu, player);
        SimpleForm.Builder builder =
            SimpleForm.builder()
            .title(
            TextUtil.parse(
            player,
            ConfigManager.configManager.getMenuRendererText(
                sourceMenu == null ? null : sourceMenu.getConfig(),
                player, "form", "info.title", "Shop", "item-name", item.getDisplayName(player), "amount", String.valueOf(getAmount()))))
            .content(
            TextUtil.parse(
            bedrockTransfer(
            ShopHelper.getProductInfoContent(
            player,
            item,
            getBuyAmount(),
            getSellAmount(),
            "form"))));
        List<ProductUIModel.Action> controls = new ArrayList<>(model.getPrimaryActions(item));
        controls.addAll(model.getExtraActions(item));
        for (var action : controls) {
            builder.button(ButtonComponent.of(TextUtil.parse(player, action.label())));
        }
        builder.button(
            ButtonComponent.of(
            TextUtil.parse(
            player,
            ConfigManager.configManager.getMenuRendererText(
                sourceMenu == null ? null : sourceMenu.getConfig(),
                player, "form", "info.buttons.back", "Back"))));
        builder.validResultHandler(
            (submitted, response) ->
            handleResponse(
            submitted,
            () -> {
                MenuStatusManager.menuStatusManager.removeOpenGUIStatus(
                    player, this);
                int index = response.clickedButtonId();
                if (index >= controls.size() && backAction != null) {
                    backAction.run();
                    return;
                }
                if (!item.isVisibleInMenu("form")
                    || !item.canDisplay(MenuSender.of(player, "form"))) {
                    finishGUI();
                    return;
                }
                if (index >= controls.size()) {
                    ShopGUI.openGUI(player, item.getShopObject(), true, true);
                    return;
                }
                var action = controls.get(index);
                if (action.id().equals("buy")
                    || action.id().equals("buy-or-sell")
                    && !item.getBuyPrice().empty) {
                    doThing(true);
                } else if (action.id().equals("sell")
                    || action.id().equals("buy-or-sell")) {
                    doThing(false);
                } else {
                    action.execute(
                        item, player, getBuyAmount(), getSellAmount());
                }
            }));
        builder.closedOrInvalidResultHandler(
            (submitted, response) -> handleResponse(submitted, this::finishGUI));
        form = builder.build();
    }

    public ObjectItem getItem() {
        return item;
    }

    public void doThing(boolean buyOrSell) {
        ObjectMenu sourceMenu = item.getShopObject().getShopMenuObject();
        MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, this);
        if (!buyOrSell && item.openPriceModifierMenu(player)) {
            return;
        }
        String action = buyOrSell ? "buy" : "all".equals(amount) ? "sell-all" : "sell";
        item.executeClickAction(
            action,
            buyOrSell ? ClickType.LEFT : ClickType.RIGHT,
            player,
            getBuyAmount(),
            getSellAmount());
        if (ConfigManager.configManager.getMenuRendererBoolean(
            sourceMenu == null ? null : sourceMenu.getConfig(),
            "form", "not-auto-close", true)) {
            ShopGUI.openGUI(player, item.getShopObject(), true, true);
        }
    }

    public int getAmount() {
        if (amount == null) {
            return item.getBuyPrice().empty
            ? item.getDefaultSellAmount()
            : item.getDefaultBuyAmount();
        }
        if (!enableBuyMore) {
            return 1;
        }
        int realAmount;
        try {
            realAmount = Integer.parseInt(amount);
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

    private int getBuyAmount() {
        return amount == null ? item.getDefaultBuyAmount() : getAmount();
    }

    private int getSellAmount() {
        return amount == null ? item.getDefaultSellAmount() : getAmount();
    }
}
