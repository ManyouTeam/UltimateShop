package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.api.ShopHelper;
import org.bukkit.entity.Player;

public class SubSellHand extends AbstractCommand {

    public SubSellHand() {
        this.id = "sellhand";
        this.requiredPermission = "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ShopHelper.sellMainHandStack(player);
    }
}
