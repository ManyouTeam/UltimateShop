package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.entity.Player;

import java.util.Random;

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

    public String parseValue() {
        String[] element = value.split("~");
        if (element.length == 1) {
            return value;
        }
        int min = Integer.parseInt(element[0]);
        int max = Integer.parseInt(element[1]);
        Random random = new Random();
        return String.valueOf(random.nextInt(max - min + 1) + min);
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