package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Objects;

public class GUIListener implements Listener {

    private final Player player;
    private final InvGUI gui;

    public GUIListener(InvGUI gui) {
        this.gui = gui;
        this.player = gui.getPlayer();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        try {
            if (e.getWhoClicked().equals(player)) {
                if (!Objects.equals(e.getClickedInventory(), gui.getInv())) {
                    if (e.getClick().isShiftClick()) {
                        e.setCancelled(!gui.getChangeable());
                    }
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
            }
        }
        catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your menu configs has wrong, error message: " +
                    throwable.getMessage());
            if (ConfigManager.configManager.getBoolean("debug")) {
                throwable.fillInStackTrace();
            }
            AbstractGUI.playerList.remove(player);
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
            if (!Objects.equals(e.getInventory(), gui.getInv())) {
                return;
            }
            HandlerList.unregisterAll(this);
            player.updateInventory();
            if (AbstractGUI.playerList.containsKey(player)) {
                gui.removeOpenGUIStatus();
            }
            // 判定是否要打开上一页菜单
            if (gui.closeEventHandle(e.getInventory())) {
                if (UltimateShop.isFolia) {
                    Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> {
                        if (gui.previousGUI != null && gui.guiMode == GUIMode.NOT_EDITING) {
                            gui.previousGUI.openGUI(true);
                        }
                    }, 4);
                    return;
                }
                Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                    if (gui.previousGUI != null && gui.guiMode == GUIMode.NOT_EDITING) {
                        gui.previousGUI.openGUI(true);
                    }
                }, 4L);
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
