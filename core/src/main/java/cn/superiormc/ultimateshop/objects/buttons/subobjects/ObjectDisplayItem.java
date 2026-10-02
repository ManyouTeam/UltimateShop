package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ItemMaterialManager;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.methods.ModifyDisplayItem;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.ObjectCondition;
import cn.superiormc.ultimateshop.objects.items.products.ObjectSingleProduct;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

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
        return getDisplayItem(player, item == null ? 1 : item.getDefaultBuyAmount(),
                item == null ? 1 : item.getDefaultSellAmount());
    }

    public ObjectDisplayItemStack getDisplayItem(Player player, int buyAmount, int sellAmount) {
        usedSection = null;
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
                    addLoreDisplayItem = tempVal2.clone();
                    if (buyAmount == sellAmount) {
                        addLoreDisplayItem.setAmount(buyAmount);
                    }
                    if (!section.contains("bedrock")) {
                        usedSection = singleProduct.singleSection;
                    }
                }
            }
        } else {
            if (conditionSection == null) {
                ItemStack displayItem = BuildItem.buildItemStack(player, section,
                        getDisplayAmount(section, player, buyAmount, sellAmount));
                addLoreDisplayItem = displayItem.clone();
            } else {
                for (String conditionID : section.getKeys(false)) {
                    ConfigurationSection tempVal1 = conditionSection.getConfigurationSection(conditionID);
                    if (tempVal1 != null) {
                        ObjectCondition condition = new ObjectCondition(tempVal1);
                        ConfigurationSection tempVal2 = section.getConfigurationSection(conditionID);
                        if (condition.getAllBoolean(new ObjectThingRun(player))) {
                            if (tempVal2 == null) {
                                continue;
                            }
                            ItemStack displayItem = BuildItem.buildItemStack(player,
                                    tempVal2,
                                    getDisplayAmount(tempVal2, player, buyAmount, sellAmount));
                            addLoreDisplayItem = displayItem.clone();
                            usedSection = tempVal2;
                            break;
                        }
                    }
                }
            }
        }
        if (addLoreDisplayItem == null) {
            return ObjectDisplayItemStack.getAir();
        }
        if (ConfigManager.configManager.getBoolean("menu.buy-more-menu.display-item-max-stack")
                && CommonUtil.getMinorVersion(20, 5)) {
            ItemMeta meta = addLoreDisplayItem.getItemMeta();
            if (meta != null) {
                meta.setMaxStackSize(99);
                addLoreDisplayItem.setItemMeta(meta);
            }
        }
        if (usedSection == null) {
            usedSection = section;
        }
        return new ObjectDisplayItemStack(player, addLoreDisplayItem, usedSection, item);
    }

    private int getDisplayAmount(ConfigurationSection settings, Player player, int buyAmount, int sellAmount) {
        if (!settings.contains("amount") && item != null && buyAmount == sellAmount) {
            return buyAmount;
        }
        return MathUtil.doCalculate(TextUtil.withPAPI(settings.getString("amount", "1"), player)).intValue();
    }

    public Set<String> getPossibleSprites() {
        if (!useFirstProduct) {
            return ItemMaterialManager.getConfiguredSprites(section);
        }
        Set<String> result = new HashSet<>();
        if (section != null && section.contains("sprite")) {
            result.add(section.getString("sprite"));
        } else if (item != null && ConfigManager.configManager.getBoolean("display-item.auto-set-first-product")) {
            for (ObjectSingleProduct product : item.getReward().singleProducts) {
                result.addAll(ItemMaterialManager.getConfiguredSprites(product.singleSection));
            }
        }
        return result;
    }

    public int getAmountPlaceholder(Player player) {
        if (!ConfigManager.configManager.getBoolean("display-item.calculate-amount")) {
            return 1;
        }
        return getDisplayItem(player).getItemStack().getAmount();
    }

    public ObjectDisplayItemStack getDisplayItem(Player player, int multi) {
        return getDisplayItem(player, multi, item == null ? multi : item.getDefaultBuyAmount(),
                item == null ? multi : item.getDefaultSellAmount());
    }

    public ObjectDisplayItemStack getDisplayItem(Player player, int multi, int buyAmount, int sellAmount) {
        ObjectDisplayItemStack addLoreDisplayItem = getDisplayItem(player, buyAmount, sellAmount);
        if (item != null) {
            if (section != null && !section.getBoolean("modify-lore", true)) {
                return addLoreDisplayItem;
            }
            boolean useDialog = false;
            ObjectMenu menu = item.getShopObject().getShopMenuObject();
            if (menu != null) {
                useDialog = menu.isUseDialog();
            }
            return ModifyDisplayItem.modifyItem(player, multi, addLoreDisplayItem, item, false, useDialog,
                    "general", buyAmount, sellAmount);
        }
        return addLoreDisplayItem;
    }

}
