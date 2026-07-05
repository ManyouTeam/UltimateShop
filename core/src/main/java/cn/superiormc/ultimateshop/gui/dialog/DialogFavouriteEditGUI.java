package cn.superiormc.ultimateshop.gui.dialog;

import cn.superiormc.ultimateshop.gui.DialogGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteResultButton;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;

class DialogFavouriteEditGUI extends DialogGUI {

    private final ObjectFavouriteMenu menu;

    private final ObjectFavouriteResultButton result;

    DialogFavouriteEditGUI(Player player, ObjectFavouriteMenu menu, ObjectFavouriteResultButton result) {
        super(player);
        this.menu = menu;
        this.result = result;
    }

    @Override
    public void constructGUI() {
        String itemName = result.getItem().getDisplayName(player);
        DialogView.Builder builder = DialogView.builder(getDialogText("favourite-edit.title",
                "item-name", itemName));
        builder.body(getDialogText("favourite-edit.content",
                "item-name", itemName, "index", String.valueOf(result.getIndex() + 1)));
        builder.action(action("forward", () -> move(-1)));
        builder.action(action("backward", () -> move(1)));
        builder.action(action("remove", this::remove));
        builder.action(action("back", this::reopen));
        dialog = builder.build();
    }

    private DialogAction action(String id, Runnable runnable) {
        return DialogAction.of(id, getDialogText("favourite-edit.buttons." + id), response -> runnable.run());
    }

    private void move(int offset) {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        if (cache != null) cache.moveFavouriteProduct(menu.getName(), result.getIndex(), result.getIndex() + offset);
        reopen();
    }
    private void remove() {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        if (cache != null && cache.removeFavouriteProduct(menu.getName(), result.getItem()))
            LanguageManager.languageManager.sendStringText(player, "favourite-removed", "item",
                    result.getItem().getDisplayName(player), "menu", menu.getName());
        reopen();
    }

    private void reopen() {
        new DialogFavouriteGUI(player, menu, true, true).openGUI(true);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }
}
