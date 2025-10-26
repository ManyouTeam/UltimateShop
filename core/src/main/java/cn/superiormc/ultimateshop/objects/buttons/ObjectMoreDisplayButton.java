package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectMoreDisplayButton extends AbstractButton {

    private final ObjectItem item;

    public ObjectMoreDisplayButton(ConfigurationSection config, ObjectItem item) {
        super(config);
        this.item = item;
        this.type = ButtonType.DISPLAY;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        ObjectDisplayItemStack tempVal1 = item.getDisplayItem(player, multi);
        if (tempVal1 == null) {
            return ObjectDisplayItemStack.getAir();
        }
        return tempVal1;
    }
}
