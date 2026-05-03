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
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title);
        }
        inv.clear();
        inv.setItem(11, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.root.shops.name", "&eEdit Shops"),
                List.of(
                        EditorLang.text(player, "editor.root.shops.desc", "&7Open shop yml files under &f/shops"),
                        EditorLang.text(player, "editor.action.open", "&aLeft click to open")
                )));
        inv.setItem(15, EditorUtil.createItem(Material.BOOK,
                EditorLang.text(player, "editor.root.menus.name", "&eEdit Menus"),
                List.of(
                        EditorLang.text(player, "editor.root.menus.desc", "&7Open menu yml files under &f/menus"),
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
        if (slot == 15) {
            MenuStatusManager.menuStatusManager.openScope(player, EditorScope.MENU, 0);
            return true;
        }
        if (slot == 22) {
            player.closeInventory();
            return true;
        }
        return true;
    }
}
