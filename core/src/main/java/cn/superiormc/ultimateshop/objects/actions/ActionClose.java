package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.entity.Player;

public class ActionClose extends AbstractRunAction {

    public ActionClose() {
        super("close");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        AbstractGUI gui = MenuStatusManager.menuStatusManager.getOpeningGUI(player);
        if (gui == null) {
            SchedulerUtil.runTaskLater(player, player::closeInventory, 2L);
        } else {
            gui.closeGUI();
        }
    }
}
