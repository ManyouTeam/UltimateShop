package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormInfoGUI extends FormGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private final String amount;

    public FormInfoGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.amount = "1";
        constructGUI();
    }

    public FormInfoGUI(Player owner, ObjectItem item, String amount) {
        super(owner);
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.amount = amount;
        constructGUI();
    }

    @Override
    public void constructGUI() {
        ObjectCache tempVal1 = CacheManager.cacheManager.getObjectCache(player);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.player-not-found",
                    "player",
                    player.getName());
            return;
        }
        SimpleForm.Builder tempVal2 = SimpleForm.builder();

        tempVal2.title(TextUtil.parse(player, ConfigManager.configManager.getString(player, "menu.bedrock.info.title", "Shop",
                        "item-name", item.getDisplayName(player),
                        "amount", amount)));
        List<String> content = new ArrayList<>();
        if (item.getDisplayItem(player).hasItemMeta() && item.getDisplayItem(player).getItemMeta().hasLore()) {
            content.addAll(UltimateShop.methodUtil.getItemLore(item.getDisplayItem(player).getItemMeta()));
            content.add(" ");
        }
        content.addAll(ModifyDisplayItem.getModifiedLore(player,
                getAmount(),
                item,
                false,
                true,
                "general"
        ));
        tempVal2.content(TextUtil.parse(bedrockTransfer(content)));
        String itemName = item.getDisplayName(player);
        // 购买
        ButtonComponent buy = ButtonComponent.of(TextUtil.parse(player, ConfigManager.configManager.getString(player,
                "menu.bedrock.info.buttons.buy", "Buy", "item-name", itemName)));
        // 回收
        ButtonComponent sell = ButtonComponent.of(TextUtil.parse(player, ConfigManager.configManager.getString(player,
                "menu.bedrock.info.buttons.sell", "Sell", "item-name", itemName)));
        // 一键回收
        ButtonComponent sellAll = ButtonComponent.of(TextUtil.parse(player, ConfigManager.configManager.getString(player,
                "menu.bedrock.info.buttons.sell-all", "Sell All", "item-name", itemName)));
        // 选择数量
        ButtonComponent buyMore = ButtonComponent.of(TextUtil.parse(player, ConfigManager.configManager.getString(player,
                "menu.bedrock.info.buttons.buy-more", "Buy More", "item-name", itemName)));
        Map<ClickType, ButtonComponent> clickEvents = new HashMap<>();
        for (ClickType type : ClickType.values()) {
            String clickType = ConfigManager.configManager.getClickAction(type, item);
            if (CommonUtil.containsAnyString(clickType, "buy", "sell", "buy-or-sell", "sell-all", "select-amount")) {
                continue;
            }
            ConfigurationSection tempVal3 = ConfigManager.configManager.getSection("menu.click-event-actions." + clickType);
            if (tempVal3 == null) {
                continue;
            }
            if (tempVal3.getBoolean("buy-only", false) && item.getBuyPrice().empty) {
                continue;
            }
            if (tempVal3.getBoolean("sell-only", false) && item.getSellPrice().empty) {
                continue;
            }
            ButtonComponent clickEvent = ButtonComponent.of(TextUtil.parse(player, tempVal3.getString("display-name", clickType)));
            clickEvents.put(type, clickEvent);
        }
        // 返回
        ButtonComponent back = ButtonComponent.of(TextUtil.parse(player, ConfigManager.configManager.getString(player,
                "menu.bedrock.info.buttons.back", "Back"
        )));
        if (!item.getBuyPrice().empty) {
            tempVal2.button(buy);
        }
        if (!item.getSellPrice().empty) {
            tempVal2.button(sell);
            if (ConfigManager.configManager.containsClickAction("sell-all") && item.isEnableSellAll()) {
                tempVal2.button(sellAll);
            }
        }
        if (item.getBuyMore() && ConfigManager.configManager.containsClickAction("select-amount")) {
            tempVal2.button(buyMore);
        }
        for (ButtonComponent buttonComponent : clickEvents.values()) {
            tempVal2.button(buttonComponent);
        }
        tempVal2.button(back);
        tempVal2.validResultHandler(response -> {
            removeOpenGUIStatus();
            if (response.clickedButton().equals(buy)) {
                doThing(true);
            } else if (response.clickedButton().equals(sell)) {
                doThing(false);
            } else if (response.clickedButton().equals(buyMore)) {
                FormBuyMoreGUI buyOrSellGUI = new FormBuyMoreGUI(player, item);
                buyOrSellGUI.openGUI(true);
            } else if (response.clickedButton().equals(back)) {
                FormShopGUI shopGUI = new FormShopGUI(player, item.getShopObject(), item.getShopObject().getShopMenuObject(), true);
                shopGUI.openGUI(true);
            } else if (response.clickedButton().equals(sellAll)) {
                SellProductMethod.startSell(item,
                        player,
                        !ConfigManager.configManager.getBoolean("placeholder.click.enabled"),
                        false,
                        true,
                        menu.getSection().getInt("max-amount", 64));
                if (ConfigManager.configManager.getBoolean("menu.bedrock.not-auto-close")) {
                    ShopGUI.openGUI(player, item.getShopObject(), true, true);
                }
            }
            for (ClickType type : clickEvents.keySet()) {
                ButtonComponent buttonComponent = clickEvents.get(type);
                if (response.clickedButton().equals(buttonComponent)) {
                    String clickType = ConfigManager.configManager.getClickAction(type, item);
                    ObjectAction action = new ObjectAction(
                            ConfigManager.configManager.getSection("menu.click-event-actions." + clickType),
                            item);
                    action.runAllActions(new ObjectThingRun(player, type));
                    if (action.getLastTradeStatus() != null && action.getLastTradeStatus().getStatus() != ProductTradeStatus.Status.DONE) {
                        item.getFailAction().runAllActions(new ObjectThingRun(player, type));
                    }
                }
            }
        });
        tempVal2.closedOrInvalidResultHandler(response -> removeOpenGUIStatus());
        form = tempVal2.build();
    }

    private String bedrockTransfer(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    public ObjectItem getItem() {
        return item;
    }

    public void doThing(boolean buyOrSell) {
        removeOpenGUIStatus();
        if (amount == null) {
            return;
        }
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        if (!buyOrSell && amount.equals("all")) {
            if (!item.getSellPrice().empty) {
                SellProductMethod.startSell(item,
                        player,
                        !b,
                        false,
                        true,
                        menu.getSection().getInt("max-amount", 64));
            }
            return;
        }

        if (buyOrSell) {
            if (!item.getBuyPrice().empty) {
                BuyProductMethod.startBuy(item,
                        player,
                        !b,
                        false,
                        getAmount());
            }
        } else {
            if (!item.getSellPrice().empty) {
                SellProductMethod.startSell(item,
                        player,
                        !b,
                        false,
                        getAmount());
            }
        }
        if (ConfigManager.configManager.getBoolean("menu.bedrock.not-auto-close")) {
            ShopGUI.openGUI(player, item.getShopObject(), true, true);
        }
    }

    public int getAmount() {
        int realAmount;
        try {
            realAmount = Integer.parseInt(amount);
            if (realAmount < 1) {
                realAmount = 1;
            } else if (realAmount > menu.getSection().getInt("max-amount", 64)) {
                realAmount = menu.getSection().getInt("max-amount", 64);
            }
        }
        catch (Throwable e) {
            realAmount = 1;
        }
        return realAmount;
    }
}
