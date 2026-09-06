package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class AbstractButton {

    public ConfigurationSection config;

    public ButtonType type;

    public AbstractButton(ConfigurationSection config) {
        this.config = config;
    }

    public AbstractButton() {
        // Empty...
    }

    public abstract ObjectDisplayItemStack getDisplayItem(Player player, int multi);

    public void clickEvent(ClickType type, Player player) {
       return;
    }

    public ConfigurationSection getButtonConfig() {
        return config;
    }

    public ButtonType getType() {
        return type;
    }

    public boolean canDisplay(MenuSender menuSender) {
        return true;
    }

    public boolean hasCloseAction() {
        if (config == null) {
            return false;
        }
        ConfigurationSection actions = config.getConfigurationSection("actions");
        if (actions == null) {
            return false;
        }
        for (String key : actions.getKeys(false)) {
            ConfigurationSection action = actions.getConfigurationSection(key);
            if (action != null && "close".equalsIgnoreCase(action.getString("type"))) {
                return true;
            }
        }
        return false;
    }

    public boolean usesItemActionDialogLayout(String menuLayout) {
        String layout = config == null ? menuLayout : config.getString("dialog.layout", menuLayout);
        return "item-action-list".equalsIgnoreCase(layout);
    }

    @Override
    public String toString() {
        if (config == null) {
            return "Empty Button";
        }
        return "Button Config: " + config.getCurrentPath();
    }
}
