package cn.superiormc.ultimateshop.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Set;

public abstract class InvGUI extends AbstractGUI {

    protected Inventory inv;

    public InvGUI(Player owner) {
        super(owner);
    }

    public abstract boolean clickEventHandle(ClickType type, int slot);

    public abstract boolean closeEventHandle();

    public abstract boolean dragEventHandle(Set<Integer> slots);

    @Override
    public void openGUI() {
        owner.openInventory(inv);
    }

    public Inventory getInv() {
        return inv;
    }

    protected void setExtraSlots (ItemStack itemStack) {
        if (Objects.nonNull(inv)) {
            for (int i = 0 ; i < inv.getSize(); i++) {
                if (Objects.isNull(inv.getItem(i)) || Objects.requireNonNull(inv.getItem(i)).getType().isAir()) {
                    inv.setItem(i, itemStack);
                }
            }
        }
    }
}
