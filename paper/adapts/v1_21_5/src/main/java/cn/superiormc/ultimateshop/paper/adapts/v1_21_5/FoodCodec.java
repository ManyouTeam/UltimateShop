package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FoodCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "food";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection foodKey = section.getConfigurationSection("food");
        if (foodKey != null) {
            FoodProperties.Builder builder = FoodProperties.food();
            if (foodKey.contains("can-always-eat")) {
                builder.canAlwaysEat(foodKey.getBoolean("can-always-eat"));
            }
            int foodNutrition = foodKey.getInt("nutrition", -1);
            if (foodNutrition > 0) {
                builder.nutrition(foodNutrition);
            }
            double foodSaturation = foodKey.getDouble("saturation", -1);
            if (foodSaturation > 0) {
                builder.saturation((float) foodSaturation);
            }
            item.setData(DataComponentTypes.FOOD, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.FOOD)) {
            FoodProperties food = item.getData(DataComponentTypes.FOOD);
            if (food != null) {
                ConfigurationSection foodSection = section.createSection("food");
                foodSection.set("can-always-eat", food.canAlwaysEat());
                foodSection.set("nutrition", food.nutrition());
                foodSection.set("saturation", food.saturation());
            }
        }
    }

}
