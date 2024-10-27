package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;

public class ConditionNot extends AbstractCheckCondition {

    public ConditionNot() {
        super("not");
        setRequiredArgs("conditions");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        if (UltimateShop.freeVersion) {
            return true;
        }
        ConfigurationSection notSection = singleCondition.getSection().getConfigurationSection("conditions");
        if (notSection == null) {
            return true;
        }
        ObjectCondition condition = new ObjectCondition(notSection);
        return !condition.getAllBoolean(thingRun);
    }
}
