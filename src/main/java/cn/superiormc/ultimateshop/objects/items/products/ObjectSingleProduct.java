package cn.superiormc.ultimateshop.objects.items.products;

import cn.superiormc.ultimateshop.hooks.ItemsHook;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
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

}
