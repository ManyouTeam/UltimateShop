package cn.superiormc.ultimateshop.methods;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class StaticPlaceholder {

    public static String getCompareValue(Player player, BigDecimal baseValue, BigDecimal compareValue) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ConfigurationSection section = ConfigManager.configManager.getSection("placeholder.compare");
        if (section == null) {
            return "ERROR: Unknown Placeholder";
        }
        if (compareValue.compareTo(baseValue) > 0) {
            return CommonUtil.modifyString(ConfigManager.configManager.getString(player, "placeholder.compare.up", "↑"),
                    "base", baseValue.toString(), "compare", compareValue.toString());
        } else if (compareValue.compareTo(baseValue) == 0) {
            return CommonUtil.modifyString(ConfigManager.configManager.getString(player, "placeholder.compare.same", "-"),
                    "base", baseValue.toString(), "compare", compareValue.toString());
        } else {
            return CommonUtil.modifyString(ConfigManager.configManager.getString(player, "placeholder.compare.down", "↓"),
                    "base", baseValue.toString(), "compare", compareValue.toString());
        }
    }
}
