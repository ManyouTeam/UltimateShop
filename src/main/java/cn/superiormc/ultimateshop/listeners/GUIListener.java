package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.gui.InvGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

public class GUIListener implements Listener {
    private Player player = null;
    private InvGUI gui = null;

    public GUIListener(InvGUI gui) {
        this.gui = gui;
        this.player = gui.getOwner();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        try {
            if (!Objects.equals(e.getClickedInventory(), gui.getInv())) {
                e.setCancelled(true);
                return;
            }
            if (e.getWhoClicked().equals(player)) {
                e.setCancelled(gui.clickEventHandle(e.getClick(), e.getSlot()));
                if (e.getClick().toString().equals("SWAP_OFFHAND") && e.isCancelled()) {
                    player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
                }
            }
        }
        catch (Exception ep) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked().equals(player)) {
            if (gui.dragEventHandle(e.getInventorySlots())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer().equals(player)) {
            if (gui.closeEventHandle()) {
                HandlerList.unregisterAll(this);
                player.updateInventory();
            } else {
                gui.openGUI();
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
