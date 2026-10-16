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

public class StoredEnchantsCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "stored-enchants";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection storedEnchantsKey = section.getConfigurationSection("stored-enchants");
        if (storedEnchantsKey != null) {
            ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();
            for (String ench : storedEnchantsKey.getKeys(false)) {
                Enchantment vanillaEnchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(CommonUtil.parseNamespacedKey(ench.toLowerCase()));
                if (vanillaEnchant != null) {
                    builder.add(vanillaEnchant, storedEnchantsKey.getInt(ench, 1));
                }
            }
            item.setData(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.STORED_ENCHANTMENTS)) {
            ItemEnchantments enchants = item.getData(DataComponentTypes.STORED_ENCHANTMENTS);
            if (enchants != null) {
                ConfigurationSection enchantsSection = section.createSection("stored-enchants"); // 
                enchants.enchantments().forEach((enchantment, level) -> {
                    Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getKey(enchantment);
                    if (key != null) {
                        enchantsSection.set(key.asString(), level); // ?
                    }
                });
            }
        }
    }

}
