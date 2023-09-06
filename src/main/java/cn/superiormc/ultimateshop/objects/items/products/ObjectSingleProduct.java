package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectSingleProduct extends AbstractSingleThing {

    public ObjectSingleProduct() {
        super();
    }

    public ObjectSingleProduct(ConfigurationSection singleSection) {
        super(singleSection);
    }

    public String getDisplayName(double amount) {
        if (singleSection == null) {
            return ConfigManager.configManager.getString("placeholder.price.unknown");
        }
        String tempVal1 = singleSection.getString("placeholder",
                ConfigManager.configManager.getString("placeholder.price.unknown"));
        return CommonUtil.modifyString(tempVal1,
                "amount",
                String.valueOf(amount));
    }

}
