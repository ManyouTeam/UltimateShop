package cn.superiormc.ultimateshop.objects.buttons;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectMoreDisplayButton extends AbstractButton {

    private ObjectItem item;

    public ObjectMoreDisplayButton(ConfigurationSection config, ObjectItem item) {
        super(config);
        this.item = item;
        this.type = ButtonType.DISPLAY;
    }

    @Override
    public ItemStack getDisplayItem(Player player, int multi) {
        ItemStack tempVal1 = item.getDisplayItem(player);
        if (tempVal1 == null) {
            return new ItemStack(Material.STONE);
        }
        tempVal1.setAmount(multi);
        return tempVal1;
    }
}
