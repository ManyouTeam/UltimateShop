package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.CreateShopGUI;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
import cn.superiormc.ultimateshop.gui.inv.editor.EditShopGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.subinventory.EditEconomyItem;
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
            if (InvGUI.guiCache.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                InvGUI tempVal1 = CreateShopGUI.guiCache.get(event.getPlayer());
                if (tempVal1 instanceof CreateShopGUI) {
                    CreateShopGUI gui = (CreateShopGUI) tempVal1;
                    if (gui.editMode == GUIMode.EDIT_SHOP_NAME) {
                        gui.shopName = TextUtil.parse(event.getMessage());
                        gui.openGUI();
                    }
                    if (gui.editMode == GUIMode.EDIT_SHOP_ID) {
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
                    if (gui.editMode == GUIMode.EDIT_MENU_ID) {
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
                } else if (tempVal1 instanceof EditShopGUI) {
                    EditShopGUI gui = (EditShopGUI) tempVal1;
                    if (gui.guiMode == GUIMode.EDIT_SHOP_NAME) {
                        gui.config.set("settings.shop-name", TextUtil.parse(event.getMessage()));
                        gui.openGUI();
                    }
                    if (gui.guiMode == GUIMode.EDIT_MENU_ID) {
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
                } else if (tempVal1 instanceof EditEconomyItem) {
                    EditEconomyItem gui = (EditEconomyItem) tempVal1;
                    if (gui.guiMode == GUIMode.EDIT_ECONOMY_TYPE) {
                        gui.section.set("economy-type", event.getMessage());
                        gui.openGUI();
                    }
                    if (gui.guiMode == GUIMode.EDIT_ECONOMY_AMOUNT) {
                        gui.section.set("amount", event.getMessage());
                        gui.openGUI();
                    }
                }
            }
        });
    }

}
