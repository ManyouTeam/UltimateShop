package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class EditorInvGUI extends InvGUI {

    public EditorInvGUI(Player owner) {
        super(owner);
    }

    public EditorMode editMode;

    public static Map<Player, EditorInvGUI> guiCache = new HashMap<>();

    public EditorInvGUI previousGUI;

    @Override
    public void openGUI() {
        guiCache.put(owner, this);
        editMode = EditorMode.NOT_EDITING;
        constructGUI();
        if (inv != null) {
            owner.getPlayer().openInventory(inv);
        }
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        if (!guiCache.containsKey(owner)) {
            return true;
        }
        if (editMode == EditorMode.NOT_EDITING) {
            guiCache.remove(owner);
            return true;
        }
        return false;
    }
}
