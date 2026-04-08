package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ObjectSearchActionButton extends AbstractButton {

    private final ObjectDisplayItem displayItem;

    private final String actionType;

    public ObjectSearchActionButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.SEARCH_ACTION;
        this.actionType = config == null ? "none" : config.getString("action-type", "none");
        this.displayItem = new ObjectDisplayItem(
                config == null ? null : config.getConfigurationSection("display-item"),
                config == null ? null : config.getConfigurationSection(
                        ConfigManager.configManager.getString("conditions.display-item-key"))
        );
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        if (displayItem == null) {
            return ObjectDisplayItemStack.getAir();
        }
        return displayItem.getDisplayItem(player);
    }

    public String getActionType() {
        return actionType == null ? "none" : actionType.toLowerCase(Locale.ROOT);
    }
}
