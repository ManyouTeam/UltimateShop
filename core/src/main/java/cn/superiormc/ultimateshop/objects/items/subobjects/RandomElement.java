package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.entity.Player;

public class RandomElement {

    private final String value; // 元素名称

    private final int rate; // 权重

    private final ObjectCondition condition; // 条件

    public RandomElement(String value, int rate, ObjectCondition condition) {
        this.value = value;
        this.rate = rate;
        this.condition = condition;
    }

    public String getValue() {
        return value;
    }

    public int getRate() {
        return rate;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

    public boolean isAvailable(Player player) {
        return condition.getAllBoolean(new ObjectThingRun(player));
    }
}