package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StaticPlaceholder {

    public static String getCompareValue(BigDecimal baseValue, BigDecimal compareValue) {
        if (UltimateShop.freeVersion) {
            return "";
        }
        ConfigurationSection section = ConfigManager.configManager.getSection("placeholder.compare");
        if (section == null) {
            return "";
        }
        if (compareValue.compareTo(baseValue) > 0) {
            return TextUtil.parse(ConfigManager.configManager.getString("placeholder.compare.up", "↑"));
        } else if (compareValue.compareTo(baseValue) == 0) {
            return TextUtil.parse(ConfigManager.configManager.getString("placeholder.compare.same", "-"));
        } else {
            return TextUtil.parse(ConfigManager.configManager.getString("placeholder.compare.down", "↓"));
        }
    }

    public static double getDiscountValue(String papiID, Player player) {
        if (UltimateShop.freeVersion) {
            return 1D;
        }
        ConfigurationSection section = ConfigManager.configManager.getSection("placeholder.discount." + papiID);
        ConfigurationSection conditionSection = ConfigManager.configManager.getSection("placeholder.discount-conditions");
        if (section == null || conditionSection == null) {
            return 1D;
        }
        Set<String> groupNameSet = conditionSection.getKeys(false);
        List<Double> result = new ArrayList<>();
        for (String groupName : groupNameSet) {
            ObjectCondition condition = new ObjectCondition(conditionSection.getStringList(groupName));
            if (condition.getBoolean(player)) {
                result.add(section.getDouble(groupName));
            }
            else {
                if (section.getDouble("default") != 0D) {
                    result.add(section.getDouble("default"));
                }
            }
        }
        if (result.size() == 0) {
            result.add(1D);
        }
        if (section.getString("mode", "MIN").toUpperCase().equals("MIN")) {
            return Collections.min(result);
        }
        return Collections.max(result);
    }
}
