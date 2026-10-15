package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
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

    private String searchKeywords;

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
        dialog = null;
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        DialogView.Builder builder = DialogView.builder(CommonUtil.parseLang(player, menu.getString("title", "")));
        builder.keepOpenAfterAction(true);
        String content = menu.getString("content", null);
        if (content != null && !content.isEmpty()) {
            builder.body(content);
        }
        builder.buttonWidth(menu.getInt("ui.renderers.dialog.button-width", 150));
        builder.columns(menu.getInt("ui.renderers.dialog.columns", 2));
        String dialogLayout = ConfigManager.configManager.getDialogLayout(menu.getConfig());
        boolean itemActionLayout = DialogView.Layout.fromConfig(dialogLayout) == DialogView.Layout.ITEM_ACTION_LIST;
        builder.layout(dialogLayout);
        builder.input(DialogInput.text("keyword", ConfigManager.configManager.getMenuRendererText(
            getMenu() == null ? null : getMenu().getConfig(),
            player, "dialog", "search.input", ""), searchKeywords));
        builder.action(DialogAction.of("search", ConfigManager.configManager.getMenuRendererText(
            getMenu() == null ? null : getMenu().getConfig(),
            player, "dialog", "search.buttons.search", ""), response -> {
            String keyword = response.getText("keyword");
            searchKeywords = keyword == null ? "" : keyword;
        }));
        if (!searchKeywords.trim().isEmpty()) {
            addResults(builder, itemActionLayout);
        }
        for (Map.Entry<Integer, AbstractButton> entry : menu.getMenu(MenuSender.of(player, MenuSender.dialogPresentation(ConfigManager.configManager.getDialogLayout(menu.getConfig())))).entrySet()) {
            AbstractButton button = entry.getValue();
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            DialogAction action = display.parseToDialogButton("slot_" + entry.getKey(), button.getShowSprite(),
                    response -> {
                        closeGUI();
                        button.clickEvent(ClickType.LEFT, player);
                    });
            if (action != null) {
                if (button.hasCloseAction()) {
                    builder.action(action);
                } else if (!button.usesItemActionDialogLayout(dialogLayout)) {
                    builder.action(action);
                } else {
                    builder.layout("item-action-list");
                    builder.itemAction(display.getItemStack(), action);
                }
            }
        }
        dialog = builder.build();
    }

    private void addResults(DialogView.Builder builder, boolean itemActionLayout) {
        List<ObjectItem> matched = ShopHelper.getTargetItems(searchKeywords, player);
        if (matched == null) {
            matched = Collections.emptyList();
        }
        int limit = Math.min(matched.size(), menu.getResultSlots().size());
        for (int i = 0; i < limit; i++) {
            ObjectSearchResultButton result = new ObjectSearchResultButton(matched.get(i), menu.getResultLore());
            ObjectDisplayItemStack display = result.getDisplayItem(player, 1);
            ObjectItem item = matched.get(i);
            DialogAction action = display.parseToDialogButton("result_" + i, result.getItem().getShowSprite(),
                    response -> {
                        closeGUI();
                        new DialogInfoGUI(player, item, null, () -> openGUI(true)).openGUI(true);
                    });
            if (action != null) {
                if (itemActionLayout) {
                    builder.itemAction(display.getItemStack(), action);
                } else {
                    builder.action(action);
                }
            }
        }
    }

    @Override
    public void updateGUI() {
        openGUI(true, false);
        if (dialog == null && MenuStatusManager.menuStatusManager.getOpeningGUI(player) == this) {
            closeGUI();
        }
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
