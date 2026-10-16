package cn.superiormc.ultimateshop.paper.adapts.v1_21_5;

import cn.superiormc.ultimateshop.paper.adapts.ComponentCodec;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToolCodec implements ComponentCodec {

    @Override
    public String getIdentifier() {
        return "tool";
    }

    @Override
    public void apply(ItemStack item, Player player, ConfigurationSection section, String... args) {
        ConfigurationSection toolKey = section.getConfigurationSection("tool");
        if (toolKey != null) {
            Tool.Builder builder = Tool.tool();
            int damagePerBlock = toolKey.getInt("damage-per-block", -1);
            if (damagePerBlock >= 0) {
                builder.damagePerBlock(damagePerBlock);
            }
            double miningSpeed = toolKey.getDouble("mining-speed", -1);
            if (miningSpeed > 0) {
                builder.defaultMiningSpeed((float) miningSpeed);
            }
            if (toolKey.contains("destroy-blocks-in-creative")) {
                builder.canDestroyBlocksInCreative(toolKey.getBoolean("destroy-blocks-in-creative"));
            }
            for (String rules : toolKey.getStringList("rules")) {
                Set<TypedKey<BlockType>> blockTypeKeys = new HashSet<>();
                String[] ruleParseResult = rules.replace(" ", "").split(",");
                if (ruleParseResult.length < 3) {
                    continue;
                }
                int i = 0;
                for (String singleMaterial : ruleParseResult) {
                    TypedKey<BlockType> key = RegistryKey.BLOCK.typedKey(singleMaterial);
                    if (Registry.BLOCK.get(key) == null) {
                        break;
                    }
                    blockTypeKeys.add(key);
                    i ++;
                }
                RegistryKeySet<BlockType> blockTypes = RegistrySet.keySet(RegistryKey.BLOCK, blockTypeKeys);
                String booleanValue = ruleParseResult[i + 1];
                Boolean parsedBoolean = null;
                if (!booleanValue.equalsIgnoreCase("null")) {
                    parsedBoolean = Boolean.valueOf(booleanValue);
                }
                Tool.Rule rule = Tool.rule(blockTypes, Float.parseFloat(ruleParseResult[i]), TriState.byBoolean(parsedBoolean));
                builder.addRule(rule);
            }
            item.setData(DataComponentTypes.TOOL, builder.build());
        }
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        if (item.isDataOverridden(DataComponentTypes.TOOL)) {
            Tool tool = item.getData(DataComponentTypes.TOOL);
            if (tool != null) {
                ConfigurationSection toolSection = section.createSection("tool");
                toolSection.set("damage-per-block", tool.damagePerBlock());
                toolSection.set("mining-speed", tool.defaultMiningSpeed());
                toolSection.set("destroy-blocks-in-creative", tool.canDestroyBlocksInCreative());
                List<String> rules = new ArrayList<>();
                for (Tool.Rule rule : tool.rules()) {
                    Iterable<TypedKey<BlockType>> blocksIterable = rule.blocks();
                    String blocks = StreamSupport.stream(blocksIterable.spliterator(), false)
                            .map(TypedKey::asString)
                            .collect(Collectors.joining(","));
                    rules.add(blocks + "," + rule.speed() + "," + rule.correctForDrops().toBoolean());
                }
                toolSection.set("rules", rules);
            }
        }
    }

}
