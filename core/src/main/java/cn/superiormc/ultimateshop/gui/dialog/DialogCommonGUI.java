package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Map;

public class DialogCommonGUI extends DialogGUI {

    private final ObjectMenu menu;

    private final boolean bypass;

    public DialogCommonGUI(Player player, ObjectMenu menu, boolean bypass) {
        super(player);
        this.menu = menu;
        this.bypass = bypass;
    }

    @Override
    public void constructGUI() {
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        DialogView.Builder builder = DialogView.builder(menu.getString("title", ""));
        String content = menu.getString("dialog.content", null);
        if (content != null && !content.isEmpty()) {
            builder.body(content);
        }
        builder.buttonWidth(menu.getInt("dialog.button-width", 150));
        builder.columns(menu.getInt("dialog.columns", 2));
        for (Map.Entry<Integer, AbstractButton> entry : menu.getMenu(MenuSender.of(player)).entrySet()) {
            AbstractButton button = entry.getValue();
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            DialogAction action = display.parseToDialogButton("slot_" + entry.getKey(),
                    response -> button.clickEvent(ClickType.LEFT, player));
            if (action != null) builder.action(action);
        }
        dialog = builder.build();
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
