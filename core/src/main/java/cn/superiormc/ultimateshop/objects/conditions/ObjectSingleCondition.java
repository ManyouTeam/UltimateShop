package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.objects.AbstractSingleRun;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;

public class ObjectSingleCondition extends AbstractSingleRun {

    private final ObjectCondition condition;

    private ObjectAction notMeetActions;

    private ObjectAction meetActions;

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection) {
        super(conditionSection);
        this.condition = condition;
        initActions();
    }

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection, ObjectItem item) {
        super(conditionSection, item);
        this.condition = condition;
        initActions();
    }

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection, ObjectShop shop) {
        super(conditionSection, shop);
        this.condition = condition;
        initActions();
    }

    private void initActions() {
        this.meetActions = new ObjectAction(section.getConfigurationSection("meet-actions"));
        this.notMeetActions = new ObjectAction(section.getConfigurationSection("not-meet-actions"));
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
        if (thingRun.getPlayer() != null && bedrockOnly && !CommonUtil.isBedrockPlayer(thingRun.getPlayer())) {
            return true;
        }
        if (thingRun.getPlayer() != null && javaOnly && CommonUtil.isBedrockPlayer(thingRun.getPlayer())) {
            return true;
        }
        boolean result = ConditionManager.conditionManager.checkBoolean(this, thingRun);
        if (!UltimateShop.freeVersion) {
            if (result) {
                meetActions.runAllActions(thingRun);
            } else {
                notMeetActions.runAllActions(thingRun);
            }
        }
        return result;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

}
