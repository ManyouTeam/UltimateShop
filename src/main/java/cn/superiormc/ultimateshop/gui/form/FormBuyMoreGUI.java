package cn.superiormc.ultimateshop.gui.form;

import cn.superiormc.ultimateshop.cache.PlayerCache;
import cn.superiormc.ultimateshop.gui.FormGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
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

        tempVal2.title(TextUtil.parse(player, ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.title"))
                .replace("{item-name}", item.getDisplayName(getPlayer().getPlayer())));

        tempVal2.input(TextUtil.parse(player,
                        ConfigManager.configManager.getString("menu.bedrock.buy-or-sell.buttons.amount.name")),
                getButtonTab());
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
