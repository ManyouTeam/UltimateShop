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

public class NameCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "name";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String displayNameKey = section.getString("name", section.getString("display"));
        if (displayNameKey != null) {
            displayNameKey = CommonUtil.modifyString(player, displayNameKey, args);
            item.setData(DataComponentTypes.CUSTOM_NAME, PaperTextUtil.modernParse(displayNameKey, player));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.CUSTOM_NAME)) {
            section.set("name", PaperTextUtil.changeToString(item.getData(DataComponentTypes.CUSTOM_NAME)));
        }
    }

}
