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

public class LoreCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "lore";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        List<String> lores = section.getStringList("lore");
        if (!lores.isEmpty()) {
            ItemLore.Builder builder = ItemLore.lore();
            for (String lore : lores) {
                lore = CommonUtil.modifyString(player, lore, args);
                for (String singleLore : lore.split("\\\\n")) {
                    builder.addLine(PaperTextUtil.modernParse(singleLore, player));
                }
            }
            item.setData(DataComponentTypes.LORE, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.LORE)) {
            ItemLore lore = item.getData(DataComponentTypes.LORE);
            if (lore != null) {
                List<String> lores = lore.lines().stream()
                        .map(PaperTextUtil::changeToString)
                        .toList();
                section.set("lore", lores);
            }
        }
    }

}
