package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectMoreBuyButton extends AbstractButton {

    private ObjectItem item;


    public ObjectMoreBuyButton(ConfigurationSection config, ObjectItem item) {
        super(config);
        this.item = item;
        this.type = ButtonType.CONFIRM;
    }

    @Override
    public ItemStack getDisplayItem(Player player, int multi) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-item");
        if (tempVal1 == null) {
            return new ItemStack(Material.BEDROCK);
        }
        ItemStack addLoreDisplayItem = ItemUtil.buildItemStack(tempVal1, Integer.parseInt
                (TextUtil.withPAPI(tempVal1.getString("amount", "1"), player)));
        return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item);
    }
}
