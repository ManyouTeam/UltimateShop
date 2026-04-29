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

import java.util.Objects;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = e.getWhoClicked() instanceof Player clickedPlayer ? clickedPlayer : null;
        InvGUI gui = MenuStatusManager.menuStatusManager.getActiveInvGUI(player);
        if (gui == null) {
            return;
        }
        try {
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
            ErrorManager.errorManager.sendErrorMessage("搂cError: Your menu configs has wrong, error message: " + throwable.getMessage());
            throwable.printStackTrace();
            MenuStatusManager.menuStatusManager.removeGUIStatus(player);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Player player = e.getWhoClicked() instanceof Player clickedPlayer ? clickedPlayer : null;
        InvGUI gui = MenuStatusManager.menuStatusManager.getActiveInvGUI(player);
        if (gui == null) {
            return;
        }
        if (e.getRawSlots().stream().noneMatch(slot -> slot < gui.getInv().getSize())) {
            return;
        }
        if (gui.dragEventHandle(e.getNewItems())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = e.getPlayer() instanceof Player closingPlayer ? closingPlayer : null;
        InvGUI gui = MenuStatusManager.menuStatusManager.getActiveInvGUI(player);
        if (gui == null) {
            return;
        }
        if (!Objects.equals(e.getInventory(), gui.getInv())) {
            return;
        }
        MenuStatusManager.menuStatusManager.removeActiveInvGUI(player, gui);
        if (UltimateShop.usePacketEvents) {
            PacketInventoryUtil.packetInventoryUtil.clear(player);
        }
        player.updateInventory();
        if (MenuStatusManager.menuStatusManager.hasOpeningGUI(player)) {
            MenuStatusManager.menuStatusManager.removeOpenGUIStatus(player, gui);
        }
        if (gui.closeEventHandle(e.getInventory())) {
            if (gui.getMenu() != null) {
                gui.getMenu().doCloseAction(player);
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e){
        if (MenuStatusManager.menuStatusManager.getActiveInvGUI(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
    }
}
