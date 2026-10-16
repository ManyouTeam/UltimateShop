package cn.superiormc.ultimateshop.paper.adapts.v26_3;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MobVisibility;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import org.bukkit.entity.EntityType;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MobVisibilityCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "mob-visibility";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection value = section.getConfigurationSection("mob-visibility");
        if (value != null) {
            List<TypedKey<EntityType>> entities = value.getStringList("entities").stream()
                    .map(CommonUtil::parseNamespacedKey)
                    .map(key -> TypedKey.create(RegistryKey.ENTITY_TYPE, key))
                    .toList();
            if (!entities.isEmpty()) {
                float visibility = (float) value.getDouble("visibility", 1.0);
                if (visibility >= 0 && visibility <= 10) {
                    item.setData(DataComponentTypes.MOB_VISIBILITY, MobVisibility.mobVisibility(
                            RegistrySet.keySet(RegistryKey.ENTITY_TYPE, entities), visibility));
                }
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.MOB_VISIBILITY)) {
            MobVisibility value = item.getData(DataComponentTypes.MOB_VISIBILITY);
            if (value != null) {
                ConfigurationSection visibility = section.createSection("mob-visibility");
                visibility.set("entities", value.targetingEntityTypes().values().stream()
                        .map(TypedKey::asString).toList());
                visibility.set("visibility", value.visibility());
            }
        }
    }
}
