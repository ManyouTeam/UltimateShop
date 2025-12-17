package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubUpdateGUITitle extends AbstractCommand {

    public SubUpdateGUITitle() {
        this.id = "updateguititle";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{1, 2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        Player whoNeed = player;
        if (args.length == 2) {
            whoNeed = Bukkit.getPlayer(args[1]);
            if (whoNeed == null) {
                LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", args[1]);
                return;
            }
        }
        GUIStatus guiStatus = AbstractGUI.playerList.get(whoNeed);
        if (guiStatus != null && guiStatus.getGUI() != null) {
            if (!(guiStatus.getGUI() instanceof InvGUI invGUI)) {
                LanguageManager.languageManager.sendStringText(player, "gui-not-opened", "player", whoNeed.getName());
                return;
            }
            if (PacketInventoryUtil.packetInventoryUtil != null) {
                PacketInventoryUtil.packetInventoryUtil.updateTitle(whoNeed, invGUI);
                LanguageManager.languageManager.sendStringText(player, "gui-updated", "player", whoNeed.getName());
            }
            return;
        }
        LanguageManager.languageManager.sendStringText(player, "gui-not-opened", "player", whoNeed.getName());
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        if (args.length == 1) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }
        Player whoNeed = Bukkit.getPlayer(args[1]);
        if (whoNeed == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[1]);
            return;
        }
        GUIStatus guiStatus = AbstractGUI.playerList.get(whoNeed);
        if (guiStatus != null && guiStatus.getGUI() != null) {
            if (!(guiStatus.getGUI() instanceof InvGUI invGUI)) {
                LanguageManager.languageManager.sendStringText("gui-not-opened", "player", whoNeed.getName());
                return;
            }
            if (PacketInventoryUtil.packetInventoryUtil != null) {
                PacketInventoryUtil.packetInventoryUtil.updateTitle(whoNeed, invGUI);
                LanguageManager.languageManager.sendStringText("gui-updated", "player", whoNeed.getName());
            }
            return;
        }
        LanguageManager.languageManager.sendStringText("gui-not-opened", "player", whoNeed.getName());
    }
}
