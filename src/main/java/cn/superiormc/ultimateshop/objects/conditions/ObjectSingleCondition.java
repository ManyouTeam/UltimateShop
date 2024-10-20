package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ObjectSingleCondition {

    private final ObjectCondition condition;

    private final ConfigurationSection conditionSection;

    private final int startApply;

    private final int endApply;

    private final List<Integer> apply;

    private final String clickType;

    private ObjectShop shop;

    private ObjectItem item;

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection) {
        this.condition = condition;
        this.conditionSection = conditionSection;
        startApply = conditionSection.getInt("start-apply", -1);
        endApply = conditionSection.getInt("end-apply", -1);
        apply = conditionSection.getIntegerList("apply");
        clickType = conditionSection.getString("click-type", null);
    }

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection, ObjectItem item) {
        this.condition = condition;
        this.conditionSection = conditionSection;
        this.item = item;
        this.shop = item.getShopObject();
        startApply = conditionSection.getInt("start-apply", -1);
        endApply = conditionSection.getInt("end-apply", -1);
        apply = conditionSection.getIntegerList("apply");
        clickType = conditionSection.getString("click-type", null);
    }

    public ObjectSingleCondition(ObjectCondition condition, ConfigurationSection conditionSection, ObjectShop shop) {
        this.condition = condition;
        this.conditionSection = conditionSection;
        this.shop = shop;
        startApply = conditionSection.getInt("start-apply", -1);
        endApply = conditionSection.getInt("end-apply", -1);
        apply = conditionSection.getIntegerList("apply");
        clickType = conditionSection.getString("click-type", null);
    }

    public boolean checkBoolean(ObjectThingRun thingRun) {
        if (startApply >= 0 && thingRun.getTimes() < startApply) {
            return true;
        }
        if (endApply >= 0 && thingRun.getTimes() > startApply) {
            return true;
        }
        if (!apply.isEmpty() && !apply.contains(thingRun.getTimes())) {
            return true;
        }
        if (clickType != null && !clickType.equals(thingRun.getType().name())) {
            return true;
        }
        return ConditionManager.conditionManager.checkBoolean(this, thingRun);
    }

    public String getString(String path) {
        return conditionSection.getString(path);
    }

    public List<String> getStringList(String path) {
        return conditionSection.getStringList(path);
    }

    public int getInt(String path) {
        return conditionSection.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return conditionSection.getInt(path, defaultValue);
    }

    public double getDouble(String path) {
        return conditionSection.getDouble(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return conditionSection.getBoolean(path, defaultValue);
    }

    public String getString(String path, Player player, double amount) {
        return replacePlaceholder(conditionSection.getString(path), player, amount);
    }

    private String replacePlaceholder(String content, Player player, double amount) {
        content = CommonUtil.modifyString(content
                ,"world", player.getWorld().getName()
                ,"amount", String.valueOf(amount)
                ,"player_x", String.valueOf(player.getLocation().getX())
                ,"player_y", String.valueOf(player.getLocation().getY())
                ,"player_z", String.valueOf(player.getLocation().getZ())
                ,"player_pitch", String.valueOf(player.getLocation().getPitch())
                ,"player_yaw", String.valueOf(player.getLocation().getYaw())
                ,"player", player.getName());
        content = TextUtil.parse(player, content);
        if (shop != null) {
            content = CommonUtil.modifyString(content, "shop-menu", shop.getShopMenu(),
                    "shop", shop.getShopName(),
                    "shop-name", shop.getShopDisplayName());
        }
        if (item != null) {
            content = CommonUtil.modifyString(content, "item", item.getProduct(),
                    "item-name", item.getDisplayName(player));
        }
        return content;
    }

    public ConfigurationSection getConditionSection() {
        return conditionSection;
    }

    public ObjectCondition getCondition() {
        return condition;
    }

}
