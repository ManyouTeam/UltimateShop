package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageResistantCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "damage-resistant";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String damageResistant = section.getString("damage-resistant");
        if (damageResistant != null) {
            item.setData(DataComponentTypes.DAMAGE_RESISTANT,
                    DamageResistant.damageResistant(TagKey.create(RegistryKey.DAMAGE_TYPE, damageResistant)));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.DAMAGE_RESISTANT)) {
            DamageResistant resistant = item.getData(DataComponentTypes.DAMAGE_RESISTANT);
            if (resistant != null) {
                section.set("damage-resistant", resistant.types().key().asString());
            }
        }
    }
}
