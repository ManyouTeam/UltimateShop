package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantableCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "enchantable";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        int enchantable = section.getInt("enchantable", -1);
        if (enchantable >= 0) {
            item.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantable));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ENCHANTABLE)) {
            Enchantable enchantable = item.getData(DataComponentTypes.ENCHANTABLE);
            if (enchantable != null) {
                section.set("enchantable", enchantable.value());
            }
        }
    }

}
