package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorScope;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class EditorRootGUI extends InvGUI {

    public EditorRootGUI(Player owner) {
        super(owner);
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.root.title", "UltimateShop Editor");
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title, this);
        }
        inv.clear();
        inv.setItem(11, EditorUtil.createItem(Material.CHEST,
            EditorLang.text(player, "editor.root.shops.name", "&eEdit Shops"),
            List.of(
            EditorLang.text(player, "editor.root.shops.desc", "&7Open shop yml files under &f/shops"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(12, EditorUtil.createItem(Material.BOOK,
            EditorLang.text(player, "editor.root.menus.name", "&eEdit Menus"),
            List.of(
            EditorLang.text(player, "editor.root.menus.desc", "&7Open menu yml files under &f/menus/common_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(13, EditorUtil.createItem(Material.WRITABLE_BOOK,
            EditorLang.text(player, "editor.root.shop-menu-templates.name", "&eEdit Shop Menu Templates"),
            List.of(
            EditorLang.text(player, "editor.root.shop-menu-templates.desc", "&7Open menu yml files under &f/menus/shop_menu_templates"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(14, EditorUtil.createItem(Material.CHEST_MINECART,
            EditorLang.text(player, "editor.root.buy-more-menus.name", "&eEdit Buy More Menus"),
            List.of(
            EditorLang.text(player, "editor.root.buy-more-menus.desc", "&7Open menu yml files under &f/menus/buy_more_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(15, EditorUtil.createItem(Material.NAME_TAG,
            EditorLang.text(player, "editor.root.favourite-menus.name", "&eEdit Favourite Menus"),
            List.of(
            EditorLang.text(player, "editor.root.favourite-menus.desc", "&7Open menu yml files under &f/menus/favourite_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(16, EditorUtil.createItem(Material.COMPASS,
            EditorLang.text(player, "editor.root.search-menus.name", "&eEdit Search Menus"),
            List.of(
            EditorLang.text(player, "editor.root.search-menus.desc", "&7Open menu yml files under &f/menus/search_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(21, EditorUtil.createItem(Material.EMERALD,
            EditorLang.text(player, "editor.root.item-sell-menus.name", "&eEdit Item Sell Menus"),
            List.of(
            EditorLang.text(player, "editor.root.item-sell-menus.desc", "&7Open menu yml files under &f/menus/item_sell_menus"),
            EditorLang.text(player, "editor.action.open", "&aLeft click to open")
            )));
        inv.setItem(22, EditorUtil.createItem(Material.BARRIER,
            EditorLang.text(player, "editor.root.close.name", "&cClose"),
            List.of(EditorLang.text(player, "editor.root.close.desc", "&7Close the editor"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == 11) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.SHOP, 0);
            return true;
        }
        if (slot == 12) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.MENU, 0);
            return true;
        }
        if (slot == 13) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.SHOP_MENU_TEMPLATE, 0);
            return true;
        }
        if (slot == 14) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.BUY_MORE_MENU, 0);
            return true;
        }
        if (slot == 15) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.FAVOURITE_MENU, 0);
            return true;
        }
        if (slot == 16) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.SEARCH_MENU, 0);
            return true;
        }
        if (slot == 21) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.ITEM_SELL_MENU, 0);
            return true;
        }
        if (slot == 22) {
            player.closeInventory();
            return true;
        }
        return true;
    }
}
