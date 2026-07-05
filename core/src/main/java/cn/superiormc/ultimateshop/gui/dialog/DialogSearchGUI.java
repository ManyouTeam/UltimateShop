package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectSearchResultButton;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectSearchMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DialogSearchGUI extends DialogGUI {

    private final ObjectSearchMenu menu;

    private final boolean bypass;

    private final String searchKeywords;

    public DialogSearchGUI(Player player, ObjectSearchMenu menu, boolean bypass) {
        this(player, menu, bypass, "");
    }

    private DialogSearchGUI(Player player, ObjectSearchMenu menu, boolean bypass, String keyword) {
        super(player);
        this.menu = menu;
        this.bypass = bypass;
        this.searchKeywords = keyword;
    }

    @Override
    public void constructGUI() {
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        DialogView.Builder builder = DialogView.builder(CommonUtil.parseLang(player, menu.getString("title", "")));
        String content = menu.getString("dialog.content", null);
        if (content != null && !content.isEmpty()) {
            builder.body(content);
        }
        builder.buttonWidth(menu.getInt("dialog.button-width", 150));
        builder.columns(menu.getInt("dialog.columns", 2));
        builder.input(DialogInput.text("keyword", getDialogText("search.input"), searchKeywords));
        builder.action(DialogAction.of("search", getDialogText("search.buttons.search"), response ->
                new DialogSearchGUI(player, menu, true,
                        response.getText("keyword") == null ? "" : response.getText("keyword")).openGUI(true)));
        if (!searchKeywords.trim().isEmpty()) addResults(builder);
        for (Map.Entry<Integer, AbstractButton> entry : menu.getMenu(MenuSender.of(player)).entrySet()) {
            AbstractButton button = entry.getValue();
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            DialogAction action = display.parseToDialogButton("slot_" + entry.getKey(),
                    response -> button.clickEvent(ClickType.LEFT, player));
            if (action != null) builder.action(action);
        }
        dialog = builder.build();
    }

    private void addResults(DialogView.Builder builder) {
        List<ObjectItem> matched = ShopHelper.getTargetItems(searchKeywords, player);
        if (matched == null) matched = Collections.emptyList();
        int limit = Math.min(matched.size(), menu.getResultSlots().size());
        for (int i = 0; i < limit; i++) {
            ObjectSearchResultButton result = new ObjectSearchResultButton(matched.get(i), menu.getResultLore());
            ObjectDisplayItemStack display = result.getDisplayItem(player, 1);
            ObjectItem item = matched.get(i);
            DialogAction action = display.parseToDialogButton("result_" + i,
                    response -> new DialogInfoGUI(player, item).openGUI(true));
            if (action != null) builder.action(action);
        }
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
