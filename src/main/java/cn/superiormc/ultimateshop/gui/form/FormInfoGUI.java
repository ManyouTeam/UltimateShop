package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.ArrayList;
import java.util.List;

public class FormInfoGUI extends FormGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private String amount;

    public FormInfoGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
        this.menu = ObjectMoreMenu.moreMenus.get(item);
        this.amount = "1";
        constructGUI();
    }

    public FormInfoGUI(Player owner, ObjectItem item, String amount) {
        super(owner);
        this.item = item;
        this.menu = ObjectMoreMenu.moreMenus.get(item);
        this.amount = amount;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        PlayerCache tempVal1 = CacheManager.cacheManager.getPlayerCache(player.getPlayer());
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.player-not-found",
                    "player",
                    player.getName());
            return;
        }
        SimpleForm.Builder tempVal2 = SimpleForm.builder();

        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString("menu.bedrock.info.title"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));
        List<String> content = new ArrayList<>();
        if (item.getDisplayItem(player).hasItemMeta() && item.getDisplayItem(player).getItemMeta().hasLore()) {
            content.addAll(item.getDisplayItem(player).getItemMeta().getLore());
            content.add(" ");
        }
        content.addAll(ModifyDisplayItem.getModifiedLore(player.getPlayer(),
                getAmount(),
                item,
                false,
                true,
                "general"
        ));
        tempVal2.content(bedrockTransfer(content));
        // 购买
        ButtonComponent buy = ButtonComponent.of(TextUtil.parse(ConfigManager.configManager.getString(
                "menu.bedrock.info.buttons.buy"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));
        // 回收
        ButtonComponent sell = ButtonComponent.of(TextUtil.parse(ConfigManager.configManager.getString(
                        "menu.bedrock.info.buttons.sell"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));
        ButtonComponent buyMore = ButtonComponent.of(TextUtil.parse(ConfigManager.configManager.getString(
                        "menu.bedrock.info.buttons.buy-more"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));
        if (!item.getBuyPrice().empty) {
            tempVal2.button(buy);
        }
        if (!item.getSellPrice().empty) {
            tempVal2.button(sell);
        }
        if (item.getBuyMore() && ConfigManager.configManager.containsClickAction("select-amount")) {
            tempVal2.button(buyMore);
        }
        tempVal2.validResultHandler(response -> {
            removeOpenGUIStatus();
            if (response.clickedButton().equals(buy)) {
                doThing(true);
            }
            else if (response.clickedButton().equals(sell)) {
                doThing(false);
            } else if (response.clickedButton().equals(buyMore)) {
                FormBuyOrSellGUI buyOrSellGUI = new FormBuyOrSellGUI(player, item);
                buyOrSellGUI.openGUI(true);
            }
        });
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
                SellProductMethod.startSell(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        true,
                        menu.getSection().getInt("max-amount", 64));
            }
            return;
        }

        if (buyOrSell) {
            if (!item.getBuyPrice().empty) {
                BuyProductMethod.startBuy(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        getAmount());
            }
        }
        else {
            if (!item.getSellPrice().empty) {
                SellProductMethod.startSell(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        getAmount());
            }
        }
        if (ConfigManager.configManager.getBoolean("menu.bedrock.not-auto-close")) {
            OpenGUI.openShopGUI(player, item.getShopObject(), true, true);
        }
    }

    public int getAmount() {
        int realAmount;
        try {
            realAmount = Integer.parseInt(amount);
            if (realAmount < 1) {
                realAmount = 1;
            }
            else if (realAmount > menu.getSection().getInt("max-amount", 64)) {
                realAmount = menu.getSection().getInt("max-amount", 64);
            }
        }
        catch (Throwable e) {
            realAmount = 1;
        }
        return realAmount;
    }
}
