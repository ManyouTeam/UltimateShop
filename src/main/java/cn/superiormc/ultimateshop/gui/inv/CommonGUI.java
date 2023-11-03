package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CommonGUI extends InvGUI {

    private ObjectMenu commonMenu = null;

    private String fileName;

    public CommonGUI(Player owner, String fileName) {
        super(owner);
        this.fileName = fileName;
        constructGUI();
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
        if (!commonMenu.getCondition().getBoolean(owner.getPlayer())) {
            LanguageManager.languageManager.sendStringText(owner,
                    "menu-condition-not-meet",
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
        if (menuButtons.get(slot) == null) {
            return true;
        }
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

    public Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = menuButtons;
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (int i : tempVal1.keySet()) {
            resultItems.put(i, tempVal1.get(i).getDisplayItem(player, 1));
        }
        return resultItems;
    }

}
