package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.objects.items.products.ObjectProducts;
import cn.superiormc.ultimateshop.objects.items.products.ObjectSingleProduct;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Map;

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
        return getDisplayItem(player, 1, false);
    }

    public int getAmountPlaceholder(Player player) {
        if (!ConfigManager.configManager.getBoolean("display-item.calculate-amount")) {
            return 1;
        }
        return getDisplayItem(player).getItemStack().getAmount();
    }

    public ObjectDisplayItemStack getDisplayItem(Player player, int multi, boolean modifyLore) {
        ItemStack addLoreDisplayItem = null;
        ObjectDisplayItemStack addLoreDisplayItemObject = new ObjectDisplayItemStack(player);
        addLoreDisplayItemObject.setAmount(multi);
        if (useFirstProduct && item != null) {
            if (ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
                ObjectProducts product = item.getReward();
                if (product == null) {
                    return ObjectDisplayItemStack.getAir();
                }
                Map<ObjectSingleProduct, BigDecimal> tempVal1 = product.getAmount(player, 0, multi, true);
                if (tempVal1.isEmpty()) {
                    return ObjectDisplayItemStack.getAir();
                }
                ObjectSingleProduct singleProduct = null;
                double cost = 0;
                for (ObjectSingleProduct tempVal5 : tempVal1.keySet()) {
                    if (tempVal5.empty) {
                        continue;
                    }
                    singleProduct = tempVal5;
                    cost = tempVal1.get(tempVal5).doubleValue();
                    break;
                }
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
                        MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue() * multi);
                addLoreDisplayItem = displayItem.clone();
            } else {
                for (String conditionID : section.getKeys(false)) {
                    if (ProductTradeStatus.getAllNames().contains(conditionID)) {
                        if (addLoreDisplayItemObject.getBuyStatus().getStatus().name().equalsIgnoreCase(conditionID)) {
                            String amount = section.getString("amount", "1");
                            ItemStack displayItem = BuildItem.buildItemStack(player,
                                    section.getConfigurationSection(conditionID),
                                    MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue() * multi);
                            addLoreDisplayItem = displayItem.clone();
                            usedSection = section.getConfigurationSection(conditionID);
                            break;
                        }
                    } else {
                        ConfigurationSection tempVal1 = conditionSection.getConfigurationSection(conditionID);
                        if (tempVal1 != null) {
                            ObjectCondition condition = new ObjectCondition(tempVal1);
                            if (condition.getAllBoolean(new ObjectThingRun(player))) {
                                String amount = section.getString("amount", "1");
                                ItemStack displayItem = BuildItem.buildItemStack(player,
                                        section.getConfigurationSection(conditionID),
                                        MathUtil.doCalculate(TextUtil.withPAPI(amount, player)).intValue() * multi);
                                addLoreDisplayItem = displayItem.clone();
                                usedSection = section.getConfigurationSection(conditionID);
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (addLoreDisplayItem == null) {
            return ObjectDisplayItemStack.getAir();
        }
        addLoreDisplayItemObject.setBaseSetting(addLoreDisplayItem, usedSection, item);
        if (usedSection == null) {
            usedSection = section;
        }
        if (item != null) {
            if (!usedSection.getBoolean("modify-lore", true) || !section.getBoolean("modify-lore", true) || !modifyLore) {
                return addLoreDisplayItemObject;
            }
            return ModifyDisplayItem.modifyItem(player, addLoreDisplayItemObject, item, false);
        }
        return addLoreDisplayItemObject;
    }

}
