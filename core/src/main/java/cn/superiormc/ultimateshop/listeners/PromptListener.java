package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.gui.PromptUtil;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PromptListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!MenuStatusManager.menuStatusManager.hasPrompt(player)) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        SchedulerUtil.runSync(() -> {
            if (PromptUtil.matchesCancel(player, message)) {
                if (MenuStatusManager.menuStatusManager.cancelPromptAndShouldReopen(player)) {
                    MenuStatusManager.menuStatusManager.reopen(player);
                }
                return;
            }
            MenuStatusManager.menuStatusManager.submitPrompt(player, message);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MenuStatusManager.menuStatusManager.clear(event.getPlayer());
    }
}
