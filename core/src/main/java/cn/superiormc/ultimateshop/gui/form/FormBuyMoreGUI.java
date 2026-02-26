package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;

public class FormBuyMoreGUI extends FormGUI {

    private final ObjectItem item;

    public FormBuyMoreGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
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
        CustomForm.Builder tempVal2 = CustomForm.builder();

        tempVal2.title(TextUtil.parse(player, ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.buy-or-sell.title", "Buy More Menu",
                "item-name", item.getDisplayName(player))));

        tempVal2.input(TextUtil.parse(player, ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.buy-or-sell.buttons.amount.name")), getButtonTab());
        tempVal2.validResultHandler(response -> {
            FormInfoGUI infoGUI = new FormInfoGUI(player, item, response.next());
            infoGUI.openGUI(true);
        });
        tempVal2.closedOrInvalidResultHandler(response -> removeOpenGUIStatus());
        form = tempVal2.build();
    }

    private String getButtonTab() {
        return TextUtil.parse(player, ConfigManager.configManager.getStringOrDefault("menu.bedrock.buy-or-sell.buttons.amount.buy-tip",
                "menu.bedrock.buy-or-sell.buttons.amount.tip", ""));
    }
}
