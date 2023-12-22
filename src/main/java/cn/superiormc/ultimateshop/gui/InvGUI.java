package cn.superiormc.ultimateshop.gui;

import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class InvGUI extends AbstractGUI {

    protected Inventory inv;

    public Map<Integer, AbstractButton> menuButtons = new HashMap<>();

    public Map<Integer, ItemStack> menuItems = new HashMap<>();

    public InvGUI(Player owner) {
        super(owner);
    }

    public abstract boolean clickEventHandle(Inventory inventory, ClickType type, int slot);

    public abstract boolean closeEventHandle(Inventory inventory);

    public abstract boolean dragEventHandle(Map<Integer, ItemStack> newItems);

    @Override
    public void openGUI() {
        if (inv != null) {
            owner.getPlayer().openInventory(inv);
        }
    }

    public Inventory getInv() {
        return inv;
    }
}
