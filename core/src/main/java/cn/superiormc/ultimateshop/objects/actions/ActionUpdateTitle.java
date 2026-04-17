package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.PacketInventoryUtil;

public class ActionUpdateTitle extends AbstractRunAction {

    public ActionUpdateTitle() {
        super("update_title");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        GUIStatus guiStatus = MenuStatusManager.menuStatusManager.getGUIStatus(thingRun.getPlayer());
        if (guiStatus != null && guiStatus.getGUI() != null && guiStatus.getGUI() instanceof InvGUI invGUI) {
            if (UltimateShop.usePacketEvents) {
                PacketInventoryUtil.packetInventoryUtil.updateTitle(thingRun.getPlayer(), invGUI);
            }
        }
    }
}
