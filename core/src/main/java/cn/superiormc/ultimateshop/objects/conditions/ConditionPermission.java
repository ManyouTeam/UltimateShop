package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.CommonUtil;

public class ConditionPermission extends AbstractCheckCondition {

    public ConditionPermission() {
        super("permission");
        setRequiredArgs("permission");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        return CommonUtil.checkPermission(thingRun.getPlayer(), singleCondition.getString("permission"));
    }
}
