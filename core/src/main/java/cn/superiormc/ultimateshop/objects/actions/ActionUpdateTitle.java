package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;

public class ActionUpdateTitle extends AbstractRunAction {

    public ActionUpdateTitle() {
        super("update_title");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        GUIStatus guiStatus = AbstractGUI.playerList.get(thingRun.getPlayer());
        if (guiStatus != null && guiStatus.getGUI() != null && guiStatus.getGUI() instanceof InvGUI invGUI) {
            if (PacketInventoryUtil.packetInventoryUtil != null) {
                PacketInventoryUtil.packetInventoryUtil.updateTitle(thingRun.getPlayer(), invGUI);
            }
        }
    }
}
