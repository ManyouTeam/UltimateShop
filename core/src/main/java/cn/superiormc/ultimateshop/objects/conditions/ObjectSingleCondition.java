package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.objects.AbstractSingleRun;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.configuration.ConfigurationSection;

public class ObjectSingleCondition extends AbstractSingleRun {

    private final ObjectCondition condition;

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection) {
        super(conditionSection);
        this.condition = condition;
    }

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection, ObjectItem item) {
        super(conditionSection, item);
        this.condition = condition;
    }

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection, ObjectShop shop) {
        super(conditionSection, shop);
        this.condition = condition;
    }

    public boolean checkBoolean(ObjectThingRun thingRun) {
        if (startApply >= 0 && thingRun.getTimes() < startApply) {
            return true;
        }
        if (endApply >= 0 && thingRun.getTimes() > startApply) {
            return true;
        }
        if (!apply.isEmpty() && !apply.contains(thingRun.getTimes())) {
            return true;
        }
        if (clickType != null && !clickType.equals(thingRun.getType().name())) {
            return true;
        }
        return ConditionManager.conditionManager.checkBoolean(this, thingRun);
    }

    public ObjectCondition getCondition() {
        return condition;
    }

}
