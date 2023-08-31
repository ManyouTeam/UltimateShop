package cn.superiormc.ultimateshop.objects.buttons;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ObjectMoreBuyButton extends AbstractButton {

    private ObjectItem item;


    public ObjectMoreBuyButton(ConfigurationSection config, ObjectItem item) {
        super(config);
        this.item = item;
        this.type = ButtonType.CONFIRM;
    }

    @Override
    public ItemStack getDisplayItem(Player player) {
        ConfigurationSection tempVal1 = config.getConfigurationSection("display-item");
        if (tempVal1 == null) {
            return new ItemStack(Material.BEDROCK);
        }
        return ItemUtil.buildItemStack(tempVal1);
    }
}
