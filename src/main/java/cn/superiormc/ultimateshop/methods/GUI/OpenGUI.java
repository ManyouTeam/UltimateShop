package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.form.FormCommonGUI;
import cn.superiormc.ultimateshop.gui.form.FormShopGUI;
import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.SellAllGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.menus.MenuType;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

public class OpenGUI {

    public static void openShopGUI(Player player, ObjectShop shop, boolean bypass, boolean reopen) {
        if (shop == null) {
            return;
        }
        if (UltimateShop.useGeyser && CommonUtil.isBedrockPlayer(player)) {
            FormShopGUI formShopGUI = new FormShopGUI(player, shop, bypass);
            formShopGUI.openGUI(reopen);
            if (formShopGUI.getMenu() != null) {
                formShopGUI.getMenu().doOpenAction(player);
            }
            return;
        }
        ShopGUI gui = new ShopGUI(player, shop, bypass);
        gui.openGUI(reopen);
        if (gui.getMenu() != null) {
            gui.getMenu().doOpenAction(player);
        }
    }

    public static void openCommonGUI(Player player, String fileName, boolean bypass, boolean reopen) {
        ObjectMenu commonMenu = ObjectMenu.commonMenus.get(fileName);
        if (commonMenu == null || commonMenu.menuConfigs == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.menu-not-found",
                    "menu",
                    fileName);
            return;
        }

        if (commonMenu.getType().equals(MenuType.More)) {
            LanguageManager.languageManager.sendStringText(player, "error.buy-more-menu-direct-open");
            return;
        }

        if (UltimateShop.useGeyser && CommonUtil.isBedrockPlayer(player)) {
            FormCommonGUI formCommonGUI = new FormCommonGUI(player, commonMenu, bypass);
            formCommonGUI.openGUI(reopen);
            if (formCommonGUI.getMenu() != null) {
                formCommonGUI.getMenu().doOpenAction(player);
            }
            return;
        }
        CommonGUI gui = new CommonGUI(player, commonMenu, bypass);
        gui.openGUI(reopen);
        if (gui.getMenu() != null) {
            gui.getMenu().doOpenAction(player);
        }
    }

    public static void openMoreGUI(Player player, ObjectItem item) {
        BuyMoreGUI gui = new BuyMoreGUI(player, item);
        gui.openGUI(true);
    }

    public static void openSellAllGUI(Player player) {
        SellAllGUI gui = new SellAllGUI(player);
        gui.openGUI(true);
    }

}
