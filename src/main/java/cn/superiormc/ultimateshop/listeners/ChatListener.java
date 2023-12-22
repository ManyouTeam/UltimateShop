package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.editor.CreateShopGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorInvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorMode;
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
            Bukkit.getConsoleSender().sendMessage("5555");
            if (EditorInvGUI.guiCache.containsKey(event.getPlayer())) {
                Bukkit.getConsoleSender().sendMessage("6666");
                event.setCancelled(true);
                EditorInvGUI tempVal1 = CreateShopGUI.guiCache.get(event.getPlayer());
                if (tempVal1 instanceof CreateShopGUI) {
                    CreateShopGUI gui = (CreateShopGUI) tempVal1;
                    if (gui.editMode == EditorMode.EDIT_SHOP_NAME) {
                        gui.shopName = TextUtil.parse(event.getMessage());
                        gui.openGUI();
                    }
                    if (gui.editMode == EditorMode.EDIT_SHOP_ID) {
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
                    if (gui.editMode == EditorMode.EDIT_MENU_ID) {
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
                }
                else if (tempVal1 instanceof EditShopGUI) {
                    EditShopGUI gui = (EditShopGUI) tempVal1;
                    if (gui.editMode == EditorMode.EDIT_SHOP_NAME) {
                        gui.config.set("settings.shop-name", TextUtil.parse(event.getMessage()));
                        gui.openGUI();
                    }
                    if (gui.editMode == EditorMode.EDIT_MENU_ID) {
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
            }
        });
    }

}
