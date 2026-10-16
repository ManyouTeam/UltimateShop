package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UseCooldownCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "use-cooldown";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection useCooldown = section.getConfigurationSection("use-cooldown");
        if (useCooldown != null) {
            double cooldownSeconds = useCooldown.getDouble("cooldown-seconds", -1);
            if (cooldownSeconds >= 0) {
                UseCooldown.Builder builder = UseCooldown.useCooldown((float) cooldownSeconds);
                String cooldownGroup = useCooldown.getString("cooldown-group", null);
                if (cooldownGroup != null) {
                    builder.cooldownGroup(CommonUtil.parseNamespacedKey(cooldownGroup));
                }
                item.setData(DataComponentTypes.USE_COOLDOWN, builder.build());
            }
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.USE_COOLDOWN)) {
            UseCooldown cooldown = item.getData(DataComponentTypes.USE_COOLDOWN);
            if (cooldown != null) {
                ConfigurationSection cooldownSection = section.createSection("use-cooldown");
                cooldownSection.set("cooldown-seconds", cooldown.seconds());
                if (cooldown.cooldownGroup() != null) {
                    cooldownSection.set("cooldown-group", cooldown.cooldownGroup().asString());
                }
            }
        }
    }

}
