package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class InvGUI extends AbstractGUI {

    protected Inventory inv;

    public Map<Integer, AbstractButton> menuButtons = new HashMap<>();

    public Map<Integer, ItemStack> menuItems = new HashMap<>();

    public Map<Integer, ItemStack> items = new HashMap<>();

    protected final Map<Integer, ItemStack> packetItems = new HashMap<>();

    protected SchedulerUtil runTask = null;

    public String title;

    public boolean opened = false;

    public InvGUI(Player owner) {
        super(owner);
    }

    public void setInvItem(int slot, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            items.remove(slot);
            packetItems.remove(slot);
            if (inv != null) {
                inv.setItem(slot, null);
            }
            return;
        }
        items.put(slot, item.clone());
    }

    @Override
    public void updateGUI() {
        items.clear();
        packetItems.clear();
        constructGUI();
        if (inv == null) {
            return;
        }
        for (int i = 0; i < inv.getSize(); i++) {
            if (!isChangeableSlot(i)) {
                inv.setItem(i, null);
            }
        }
        for (int i : items.keySet()) {
            ItemStack itemStack = items.get(i);
            if (itemStack != null && !itemStack.getType().isAir()) {
                if (!isChangeableSlot(i) && isPacketMode()) {
                    packetItems.put(i, itemStack.clone());
                } else {
                    inv.setItem(i, itemStack.clone());
                }
            }
        }
        if (isPacketMode() && opened) {
            sendPacketItems();
        }
    }

    public void updateSlot(int slot) {
        menuItems.put(slot, getMenuItem(player, slot));
        setInvItem(slot, menuItems.get(slot));
        ItemStack itemStack = items.get(slot);
        if (isPacketMode() && !isChangeableSlot(slot)) {
            if (itemStack == null || itemStack.getType().isAir()) {
                packetItems.remove(slot);
            } else {
                packetItems.put(slot, itemStack.clone());
            }
            sendPacketItems();
            return;
        }
        if (inv != null) {
            inv.setItem(slot, itemStack == null ? null : itemStack.clone());
        }
    }

    public boolean getChangeable() {
        return false;
    }

    protected boolean isChangeableSlot(int slot) {
        return false;
    }

    public boolean isPacketMode() {
        return ConfigManager.configManager.getBoolean("menu.packet-mode") &&
                UltimateShop.usePacketEvents &&
                PacketInventoryUtil.packetInventoryUtil != null &&
                !getChangeable();
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    public void normalButtonClickEventHandle(ClickType type, int slot) {
        AbstractButton normalButton = menuButtons.get(slot);
        if (normalButton != null) {
            normalButton.clickEvent(type, player);
            if (ConfigManager.configManager.getBooleanOrDefault("menu.shop.click-update", "menu.menu-update.click-update")) {
                updateGUI();
            } else {
                normalButton.guiUpdateSlot(slot, this);
            }
        }
    }

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
        GUIStatus previousStatus = MenuStatusManager.menuStatusManager.getGUIStatus(player);
        if (!MenuStatusManager.menuStatusManager.canOpenGUI(player, this, reopen)) {
            return;
        }
        updateGUI();
        if (inv != null) {
            player.openInventory(inv);
            this.opened = true;
            MenuStatusManager.menuStatusManager.setActiveInvGUI(player, this);
            sendPacketItems();
            if (getMenu() != null) {
                getMenu().doOpenAction(player, reopen);
            }
        } else if (previousStatus == null) {
            MenuStatusManager.menuStatusManager.removeGUIStatus(player);
        } else {
            MenuStatusManager.menuStatusManager.setGUIStatus(player, previousStatus);
        }
        if (ConfigManager.configManager.getBooleanOrDefault("menu.shop.update", "menu.menu-update.circle-update") ||
                ConfigManager.configManager.getBoolean("menu.title-update.circle-update")) {
            runTask = SchedulerUtil.runTaskTimerAsynchronously(()->{
                if (ConfigManager.configManager.getBooleanOrDefault("menu.shop.update", "menu.menu-update.circle-update")) {
                    updateGUI();
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

    public Map<Integer, ItemStack> getPacketItems() {
        return packetItems;
    }

    public void sendPacketItems() {
        if (!isPacketMode() || inv == null || !Objects.equals(player.getOpenInventory().getTopInventory(), inv)) {
            return;
        }
        PacketInventoryUtil.packetInventoryUtil.updateItems(player, this);
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
