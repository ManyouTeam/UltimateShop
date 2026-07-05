package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            try {
                Inventory topInventory = e.getView().getTopInventory();
                InvGUI gui = topInventory.getHolder() instanceof InvGUI ? (InvGUI) topInventory.getHolder() : null;
                if (gui == null) {
                    return;
                }
                if (!topInventory.equals(gui.getInventory())) {
                    player.closeInventory();
                    ErrorManager.errorManager.sendErrorMessage("§cError: Found unregistered GUI Listener, now force close the inventory and then delete the excess GUI Listener. If this always heppens, please report to the plugin author.");
                    return;
                }
                if (!Objects.equals(e.getClickedInventory(), gui.getInventory())) {
                    if (e.getClick().isShiftClick() || e.getClick() == ClickType.DOUBLE_CLICK || ConfigManager.configManager.getBoolean("menu.ignore-click-outside")) {
                        e.setCancelled(!gui.getChangeable());
                    }
                    return;
                }
                if (e.getClick() == ClickType.DOUBLE_CLICK) {
                    e.setCancelled(!gui.getChangeable());
                    return;
                }
                if (gui.getCooldown()) {
                    e.setCancelled(true);
                    return;
                } else {
                    gui.addCooldown();
                }
                if (gui.clickEventHandle(e.getClickedInventory(), e.getClick(), e.getSlot())) {
                    e.setCancelled(true);
                }
                gui.afterClickEventHandle(e.getCursor(), e.getCurrentItem(), e.getSlot());
                if (CommonUtil.getMajorVersion(16) && e.getClick() == ClickType.SWAP_OFFHAND && e.isCancelled()) {
                    player.getInventory().setItemInOffHand(player.getInventory().getItemInOffHand());
                }
                if (ConfigManager.configManager.getBoolean("menu.title-update.click-update") && UltimateShop.usePacketEvents
                        && player.getOpenInventory().getTopInventory().equals(gui.getInventory())) {
                    PacketInventoryUtil.packetInventoryUtil.updateTitle(player, gui);
                }
            } catch (Throwable throwable) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Your menu configs has wrong, error message: " + throwable.getMessage());
                throwable.printStackTrace();
                MenuStatusManager.menuStatusManager.removeGUIStatus(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            Inventory topInventory = e.getView().getTopInventory();
            InvGUI gui = topInventory.getHolder() instanceof InvGUI ? (InvGUI) topInventory.getHolder() : null;
            if (gui == null) {
                return;
            }
            if (!e.getView().getTopInventory().equals(gui.getInventory())) {
                player.closeInventory();
                ErrorManager.errorManager.sendErrorMessage("§cError: Found unregistered GUI Listener, now force close the inventory and then delete the excess GUI Listener. If this always heppens, please report to the plugin author.");
                return;
            }
            if (gui.dragEventHandle(e.getNewItems())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player player) {
            Inventory topInventory = e.getView().getTopInventory();
            InvGUI gui = topInventory.getHolder() instanceof InvGUI ? (InvGUI) topInventory.getHolder() : null;
            if (gui == null) {
                return;
            }
            if (!Objects.equals(e.getInventory(), gui.getInventory())) {
                return;
            }
            if (gui.closeEventHandle(e.getInventory())) {
                if (UltimateShop.usePacketEvents) {
                    PacketInventoryUtil.packetInventoryUtil.clear(player);
                }
                gui.finishGUI();
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        Inventory topInventory = e.getPlayer().getOpenInventory().getTopInventory();
        if (topInventory.getHolder() instanceof InvGUI) {
            e.setCancelled(true);
        }
    }
}
