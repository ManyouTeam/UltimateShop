package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.gui.inv.SearchGUI;
import org.bukkit.entity.Player;

public class SubSearchGUI extends AbstractCommand {

    public SubSearchGUI() {
        this.id = "searchgui";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = true;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        SearchGUI.openGUI(player);
    }
}
