package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TooltipDisplayCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "hide-tooltip";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        List<String> itemFlagKey = section.getStringList("hide-tooltip");
        if (!itemFlagKey.isEmpty()) {
            TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
            for (String flag : itemFlagKey) {
                DataComponentType type = Registry.DATA_COMPONENT_TYPE.get(CommonUtil.parseNamespacedKey(flag));
                if (type != null) {
                    builder.addHiddenComponents(type);
                }
            }
            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.TOOLTIP_DISPLAY)) {
            TooltipDisplay tooltipDisplay = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
            if (tooltipDisplay != null) {
                List<String> hiddenComponents = tooltipDisplay.hiddenComponents()
                        .stream()
                        .map(DataComponentType::key)
                        .map(Key::asString)
                        .collect(Collectors.toList());
                section.set("hide-tooltip", hiddenComponents);
            }
        }
    }

}
