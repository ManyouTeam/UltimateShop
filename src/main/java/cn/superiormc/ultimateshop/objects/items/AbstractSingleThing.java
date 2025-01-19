package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.hooks.ItemPriceUtil;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectSinglePrice;
import cn.superiormc.ultimateshop.objects.items.prices.PriceMode;
import cn.superiormc.ultimateshop.objects.items.products.ObjectSingleProduct;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectDisplayPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractSingleThing implements Comparable<AbstractSingleThing> {

    public ThingType type;

    public ConfigurationSection singleSection;

    public ObjectAction giveAction;

    public ObjectCondition condition;

    public boolean empty;

    private final ObjectDisplayPlaceholder displayPlaceholder;

    private String id;

    public AbstractThings things;

    public AbstractSingleThing() {
        initType(null);
        this.empty = true;
        this.displayPlaceholder = new ObjectDisplayPlaceholder(this);
    }

    public AbstractSingleThing(String id, AbstractThings things) {
        this.id = id;
        this.singleSection = things.section.getConfigurationSection(id);
        if (singleSection != null && singleSection.contains("custom-type")) {
            initType(ConfigManager.configManager.config.getConfigurationSection("prices." +
                    singleSection.getString("custom-type")));
        }
        else {
            initType(singleSection);
        }
        this.empty = false;
        this.displayPlaceholder = new ObjectDisplayPlaceholder(this);
    }

    private void initType(ConfigurationSection section) {
        if (section == null) {
            type = ThingType.UNKNOWN;
        } else if (section.contains("hook-plugin") && section.contains("hook-item")) {
            type = ThingType.HOOK_ITEM;
        } else if (section.contains("match-item") && CommonUtil.checkPluginLoad("MythicChanger")) {
            type = ThingType.MATCH_ITEM;
        } else if (section.contains("match-placeholder") && !UltimateShop.freeVersion) {
            type = ThingType.CUSTOM;
        } else if (section.contains("economy-plugin")) {
            type = ThingType.HOOK_ECONOMY;
        } else if (section.contains("economy-type") && !section.contains("economy-plugin")) {
            type = ThingType.VANILLA_ECONOMY;
        } else if (section.contains("material")) {
            type = ThingType.VANILLA_ITEM;
        } else {
            type = ThingType.FREE;
        }
    }

    protected void initCondition() {
        ConfigurationSection conditions = null;
        if (this instanceof ObjectSinglePrice) {
            ObjectPrices objectPrices = (ObjectPrices) this.things;
            if (objectPrices.getPriceMode() == PriceMode.BUY) {
                conditions = things.item.getItemConfig().getConfigurationSection(ConfigManager.configManager.getString("conditions.buy-prices-key") + "." + id);
            } else {
                conditions = things.item.getItemConfig().getConfigurationSection(ConfigManager.configManager.getString("conditions.sell-prices-key") + "." + id);
            }
        } else if (this instanceof ObjectSingleProduct) {
            conditions = things.item.getItemConfig().getConfigurationSection(ConfigManager.configManager.getString("conditions.products-key") + "." + id);
        }
        if (conditions == null) {
            conditions = singleSection.getConfigurationSection("conditions");
        }
        condition = new ObjectCondition(conditions);
    }

    protected void initAction() {
        giveAction = new ObjectAction(singleSection.getConfigurationSection("give-actions"), things.getItem());
    }

    public GiveItemStack playerCanGive(Player player,
                              double cost) {
        if (singleSection == null) {
            return new GiveItemStack(this);
        }
        switch (type) {
            case VANILLA_ITEM:  case HOOK_ITEM: case MATCH_ITEM:
                if (ConfigManager.configManager.getString("give-item.give-method", "BUKKIT").equalsIgnoreCase("BUKKIT")) {
                    return getItemThing(singleSection,
                            player,
                            cost, true);
                } else {
                    return getItemThing(singleSection, player, cost, false);
                }
            case HOOK_ECONOMY : case VANILLA_ECONOMY: case CUSTOM:
                return new GiveItemStack(cost, this);
        }
        return new GiveItemStack(this);
    }

    public boolean getCondition(Player player) {
        if (condition == null) {
            return true;
        }
        return condition.getAllBoolean(new ObjectThingRun(player));
    }

    public double playerHasAmount(Inventory inventory, Player player) {
        return playerHasAmount(inventory, singleSection, player);
    }

    public double playerHasAmount(Inventory inventory, ConfigurationSection section, Player player) {
        if (section == null) {
            return 0;
        }
        switch (type) {
            case HOOK_ITEM:
                String pluginName = section.getString("hook-plugin", "");
                String itemID = section.getString("hook-item", "");
                if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                    itemID = section.getString("hook-item-type") + ";;" + itemID;
                } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                    itemID = itemID + ";;" + section.getString("hook-item-type");
                }
                return ItemPriceUtil.getItemAmount(inventory,
                        pluginName,
                        itemID);
            case VANILLA_ITEM:
                ItemStack tempVal1 = getItemThing(section, player, 1, true).getTargetItem();
                if (tempVal1 == null) {
                    return 0;
                }
                return ItemPriceUtil.getItemAmount(inventory, tempVal1);
            case MATCH_ITEM:
                return ItemPriceUtil.getItemAmount(inventory, section);
            case HOOK_ECONOMY:
                return HookManager.hookManager.getEconomyAmount(player, section.getString("economy-plugin"),
                        section.getString("economy-type", "default"));
            case VANILLA_ECONOMY:
                return HookManager.hookManager.getEconomyAmount(player,
                        section.getString("economy-type"));
            case CUSTOM:
                return Double.parseDouble(TextUtil.parse(player, section.getString("match-placeholder", "0")));
            case UNKNOWN:
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §c" +
                        "There is something wrong in your shop configs!");
                return 0;
        }
        return 0;
    }

    public boolean playerHasEnough(Inventory inventory,
                                   Player player,
                                   boolean take,
                                   double cost) {
        return playerHasEnough(inventory,
                singleSection,
                player,
                take,
                cost);
    }

    public boolean playerHasEnough(Inventory inventory,
                                   ConfigurationSection section,
                                   Player player,
                                   boolean take,
                                   double cost) {
        if (section == null) {
            return false;
        }

        if (cost < 0) {
            return false;
        }
        switch (type) {
            case HOOK_ITEM:
                String pluginName = section.getString("hook-plugin", "");
                String itemID = section.getString("hook-item", "");
                if (pluginName.equals("MMOItems") && !itemID.contains(";;")) {
                    itemID = section.getString("hook-item-type") + ";;" + itemID;
                } else if (pluginName.equals("EcoArmor") && !itemID.contains(";;")) {
                    itemID = itemID + ";;" + section.getString("hook-item-type");
                }
                return ItemPriceUtil.getPrice(inventory,
                        player,
                        pluginName,
                        itemID,
                        (int) cost, take);
            case VANILLA_ITEM:
                ItemStack itemStack = getItemThing(section, player, 1, true).getTargetItem();
                if (itemStack == null) {
                    return false;
                }
                return ItemPriceUtil.getPrice(inventory, player, itemStack, (int) cost, take);
            case MATCH_ITEM:
                return ItemPriceUtil.getPrice(inventory, player, section, (int) cost, take);
            case HOOK_ECONOMY:
                return HookManager.hookManager.getPrice(player,
                        section.getString("economy-plugin"),
                        section.getString("economy-type", "default"),
                        cost, take);
            case VANILLA_ECONOMY:
                return HookManager.hookManager.getPrice(player,
                        section.getString("economy-type"),
                        (int) cost, take);
            case CUSTOM:
                return Double.parseDouble(TextUtil.parse(player, section.getString("match-placeholder", "0"))) >= cost;
            case FREE:
                return true;
            case UNKNOWN:
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §c" +
                        "There is something wrong in your shop configs!");
                return false;
        }
        return false;
    }

    public GiveItemStack getItemThing(ConfigurationSection section,
                                   Player player,
                                   double cost,
                                   boolean displayOnly) {
        if (section == null) {
            if (singleSection == null) {
                return new GiveItemStack(this);
            }
            section = singleSection;
        }
        if (!section.getBoolean("give-item", true) || (!section.contains("material") && !section.contains("hook-item"))) {
            return new GiveItemStack(this);
        }
        int amount = (int) cost;
        ItemStack targetItem = BuildItem.buildItemStack(player, section, 1);
        if (targetItem == null) {
            return new GiveItemStack(this);
        }
        ItemStack displayItem = targetItem.clone();
        displayItem.setAmount((int) cost);
        if (displayOnly) {
            return new GiveItemStack(targetItem, displayItem, this);
        }
        Collection<ItemStack> result = new ArrayList<>();
        int leftAmount = 0;
        // leftAmount 代表物品玩家当前背包可以重复利用的堆叠数量
        int emptySlots = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                emptySlots ++;
            } else if (item.isSimilar(targetItem)) {
                leftAmount = leftAmount + targetItem.getMaxStackSize() - item.getAmount();
                if (leftAmount < 0) {
                    leftAmount = 0;
                }
            }
        }
        int requiredSlots = 0;
        if (amount > leftAmount) {
            requiredSlots = (int) Math.ceil((double) (amount - leftAmount) / targetItem.getMaxStackSize());
            boolean first = true;
            for (int i = 0 ; i < requiredSlots ; i ++) {
                if (first) {
                    ItemStack tempVal1 = targetItem.clone();
                    tempVal1.setAmount((int) cost - (requiredSlots - 1) * targetItem.getMaxStackSize());
                    result.add(tempVal1);
                    first = false;
                    continue;
                }
                ItemStack tempVal1 = targetItem.clone();
                tempVal1.setAmount(targetItem.getMaxStackSize());
                result.add(tempVal1);
            }
        } else {
            result.add(displayItem);
        }
        return new GiveItemStack(result, targetItem, displayItem, emptySlots >= requiredSlots, this);
    }

    public abstract String getDisplayName(int multi, BigDecimal amount, boolean alwaysStatic);

    public String getId() {
        return id;
    }

    public ObjectDisplayPlaceholder getDisplayPlaceholder() {
        return displayPlaceholder;
    }

    @Override
    public int compareTo(@NotNull AbstractSingleThing otherThing) {
        int len1 = getId().length();
        int len2 = otherThing.getId().length();
        int minLength = Math.min(len1, len2);

        for (int i = 0; i < minLength; i++) {
            char c1 = getId().charAt(i);
            char c2 = otherThing.getId().charAt(i);

            if (c1 != c2) {
                if (Character.isDigit(c1) && Character.isDigit(c2)) {
                    // 如果字符都是数字，则按照数字大小进行比较
                    return Integer.compare(Integer.parseInt(getId().substring(i)), Integer.parseInt(otherThing.getId().substring(i)));
                } else {
                    // 否则，按照字符的unicode值进行比较
                    return c1 - c2;
                }
            }
        }

        return len1 - len2;
    }

}
