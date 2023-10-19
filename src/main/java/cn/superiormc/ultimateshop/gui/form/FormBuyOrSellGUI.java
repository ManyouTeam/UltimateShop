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

    private FormType mode = FormType.UNKNOWN;

    public FormBuyOrSellGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
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

        // buy
        if (!item.getBuyPrice().empty) {
            this.mode = FormType.BUY;
        }

        // sell
        if (!item.getSellPrice().empty) {
            if (this.mode == FormType.BUY) {
                tempVal2.toggle(TextUtil.parse(
                        ConfigManager.configManager.getString("menu.bedrock.buy-or-sell")));
            }
            else {
                this.mode = FormType.SELL;
            }
        }

        tempVal2.input(TextUtil.parse(
                ConfigManager.configManager.getString("menu.bedrock.amount")),
                TextUtil.parse(
                        ConfigManager.configManager.getString("menu.bedrock.amount-tip")));

        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString("menu.bedrock.title"))
                .replace("{item-name}", item.getDisplayName(getOwner().getPlayer())));
        tempVal2.validResultHandler(response -> {
            doThing(response.next(), response.next());
        });
        form = tempVal2.build();
    }

    private void doThing(boolean buyOrSell, String amount) {
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        if (amount.equals("all")) {
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
        }
        catch (Exception e) {
            realAmount = 1;
        }
        if (!buyOrSell) {
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
