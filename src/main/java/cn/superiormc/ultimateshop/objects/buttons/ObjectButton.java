package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectButton extends AbstractButton {

    private ObjectShop shop;

    private ObjectAction action;

    private ObjectAction failAction;

    private ObjectCondition condition;

    private ObjectDisplayItem displayItem;

    public ObjectButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.COMMON;
        if (config == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: " +
                    "Can not found config for button, there is something wrong in your menu configs!");
            return;
        }
        initButton();
    }

    public ObjectButton(ConfigurationSection config, ObjectShop shop) {
        super(config);
        this.type = ButtonType.COMMON;
        this.shop = shop;
        if (config == null) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: " +
                    "Can not found config for button, there is something wrong in your menu configs!");
            return;
        }
        initButton();
    }

    private void initButton() {
        if (shop == null) {
            action = new ObjectAction(config.getStringList("actions"));
            failAction = new ObjectAction(config.getStringList("fail-actions"));
        }
        else {
            action = new ObjectAction(config.getStringList("actions"), shop);
            failAction = new ObjectAction(config.getStringList("fail-actions"), shop);
        }
        condition = new ObjectCondition(config.getStringList("conditions"));
        displayItem = new ObjectDisplayItem(config.getConfigurationSection("display-item"),
                config.getConfigurationSection("display-item-conditions"));
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (condition != null && !condition.getBoolean(player)) {
            failAction.doAction(player, 1, 1, false, type);
            return;
        }
        if (action != null) {
            action.doAction(player, 1, 1, false, type);
        }
    }

    @Override
    public ItemStack getDisplayItem(Player player, int unUsed) {
        if (displayItem == null) {
            return new ItemStack(Material.AIR);
        }
        return displayItem.getDisplayItem(player);
    }
}
