package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GetDiscountValue {

    public static double getDiscountLimits(String papiID, Player player) {
        if (UltimateShop.freeVersion) {
            return 1D;
        }
        ConfigurationSection section = ConfigManager.configManager.config.
                getConfigurationSection("placeholder.discount." + papiID);
        ConfigurationSection conditionSection = ConfigManager.configManager.config.
                getConfigurationSection("placeholder.discount-conditions");
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
