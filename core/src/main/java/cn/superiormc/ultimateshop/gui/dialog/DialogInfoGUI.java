package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DialogInfoGUI extends DialogGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private final int amount;

    private final int buyAmount;

    private final int sellAmount;

    private final boolean enableBuyMore;

    private final Runnable backAction;

    public DialogInfoGUI(Player player, ObjectItem item) {
        this(player, item, null);
    }

    public DialogInfoGUI(Player player, ObjectItem item, String amount) {
        this(player, item, amount, null);
    }

    public DialogInfoGUI(Player player, ObjectItem item, String amount, Runnable backAction) {
        super(player);
        this.backAction = backAction;
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.enableBuyMore = item.getBuyMore() && menu != null && ConfigManager.configManager.containsClickAction("select-amount", item, player);
        this.buyAmount = amount == null ? item.getDefaultBuyAmount() : getAmount(amount);
        this.sellAmount = amount == null ? item.getDefaultSellAmount() : getAmount(amount);
        this.amount = item.getBuyPrice().empty ? sellAmount : buyAmount;
    }

    @Override
    public void constructGUI() {
        if (CacheManager.cacheManager.getObjectCache(player) == null) {
            LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", player.getName());
            return;
        }
        String itemName = item.getDisplayName(player);
        String title = getDialogText("info.title", "item-name", itemName, "amount", String.valueOf(amount));
        DialogView.Builder builder = DialogView.builder(title);
        builder.keepOpenAfterAction(ConfigManager.configManager.getBoolean("menu.dialog.not-auto-close"));
        ItemStack displayItem = item.getDisplayItem(player);
        if (ConfigManager.configManager.getBoolean("menu.dialog.info.display-item") &&
                !displayItem.getType().isAir()) {
            builder.item(displayItem);
        }
        List<String> content = ShopHelper.getProductInfoContent(player, item, buyAmount, sellAmount, "dialog");
        if (!content.isEmpty()) {
            builder.body(String.join("\n", content));
        }
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        if (!item.getBuyPrice().empty) {
            builder.action(DialogAction.of("buy",
                    getDialogText("info.buttons.buy", "item-name", itemName), response -> BuyProductMethod.startBuy(item, player, !b, false, buyAmount)));
        }
        if (!item.getRawSellPrice().empty) {
            builder.action(DialogAction.of("sell", getDialogText("info.buttons.sell", "item-name", itemName),
                    response -> {
                        if (!item.openPriceModifierMenu(player)) {
                            SellProductMethod.startSell(item, player, !b, false, sellAmount);
                        }
                    }));
            if (ConfigManager.configManager.containsClickAction("sell-all", item, player) && item.isEnableSellAll()) {
                builder.action(DialogAction.of("sell_all", getDialogText("info.buttons.sell-all", "item-name", itemName),
                        response -> {
                            if (!item.openPriceModifierMenu(player)) {
                                int maxAmount = menu == null
                                        ? 64
                                        : menu.getSection().getInt("max-amount", 64);
                                SellProductMethod.startSell(item, player, !b, false, true,
                                        maxAmount);
                            }
                        }));
            }
        }
        if (item.getBuyMore() && ConfigManager.configManager.containsClickAction("select-amount", item, player)) {
            builder.action(DialogAction.of("amount", getDialogText("info.buttons.buy-more", "item-name", itemName),
                    response -> new DialogBuyMoreGUI(player, item).openGUI(true)));
        }
        addCustomActions(builder);
        builder.action(DialogAction.of("back", getDialogText("info.buttons.back"), response -> {
            if (backAction != null) {
                backAction.run();
            } else {
                ShopGUI.openGUI(player, item.getShopObject(), false, true);
            }
        }));
        dialog = builder.build();
    }

    private void addCustomActions(DialogView.Builder builder) {
        for (ClickType type : ClickType.values()) {
            String actionType = ConfigManager.configManager.getClickAction(type, item, player);
            if (CommonUtil.containsAnyString(actionType, "buy", "sell", "buy-or-sell", "sell-all", "select-amount")) continue;
            ConfigurationSection section = ConfigManager.configManager.getSection("menu.click-event-actions." + actionType);
            if (section == null || section.getBoolean("buy-only", false) && item.getBuyPrice().empty
                    || section.getBoolean("sell-only", false) && item.getSellPrice().empty) continue;
            builder.action(DialogAction.of("click_" + type.name(), section.getString("display-name", actionType), response -> {
                ObjectAction action = new ObjectAction(section, item);
                action.runAllActions(new ObjectThingRun(player, type, buyAmount, sellAmount));
                if (action.getLastTradeStatus() != null && action.getLastTradeStatus().getStatus() != ProductTradeStatus.Status.DONE) {
                    item.getFailAction().runAllActions(new ObjectThingRun(player, type));
                }
            }));
        }
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
        }
        catch (Throwable e) {
            realAmount = 1;
        }
        return realAmount;
    }
}
