package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectFavouriteEmptyButton extends AbstractButton {

    public ObjectFavouriteEmptyButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.FAVOURITE_EMPTY;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        return new ObjectDisplayItemStack(buildDisplayItem(player, 0, 0));
    }

    public ItemStack buildDisplayItem(Player player, int favouriteAmount, int slotIndex) {
        ConfigurationSection displaySection = config == null ? null : config.getConfigurationSection("display-item");
        if (displaySection == null) {
            return ObjectDisplayItemStack.getAir().getItemStack();
        }
        return BuildItem.buildItemStack(player,
                displaySection,
                displaySection.getInt("amount", 1),
                "favourite-amount", String.valueOf(favouriteAmount),
                "favorite-amount", String.valueOf(favouriteAmount),
                "slot-index", String.valueOf(slotIndex));
    }
}
