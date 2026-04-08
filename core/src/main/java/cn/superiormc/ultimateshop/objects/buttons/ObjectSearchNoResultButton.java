package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectSearchNoResultButton extends AbstractButton {

    public ObjectSearchNoResultButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.SEARCH_NO_RESULT;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        return new ObjectDisplayItemStack(buildDisplayItem(player, 0, ""));
    }

    public ItemStack buildDisplayItem(Player player, int inputAmount, String nameKeyword) {
        ConfigurationSection displaySection = config == null ? null : config.getConfigurationSection("display-item");
        if (displaySection == null) {
            return ObjectDisplayItemStack.getAir().getItemStack();
        }
        return BuildItem.buildItemStack(player,
                displaySection,
                displaySection.getInt("amount", 1),
                "input-amount", String.valueOf(inputAmount),
                "name-keyword", nameKeyword);
    }
}
