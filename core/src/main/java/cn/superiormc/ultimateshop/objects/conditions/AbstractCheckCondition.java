package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;

public abstract class AbstractCheckCondition {

    private final String type;

    private String[] requiredArgs;

    public AbstractCheckCondition(String type) {
        this.type = type;
    }

    protected void setRequiredArgs(String... requiredArgs) {
        this.requiredArgs = requiredArgs;
    }

    public boolean checkCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        if (requiredArgs != null) {
            for (String arg : requiredArgs) {
                if (!singleCondition.getSection().contains(arg)) {
                    ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your condition missing required arg: " + arg + ".");
                    return true;
                }
            }
        }
        return onCheckCondition(singleCondition, thingRun);
    }

    protected abstract boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun);

    public String getType() {
        return type;
    }
}
