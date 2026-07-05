package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
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

import java.util.ArrayList;
import java.util.List;

public class DialogInfoGUI extends DialogGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private final String amount;

    public DialogInfoGUI(Player player, ObjectItem item) {
        this(player, item, "1");
    }

    public DialogInfoGUI(Player player, ObjectItem item, String amount) {
        super(player);
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.amount = amount;
    }

    @Override
    public void constructGUI() {
        if (CacheManager.cacheManager.getObjectCache(player) == null) {
            LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", player.getName());
            return;
        }
        String itemName = item.getDisplayName(player);
        String title = getDialogText("info.title", "item-name", itemName, "amount", amount);
        DialogView.Builder builder = DialogView.builder(title);
        List<String> content = new ArrayList<>();
        if (item.getDisplayItem(player).hasItemMeta()) {
            List<String> lore = UltimateShop.methodUtil.getItemLore(item.getDisplayItem(player).getItemMeta());
            if (lore != null) content.addAll(lore);
        }
        int nowAmount = Math.max(1, Math.min(Integer.parseInt(amount), menu.getSection().getInt("max-amount", 64)));
        content.addAll(ModifyDisplayItem.getModifiedLore(player, nowAmount, item, false, true, "general"));
        if (!content.isEmpty()) {
            builder.body(String.join("\n", content));
        }
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        if (!item.getBuyPrice().empty) {
            builder.action(DialogAction.of("buy",
                    getDialogText("info.buttons.buy", "item-name", itemName), response -> BuyProductMethod.startBuy(item, player, !b, false, nowAmount)));
        }
        if (!item.getSellPrice().empty) {
            builder.action(DialogAction.of("sell", getDialogText("info.buttons.sell", "item-name", itemName),
                    response -> SellProductMethod.startSell(item, player, !b, false, nowAmount)));
            if (ConfigManager.configManager.containsClickAction("sell-all") && item.isEnableSellAll()) {
                builder.action(DialogAction.of("sell_all", getDialogText("info.buttons.sell-all", "item-name", itemName),
                        response -> SellProductMethod.startSell(item, player, !b, false, true,
                                menu.getSection().getInt("max-amount", 64))));
            }
        }
        if (item.getBuyMore() && ConfigManager.configManager.containsClickAction("select-amount")) {
            builder.action(DialogAction.of("amount", getDialogText("info.buttons.buy-more", "item-name", itemName),
                    response -> new DialogBuyMoreGUI(player, item).openGUI(true)));
        }
        addCustomActions(builder);
        builder.action(DialogAction.of("back", getDialogText("info.buttons.back"), response ->
                new DialogShopGUI(player, item.getShopObject(), item.getShopObject().getShopMenuObject(), true).openGUI(true)));
        dialog = builder.build();
    }

    private void addCustomActions(DialogView.Builder builder) {
        for (ClickType type : ClickType.values()) {
            String actionType = ConfigManager.configManager.getClickAction(type, item);
            if (CommonUtil.containsAnyString(actionType, "buy", "sell", "buy-or-sell", "sell-all", "select-amount")) continue;
            ConfigurationSection section = ConfigManager.configManager.getSection("menu.click-event-actions." + actionType);
            if (section == null || section.getBoolean("buy-only", false) && item.getBuyPrice().empty
                    || section.getBoolean("sell-only", false) && item.getSellPrice().empty) continue;
            builder.action(DialogAction.of("click_" + type.name(), section.getString("display-name", actionType), response -> {
                ObjectAction action = new ObjectAction(section, item);
                action.runAllActions(new ObjectThingRun(player, type));
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
}
