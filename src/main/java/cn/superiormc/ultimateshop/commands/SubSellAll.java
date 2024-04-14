package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubSellAll extends AbstractCommand {

    public SubSellAll() {
        this.id = "sellall";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        OpenGUI.openSellAllGUI(player);
    }
}
