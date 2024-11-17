package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.PaperUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class CommonGUI extends InvGUI {

    private ObjectMenu commonMenu = null;

    private final boolean bypass;

    public CommonGUI(Player owner, ObjectMenu menu, boolean bypass) {
        super(owner);
        this.commonMenu = menu;
        this.bypass = bypass;
    }

    @Override
    protected void constructGUI() {
        if (!bypass && !commonMenu.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    commonMenu.getName());
            return;
        }
        menuButtons = commonMenu.getMenu();
        menuItems = getMenuItems(player.getPlayer());
        if (Objects.isNull(inv)) {
            inv = PaperUtil.createNewInv(player, commonMenu.getInt("size", 54),
                    commonMenu.getString("title", "Shop"));
        }
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (menuButtons.get(slot) == null) {
            return true;
        }
        menuButtons.get(slot).clickEvent(type, player.getPlayer());
        if (ConfigManager.configManager.getBoolean("menu.shop.click-update")) {
            constructGUI();
        } else {
            menuItems.put(slot, getMenuItem(player, slot));
            inv.setItem(slot, menuItems.get(slot));
        }
        return true;
    }

    @Override
    public ObjectMenu getMenu() {
        return commonMenu;
    }

}
