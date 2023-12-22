package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorInvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class GUIListener implements Listener {

    private Player player = null;
    private InvGUI gui = null;

    public GUIListener(InvGUI gui) {
        this.gui = gui;
        this.player = gui.getOwner();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        //try {
            if (e.getWhoClicked().equals(player)) {
                if (e.getClickedInventory() != gui.getInv()) {
                    return;
                }
                e.setCancelled(gui.clickEventHandle(e.getClickedInventory(), e.getClick(), e.getSlot()));
                if (e.getClick().toString().equals("SWAP_OFFHAND") && e.isCancelled()) {
                    player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
                }
            }
        //}
        //catch (Exception ep) {
        //    e.setCancelled(true);
        //}
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked().equals(player)) {
            if (gui.dragEventHandle(e.getNewItems())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer().equals(player)) {
            if (gui.closeEventHandle(e.getInventory())) {
                HandlerList.unregisterAll(this);
                player.updateInventory();
                if (gui instanceof EditorInvGUI) {
                    EditorInvGUI tempVal1 = (EditorInvGUI) gui;
                    if (tempVal1.previousGUI != null) {
                        tempVal1.previousGUI.openGUI();
                        Listener guiListener = new GUIListener(tempVal1.previousGUI);
                        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        if (e.getPlayer().equals(player)) {
            e.setCancelled(true);
        }
    }
}
