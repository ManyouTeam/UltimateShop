package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class ActionDelay extends AbstractRunAction {

    public ActionDelay() {
        super("delay");
        setRequiredArgs("time", "actions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        if (UltimateShop.freeVersion) {
            return;
        }
        ConfigurationSection chanceSection = singleAction.getActionSection().getConfigurationSection("actions");
        if (chanceSection == null) {
            return;
        }
        long time = singleAction.getActionSection().getLong("time");
        ObjectAction action = new ObjectAction(chanceSection);
        if (UltimateShop.isFolia) {
            Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, work -> action.runAllActions(thingRun), time);
        } else {
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> action.runAllActions(thingRun), time);
        }
    }
}
