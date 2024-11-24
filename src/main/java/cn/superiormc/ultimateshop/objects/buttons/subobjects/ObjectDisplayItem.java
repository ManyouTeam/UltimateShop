package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectDisplayItem {

    private final ConfigurationSection section;

    private final ConfigurationSection conditionSection;

    private ObjectItem item;

    public ObjectDisplayItem(ConfigurationSection section, ConfigurationSection conditionSection, ObjectItem item) {
        this.section = section;
        this.conditionSection = conditionSection;
        this.item = item;
    }

    public ObjectDisplayItem(ConfigurationSection section, ConfigurationSection conditionSection) {
        this.section = section;
        this.conditionSection = conditionSection;
    }

    public ItemStack getDisplayItem(Player player) {
        ItemStack addLoreDisplayItem = null;
        if (section == null) {
            if (item != null &&
                ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
                addLoreDisplayItem = item.getReward().getDisplayItem(player);
            }
        }
        else {
            // 显示物品
            if (conditionSection == null) {
                String amount = section.getString("amount", "1");
                ItemStack displayItem = BuildItem.buildItemStack(player, section,
                        MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue());
                addLoreDisplayItem = displayItem.clone();
            }
            else {
                for (String conditionID : section.getKeys(false)) {
                    ConfigurationSection tempVal1 = conditionSection.getConfigurationSection(conditionID);
                    if (tempVal1 != null) {
                        ObjectCondition condition = new ObjectCondition(tempVal1);
                        if (condition.getAllBoolean(new ObjectThingRun(player))) {
                            String amount = section.getString("amount", "1");
                            ItemStack displayItem = BuildItem.buildItemStack(player,
                                    section.getConfigurationSection(conditionID),
                                    MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue());
                            addLoreDisplayItem = displayItem.clone();
                            break;
                        }
                    }
                }
            }
        }
        if (addLoreDisplayItem == null) {
            addLoreDisplayItem = new ItemStack(Material.AIR);
        }
        return addLoreDisplayItem;
    }

    public ItemStack getDisplayItem(Player player, int multi) {
        ItemStack addLoreDisplayItem = getDisplayItem(player);
        if (item != null) {
            if (section != null && !section.getBoolean("modify-lore", true)) {
                return addLoreDisplayItem;
            }
            return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item, false);
        }
        return addLoreDisplayItem;
    }

}
