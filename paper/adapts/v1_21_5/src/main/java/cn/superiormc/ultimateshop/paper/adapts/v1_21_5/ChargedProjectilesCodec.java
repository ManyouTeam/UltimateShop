package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.methods.Items.DebuildItem;
import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.paper.methods.BuildItemPaper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChargedProjectilesCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "charged-projectiles";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection chargedProjectilesKey = section.getConfigurationSection("charged-projectiles");
        if (chargedProjectilesKey != null) {
            ChargedProjectiles.Builder builder = ChargedProjectiles.chargedProjectiles();
            for (String key : chargedProjectilesKey.getKeys(false)) {
                ConfigurationSection contentItemSection = chargedProjectilesKey.getConfigurationSection(key);
                if (contentItemSection != null) {
                    builder.add(BuildItemPaper.buildItemStack(player,
                            contentItemSection,
                            contentItemSection.getInt("amount"),
                            args));
                }
            }
            item.setData(DataComponentTypes.CHARGED_PROJECTILES, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.CHARGED_PROJECTILES)) {
            ChargedProjectiles chargedProjectiles = item.getData(DataComponentTypes.CHARGED_PROJECTILES);
            if (chargedProjectiles != null) {
                ConfigurationSection chargedSection = section.createSection("charged-projectiles");
                int index = 0;
                for (ItemStack subItem : chargedProjectiles.projectiles()) {
                    chargedSection.set(String.valueOf(index++), DebuildItem.debuildItem(subItem, new MemoryConfiguration()));
                }
            }
        }
    }

}
