package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;

import java.util.List;

public class FormInfoGUI extends FormGUI {

    private ObjectItem item;

    public FormInfoGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        PlayerCache tempVal1 = CacheManager.cacheManager.getPlayerCache(player.getPlayer());
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player.getPlayer(),
                    "error.player-not-found",
                    "player",
                    player.getPlayer().getName());
            return;
        }
        SimpleForm.Builder tempVal2 = SimpleForm.builder();

        tempVal2.title(TextUtil.parse(ConfigManager.configManager.getString("menu.bedrock.info.title"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));
        tempVal2.content(bedrockTransfer(ModifyDisplayItem.getModifiedLore(player.getPlayer(),
                1,
                item,
                false,
                true,
                "general"
        )));
        // 购买
        ButtonComponent buy = ButtonComponent.of(TextUtil.parse(ConfigManager.configManager.getString(
                "menu.bedrock.info.buttons.buy"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));
        // 回收
        ButtonComponent sell = ButtonComponent.of(TextUtil.parse(ConfigManager.configManager.getString(
                        "menu.bedrock.info.buttons.sell"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));

        if (!item.getBuyPrice().empty) {
            tempVal2.button(buy);
        }
        if (!item.getSellPrice().empty) {
            tempVal2.button(sell);
        }

        tempVal2.validResultHandler(response -> {
            if (response.clickedButton().equals(buy)) {
                if (item.getBuyMore()) {
                    FormBuyOrSellGUI buyOrSellGUI = new FormBuyOrSellGUI(player, item, FormType.BUY);
                    buyOrSellGUI.openGUI();
                }
                else {
                    doThing(true);
                }
            }
            else if (response.clickedButton().equals(sell)) {
                if (item.getBuyMore()) {
                    FormBuyOrSellGUI buyOrSellGUI = new FormBuyOrSellGUI(player, item, FormType.SELL);
                    buyOrSellGUI.openGUI();
                }
                else {
                    doThing(false);
                }
            }
        });
        tempVal2.closedResultHandler(response -> {
            removeOpenGUIStatus();
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

    public void doThing(boolean buyOrSell) {
        boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
        if (!buyOrSell) {
            if (!item.getBuyPrice().empty) {
                BuyProductMethod.startBuy(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        1);
            }
        }
        else {
            if (!item.getSellPrice().empty) {
                SellProductMethod.startSell(item.getShop(),
                        item.getProduct(),
                        player.getPlayer(),
                        !b,
                        false,
                        1);
            }
        }
    }
}
