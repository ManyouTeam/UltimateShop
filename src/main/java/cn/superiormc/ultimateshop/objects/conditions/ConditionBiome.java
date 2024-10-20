package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;

public class ConditionBiome extends AbstractCheckCondition {

    public ConditionBiome() {
        super("biome");
        setRequiredArgs("biome");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        return thingRun.getPlayer().getLocation().getBlock().getBiome().name().equals(singleCondition.getString("biome").toUpperCase());
    }
}
