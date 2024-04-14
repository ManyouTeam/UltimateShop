package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public abstract class InvGUI extends AbstractGUI {

    public static Map<Player, InvGUI> guiCache = new HashMap<>();

    protected Inventory inv;

    public Map<Integer, AbstractButton> menuButtons = new HashMap<>();

    public Map<Integer, ItemStack> menuItems = new HashMap<>();

    public InvGUI previousGUI;

    public Listener guiListener;

    public GUIMode guiMode;

    protected BukkitRunnable runTask = null;

    public InvGUI(Player owner) {
        super(owner);
    }

    public boolean getChangeable() {
        return false;
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    public boolean closeEventHandle(Inventory inventory) {
        return guiMode != null && guiMode == GUIMode.NOT_EDITING;
    }

    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        return true;
    }

    public void afterClickEventHandle(ItemStack item, ItemStack currentItem, int slot) {
        return;
    }

    public void openGUI() {
        if (!super.canOpenGUI()) {
            return;
        }
        constructGUI();
        if (inv != null) {
            player.openInventory(inv);
            guiCache.remove(player);
            this.guiListener = new GUIListener(this);
            Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
        }
        if (guiMode != GUIMode.NOT_EDITING) {
            guiMode = GUIMode.NOT_EDITING;
        }
    }

    public Inventory getInv() {
        return inv;
    }

    public ConfigurationSection getSection() {
        return null;
    }
}
