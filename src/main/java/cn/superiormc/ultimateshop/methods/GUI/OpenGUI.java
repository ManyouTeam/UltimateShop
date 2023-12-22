package cn.superiormc.ultimateshop.methods.GUI;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.form.FormCommonGUI;
import cn.superiormc.ultimateshop.gui.form.FormShopGUI;
import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.SellAllGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.*;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.geysermc.floodgate.api.FloodgateApi;

public class OpenGUI {

    public static void openShopGUI(Player player, ObjectShop shop) {
        if (shop == null) {
            return;
        }
        if (CommonUtil.getClass("org.geysermc.floodgate.api.FloodgateApi")) {
            if (!UltimateShop.freeVersion &&
                    FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                FormShopGUI formShopGUI = new FormShopGUI(player, shop);
                formShopGUI.openGUI();
                return;
            }
        }
        ShopGUI gui = new ShopGUI(player, shop);
        gui.openGUI();
        Listener guiListener = new GUIListener(gui);
        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
    }

    public static void openCommonGUI(Player player, String fileName) {
        if (CommonUtil.getClass("org.geysermc.floodgate.api.FloodgateApi")) {
            if (!UltimateShop.freeVersion &&
                    FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                FormCommonGUI formCommonGUI = new FormCommonGUI(player, fileName);
                formCommonGUI.openGUI();
                return;
            }
        }
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

    public static void openSellAllGUI(Player player) {
        SellAllGUI gui = new SellAllGUI(player);
        gui.openGUI();
        Listener guiListener = new GUIListener(gui);
        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
    }

    public static void openEditorGUI(Player player) {
        if (EditorInvGUI.guiCache.containsKey(player)) {
            LanguageManager.languageManager.sendStringText(player, "editor.already-editing");
            return;
        }
        CreateOrEditShopGUI gui = new CreateOrEditShopGUI(player);
        gui.openGUI();
        Listener guiListener = new GUIListener(gui);
        Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
    }

}
