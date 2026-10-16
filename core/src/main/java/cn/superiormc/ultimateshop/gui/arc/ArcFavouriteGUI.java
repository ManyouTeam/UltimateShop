package cn.superiormc.ultimateshop.gui.arc;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEditModeButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEmptyButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

final class ArcFavouriteGUI extends ArcCommonGUI {

    private final ObjectFavouriteMenu favouriteMenu;

    private boolean editing;

    ArcFavouriteGUI(Player player, ObjectFavouriteMenu menu, boolean bypass, boolean editing, ArcMenuRuntime runtime) {
        super(player, menu, bypass, runtime);
        this.favouriteMenu = menu;
        this.editing = editing;
    }

    @Override
    protected void prepareMenu() {
        super.prepareMenu();
        title = TextUtil.withPAPI(CommonUtil.parseLang(player, favouriteMenu.getString("title", "Favourite")), player);
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        Map<FavouriteProductReference, ObjectItem> products = cache == null
            ? Collections.emptyMap() : cache.getResolvedFavouriteProducts(favouriteMenu.getName());
        int amount = products.size();
        int displayed = 0;
        int size = favouriteMenu.getInt("size", 54);
        for (Map.Entry<FavouriteProductReference, ObjectItem> entry : products.entrySet()) {
            if (displayed >= favouriteMenu.getResultSlots().size()) {
                break;
            }
            int slot = favouriteMenu.getResultSlots().get(displayed);
            ObjectFavouriteResultButton button = new ObjectFavouriteResultButton(entry.getValue(), displayed,
                editing ? favouriteMenu.getEditingResultLore() : favouriteMenu.getResultLore(), editing);
            menuButtons.put(slot, button);
            menuItems.put(slot, button.getDisplayItem(player, 1).getItemStack());
            displayed++;
        }
        ObjectFavouriteEmptyButton emptyButton = favouriteMenu.getEmptyButton();
        if (emptyButton != null) {
            for (int i = displayed; i < favouriteMenu.getResultSlots().size(); i++) {
                int slot = favouriteMenu.getResultSlots().get(i);
                menuButtons.put(slot, emptyButton);
                menuItems.put(slot, emptyButton.buildDisplayItem(player, amount, i + 1));
            }
        }
        ObjectFavouriteEditModeButton editButton = favouriteMenu.getEditModeButton();
        int editSlot = favouriteMenu.getEditModeSlot();
        if (editButton != null && editSlot >= 0 && editSlot < size) {
            menuButtons.put(editSlot, editButton);
            menuItems.put(editSlot, editButton.getDisplayItem(player, editing, amount).getItemStack());
        }
    }

    @Override
    protected ObjectItem product(int slot) {
        if (editing) {
            return null;
        }
        return buttons.get(slot) instanceof ObjectFavouriteResultButton result ? result.getItem() : super.product(slot);
    }

    @Override
    protected boolean isProductCard(int slot) {
        return super.isProductCard(slot) || buttons.get(slot) instanceof ObjectFavouriteResultButton;
    }

    @Override
    protected void dispatchClick(int slot, ClickType type) {
        if (slot == favouriteMenu.getEditModeSlot() && favouriteMenu.getEditModeButton() != null) {
            editing = !editing;
            return;
        }
        AbstractButton button = buttons.get(slot);
        if (button instanceof ObjectFavouriteResultButton result) {
            if (editing) {
                handleEdit(type, result.getIndex(), result.getItem());
            } else {
                result.getItem().clickEvent(type, player);
            }
            return;
        }
        if (button instanceof ObjectFavouriteEmptyButton || button instanceof ObjectFavouriteEditModeButton) {
            return;
        }
        super.dispatchClick(slot, type);
    }

    private void handleEdit(ClickType type, int index, ObjectItem item) {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        if (cache == null) {
            return;
        }
        if (type == ClickType.LEFT) {
            cache.moveFavouriteProduct(favouriteMenu.getName(), index, index - 1);
        } else if (type == ClickType.RIGHT) {
            cache.moveFavouriteProduct(favouriteMenu.getName(), index, index + 1);
        } else if (type == ClickType.DROP || type == ClickType.CONTROL_DROP || type == ClickType.MIDDLE) {
            if (cache.removeFavouriteProduct(favouriteMenu.getName(), item)) {
                LanguageManager.languageManager.sendStringText(player, "favourite-removed",
                    "item", item.getDisplayName(player), "menu", favouriteMenu.getName());
            }
        }
    }

    private boolean isEditing(int slot) {
        return buttons.get(slot) instanceof ObjectFavouriteResultButton favourite && favourite.isEditing();
    }

    @Override
    protected double cardActionHeight(int slot) {
        return isEditing(slot) ? number("cards.button-height", 27, 18, 36)
            + number("cards.buttons.inset-bottom", 6, 3, 16) : 0;
    }

    @Override
    protected void renderCardActions(int slot, double x, double y, double width, double height) {
        if (!isEditing(slot)) {
            return;
        }
        String[] keys = {"forward", "backward", "remove"};
        String[] fallbacks = {"Move Forward", "Move Backward", "Remove"};
        ClickType[] clicks = {ClickType.LEFT, ClickType.RIGHT, ClickType.DROP};
        double inset = number("cards.buttons.inset-x", 4, 2, 16);
        double gap = number("cards.buttons.gap", 3, 2, 12);
        double buttonWidth = (width - inset * 2 - gap * 2) / 3;
        double buttonHeight = number("cards.button-height", 27, 18, 36);
        for (int i = 0; i < keys.length; i++) {
            ClickType click = clicks[i];
            String label = ConfigManager.configManager.getMenuRendererText(
                getMenu().getConfig(), player, "form", "favourite-edit.buttons." + keys[i], fallbacks[i]);
            button("slot:" + slot + ":edit:" + keys[i],
                x + inset + i * (buttonWidth + gap),
                y + height - buttonHeight - number("cards.buttons.inset-bottom", 6, 3, 16),
                buttonWidth, buttonHeight,
                text("favourite-edit." + keys[i], label), List.of(label),
                type -> click(slot, click));
        }
    }
}
