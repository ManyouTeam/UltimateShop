package cn.superiormc.ultimateshop.objects;

import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class AbstractSingleRun {

    protected ConfigurationSection section;

    protected int startApply;

    protected int endApply;

    protected List<Integer> apply;

    protected boolean multiOnce;

    protected boolean sellAllOnce;

    protected boolean openOnce;

    protected String clickType;

    private ObjectShop shop;

    private ObjectItem item;

    public AbstractSingleRun(ConfigurationSection section) {
        this.section = section;
        startApply = section.getInt("start-apply", -1);
        endApply = section.getInt("end-apply", -1);
        apply = section.getIntegerList("apply");
        multiOnce = section.getBoolean("multi-once", false);
        sellAllOnce = section.getBoolean("sell-all-once", false);
        openOnce = section.getBoolean("open-once", false);
        clickType = section.getString("click-type", null);
    }

    public AbstractSingleRun(ConfigurationSection section, ObjectItem item) {
        this.section = section;
        this.item = item;
        this.shop = item.getShopObject();
        startApply = section.getInt("start-apply", -1);
        endApply = section.getInt("end-apply", -1);
        apply = section.getIntegerList("apply");
        multiOnce = section.getBoolean("multi-once", false);
        sellAllOnce = section.getBoolean("sell-all-once", false);
        openOnce = section.getBoolean("open-once", false);
        clickType = section.getString("click-type", null);
    }

    public AbstractSingleRun(ConfigurationSection section, ObjectShop shop) {
        this.section = section;
        this.shop = shop;
        startApply = section.getInt("start-apply", -1);
        endApply = section.getInt("end-apply", -1);
        apply = section.getIntegerList("apply");
        multiOnce = section.getBoolean("multi-once", false);
        sellAllOnce = section.getBoolean("sell-all-once", false);
        openOnce = section.getBoolean("open-once", false);
        clickType = section.getString("click-type", null);
    }

    protected String replacePlaceholder(String content, Player player, double amount) {
        content = CommonUtil.modifyString(content
                ,"world", player.getWorld().getName()
                ,"amount", String.valueOf(amount)
                ,"player_x", String.valueOf(player.getLocation().getX())
                ,"player_y", String.valueOf(player.getLocation().getY())
                ,"player_z", String.valueOf(player.getLocation().getZ())
                ,"player_pitch", String.valueOf(player.getLocation().getPitch())
                ,"player_yaw", String.valueOf(player.getLocation().getYaw())
                ,"player", player.getName());
        content = TextUtil.parse(content, player);
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

    public String getString(String path) {
        return section.getString(path);
    }

    public List<String> getStringList(String path) {
        return section.getStringList(path);
    }

    public int getInt(String path) {
        return section.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return section.getInt(path, defaultValue);
    }

    public double getDouble(String path) {
        return section.getDouble(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return section.getBoolean(path, defaultValue);
    }

    public String getString(String path, Player player, double amount) {
        return replacePlaceholder(section.getString(path), player, amount);
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
