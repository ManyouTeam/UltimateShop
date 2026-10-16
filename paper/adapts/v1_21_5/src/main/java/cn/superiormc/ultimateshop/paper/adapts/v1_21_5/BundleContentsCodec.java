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

public class BundleContentsCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "bundle-contents";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection bundleContentKey = section.getConfigurationSection("bundle-contents");
        if (bundleContentKey != null) {
            BundleContents.Builder builder = BundleContents.bundleContents();
            for (String key : bundleContentKey.getKeys(false)) {
                ConfigurationSection contentItemSection = bundleContentKey.getConfigurationSection(key);
                if (contentItemSection != null) {
                    builder.add(BuildItemPaper.buildItemStack(player,
                            contentItemSection,
                            contentItemSection.getInt("amount"),
                            args));
                }
            }
            item.setData(DataComponentTypes.BUNDLE_CONTENTS, builder.build());

        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.BUNDLE_CONTENTS)) {
            BundleContents bundleContents = item.getData(DataComponentTypes.BUNDLE_CONTENTS);
            ConfigurationSection bundleSection = section.createSection("bundle-contents");
            int index = 0;
            if (bundleContents != null) {
                for (ItemStack subItem : bundleContents.contents()) {
                    bundleSection.set(String.valueOf(index++), DebuildItem.debuildItem(subItem, new MemoryConfiguration()));
                }
            }
        }
    }

}
