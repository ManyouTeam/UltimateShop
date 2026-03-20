package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EditorChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!EditorManager.editorManager.hasPrompt(player)) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        SchedulerUtil.runSync(() -> {
            if (EditorLang.matchesCancel(player, message)) {
                EditorManager.editorManager.cancelPrompt(player);
                EditorManager.editorManager.reopen(player);
                return;
            }
            EditorManager.editorManager.submitPrompt(player, message);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        EditorManager.editorManager.clear(event.getPlayer());
    }
}
