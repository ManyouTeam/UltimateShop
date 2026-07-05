package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.LinkedHashMap;
import java.util.Map;

public class DialogShopGUI extends DialogGUI {

    private final ObjectShop shop;
    private final ObjectMenu menu;
    private final boolean bypass;

    public DialogShopGUI(Player player, ObjectShop shop, ObjectMenu menu, boolean bypass) {
        super(player);
        this.shop = shop;
        this.menu = menu;
        this.bypass = bypass;
    }

    @Override
    public void constructGUI() {
        ObjectCache playerCache = CacheManager.cacheManager.getObjectCache(player);
        if (playerCache == null) {
            LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", player.getName());
            return;
        }
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        for (ObjectItem item : shop.getProductList()) {
            ObjectUseTimesCache cache = playerCache.getUseTimesCache().get(item);
            if (cache != null) {
                cache.refreshTimes();
            }
            cache = CacheManager.cacheManager.serverCache.getUseTimesCache().get(item);
            if (cache != null) {
                cache.refreshTimes();
            }
        }
        Map<Integer, AbstractButton> source = menu.getMenu(MenuSender.of(player));
        Map<Integer, AbstractButton> ordered = new LinkedHashMap<>();
        source.forEach((slot, button) -> {
            if (button instanceof ObjectItem) {
                ordered.put(slot, button);
        } });
        source.forEach((slot, button) -> {
            if (!(button instanceof ObjectItem)) {
                ordered.put(slot, button);
        } });

        String title = menu.getString("title", shop.getShopDisplayName())
                .replace("{shop-name}", shop.getShopDisplayName()).replace("{shop-id}", shop.getShopName());
        DialogView.Builder builder = DialogView.builder(title);
        String content = menu.getString("dialog.content", null);
        if (content != null && !content.isEmpty()) {
            builder.body(content);
        }
        builder.buttonWidth(menu.getInt("dialog.button-width", 150));
        builder.columns(menu.getInt("dialog.columns", 2));
        ordered.forEach((slot, button) -> addButton(builder, slot, button));
        dialog = builder.build();
    }

    private void addButton(DialogView.Builder builder, int slot, AbstractButton button) {
        ObjectDisplayItemStack display = button.getDisplayItem(player, 1);
        DialogAction action = display.parseToDialogButton("slot_" + slot, response -> {
            if (button instanceof ObjectItem item) {
                new DialogInfoGUI(player, item).openGUI(true);
            } else {
                button.clickEvent(ClickType.LEFT, player);
            }
        });
        if (action != null) {
            builder.action(action);
        }
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
