package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.form.FormCommonGUI;
import cn.superiormc.ultimateshop.gui.form.FormShopGUI;
import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.SellAllGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OpenGUI {

    public static void openShopGUI(Player player, ObjectShop shop, boolean bypassBedrockCheck) {
        if (shop == null) {
            return;
        }
        if (UltimateShop.useGeyser && (bypassBedrockCheck || CommonUtil.isBedrockPlayer(player))) {
            FormShopGUI formShopGUI = new FormShopGUI(player, shop);
            formShopGUI.openGUI();
            formShopGUI.getMenu().doAction(player);
            return;
        }
        ShopGUI gui = new ShopGUI(player, shop);
        gui.openGUI();
        if (gui.getMenu() != null) {
            gui.getMenu().doAction(player);
        }
    }

    public static void openCommonGUI(Player player, String fileName, boolean bypassBedrockCheck) {
        if (UltimateShop.useGeyser && (bypassBedrockCheck || CommonUtil.isBedrockPlayer(player))) {
            FormCommonGUI formCommonGUI = new FormCommonGUI(player, fileName);
            formCommonGUI.openGUI();
            formCommonGUI.getMenu().doAction(player);
            return;
        }
        CommonGUI gui = new CommonGUI(player, fileName);
        gui.openGUI();
        if (gui.getMenu() != null) {
            gui.getMenu().doAction(player);
        }
    }

    public static void openMoreGUI(Player player, ObjectItem item) {
        BuyMoreGUI gui = new BuyMoreGUI(player, item);
        gui.openGUI();
    }

    public static void openSellAllGUI(Player player) {
        SellAllGUI gui = new SellAllGUI(player);
        gui.openGUI();
    }


}
