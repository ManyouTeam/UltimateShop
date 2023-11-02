package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectButton extends AbstractButton {

    private ObjectShop shop;

    private ObjectAction action;

    private ObjectCondition condition;

    private ObjectDisplayItem displayItem;

    public ObjectButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.COMMON;
        initButton();
    }

    public ObjectButton(ConfigurationSection config, ObjectShop shop) {
        super(config);
        this.type = ButtonType.COMMON;
        this.shop = shop;
        initButton();
    }

    private void initButton() {
        if (shop == null) {
            action = new ObjectAction(config.getStringList("actions"));
        }
        else {
            action = new ObjectAction(config.getStringList("actions"), shop);
        }
        condition = new ObjectCondition(config.getStringList("conditions"));
        displayItem = new ObjectDisplayItem(config.getConfigurationSection("display-item"),
                config.getConfigurationSection("display-item-conditions"));
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (condition != null && !condition.getBoolean(player)) {
            return;
        }
        action.doAction(player, 1, 1);
    }

    @Override
    public ItemStack getDisplayItem(Player player, int unUsed) {
        if (displayItem == null) {
            return new ItemStack(Material.STONE);
        }
        return displayItem.getDisplayItem(player);
    }
}
