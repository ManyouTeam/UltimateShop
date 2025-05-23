package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.inv.SellAllGUI;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionSellAllMenu extends AbstractRunAction {

    public ActionSellAllMenu() {
        super("sell_all_menu");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        SellAllGUI.openGUI(player);
    }
}
