package cn.superiormc.ultimateshop.paper.adapts.v26_3;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.block.pot.PotPatternType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProvidesPotteryPatternCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "provides-pottery-pattern";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        String key = section.getString("provides-pottery-pattern");
        if (key != null) {
            PotPatternType pattern = RegistryAccess.registryAccess()
                    .getRegistry(RegistryKey.DECORATED_POT_PATTERN)
                    .get(CommonUtil.parseNamespacedKey(key));
            if (pattern != null) {
                item.setData(DataComponentTypes.PROVIDES_POTTERY_PATTERN, pattern);
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.PROVIDES_POTTERY_PATTERN)) {
            PotPatternType pattern = item.getData(DataComponentTypes.PROVIDES_POTTERY_PATTERN);
            if (pattern != null) {
                section.set("provides-pottery-pattern", RegistryAccess.registryAccess()
                        .getRegistry(RegistryKey.DECORATED_POT_PATTERN).getKey(pattern).asString());
            }
        }
    }
}
