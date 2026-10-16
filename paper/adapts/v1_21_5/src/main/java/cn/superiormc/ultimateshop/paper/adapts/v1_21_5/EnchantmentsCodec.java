package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.*;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantmentsCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "enchants";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection enchantsKey = section.getConfigurationSection("enchants");
        if (enchantsKey != null) {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
            for (String ench : enchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(CommonUtil.parseNamespacedKey(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    builder.add(vanillaEnchant, enchantsKey.getInt(ench, 1));
                }
            }
            item.setData(DataComponentTypes.ENCHANTMENTS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ENCHANTMENTS)) {
            ItemEnchantments enchants = item.getData(DataComponentTypes.ENCHANTMENTS);
            if (enchants != null) {
                ConfigurationSection enchantsSection = section.createSection("enchants"); // 
                enchants.enchantments().forEach((enchantment, level) -> {
                    Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getKey(enchantment);
                    if (key != null) {
                        enchantsSection.set(key.asString(), level);
                    }
                });
            }
        }
    }

}
