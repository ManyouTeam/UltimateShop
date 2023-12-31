package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
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

    private Player player;
    private InvGUI gui = null;

    public GUIListener(InvGUI gui) {
        this.gui = gui;
        this.player = gui.getOwner();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        try {
            if (e.getWhoClicked().equals(player)) {
                if (e.getClickedInventory() != gui.getInv()) {
                    return;
                }
                if (gui.clickEventHandle(e.getClickedInventory(), e.getClick(), e.getSlot())) {
                    e.setCancelled(true);
                }
                gui.afterClickEventHandle(e.getCursor(), e.getCurrentItem(), e.getSlot());
                if (e.getClick().toString().equals("SWAP_OFFHAND") && e.isCancelled()) {
                    player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
                }
            }
        }
        catch (Exception ep) {
            ep.printStackTrace();
            e.setCancelled(true);
        }
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
            HandlerList.unregisterAll(this);
            player.updateInventory();
            // 判定是否要打开上一页菜单
            if (gui.closeEventHandle(e.getInventory())) {
                Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                    if (gui.previousGUI != null && gui.guiMode == GUIMode.NOT_EDITING) {
                        gui.previousGUI.openGUI();
                    }
                }, 2L);
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
