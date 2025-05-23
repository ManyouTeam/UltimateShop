package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;

public class ActionConditional extends AbstractRunAction {

    public ActionConditional() {
        super("conditional");
        setRequiredArgs("actions", "conditions");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        if (UltimateShop.freeVersion) {
            return;
        }
        ConfigurationSection conditionSection = singleAction.getSection().getConfigurationSection("conditions");
        if (conditionSection == null) {
            return;
        }
        ObjectCondition condition = new ObjectCondition(conditionSection);
        if (!condition.getAllBoolean(thingRun)) {
            return;
        }
        ConfigurationSection actionSection = singleAction.getSection().getConfigurationSection("actions");
        if (actionSection == null) {
            return;
        }
        ObjectAction action = new ObjectAction(actionSection);
        action.runAllActions(thingRun);
    }
}
