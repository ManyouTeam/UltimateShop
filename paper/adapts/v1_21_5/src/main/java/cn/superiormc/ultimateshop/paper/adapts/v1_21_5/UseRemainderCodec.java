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

public class UseRemainderCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "use-reminder";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection useReminderKey = section.getConfigurationSection("use-reminder");
        if (useReminderKey != null) {
            item.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(BuildItemPaper.buildItemStack(player, useReminderKey, useReminderKey.getInt("amount"))));
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.USE_REMAINDER)) {
            UseRemainder reminder = item.getData(DataComponentTypes.USE_REMAINDER);
            if (reminder != null) {
                ConfigurationSection reminderSection = section.createSection("use-reminder");
                reminderSection.set("item", DebuildItem.debuildItem(reminder.transformInto(), new MemoryConfiguration()));
            }
        }
    }

}
