package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionBack extends AbstractRunAction {

    public ActionBack() {
        super("back");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        if (player != null) {
            String fallbackMenu = singleAction.contains("menu")
                    ? singleAction.getString("menu", player, thingRun.getAmount())
                    : null;
            String fallbackShop = singleAction.contains("shop")
                    ? singleAction.getString("shop", player, thingRun.getAmount())
                    : null;
            MenuStatusManager.menuStatusManager.openPreviousGUI(player, fallbackMenu, fallbackShop);
        }
    }
}
