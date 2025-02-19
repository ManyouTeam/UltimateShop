package cn.superiormc.ultimateshop.objects.items.subobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ObjectConditionalPlaceholder {

    private final String id;

    private final ConfigurationSection section;

    private final ConfigurationSection valueSection;

    private final ConfigurationSection conditionSection;

    private final ConditionalPlaceholderType mode;

    public ObjectConditionalPlaceholder(String id, ConfigurationSection section) {
        this.id = id;
        this.section = section;
        this.valueSection = section.getConfigurationSection("value");
        this.conditionSection = section.getConfigurationSection("conditions");
        String mode = section.getString("mode", "DEFAULT");
        if (mode.equals("DEFAULT")) {
            this.mode = ConditionalPlaceholderType.DEFAULT;
        } else if (mode.equals("MAX")) {
            this.mode = ConditionalPlaceholderType.MAX;
        } else {
            this.mode = ConditionalPlaceholderType.MIN;
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fLoaded conditional placeholder: " + id + ".yml!");
    }

    public String getID() {
        return id;
    }

    public String getValue(Player player) {
        if (section == null || valueSection == null || conditionSection == null) {
            return "";
        }
        Set<String> groupNameSet = conditionSection.getKeys(false);
        List<Double> result = new ArrayList<>();
        for (String groupName : groupNameSet) {
            ObjectCondition condition = new ObjectCondition(conditionSection.getConfigurationSection(groupName));
            if (condition.getAllBoolean(new ObjectThingRun(player))) {
                if (mode.equals(ConditionalPlaceholderType.DEFAULT)) {
                    return TextUtil.parse(player, valueSection.getString(groupName, ""));
                } else {
                    result.add(valueSection.getDouble(groupName, 1D));
                }
            }
        }
        if (mode.equals(ConditionalPlaceholderType.DEFAULT)) {
            return TextUtil.parse(player, valueSection.getString("default", ""));
        } else {
            result.add(valueSection.getDouble("default", 1D));
        }
        if (mode.equals(ConditionalPlaceholderType.MIN)) {
            return String.valueOf(Collections.min(result));
        }
        return String.valueOf(Collections.max(result));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ObjectConditionalPlaceholder) {
            return ((ObjectConditionalPlaceholder) object).getID().equals(getID());
        }
        return false;
    }

    public static String getNowValue(String id, Player player) {
        if (UltimateShop.freeVersion) {
            return "ERROR: Free Version";
        }
        ObjectConditionalPlaceholder tempVal1 = ConfigManager.configManager.getConditionalPlaceholder(id);
        if (tempVal1 == null) {
            return "Error: Unknown Placeholder";
        }
        return tempVal1.getValue(player);
    }
}

enum ConditionalPlaceholderType {

    DEFAULT,
    MAX,
    MIN
}
