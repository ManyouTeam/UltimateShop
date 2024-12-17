package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.objects.items.products.ObjectSingleProduct;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ObjectDisplayItem {

    private final ConfigurationSection section;

    private ConfigurationSection usedSection;

    private boolean useFirstProduct = false;

    private final ConfigurationSection conditionSection;

    private ObjectItem item;

    public ObjectDisplayItem(ConfigurationSection section, ConfigurationSection conditionSection, ObjectItem item) {
        if (section == null) {
            useFirstProduct = true;
            this.section = item.getItemConfig();
        } else {
            this.section = section;
        }
        this.conditionSection = conditionSection;
        this.item = item;
    }

    public ObjectDisplayItem(ConfigurationSection section, ConfigurationSection conditionSection) {
        this.section = section;
        this.conditionSection = conditionSection;
    }

    public ObjectDisplayItemStack getDisplayItem(Player player) {
        ItemStack addLoreDisplayItem = null;
        if (useFirstProduct) {
            if (item != null && ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
                ObjectSingleProduct singleProduct = item.getReward().getTargetProduct(player);
                if (singleProduct == null) {
                    return ObjectDisplayItemStack.getAir();
                }
                double cost = singleProduct.getAmount(player, 0, true).doubleValue();
                ItemStack tempVal2 = singleProduct.getItemThing(null, player, cost, true).getDisplayItem();
                if (tempVal2 != null) {
                    addLoreDisplayItem = tempVal2;
                    if (!section.contains("bedrock")) {
                        usedSection = singleProduct.singleSection;
                    }
                }
            }
        } else {
            // 显示物品
            if (conditionSection == null) {
                String amount = section.getString("amount", "1");
                ItemStack displayItem = BuildItem.buildItemStack(player, section,
                        MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue());
                addLoreDisplayItem = displayItem.clone();
            } else {
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
                            usedSection = section.getConfigurationSection(conditionID);
                            break;
                        }
                    }
                }
            }
        }
        if (addLoreDisplayItem == null) {
            return ObjectDisplayItemStack.getAir();
        }
        if (usedSection == null) {
            usedSection = section;
        }
        return new ObjectDisplayItemStack(player, addLoreDisplayItem, usedSection, item);
    }

    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        ObjectDisplayItemStack addLoreDisplayItem = getDisplayItem(player);
        if (item != null) {
            if (section != null && !section.getBoolean("modify-lore", true)) {
                return addLoreDisplayItem;
            }
            return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item, false);
        }
        return addLoreDisplayItem;
    }

}
