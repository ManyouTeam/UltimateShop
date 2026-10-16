package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "trim";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection trimKey = section.getConfigurationSection("trim");
        if (trimKey != null) {
            String trimMaterialKey = trimKey.getString("material");
            String trimPatternKey = trimKey.getString("pattern");
            if (trimMaterialKey != null && trimPatternKey != null) {
                NamespacedKey trimMaterialNamespacedKey = CommonUtil.parseNamespacedKey(trimMaterialKey);
                NamespacedKey trimPatternNamespacedKey = CommonUtil.parseNamespacedKey(trimPatternKey);
                if (trimMaterialNamespacedKey != null && trimPatternNamespacedKey != null) {
                    TrimMaterial trimMaterial = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(trimMaterialNamespacedKey);
                    TrimPattern trimPattern = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(trimPatternNamespacedKey);
                    if (trimMaterial != null && trimPattern != null) {
                        item.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(new ArmorTrim(trimMaterial, trimPattern)));
                    }
                }
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.TRIM)) {
            ItemArmorTrim armorTrim = item.getData(DataComponentTypes.TRIM);
            ConfigurationSection trimSection = section.createSection("trim");
            if (armorTrim != null) {
                NamespacedKey materialKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)
                        .getKey(armorTrim.armorTrim().getMaterial());
                NamespacedKey patternKey = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN)
                        .getKey(armorTrim.armorTrim().getPattern());
                if (materialKey != null) {
                    trimSection.set("material", materialKey.asString());
                }
                if (patternKey != null) {
                    trimSection.set("pattern", patternKey.asString());
                }
            }
        }
    }

}
