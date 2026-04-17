package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;

public class ActionUpdateGUI extends AbstractRunAction {

    public ActionUpdateGUI() {
        super("update_gui");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        GUIStatus guiStatus = MenuStatusManager.menuStatusManager.getGUIStatus(thingRun.getPlayer());
        if (guiStatus != null && guiStatus.getGUI() != null) {
            guiStatus.getGUI().updateGUI();
        }
    }
}
