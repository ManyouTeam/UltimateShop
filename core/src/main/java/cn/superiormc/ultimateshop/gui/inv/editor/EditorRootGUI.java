package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorManager;
import cn.superiormc.ultimateshop.editor.EditorScope;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorRootGUI extends InvGUI {

    private static final int SHOPS_SLOT = 11;
    private static final int MENUS_SLOT = 13;
    private static final int SHOP_MENU_TEMPLATES_SLOT = 15;
    private static final int BUY_MORE_MENUS_SLOT = 19;
    private static final int FAVOURITE_MENUS_SLOT = 21;
    private static final int SEARCH_MENUS_SLOT = 23;
    private static final int ITEM_SELL_MENUS_SLOT = 25;
    private static final int SETTINGS_SLOT = 30;
    private static final int BACK_SLOT = 31;
    private static final int CLOSE_SLOT = 32;

    private final boolean settings;

    private final Map<Integer, EditorScope> settingsScopes = new HashMap<>();

    public EditorRootGUI(Player owner, boolean settings) {
        super(owner);
        this.settings = settings;
    }

    @Override
    public void constructGUI() {
        title = settings ? EditorLang.text(player, "editor.root.settings.title", "Editor - Configuration")
                : EditorLang.text(player, "editor.root.title", "UltimateShop Editor");
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 36, title, this);
        }
        inv.clear();
        if (settings) {
            constructSettings();
            return;
        }
        inv.setItem(SHOPS_SLOT, EditorUtil.createItem(Material.CHEST,
            EditorLang.text(player, "editor.root.shops.name", "&eEdit Shops"),
            List.of(
            EditorLang.text(player, "editor.root.shops.desc", "&7Open shop yml files under &f/shops"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(MENUS_SLOT, EditorUtil.createItem(Material.BOOK,
            EditorLang.text(player, "editor.root.menus.name", "&eEdit Menus"),
            List.of(
            EditorLang.text(player, "editor.root.menus.desc", "&7Open menu yml files under &f/menus/common_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(SHOP_MENU_TEMPLATES_SLOT, EditorUtil.createItem(Material.WRITABLE_BOOK,
            EditorLang.text(player, "editor.root.shop-menu-templates.name", "&eEdit Shop Menu Templates"),
            List.of(
            EditorLang.text(player, "editor.root.shop-menu-templates.desc", "&7Open menu yml files under &f/menus/shop_menu_templates"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(BUY_MORE_MENUS_SLOT, EditorUtil.createItem(Material.CHEST_MINECART,
            EditorLang.text(player, "editor.root.buy-more-menus.name", "&eEdit Buy More Menus"),
            List.of(
            EditorLang.text(player, "editor.root.buy-more-menus.desc", "&7Open menu yml files under &f/menus/buy_more_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(FAVOURITE_MENUS_SLOT, EditorUtil.createItem(Material.NAME_TAG,
            EditorLang.text(player, "editor.root.favourite-menus.name", "&eEdit Favourite Menus"),
            List.of(
            EditorLang.text(player, "editor.root.favourite-menus.desc", "&7Open menu yml files under &f/menus/favourite_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(SEARCH_MENUS_SLOT, EditorUtil.createItem(Material.COMPASS,
            EditorLang.text(player, "editor.root.search-menus.name", "&eEdit Search Menus"),
            List.of(
            EditorLang.text(player, "editor.root.search-menus.desc", "&7Open menu yml files under &f/menus/search_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(ITEM_SELL_MENUS_SLOT, EditorUtil.createItem(Material.EMERALD,
            EditorLang.text(player, "editor.root.item-sell-menus.name", "&eEdit Item Sell Menus"),
            List.of(
            EditorLang.text(player, "editor.root.item-sell-menus.desc", "&7Open menu yml files under &f/menus/item_sell_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(CLOSE_SLOT, EditorUtil.createItem(Material.BARRIER,
            EditorLang.text(player, "editor.root.close.name", "&cClose"),
            List.of(EditorLang.text(player, "editor.root.close.desc", "&7Close the editor"))));
        inv.setItem(SETTINGS_SLOT, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.root.settings.name", "&eConfiguration"),
                List.of(EditorLang.text(player, "editor.root.settings.desc", "&7Open settings, styles and other plugin files"))));
    }

    private void constructSettings() {
        settingsScopes.clear();
        addSettingsScope(2, EditorScope.CONFIG, Material.COMPARATOR);
        addSettingsScope(4, EditorScope.UI_STYLE, Material.PAINTING);
        addSettingsScope(6, EditorScope.LANGUAGE, Material.WRITABLE_BOOK);
        addSettingsScope(10, EditorScope.RANDOM_PLACEHOLDER, Material.ENDER_PEARL);
        addSettingsScope(12, EditorScope.CONDITIONAL_PLACEHOLDER, Material.REDSTONE);
        addSettingsScope(14, EditorScope.CUSTOM_PLACEHOLDER, Material.NAME_TAG);
        addSettingsScope(16, EditorScope.SHARED_USE_TIMES, Material.CLOCK);
        addSettingsScope(20, EditorScope.ITEM, Material.ITEM_FRAME);
        addSettingsScope(22, EditorScope.SELL_STICK, Material.BLAZE_ROD);
        addSettingsScope(24, EditorScope.SELL_CHEST, Material.TRAPPED_CHEST);
        inv.setItem(BACK_SLOT, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.file-list.back", "&7Return to editor home"))));
    }

    private void addSettingsScope(int slot, EditorScope scope, Material icon) {
        settingsScopes.put(slot, scope);
        String location = scope == EditorScope.CONFIG ? "config.yml" : scope.getFolderName() + "/";
        inv.setItem(slot, EditorUtil.createItem(icon, "&e" + EditorLang.scope(player, scope),
                List.of("&7" + location, EditorLang.text(player, "editor.action.open", "&aLeft click to open"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (settings) {
            EditorScope scope = settingsScopes.get(slot);
            if (scope != null) {
                EditorManager.editorManager.openScope(player, scope, 0);
            } else if (slot == BACK_SLOT) {
                EditorManager.editorManager.openRoot(player, false);
            }
            return true;
        }
        if (slot == CLOSE_SLOT) {
            player.closeInventory();
        } else if (slot == SETTINGS_SLOT) {
            EditorManager.editorManager.openRoot(player, true);
        } else if (slot == SHOPS_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.SHOP, 0);
        } else if (slot == MENUS_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.MENU, 0);
        } else if (slot == SHOP_MENU_TEMPLATES_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.SHOP_MENU_TEMPLATE, 0);
        } else if (slot == BUY_MORE_MENUS_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.BUY_MORE_MENU, 0);
        } else if (slot == FAVOURITE_MENUS_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.FAVOURITE_MENU, 0);
        } else if (slot == SEARCH_MENUS_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.SEARCH_MENU, 0);
        } else if (slot == ITEM_SELL_MENUS_SLOT) {
            EditorManager.editorManager.openScope(player, EditorScope.ITEM_SELL_MENU, 0);
        }
        return true;
    }
}
