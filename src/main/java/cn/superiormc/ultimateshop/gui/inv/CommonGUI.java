package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.InvUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommonGUI extends InvGUI {

    private ObjectMenu commonMenu = null;

    private final String fileName;

    public CommonGUI(Player owner, String fileName) {
        super(owner);
        this.fileName = fileName;
    }

    @Override
    protected void constructGUI() {
        commonMenu = ObjectMenu.commonMenus.get(fileName);
        if (commonMenu == null || commonMenu.menuConfigs == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.menu-not-found",
                    "menu",
                    fileName);
            return;
        }
        if (!commonMenu.getCondition().getBoolean(player.getPlayer())) {
            LanguageManager.languageManager.sendStringText(player,
                    "menu-condition-not-meet",
                    "menu",
                    fileName);
            return;
        }
        menuButtons = commonMenu.getMenu();
        menuItems = getMenuItems(player.getPlayer());
        if (Objects.isNull(inv)) {
            inv = InvUtil.createNewInv(player, commonMenu.getInt("size", 54),
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
