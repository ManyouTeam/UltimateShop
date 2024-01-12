package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.gui.inv.editor.CreateShopGUI;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        CacheManager.cacheManager.addPlayerCache(event.getPlayer());
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        CacheManager.cacheManager.savePlayerCache(event.getPlayer());
        CreateShopGUI.guiCache.remove(event.getPlayer());
        OpenGUI.editorWarningCache.remove(event.getPlayer());
    }
}
