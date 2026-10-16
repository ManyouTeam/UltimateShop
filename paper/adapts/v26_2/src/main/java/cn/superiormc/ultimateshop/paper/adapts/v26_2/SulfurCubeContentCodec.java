package cn.superiormc.ultimateshop.paper.adapts.v26_2;

import cn.superiormc.ultimateshop.methods.Items.DebuildItem;
import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SulfurCubeContentCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "sulfur-cube-content";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection content = section.getConfigurationSection("sulfur-cube-content");
        if (content != null) {
            ItemStack absorbed = BuildItem.buildItemStack(player, content, 1, args);
            item.setData(DataComponentTypes.SULFUR_CUBE_CONTENT, SulfurCubeContent.sulfurCubeContent(absorbed));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.SULFUR_CUBE_CONTENT)) {
            SulfurCubeContent content = item.getData(DataComponentTypes.SULFUR_CUBE_CONTENT);
            if (content != null) {
                section.set("sulfur-cube-content", DebuildItem.debuildItem(content.absorbedItem(), new MemoryConfiguration()));
            }
        }
    }

}
