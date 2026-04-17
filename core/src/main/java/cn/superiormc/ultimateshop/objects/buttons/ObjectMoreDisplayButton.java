package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.subobjects.ObjectDisplayItemStack;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ObjectMoreDisplayButton extends AbstractButton {

    private final ObjectItem item;

    private final ObjectDisplayItem paddingItem;

    private final int slotIndex;

    public ObjectMoreDisplayButton(ConfigurationSection config,
                                   ConfigurationSection paddingItemConfig,
                                   ObjectItem item,
                                   int slotIndex) {
        super(config);
        this.item = item;
        this.slotIndex = slotIndex;
        this.paddingItem = paddingItemConfig == null ? null : new ObjectDisplayItem(paddingItemConfig, null);
        this.type = ButtonType.BUY_MORE_DISPLAY;
    }

    @Override
    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        ItemStack tempVal1 = item.getDisplayItem(player);
        if (tempVal1 == null) {
            return ObjectDisplayItemStack.getAir();
        }
        tempVal1 = tempVal1.clone();
        int maxStack;
        if (ConfigManager.configManager.getBoolean("menu.buy-more-menu.display-item-max-stack") &&
        CommonUtil.getMinorVersion(20, 5)) {
            ItemMeta meta = tempVal1.getItemMeta();
            if (meta != null) {
                meta.setMaxStackSize(99);
            }
            tempVal1.setItemMeta(meta);
            maxStack = 99;
        } else {
            maxStack = tempVal1.getMaxStackSize();
        }
        int displayAmount = multi - (slotIndex * maxStack);
        if (displayAmount <= 0) {
            if (slotIndex > 0 && paddingItem != null) {
                return paddingItem.getDisplayItem(player);
            }
            return ObjectDisplayItemStack.getAir();
        }
        tempVal1.setAmount(Math.min(displayAmount, maxStack));
        return new ObjectDisplayItemStack(tempVal1);
    }
}
