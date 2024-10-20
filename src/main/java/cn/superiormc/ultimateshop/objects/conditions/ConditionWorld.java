package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ConditionWorld extends AbstractCheckCondition {

    public ConditionWorld() {
        super("world");
        setRequiredArgs("world");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        return player.getWorld().getName().equals(singleCondition.getString("world", player, amount));
    }
}
