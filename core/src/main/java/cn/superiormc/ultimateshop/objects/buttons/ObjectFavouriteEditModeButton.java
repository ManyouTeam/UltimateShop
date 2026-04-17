package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ObjectFavouriteEditModeButton extends AbstractButton {

    public ObjectFavouriteEditModeButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.FAVOURITE_EDIT_MODE;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        return ObjectDisplayItemStack.getAir();
    }

    public ObjectDisplayItemStack getDisplayItem(Player player, boolean editing, int favouriteAmount) {
        if (config == null) {
            return ObjectDisplayItemStack.getAir();
        }
        ConfigurationSection displaySection = config.getConfigurationSection(editing ? "editing-display-item" : "normal-display-item");
        if (displaySection == null) {
            displaySection = config.getConfigurationSection("display-item");
        }
        if (displaySection == null) {
            return ObjectDisplayItemStack.getAir();
        }
        return new ObjectDisplayItemStack(player,
                BuildItem.buildItemStack(player,
                        displaySection,
                        displaySection.getInt("amount", 1),
                        "editing", String.valueOf(editing),
                        "edit-mode", String.valueOf(editing),
                        "favourite-amount", String.valueOf(favouriteAmount),
                        "favorite-amount", String.valueOf(favouriteAmount)),
                displaySection,
                null);
    }
}
