package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomModelDataCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "custom-model-data";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection customModelDataSection = section.getConfigurationSection("custom-model-data");
        if (customModelDataSection != null) {
            CustomModelData.Builder builder = CustomModelData.customModelData();
            for (String customModelDataType : customModelDataSection.getKeys(false)) {
                customModelDataType = customModelDataType.toLowerCase();
                switch (customModelDataType) {
                    case "float":
                        builder.addFloats(customModelDataSection.getFloatList("float"));
                        break;
                    case "flag":
                        builder.addFlags(customModelDataSection.getBooleanList("flag"));
                        break;
                    case "string":
                        builder.addStrings(customModelDataSection.getStringList("string"));
                        break;
                    case "color":
                        builder.addColors(CommonUtil.parseColorList(customModelDataSection.getStringList("color")));
                        break;
                }
            }
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.CUSTOM_MODEL_DATA)) {
            CustomModelData cmd = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
            if (cmd != null) {
                ConfigurationSection cmdSection = section.createSection("custom-model-data");
                cmdSection.set("float", cmd.floats());
                cmdSection.set("flag", cmd.flags());
                cmdSection.set("string", cmd.strings());
                cmdSection.set("color", cmd.colors().stream().map(CommonUtil::colorToString).toList());
            }
        }
    }

}
