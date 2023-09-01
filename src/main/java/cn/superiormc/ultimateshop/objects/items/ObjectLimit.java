package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.TextUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ObjectLimit {

    private ConfigurationSection limitSection;

    private ConfigurationSection conditionsSection;

    public ObjectLimit() {
        // Empty
    }

    public ObjectLimit(ConfigurationSection limitSection, ConfigurationSection conditionsSection) {
        this.limitSection = limitSection;
        this.conditionsSection = conditionsSection;
    }

    public int getPlayerLimits(Player player) {
        if (limitSection == null) {
            return -1;
        }
        List<Integer> result = new ArrayList<>();
        for (String conditionName : limitSection.getKeys(false)) {
            if (!conditionName.equals("default") && checkLimitsCondition(conditionName, player)) {
                result.add(MathUtil.doCalculate(
                        TextUtil.withPAPI(limitSection.getString(conditionName, "0"), player)).intValue());
            }
            else {
                if (limitSection.getDouble("default", -1) != -1) {
                    result.add(MathUtil.doCalculate(
                            TextUtil.withPAPI(limitSection.getString("default", "0"), player)).intValue());
                }
            }
        }
        if (result.size() == 0) {
            result.add(0);
        }
        return Collections.max(result);
    }

    public int getServerLimits(Player player) {
        int tempVal2 = getPlayerLimits(player);
        if (limitSection.getDouble("global", -1) != -1) {
            tempVal2 = (MathUtil.doCalculate(
                    TextUtil.withPAPI(limitSection.getString("global", "0"), player)).intValue());
        }
        return tempVal2;
    }

    private boolean checkLimitsCondition(String conditionName, Player player){
        List<String> condition;
        if (!conditionsSection.getStringList(conditionName).isEmpty()) {
            condition = conditionsSection.getStringList(conditionName);
            ObjectCondition tempVal1;
            if (condition.isEmpty()) {
                tempVal1 = new ObjectCondition();
            }
            else {
                tempVal1 = new ObjectCondition(condition);
            }
            return tempVal1.getBoolean(player);
        }
        else {
            return false;
        }
    }
}
