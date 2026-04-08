package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectSearchStateButton extends AbstractButton {

    public ObjectSearchStateButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.SEARCH_STATE;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        return new ObjectDisplayItemStack(buildDisplayItem(player, false, 0, 0, 0, ""));
    }

    public ItemStack buildDisplayItem(Player player,
                                      boolean hasSearchFilters,
                                      int resultAmount,
                                      int showingAmount,
                                      int inputAmount,
                                      String nameKeyword) {
        String path = hasSearchFilters ? "has-input.display-item" : "empty-input.display-item";
        ConfigurationSection displaySection = config == null ? null : config.getConfigurationSection(path);
        if (displaySection == null) {
            return ObjectDisplayItemStack.getAir().getItemStack();
        }
        return BuildItem.buildItemStack(player,
                displaySection,
                displaySection.getInt("amount", 1),
                "result-amount", String.valueOf(resultAmount),
                "showing-amount", String.valueOf(showingAmount),
                "input-amount", String.valueOf(inputAmount),
                "name-keyword", nameKeyword);
    }
}
