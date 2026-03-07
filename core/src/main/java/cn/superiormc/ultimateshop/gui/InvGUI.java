package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class InvGUI extends AbstractGUI {

    protected Inventory inv;

    public Map<Integer, AbstractButton> menuButtons = new HashMap<>();

    public Map<Integer, ItemStack> menuItems = new HashMap<>();

    public Listener guiListener;

    protected SchedulerUtil runTask = null;

    public String title;

    public InvGUI(Player owner) {
        super(owner);
    }

    public boolean getChangeable() {
        return false;
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    public boolean closeEventHandle(Inventory inventory) {
        if (runTask != null) {
            runTask.cancel();
        }
        return true;
    }

    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        return true;
    }

    public void afterClickEventHandle(ItemStack item, ItemStack currentItem, int slot) {
        return;
    }

    public void openGUI(boolean reopen) {
        GUIStatus previousStatus = playerList.get(player);
        if (!super.canOpenGUI(reopen)) {
            return;
        }
        constructGUI();
        if (inv != null) {
            player.openInventory(inv);
            this.guiListener = new GUIListener(this);
            Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
            if (getMenu() != null) {
                getMenu().doOpenAction(player, reopen);
            }
        } else if (previousStatus == null) {
            playerList.remove(player);
        } else {
            playerList.put(player, previousStatus);
        }
        if (ConfigManager.configManager.getBooleanOrDefault("menu.shop.update", "menu.menu-update.circle-update") ||
        ConfigManager.configManager.getBoolean("menu.title-update.circle-update")) {
            runTask = SchedulerUtil.runTaskTimerAsynchronously(()->{
                if (ConfigManager.configManager.getBooleanOrDefault("menu.shop.update", "menu.menu-update.circle-update")) {
                    constructGUI();
                }
                if (ConfigManager.configManager.getBoolean("menu.title-update.circle-update") && UltimateShop.usePacketEvents) {
                    PacketInventoryUtil.packetInventoryUtil.updateTitle(player, InvGUI.this);
                }
            }, 20L, 20L);
        }
    }

    public Inventory getInv() {
        return inv;
    }

    public Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = new HashMap<>(menuButtons);
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (Map.Entry<Integer, AbstractButton> entry : tempVal1.entrySet()) {
            resultItems.put(entry.getKey(), entry.getValue().getDisplayItem(player, 1).getItemStack());
        }
        return resultItems;
    }

    public ItemStack getMenuItem(Player player, int slot) {
        AbstractButton button = menuButtons.get(slot);
        if (button == null) {
            return new ItemStack(Material.AIR);
        }
        return button.getDisplayItem(player, 1).getItemStack();
    }
}
