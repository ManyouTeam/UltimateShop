package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import com.google.common.base.Enums;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AxolotlVariantCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "axolotl-variant";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String variantStr = section.getString("axolotl-variant");
        if (variantStr != null) {
            Axolotl.Variant variant = Enums.getIfPresent(Axolotl.Variant.class, variantStr.toUpperCase()).orNull();
            if (variant != null) {
                item.setData(DataComponentTypes.AXOLOTL_VARIANT, variant);
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.AXOLOTL_VARIANT)) {
            Axolotl.Variant axolotlVariant = item.getData(DataComponentTypes.AXOLOTL_VARIANT);
            if (axolotlVariant != null) {
                section.set("axolotl-variant", axolotlVariant.name());
            }
        }
    }

}
