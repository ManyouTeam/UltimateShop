package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ObjectMoreDisplayButton extends AbstractButton {

    private final ObjectItem item;

    public ObjectMoreDisplayButton(ConfigurationSection config, ObjectItem item) {
        super(config);
        this.item = item;
        this.type = ButtonType.DISPLAY;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        ItemStack tempVal1 = item.getDisplayItem(player);
        if (tempVal1 == null) {
            return ObjectDisplayItemStack.getAir();
        }
        if (ConfigManager.configManager.getBoolean("menu.buy-more-menu.display-item-max-stack") &&
        CommonUtil.getMinorVersion(20, 5)) {
            ItemMeta meta = tempVal1.getItemMeta();
            if (meta != null) {
                meta.setMaxStackSize(99);
            }
            tempVal1.setItemMeta(meta);
        }
        tempVal1.setAmount(multi);
        return new ObjectDisplayItemStack(tempVal1);
    }
}
