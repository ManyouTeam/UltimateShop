package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.gui.inv.SellAllGUI;
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
        SellAllGUI.openGUI(player);
    }
}
