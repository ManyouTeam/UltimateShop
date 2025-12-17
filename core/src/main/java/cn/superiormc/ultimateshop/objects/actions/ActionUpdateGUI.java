package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;

public class ActionUpdateGUI extends AbstractRunAction {

    public ActionUpdateGUI() {
        super("update_gui");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        GUIStatus guiStatus = AbstractGUI.playerList.get(thingRun.getPlayer());
        if (guiStatus != null && guiStatus.getGUI() != null) {
            guiStatus.getGUI().constructGUI();
        }
    }
}
