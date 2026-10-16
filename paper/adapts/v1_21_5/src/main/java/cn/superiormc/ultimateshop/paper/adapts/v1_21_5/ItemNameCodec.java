package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.paper.utils.PaperTextUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemNameCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "item-name";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String itemNameKey = section.getString("item-name");
        if (itemNameKey != null) {
            itemNameKey = CommonUtil.modifyString(player, itemNameKey, args);
            item.setData(DataComponentTypes.ITEM_NAME, PaperTextUtil.modernParse(itemNameKey, player));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.ITEM_NAME)) {
            section.set("item-name", PaperTextUtil.changeToString(item.getData(DataComponentTypes.ITEM_NAME)));
        }
    }

}
