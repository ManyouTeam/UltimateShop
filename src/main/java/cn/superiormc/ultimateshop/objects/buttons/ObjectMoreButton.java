package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectMoreButton extends AbstractButton {

    public ObjectMoreButton(ConfigurationSection config) {
        super(config);
        this.type = ButtonType.SELECT_AMOUNT;
    }

    @Override
    public ItemStack getDisplayItem(Player player, int unUsed) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-item");
        if (tempVal1 == null) {
            return new ItemStack(Material.BEDROCK);
        }
        return ItemUtil.buildItemStack(tempVal1, (int) Double.parseDouble
                (TextUtil.withPAPI(tempVal1.getString("amount", "1"), player)));
    }
}
