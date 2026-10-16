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

public class BreakSoundCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "break-sound";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String breakSoundKey = section.getString("break-sound");
        if (breakSoundKey != null) {
            item.setData(DataComponentTypes.BREAK_SOUND, CommonUtil.parseNamespacedKey(breakSoundKey));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.BREAK_SOUND)) {
            if (item.getData(DataComponentTypes.BREAK_SOUND) != null) {
                section.set("break-sound", item.getData(DataComponentTypes.BREAK_SOUND).asString());
            }
        }
    }

}
