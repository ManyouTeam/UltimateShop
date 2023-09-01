package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.objects.ObjectShop;
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

public class ShopGUI extends InvGUI {

    private ObjectShop shop;

    private ObjectMenu shopMenu = null;

    public ShopGUI(Player owner, ObjectShop shop) {
        super(owner);
        this.shop = shop;
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
        if (shop.getShopMenu() == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.shop-does-not-have-menu",
                    "shop",
                    shop.getShopName());
            return;
        }
        shopMenu = ObjectMenu.shopMenus.get(shop);
        if (shopMenu == null) {
            LanguageManager.languageManager.sendStringText(owner.getPlayer(),
                    "error.shop-menu-not-found",
                    "shop",
                    shop.getShopName(),
                    "menu",
                    shop.getShopMenu());
            return;
        }
        menuButtons = shopMenu.getMenu();
        menuItems = getMenuItems(owner.getPlayer());
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, shopMenu.getInt("size", 54),
                    TextUtil.parse(shopMenu.getString("title", "Shop Editor")));
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

    public Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = menuButtons;
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (int i : tempVal1.keySet()) {
            resultItems.put(i, tempVal1.get(i).getDisplayItem(player));
        }
        return resultItems;
    }

}
