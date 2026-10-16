package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

public class RarityCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "rarity";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String rarityKey = section.getString("rarity");
        if (rarityKey != null) {
            item.setData(DataComponentTypes.RARITY, Enums.getIfPresent(ItemRarity.class, rarityKey).or(ItemRarity.COMMON));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.RARITY)) {
            ItemRarity rarity = item.getData(DataComponentTypes.RARITY);
            if (rarity != null) {
                section.set("rarity", rarity.name());
            }
        }
    }

}
