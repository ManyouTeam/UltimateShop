package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DyedColorCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "leather-color";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String colorKey = section.getString("leather-color");
        if (colorKey != null) {
            item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(CommonUtil.parseColor(colorKey)));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.DYED_COLOR)) {
            DyedItemColor dyedColor = item.getData(DataComponentTypes.DYED_COLOR);
            if (dyedColor != null) {
                section.set("leather-color", CommonUtil.colorToString(dyedColor.color()));
            }
        }
    }

}
