package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEditModeButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteEmptyButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectFavouriteResultButton;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.FavouriteProductReference;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectFavouriteMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class FavouriteGUI extends InvGUI {

    private final ObjectFavouriteMenu menu;

    private final boolean bypass;

    private boolean editing;

    private final Map<Integer, ObjectFavouriteResultButton> resultButtons = new LinkedHashMap<>();

    private final Map<Integer, AbstractButton> overlayButtons = new LinkedHashMap<>();

    private FavouriteGUI(Player owner, ObjectFavouriteMenu menu, boolean bypass) {
        super(owner);
        this.menu = menu;
        this.bypass = bypass;
        this.editing = false;
    }

    private FavouriteGUI(Player owner, ObjectFavouriteMenu menu, boolean bypass, boolean editing) {
        super(owner);
        this.menu = menu;
        this.bypass = bypass;
        this.editing = editing;
    }

    @Override
    public void constructGUI() {
        if (menu == null || menu.getConfig() == null) {
            return;
        }
        if (!bypass && !menu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    menu.getName());
            return;
        }

        title = TextUtil.withPAPI(menu.getString("title", "Favourite"), player);
        if (Objects.isNull(inv)) {
            inv = UltimateShop.methodUtil.createNewInv(player, menu.getInt("size", 54), title);
        }

        inv.clear();
        resultButtons.clear();
        overlayButtons.clear();

        menuButtons = menu.getMenu(MenuSender.of(player));
        menuItems = getMenuItems(player);
        for (Map.Entry<Integer, org.bukkit.inventory.ItemStack> entry : menuItems.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < inv.getSize()) {
                inv.setItem(entry.getKey(), entry.getValue());
            }
        }

        renderFavouriteItems();
        renderEditModeButton();
    }

    private void renderFavouriteItems() {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        Map<FavouriteProductReference, ObjectItem> favouriteProducts = cache == null
                ? Collections.emptyMap()
                : cache.getResolvedFavouriteProducts(menu.getName());

        int displayed = 0;
        for (Map.Entry<FavouriteProductReference, ObjectItem> entry : favouriteProducts.entrySet()) {
            if (displayed >= menu.getResultSlots().size()) {
                break;
            }
            int slot = menu.getResultSlots().get(displayed);
            ObjectFavouriteResultButton button = new ObjectFavouriteResultButton(entry.getValue(),
                    displayed,
                    editing ? menu.getEditingResultLore() : menu.getResultLore(),
                    editing);
            resultButtons.put(slot, button);
            inv.setItem(slot, button.getDisplayItem(player, 1).getItemStack());
            displayed++;
        }

        ObjectFavouriteEmptyButton emptyButton = menu.getEmptyButton();
        if (emptyButton == null) {
            return;
        }

        for (int i = displayed; i < menu.getResultSlots().size(); i++) {
            int slot = menu.getResultSlots().get(i);
            overlayButtons.put(slot, emptyButton);
            inv.setItem(slot, emptyButton.buildDisplayItem(player, favouriteProducts.size(), i + 1));
        }
    }

    private void renderEditModeButton() {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        int favouriteAmount = cache == null ? 0 : cache.getResolvedFavouriteProducts(menu.getName()).size();
        ObjectFavouriteEditModeButton button = menu.getEditModeButton();
        int slot = menu.getEditModeSlot();
        if (button == null || slot < 0 || slot >= inv.getSize()) {
            return;
        }
        overlayButtons.put(slot, button);
        inv.setItem(slot, button.getDisplayItem(player, editing, favouriteAmount).getItemStack());
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == menu.getEditModeSlot() && menu.getEditModeButton() != null) {
            editing = !editing;
            constructGUI();
            return true;
        }

        ObjectFavouriteResultButton resultButton = resultButtons.get(slot);
        if (resultButton != null) {
            if (editing) {
                if (handleEdit(type, resultButton.getIndex(), resultButton.getItem())) {
                    queueRefresh();
                }
                return true;
            }
            resultButton.getItem().clickEvent(type, player);
            queueRefresh();
            return true;
        }

        if (overlayButtons.containsKey(slot)) {
            return true;
        }

        AbstractButton normalButton = menuButtons.get(slot);
        if (normalButton != null) {
            normalButton.clickEvent(type, player);
            if (ConfigManager.configManager.getBooleanOrDefault("menu.shop.click-update", "menu.menu-update.click-update")) {
                constructGUI();
            } else {
                menuItems.put(slot, getMenuItem(player, slot));
                inv.setItem(slot, menuItems.get(slot));
            }
        }
        return true;
    }

    private boolean handleEdit(ClickType type, int index, ObjectItem item) {
        ObjectCache cache = CacheManager.cacheManager.getObjectCache(player);
        if (cache == null) {
            return false;
        }
        if (type == ClickType.LEFT) {
            return cache.moveFavouriteProduct(menu.getName(), index, index - 1);
        }
        if (type == ClickType.RIGHT) {
            return cache.moveFavouriteProduct(menu.getName(), index, index + 1);
        }
        if (type == ClickType.DROP || type == ClickType.CONTROL_DROP) {
            return cache.removeFavouriteProduct(menu.getName(), item);
        }
        return false;
    }

    private void queueRefresh() {
        SchedulerUtil.runTaskLater(() -> {
            if (inv != null && player.getOpenInventory().getTopInventory().equals(inv)) {
                constructGUI();
            }
        }, 1L);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }

    public static void openGUI(Player player, ObjectFavouriteMenu menu, boolean bypass, boolean reopen) {
        FavouriteGUI gui = new FavouriteGUI(player, menu, bypass);
        gui.openGUI(reopen);
    }

    public static void openGUI(Player player, ObjectFavouriteMenu menu, boolean bypass, boolean reopen, boolean editing) {
        FavouriteGUI gui = new FavouriteGUI(player, menu, bypass, editing);
        gui.openGUI(reopen);
    }
}
