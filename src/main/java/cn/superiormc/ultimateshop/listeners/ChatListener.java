package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.editor.CreateShopGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorShopMode;
import cn.superiormc.ultimateshop.gui.inv.editor.EditShopGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Bukkit.getScheduler().runTask(UltimateShop.instance, () -> {
            if (CreateShopGUI.guiCache.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                CreateShopGUI gui = CreateShopGUI.guiCache.get(event.getPlayer());
                if (gui.editMode == EditorShopMode.EDIT_SHOP_NAME) {
                    gui.shopName = TextUtil.parse(event.getMessage());
                    gui.openGUI();
                }
                if (gui.editMode == EditorShopMode.EDIT_SHOP_ID) {
                    if (ConfigManager.configManager.shopConfigs.containsKey(event.getMessage())) {
                        LanguageManager.languageManager.sendStringText(event.getPlayer(),
                                "editor.shop-already-exists",
                                "shop",
                                event.getMessage());
                        return;
                    } else {
                        gui.shopID = event.getMessage();
                        gui.openGUI();
                    }
                }
                if (gui.editMode == EditorShopMode.EDIT_MENU_ID) {
                    if (!ObjectMenu.commonMenus.containsKey(event.getMessage()) &&
                            !ObjectMenu.shopMenuNames.contains(event.getMessage())) {
                        LanguageManager.languageManager.sendStringText(event.getPlayer(),
                                "error.menu-not-found",
                                "menu",
                                event.getMessage());
                        return;
                    } else {
                        gui.menuID = event.getMessage();
                        gui.openGUI();
                    }
                }
            } else if (EditShopGUI.guiCache.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                EditShopGUI gui = EditShopGUI.guiCache.get(event.getPlayer());
                if (gui.editMode == EditorShopMode.EDIT_SHOP_NAME) {
                    gui.config.set("settings.shop-name", TextUtil.parse(event.getMessage()));
                    gui.openGUI();
                }
                if (gui.editMode == EditorShopMode.EDIT_MENU_ID) {
                    if (!ObjectMenu.commonMenus.containsKey(event.getMessage()) &&
                            !ObjectMenu.shopMenuNames.contains(event.getMessage())) {
                        LanguageManager.languageManager.sendStringText(event.getPlayer(),
                                "error.menu-not-found",
                                "menu",
                                event.getMessage());
                        return;
                    } else {
                        gui.config.set("settings.menu", event.getMessage());
                        gui.openGUI();
                    }
                }
            }
        });
    }

}
