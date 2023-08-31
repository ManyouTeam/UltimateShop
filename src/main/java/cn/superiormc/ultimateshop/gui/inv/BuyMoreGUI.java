package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Set;

public class BuyMoreGUI extends InvGUI {

    private ObjectItem item;

    private ObjectMoreMenu menu;

    private int nowingAmount;

    public BuyMoreGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
        this.nowingAmount = 1;
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
        menu = ObjectMoreMenu.moreMenus.get(item);
        if (menu == null) {
            return;
        }
        // display item
        int displaySlot = menu.getDisplayItemSlot();
        ItemStack tempVal1 = menuItems.get(displaySlot);
        tempVal1.setAmount(nowingAmount);
        inv.setItem(displaySlot, tempVal1);
        // 其他物品
        menuButtons = menu.getMenu();
        menuItems = getMenuItems(owner.getPlayer());
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, menu.getInt("size", 54),
                    TextUtil.parse(menu.getString("title", "Shop")));
        }
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
        //setExtraSlots(glassPane);
    }

    @Override
    public boolean clickEventHandle(ClickType type, int slot) {
        AbstractButton button = menuButtons.get(slot);
        if (button == null) {
            return true;
        }
        switch (button.type) {
            case SELECT_AMOUNT:
                if (button.config.getInt("add-amount", -1) == -1) {
                    if (button.config.getInt("set-amount", -1) == -1) {
                        LanguageManager.languageManager.sendStringText(getOwner().getPlayer(),
                                "§x§9§8§F§B§9§8[UltimateShop] §cError: Can not find add-amount section " +
                                        "in select amount button.");
                        return true;
                    }
                    else {
                        nowingAmount = button.config.getInt("set-amount");
                    }
                }
                else {
                    nowingAmount = nowingAmount + button.config.getInt("add-amount");
                }
                if (nowingAmount < 1) {
                    nowingAmount = 1;
                }
                break;
            case DISPLAY:
                break;
            case CONFIRM:
                boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
                switch (ConfigManager.configManager.getClickAction(type)){
                    case "buy" :
                        BuyProductMethod.startBuy(item.getShop(),
                                item.getProduct(),
                                owner.getPlayer(),
                                !b,
                                false,
                                nowingAmount);
                    case "sell" :
                        SellProductMethod.startSell(item.getShop(),
                                item.getProduct(),
                                owner.getPlayer(),
                                !b,
                                false,
                                nowingAmount);
                    default:
                        LanguageManager.languageManager.sendStringText("§x§9§8§F§B§9§8[UltimateShop] §cUnknown click action: "
                                + ConfigManager.configManager.getClickAction(type));
                }
                break;
            default:
                menuButtons.get(slot).clickEvent(type, owner.getPlayer());
                break;
        }
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

}
