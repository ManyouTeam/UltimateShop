package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionOpenMenu extends AbstractRunAction {

    public ActionOpenMenu() {
        super("open_menu");
        setRequiredArgs("menu");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        OpenGUI.openCommonGUI(player, singleAction.getString("menu", player, amount), false, true);
    }
}
