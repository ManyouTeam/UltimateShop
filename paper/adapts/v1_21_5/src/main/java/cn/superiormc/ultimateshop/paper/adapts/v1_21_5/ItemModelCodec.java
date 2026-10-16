package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemModelCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "item-model";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String itemModel = section.getString("item-model", null);
        if (itemModel != null) {
            item.setData(DataComponentTypes.ITEM_MODEL, CommonUtil.parseNamespacedKey(itemModel));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) {
            Key modelKey = item.getData(DataComponentTypes.ITEM_MODEL);
            if (modelKey != null) {
                section.set("item-model", modelKey.asString());
            }
        }
    }

}
