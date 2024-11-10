package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
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
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

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
            action = new ObjectAction(config.getConfigurationSection("actions"));
            failAction = new ObjectAction(config.getConfigurationSection("fail-actions"));
        } else {
            action = new ObjectAction(config.getConfigurationSection("actions"), shop);
            failAction = new ObjectAction(config.getConfigurationSection("fail-actions"), shop);
        }
        condition = new ObjectCondition(config.getConfigurationSection("conditions"));
        displayItem = new ObjectDisplayItem(config.getConfigurationSection("display-item"),
                config.getConfigurationSection("display-item-conditions"));
    }

    @Override
    public void clickEvent(ClickType type, Player player) {
        if (condition != null && !condition.getAllBoolean(new ObjectThingRun(player, type))) {
            failAction.runAllActions(new ObjectThingRun(player, type));
            return;
        }
        if (action != null) {
            action.runAllActions(new ObjectThingRun(player, type));
        }
    }

    @Override
    public ItemStack getDisplayItem(Player player, int unUsed) {
        if (displayItem == null) {
            return new ItemStack(Material.AIR);
        }
        return displayItem.getDisplayItem(player);
    }

    /*@Override
    public ButtonComponent getBedrockButton(Player player, int multi) {
        String icon = config.getString("bedrock.icon", config.getString("bedrock-icon"));
        ItemStack displayItem = getDisplayItem(player, multi);
        if (ItemUtil.getItemNameWithoutVanilla(displayItem).trim().isEmpty() ||
                config.getBoolean("bedrock.hide", false)) {
            return null;
        }
        String tempVal3 = TextUtil.parse(ItemUtil.getItemName(displayItem), player);
        ButtonComponent tempVal1 = null;
        if (icon != null && icon.split(";;").length == 2) {
            String type = icon.split(";;")[0].toLowerCase();
            if (type.equals("url")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
            } else if (type.equals("path")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
            }
        } else {
            tempVal1 = ButtonComponent.of(tempVal3);
        }
        return tempVal1;
    }
     */


}
