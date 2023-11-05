package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

public class FormBuyOrSellGUI extends FormGUI {

    private ObjectItem item;

    private FormType mode;

    public FormBuyOrSellGUI(Player owner, ObjectItem item, FormType mode) {
        super(owner);
        this.item = item;
        this.mode = mode;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        PlayerCache tempVal1 = CacheManager.cacheManager.playerCacheMap.get(owner.getPlayer());
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.player-not-found",
                    "player",
                    owner.getPlayer().getName());
            return;
        }
        CustomForm.Builder tempVal2 = CustomForm.builder();

        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.title"))
                .replace("{item-name}", item.getDisplayName(getOwner().getPlayer())));

        tempVal2.input(TextUtil.parse(
                        ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.buttons.amount.name")),
                getButtonTab());

        tempVal2.validResultHandler(response -> {
            doThing(response.next());
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
                        owner.getPlayer(),
                        !b,
                        false,
                        true,
                        ConfigManager.configManager.getInt("menu.select-more.max-amount", 64));
            }
            return;
        }
        int realAmount = 0;
        try {
            realAmount = Integer.parseInt(amount);
            if (realAmount < 1) {
                realAmount = 1;
            }
            else if (realAmount > ConfigManager.configManager.getInt
                    ("menu.select-more.max-amount", 64)) {
                realAmount = ConfigManager.configManager.getInt
                        ("menu.select-more.max-amount", 64);
            }
        }
        catch (Exception e) {
            realAmount = 1;
        }
        if (mode == FormType.BUY) {
            if (!item.getBuyPrice().empty) {
                BuyProductMethod.startBuy(item.getShop(),
                        item.getProduct(),
                        owner.getPlayer(),
                        !b,
                        false,
                        realAmount);
            }
        }
        else {
            if (!item.getSellPrice().empty) {
                SellProductMethod.startSell(item.getShop(),
                        item.getProduct(),
                        owner.getPlayer(),
                        !b,
                        false,
                        realAmount);
            }
        }
    }
}
