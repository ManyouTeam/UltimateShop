package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class ObjectSingleAction {

    private final ObjectAction action;

    private final ConfigurationSection actionSection;

    private final int startApply;

    private final int endApply;

    private final List<Integer> apply;

    private final boolean multiOnce;

    private final boolean sellAllOnce;

    private final String clickType;

    private ObjectShop shop;

    private ObjectItem item;

    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection) {
        this.action = action;
        this.actionSection = actionSection;
        startApply = actionSection.getInt("start-apply", -1);
        endApply = actionSection.getInt("end-apply", -1);
        apply = actionSection.getIntegerList("apply");
        multiOnce = actionSection.getBoolean("multi-once", false);
        sellAllOnce = actionSection.getBoolean("sell-all-once", false);
        clickType = actionSection.getString("click-type", null);
    }

    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection, ObjectItem item) {
        this.action = action;
        this.actionSection = actionSection;
        this.item = item;
        this.shop = item.getShopObject();
        startApply = actionSection.getInt("start-apply", -1);
        endApply = actionSection.getInt("end-apply", -1);
        apply = actionSection.getIntegerList("apply");
        multiOnce = actionSection.getBoolean("multi-once", false);
        sellAllOnce = actionSection.getBoolean("sell-all-once", false);
        clickType = actionSection.getString("click-type", null);
    }

    public ObjectSingleAction(ObjectAction action, ConfigurationSection actionSection, ObjectShop shop) {
        this.action = action;
        this.actionSection = actionSection;
        this.shop = shop;
        startApply = actionSection.getInt("start-apply", -1);
        endApply = actionSection.getInt("end-apply", -1);
        apply = actionSection.getIntegerList("apply");
        multiOnce = actionSection.getBoolean("multi-once", false);
        sellAllOnce = actionSection.getBoolean("sell-all-once", false);
        clickType = actionSection.getString("click-type", null);
    }

    public void doAction(ObjectThingRun thingRun) {
        if (startApply >= 0 && thingRun.getTimes() < startApply) {
            return;
        }
        if (endApply >= 0 && thingRun.getTimes() > startApply) {
            return;
        }
        if (!apply.isEmpty() && !apply.contains(thingRun.getTimes())) {
            return;
        }
        if (sellAllOnce && thingRun.getSellAll()) {
            return;
        }
        if (clickType != null && !clickType.equals(thingRun.getType().name())) {
            return;
        }
        ActionManager.actionManager.doAction(this, thingRun);
    }

    public boolean isMultiOnce() {
        return multiOnce;
    }

    public boolean isSellAllOnce() {
        return sellAllOnce;
    }

    public String getString(String path) {
        return actionSection.getString(path);
    }

    public int getInt(String path) {
        return actionSection.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return actionSection.getInt(path, defaultValue);
    }

    public double getDouble(String path) {
        return actionSection.getDouble(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return actionSection.getBoolean(path, defaultValue);
    }

    public String getString(String path, Player player, double amount) {
        return replacePlaceholder(actionSection.getString(path), player, amount);
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
        if (CommonUtil.checkPluginLoad("PlaceholderAPI")) {
            content = PlaceholderAPI.setPlaceholders(player, content);
        }
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

    public ConfigurationSection getActionSection() {
        return actionSection;
    }

    public void setLastTradeStatus(ProductTradeStatus status) {
        this.action.setLastTradeStatus(status);
    }

    public ObjectAction getAction() {
        return action;
    }

}
