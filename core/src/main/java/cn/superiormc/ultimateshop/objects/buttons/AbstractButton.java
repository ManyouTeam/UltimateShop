package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.items.ObjectAction;
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

    public Boolean getShowSprite() {
        ConfigurationSection settings = getButtonConfig();
        return settings != null && settings.contains("dialog.show-sprite")
                ? settings.getBoolean("dialog.show-sprite") : null;
    }

    public ButtonType getType() {
        return type;
    }

    public ObjectAction getAction() {
        return null;
    }

    public boolean canDisplay(MenuSender menuSender) {
        return true;
    }

    public boolean isVisibleInMenu(String presentation) {
        ConfigurationSection settings = getButtonConfig();
        if (settings == null || presentation == null) {
            return true;
        }
        boolean fallback = true;
        if ("ore".equals(presentation) || "item-action-list".equals(presentation)) {
            fallback = settings.getBoolean("menu-visibility.dialog", true);
        }
        return settings.getBoolean("menu-visibility." + presentation, fallback);
    }

    public boolean hasShopMenuAction() {
        return hasDirectAction("shop_menu");
    }

    public boolean hasMenuNavigationAction() {
        return hasShopMenuAction() || hasDirectAction("open_menu");
    }

    private boolean hasDirectAction(String type) {
        ConfigurationSection settings = getButtonConfig();
        ConfigurationSection actions = settings == null ? null : settings.getConfigurationSection("actions");
        if (actions != null) {
            for (String key : actions.getKeys(false)) {
                ConfigurationSection action = actions.getConfigurationSection(key);
                if (action != null && type.equalsIgnoreCase(action.getString("type"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCloseAction() {
        return hasDirectAction("close");
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
