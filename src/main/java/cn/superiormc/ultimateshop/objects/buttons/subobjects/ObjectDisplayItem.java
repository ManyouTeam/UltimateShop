package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.ModifyDisplayItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ObjectDisplayItem {

    private ConfigurationSection section;

    private ConfigurationSection conditionSection;

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
                addLoreDisplayItem = item.getReward().getDisplayItem(section, player, false, 0, 1);
            }
        }
        else {
            // 显示物品
            if (conditionSection == null) {
                String amount = section.getString("amount", "1");
                ItemStack displayItem = ItemUtil.buildItemStack(player, section,
                        MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue());
                addLoreDisplayItem = displayItem.clone();
            }
            else {
                for (String conditionID : section.getKeys(false)) {
                    List<String> tempVal1 = conditionSection.getStringList(conditionID);
                    if (!tempVal1.isEmpty() && section.getConfigurationSection(conditionID) != null) {
                        ObjectCondition condition = new ObjectCondition(tempVal1);
                        if (condition.getBoolean(player)) {
                            String amount = section.getString("amount", "1");
                            ItemStack displayItem = ItemUtil.buildItemStack(player,
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
            addLoreDisplayItem = new ItemStack(Material.STONE);
        }
        return addLoreDisplayItem;
    }

    public ItemStack getDisplayItem(Player player, int multi) {
        ItemStack addLoreDisplayItem = getDisplayItem(player);
        if (item != null) {
            if (section != null && !section.getBoolean("modify-lore", true)) {
                return addLoreDisplayItem;
            }
            return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item, !item.getBuyMore());
        }
        return addLoreDisplayItem;
    }

}
