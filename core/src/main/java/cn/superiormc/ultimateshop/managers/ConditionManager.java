package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.actions.*;
import cn.superiormc.ultimateshop.objects.conditions.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConditionManager {

    public static ConditionManager conditionManager;

    private Map<String, AbstractCheckCondition> conditions;

    public ConditionManager() {
        conditionManager = this;
        initConditions();
    }

    private void initConditions() {
        conditions = new HashMap<>();
        registerNewCondition("biome", new ConditionBiome());
        registerNewCondition("permission", new ConditionPermission());
        registerNewCondition("placeholder", new ConditionPlaceholder());
        registerNewCondition("world", new ConditionWorld());
        registerNewCondition("any", new ConditionAny());
        registerNewCondition("not", new ConditionNot());
    }

    public void registerNewCondition(String actionID,
                                  AbstractCheckCondition condition) {
        if (!conditions.containsKey(actionID)) {
            conditions.put(actionID, condition);
        }
    }

    public boolean checkBoolean(ObjectSingleCondition condition, ObjectThingRun thingRun) {
        for (AbstractCheckCondition checkCondition : conditions.values()) {
            String type = condition.getString("type");
            if (checkCondition.getType().equals(type) && !checkCondition.checkCondition(condition, thingRun)) {
                return false;
            }
        }
        return true;
    }
}
