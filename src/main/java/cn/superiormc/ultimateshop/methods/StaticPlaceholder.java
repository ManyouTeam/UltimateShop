package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
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
            return "ERROR: Free Version";
        }
        ConfigurationSection section = ConfigManager.configManager.getSection("placeholder.compare");
        if (section == null) {
            return "ERROR: Unknown Placeholder";
        }
        if (compareValue.compareTo(baseValue) > 0) {
            return TextUtil.parse(CommonUtil.modifyString(ConfigManager.configManager.getString("placeholder.compare.up", "↑"),
                    "base", baseValue.toString(), "compare", compareValue.toString()));
        } else if (compareValue.compareTo(baseValue) == 0) {
            return TextUtil.parse(CommonUtil.modifyString(ConfigManager.configManager.getString("placeholder.compare.same", "-"),
                    "base", baseValue.toString(), "compare", compareValue.toString()));
        } else {
            return TextUtil.parse(CommonUtil.modifyString(ConfigManager.configManager.getString("placeholder.compare.down", "↓"),
                    "base", baseValue.toString(), "compare", compareValue.toString()));
        }
    }
}
