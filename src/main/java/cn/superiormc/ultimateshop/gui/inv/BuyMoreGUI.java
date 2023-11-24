package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.*;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
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
    protected void constructGUI() {
        menu = ObjectMoreMenu.moreMenus.get(item);
        if (menu == null) {
            return;
        }
        // display item
        menuButtons = menu.getMenu();
        menuItems = getMenuItems(owner.getPlayer());
        int displaySlot = menu.getDisplayItemSlot();
        ItemStack tempVal1 = menuItems.get(displaySlot);
        tempVal1.setAmount(nowingAmount);
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, menu.getInt("size", 54),
                    TextUtil.parse(menu.getString("title", "Shop")));
        }
        inv.setItem(displaySlot, tempVal1);
        // 其他物品
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
        //setExtraSlots(glassPane);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (!Objects.equals(inventory, getInv())) {
            return true;
        }
        AbstractButton button = menuButtons.get(slot);
        if (button == null) {
            return true;
        }
        switch (button.type) {
            case SELECT_AMOUNT:
                if (button.config.getInt("add-amount", 0) == 0) {
                    if (button.config.getInt("set-amount", -1) == -1) {
                        ErrorManager.errorManager.sendErrorMessage(
                                "§x§9§8§F§B§9§8[UltimateShop] §cError: Can not find add-amount section " +
                                        "in select amount button, or you are setting add-amount to 0?");
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
                if (nowingAmount >= ConfigManager.configManager.getInt("menu.select-more.max-amount", 64)) {
                    nowingAmount = ConfigManager.configManager.getInt("menu.select-more.max-amount", 64);
                }
                break;
            case DISPLAY:
                break;
            case CONFIRM:
                boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
                switch (ConfigManager.configManager.getClickAction(type)){
                    case "buy" :
                        if (!item.getBuyPrice().empty) {
                            BuyProductMethod.startBuy(item.getShop(),
                                    item.getProduct(),
                                    owner.getPlayer(),
                                    !b,
                                    false,
                                    nowingAmount);
                            break;
                        }
                    case "sell" :
                        if (!item.getSellPrice().empty) {
                            SellProductMethod.startSell(item.getShop(),
                                    item.getProduct(),
                                    owner.getPlayer(),
                                    !b,
                                    false,
                                    nowingAmount);
                            break;
                        }
                    case "buy-or-sell" :
                        if (item.getBuyPrice().empty && !item.getSellPrice().empty) {
                            SellProductMethod.startSell(item.getShop(), item.getProduct(), owner.getPlayer(), !b,
                                    false,
                                    nowingAmount);
                        }
                        else {
                            BuyProductMethod.startBuy(item.getShop(), item.getProduct(), owner.getPlayer(), !b,
                                    false,
                                    nowingAmount);
                        }
                        break;
                    case "sell-all" :
                        if (!item.getSellPrice().empty) {
                            SellProductMethod.startSell(item.getShop(),
                                    item.getProduct(),
                                    owner.getPlayer(),
                                    !b,
                                    false,
                                    true,
                                    nowingAmount);
                        }
                        break;
                    default:
                        ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cUnknown click action: "
                                + ConfigManager.configManager.getClickAction(type));
                        break;
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

    public Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = menuButtons;
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (int i : tempVal1.keySet()) {
            AbstractButton tempVal2 = tempVal1.get(i);
            if (tempVal2.type == ButtonType.DISPLAY) {
                ObjectMoreDisplayButton tempVal3 = null;
                tempVal3 = (ObjectMoreDisplayButton) tempVal2;
                resultItems.put(i, tempVal3.getDisplayItem(player, nowingAmount));
            }
            else if (tempVal2.type == ButtonType.CONFIRM) {
                ObjectMoreBuyButton tempVal4 = null;
                tempVal4 = (ObjectMoreBuyButton) tempVal2;
                resultItems.put(i, tempVal4.getDisplayItem(player, nowingAmount));
            }
            else {
                resultItems.put(i, tempVal2.getDisplayItem(player, 1));
            }
        }
        return resultItems;
    }

}
