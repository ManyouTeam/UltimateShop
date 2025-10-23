package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionTitle extends AbstractRunAction {

    public ActionTitle() {
        super("title");
        setRequiredArgs("main-title", "sub-title", "fade-in", "stay", "fade-out");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        UltimateShop.methodUtil.sendTitle(player,
                singleAction.getString("main-title", player, amount),
                singleAction.getString("sub-title", player, amount),
                singleAction.getInt("fade-in"),
                singleAction.getInt("stay"),
                singleAction.getInt("fade-out"));
    }
}
