package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectItem;
import cn.superiormc.ultimateshop.objects.ObjectMenu;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ui.AbstractButton;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BuyMoreGUI extends InvGUI {

    private ObjectItem item;

    public BuyMoreGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
        constructGUI();
    }

    @Override
    public void openGUI() {
        if (inv == null) {
            return;
        }
        owner.getPlayer().openInventory(inv);
    }

    @Override
    protected void constructGUI() {
        commonMenu = ObjectMenu.commonMenus.get(fileName);
        if (commonMenu == null) {
            LanguageManager.languageManager.sendStringText(owner,
                    "error.menu-not-found",
                    "menu",
                    fileName);
            return;
        }
        menuButtons = commonMenu.getMenu();
        menuItems = getMenuItems(owner.getPlayer());
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, commonMenu.getInt("size", 54),
                    TextUtil.parse(commonMenu.getString("title", "Shop")));
        }
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
        //setExtraSlots(glassPane);
    }

    @Override
    public boolean clickEventHandle(ClickType type, int slot) {
        menuButtons.get(slot).clickEvent(type, owner.getPlayer());
        constructGUI();
        return true;
    }

    @Override
    public boolean closeEventHandle() {
        return true;
    }

    @Override
    public boolean dragEventHandle(Set<Integer> slots) {
        return true;
    }

    private Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = menuButtons;
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (int i : tempVal1.keySet()) {
            resultItems.put(i, tempVal1.get(i).getDisplayItem(player));
        }
        return resultItems;
    }

}
