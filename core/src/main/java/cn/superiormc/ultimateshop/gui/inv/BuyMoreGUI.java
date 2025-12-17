package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.*;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectMoreMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BuyMoreGUI extends InvGUI {

    private final ObjectItem item;

    private final ObjectMoreMenu menu;

    private int nowingAmount;

    private BuyMoreGUI(Player owner, ObjectItem item) {
        super(owner);
        this.item = item;
        this.menu = item.getBuyMoreMenu();
        this.nowingAmount = 1;
    }

    private BuyMoreGUI(Player owner, ObjectItem item, ObjectMoreMenu menu) {
        super(owner);
        this.item = item;
        this.menu = menu;
        this.nowingAmount = 1;
    }

    @Override
    public void constructGUI() {
        if (menu == null) {
            return;
        }
        // display item
        menuButtons = menu.getMenu(MenuSender.of(player));
        menuItems = getMenuItems(player);
        int displaySlot = menu.getDisplayItemSlot();
        ItemStack tempVal1 = menuItems.get(displaySlot);
        tempVal1.setAmount(nowingAmount);
        title = menu.getString("title", "Buy More Menu");
        if (Objects.isNull(inv)) {
            inv = UltimateShop.methodUtil.createNewInv(player, menu.getInt("size", 54), title);
        }
        inv.setItem(displaySlot, tempVal1);
        // 其他物品
        for (int slot : menuButtons.keySet()) {
            inv.setItem(slot, menuItems.get(slot));
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        AbstractButton button = menuButtons.get(slot);
        if (button == null) {
            return true;
        }
        switch (button.type) {
            case SELECT_AMOUNT:
                if (button.config.getInt("add-amount", 0) == 0) {
                    if (button.config.getInt("set-amount", -1) == -1) {
                        ErrorManager.errorManager.sendErrorMessage("§cError: Can not find add-amount section " +
                                        "in select amount button, or you are setting add-amount to 0?");
                        return true;
                    } else {
                        nowingAmount = button.config.getInt("set-amount");
                    }
                } else {
                    nowingAmount = nowingAmount + button.config.getInt("add-amount");
                }
                if (nowingAmount < 1) {
                    nowingAmount = 1;
                }
                if (nowingAmount >= menu.getSection().getInt("max-amount", 64)) {
                    nowingAmount = menu.getSection().getInt("max-amount", 64);
                }
                break;
            case DISPLAY:
                break;
            case CONFIRM:
                boolean b = ConfigManager.configManager.getBoolean("placeholder.click.enabled");
                String clickType = ConfigManager.configManager.getClickAction(type, button);
                if (((ObjectMoreBuyButton)button).getClickType() != null) {
                    clickType = ((ObjectMoreBuyButton)button).getClickType().toLowerCase();
                }
                switch (clickType) {
                    case "buy" :
                        if (!item.getBuyPrice().empty) {
                            BuyProductMethod.startBuy(item,
                                    player,
                                    !b,
                                    false,
                                    nowingAmount);
                        }
                        break;
                    case "sell" :
                        if (!item.getSellPrice().empty) {
                            SellProductMethod.startSell(item,
                                    player,
                                    !b,
                                    false,
                                    nowingAmount);
                        }
                        break;
                    case "buy-or-sell" :
                        if (item.getBuyPrice().empty && !item.getSellPrice().empty) {
                            SellProductMethod.startSell(item, player, !b,
                                    false,
                                    nowingAmount);
                        } else {
                            BuyProductMethod.startBuy(item, player, !b,
                                    false,
                                    nowingAmount);
                        }
                        break;
                    case "sell-all" :
                        if (!item.getSellPrice().empty && item.isEnableSellAll()) {
                            SellProductMethod.startSell(item,
                                    player,
                                    !b,
                                    false,
                                    true,
                                    nowingAmount);
                        }
                        break;
                }
                if (!UltimateShop.freeVersion) {
                    ObjectAction action = new ObjectAction(
                            ConfigManager.configManager.getSection("menu.click-event-actions." + clickType),
                            item);
                    action.runAllActions(new ObjectThingRun(player, type));
                    if (action.getLastTradeStatus() != null && action.getLastTradeStatus().getStatus() != ProductTradeStatus.Status.DONE) {
                        item.getFailAction().runAllActions(new ObjectThingRun(player, type));
                    }
                }
                break;
            default:
                menuButtons.get(slot).clickEvent(type, player);
                break;
        }
        constructGUI();
        return true;
    }

    public Map<Integer, ItemStack> getMenuItems(Player player) {
        Map<Integer, AbstractButton> tempVal1 = menuButtons;
        Map<Integer, ItemStack> resultItems = new HashMap<>();
        for (int i : tempVal1.keySet()) {
            AbstractButton tempVal2 = tempVal1.get(i);
            if (tempVal2.type == ButtonType.DISPLAY) {
                ObjectMoreDisplayButton tempVal3;
                tempVal3 = (ObjectMoreDisplayButton) tempVal2;
                resultItems.put(i, tempVal3.getDisplayItem(player, nowingAmount).getItemStack());
            } else if (tempVal2.type == ButtonType.CONFIRM) {
                ObjectMoreBuyButton tempVal4;
                tempVal4 = (ObjectMoreBuyButton) tempVal2;
                resultItems.put(i, tempVal4.getDisplayItem(player, nowingAmount).getItemStack());
            } else {
                resultItems.put(i, tempVal2.getDisplayItem(player, 1).getItemStack());
            }
        }
        return resultItems;
    }

    public static void openGUI(Player player, ObjectItem item) {
        if (ConfigManager.configManager.getBoolean("menu.buy-more-menu.not-open-when-invalid") && item.getBuyMoreMenu().isInvalid()) {
            return;
        }
        BuyMoreGUI gui = new BuyMoreGUI(player, item);
        gui.openGUI(true);
    }

    public static void openGUI(Player player, ObjectItem item, ObjectMoreMenu menu) {
        if (ConfigManager.configManager.getBoolean("menu.buy-more-menu.not-open-when-invalid") && menu.isInvalid()) {
            return;
        }
        BuyMoreGUI gui = new BuyMoreGUI(player, item, menu);
        gui.openGUI(true);
    }
}
