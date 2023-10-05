package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class OpenGUI {

    public static void openShopGUI(Player player, ObjectShop shop) {
        if (shop == null) {
            return;
        }
        ShopGUI gui = new ShopGUI(player, shop);
        gui.openGUI();
        Listener guiListener = new GUIListener(gui);
        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
    }

    public static void openCommonGUI(Player player, String fileName) {
        CommonGUI gui = new CommonGUI(player, fileName);
        gui.openGUI();
        Listener guiListener = new GUIListener(gui);
        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
    }

    public static void openMoreGUI(Player player, ObjectItem item) {
        BuyMoreGUI gui = new BuyMoreGUI(player, item);
        gui.openGUI();
        Listener guiListener = new GUIListener(gui);
        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
    }

}
