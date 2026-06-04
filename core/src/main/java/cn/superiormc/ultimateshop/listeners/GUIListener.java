package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.ListenerManager;
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

import java.util.Objects;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            try {
                InvGUI gui = ListenerManager.listenerManager.getInvGUI(player);
                if (gui == null) {
                    return;
                }
                if (!e.getView().getTopInventory().equals(gui.getInv())) {
                    player.closeInventory();
                    ListenerManager.listenerManager.unregisterListeners(player);
                    ErrorManager.errorManager.sendErrorMessage("§cError: Found unregistered GUI Listener, now force close the inventory and then delete the excess GUI Listener. If this always heppens, please report to the plugin author.");
                    return;
                }
                if (!Objects.equals(e.getClickedInventory(), gui.getInv())) {
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
                        && player.getOpenInventory().getTopInventory().equals(gui.getInv())) {
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
            InvGUI gui = ListenerManager.listenerManager.getInvGUI(player);
            if (gui == null) {
                return;
            }
            if (!e.getView().getTopInventory().equals(gui.getInv())) {
                player.closeInventory();
                ListenerManager.listenerManager.unregisterListeners(player);
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
            InvGUI gui = ListenerManager.listenerManager.getInvGUI(player);
            if (gui == null) {
                return;
            }
            if (!Objects.equals(e.getInventory(), gui.getInv())) {
                return;
            }
            ListenerManager.listenerManager.unregisterNewGUIListener(player, gui);
            if (UltimateShop.usePacketEvents) {
                PacketInventoryUtil.packetInventoryUtil.clear(player);
            }
            if (MenuStatusManager.menuStatusManager.hasOpeningGUI(player)) {
                MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, gui);
            }
            if (gui.closeEventHandle(e.getInventory())) {
                if (gui.getMenu() != null) {
                    gui.getMenu().doCloseAction(player);
                }
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        if (ListenerManager.listenerManager.getInvGUI(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }
}
