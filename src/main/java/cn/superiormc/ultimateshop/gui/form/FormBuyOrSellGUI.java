package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

public class FormBuyOrSellGUI extends FormGUI {

    private final ObjectItem item;

    private final FormType mode;

    private final ObjectMoreMenu menu;

    public FormBuyOrSellGUI(Player owner, ObjectItem item, FormType mode) {
        super(owner);
        this.item = item;
        this.mode = mode;
        this.menu = ObjectMoreMenu.moreMenus.get(item);
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
        CustomForm.Builder tempVal2 = CustomForm.builder();

        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.title"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));

        tempVal2.input(TextUtil.parse(
                        ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.buttons.amount.name")),
                getButtonTab());

        tempVal2.validResultHandler(response -> {
            removeOpenGUIStatus();
            doThing(response.next());
            if (ConfigManager.configManager.getBoolean("menu.bedrock.not-auto-close")) {
                OpenGUI.openShopGUI(player, item.getShopObject(), true, true);
            }
        });
        form = tempVal2.build();
    }

    private String getButtonTab() {
        if (mode == FormType.BUY) {
            return TextUtil.parse(
                    ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.buttons.amount.buy-tip"));
        }
        else {
            return TextUtil.parse(
                    ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.buttons.amount.sell-tip"));
        }
    }

    private void doThing(String amount) {
        if (amount == null) {
            return;
        }
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        if (mode == FormType.SELL && amount.equals("all")) {
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
        int realAmount = 0;
        try {
            realAmount = Integer.parseInt(amount);
            if (realAmount < 1) {
                realAmount = 1;
            }
            else if (realAmount > menu.getSection().getInt("max-amount", 64)) {
                realAmount = menu.getSection().getInt("max-amount", 64);
            }
        }
        catch (Exception e) {
            realAmount = 1;
        }
        if (mode == FormType.BUY) {
            if (!item.getBuyPrice().empty) {
                BuyProductMethod.startBuy(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        realAmount);
            }
        }
        else {
            if (!item.getSellPrice().empty) {
                SellProductMethod.startSell(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        realAmount);
            }
        }
    }
}
