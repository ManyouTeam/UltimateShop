package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;

public class ConditionAny extends AbstractCheckCondition {

    public ConditionAny() {
        super("any");
        setRequiredArgs("conditions");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        if (UltimateShop.freeVersion) {
            return true;
        }
        ConfigurationSection anySection = singleCondition.getSection().getConfigurationSection("conditions");
        if (anySection == null) {
            return true;
        }
        ObjectCondition condition = new ObjectCondition(anySection);
        return condition.getAnyBoolean(thingRun);
    }
}
