package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;

public class DialogBuyMoreGUI extends DialogGUI {
    private final ObjectItem item;

    public DialogBuyMoreGUI(Player player, ObjectItem item) {
        super(player);
        this.item = item;
    }

    @Override
    public void constructGUI() {
        if (CacheManager.cacheManager.getObjectCache(player) == null) {
            LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", player.getName());
            return;
        }
        int max = item.getBuyMoreMenu().getSection().getInt("max-amount", 64);
        String title = getDialogText("buy-more.title", "item-name", item.getDisplayName(player));
        String label = getDialogText("buy-more.input");
        String confirm = getDialogText("buy-more.buttons.confirm", "item-name", item.getDisplayName(player));
        DialogView.Builder builder = DialogView.builder(title);
        if (ConfigManager.configManager.getBoolean("menu.dialog.buy-more.display-item")) {
            builder.item(item.getDisplayItem(player));
        }
        dialog = builder.input(DialogInput.number("amount", label, 1, max, 1, 1))
                .action(DialogAction.of("confirm", confirm, response -> {
                    Float amount = response.getFloat("amount");
                    new DialogInfoGUI(player, item, String.valueOf(amount == null ? 1 : Math.max(1, amount.intValue()))).openGUI(true);
                }))
                .build();
    }

    @Override
    public ObjectMenu getMenu() { return item.getBuyMoreMenu(); }
}
