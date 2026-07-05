package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.*;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.LinkedHashMap;
import java.util.Map;

public class DialogFavouriteGUI extends DialogGUI {

    private final ObjectFavouriteMenu menu;

    private final boolean bypass;

    private final boolean editing;

    public DialogFavouriteGUI(Player player, ObjectFavouriteMenu menu, boolean bypass) {
        this(player, menu, bypass, false);
    }

    public DialogFavouriteGUI(Player player, ObjectFavouriteMenu menu, boolean bypass, boolean editing) {
        super(player);
        this.menu = menu;
        this.bypass = bypass;
        this.editing = editing;
    }

    @Override
    public void constructGUI() {
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        Map<FavouriteProductReference, ObjectItem> products = cache == null ? new LinkedHashMap<>()
                : cache.getResolvedFavouriteProducts(menu.getName());
        DialogView.Builder builder = DialogView.builder(CommonUtil.parseLang(player, menu.getString("title", "")));
        String content = menu.getString("dialog.content", null);
        if (content != null && !content.isEmpty()) {
            builder.body(content);
        }
        builder.buttonWidth(menu.getInt("dialog.button-width", 150));
        builder.columns(menu.getInt("dialog.columns", 2));
        int index = 0;
        for (Map.Entry<FavouriteProductReference, ObjectItem> entry : products.entrySet()) {
            if (index >= menu.getResultSlots().size()) break;
            ObjectFavouriteResultButton result = new ObjectFavouriteResultButton(entry.getValue(), index,
                    editing ? menu.getEditingResultLore() : menu.getResultLore(), editing);
            addResult(builder, result);
            index++;
        }
        ObjectFavouriteEditModeButton edit = menu.getEditModeButton();
        if (edit != null) {
            ObjectDisplayItemStack display = edit.getDisplayItem(player, editing, products.size());
            DialogAction action = display.parseToDialogButton("edit_mode",
                    response -> new DialogFavouriteGUI(player, menu, true, !editing).openGUI(true));
            if (action != null) builder.action(action);
        }
        for (Map.Entry<Integer, AbstractButton> entry : menu.getMenu(MenuSender.of(player)).entrySet()) {
            AbstractButton button = entry.getValue();
            ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
            DialogAction action = display.parseToDialogButton("slot_" + entry.getKey(),
                    response -> button.clickEvent(ClickType.LEFT, player));
            if (action != null) builder.action(action);
        }
        dialog = builder.build();
    }

    private void addResult(DialogView.Builder builder, ObjectFavouriteResultButton result) {
        ObjectDisplayItemStack display = result.getDisplayItem(player, 1);
        DialogAction action = display.parseToDialogButton("result_" + result.getIndex(), response -> {
            if (editing) new DialogFavouriteEditGUI(player, menu, result).openGUI(true);
            else new DialogInfoGUI(player, result.getItem()).openGUI(true);
        });
        if (action != null) builder.action(action);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
