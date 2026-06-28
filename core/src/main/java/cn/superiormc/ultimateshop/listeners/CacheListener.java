package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.utils.CommandUtil;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CacheManager.cacheManager.addObjectCache(event.getPlayer());
        SchedulerUtil.runTaskLater(() -> {
            if (!event.getPlayer().isOnline()) {
                return;
            }
            CacheManager.cacheManager.loadPlayerCache(event.getPlayer());
        }, ConfigManager.configManager.getLong("cache.load-delay", 7L));
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        CommandUtil.cancelGUIUpdate(event.getPlayer());
        SellStickListener.playerList.remove(event.getPlayer());
        if (UltimateShop.usePacketEvents && PacketInventoryUtil.packetInventoryUtil != null) {
            PacketInventoryUtil.packetInventoryUtil.clear(event.getPlayer());
        }
        CacheManager.cacheManager.saveObjectCache(event.getPlayer());
        MenuStatusManager.menuStatusManager.clear(event.getPlayer());
    }
}
