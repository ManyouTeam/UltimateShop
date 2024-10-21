package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;

public class ActionChance extends AbstractRunAction {

    public ActionChance() {
        super("chance");
        setRequiredArgs("rate", "actions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        if (UltimateShop.freeVersion) {
            return;
        }
        ConfigurationSection chanceSection = singleAction.getSection().getConfigurationSection("actions");
        if (chanceSection == null) {
            return;
        }
        double rate = singleAction.getDouble("rate");
        if (RandomUtils.nextDouble(0, 100) > rate) {
            ObjectAction action = new ObjectAction(chanceSection);
            action.runAllActions(thingRun);
        }
    }
}
