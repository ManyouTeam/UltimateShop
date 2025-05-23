package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Nullable;

public class ObjectDisplayItemStack {

    public static ObjectDisplayItemStack getAir() {
        return new ObjectDisplayItemStack(new ItemStack(Material.AIR));
    }

    private final ItemStack javaItem;

    private final ItemMeta meta;

    private ConfigurationSection section;

    private Player player;

    private ObjectItem item;

    public ObjectDisplayItemStack(ItemStack javaItemOnly) {
        this.javaItem = javaItemOnly;
        this.meta = javaItemOnly.getItemMeta();
    }

    public ObjectDisplayItemStack(Player player, ItemStack javaItem, ConfigurationSection section, ObjectItem item) {
        this.javaItem = javaItem;
        this.meta = javaItem.getItemMeta();
        this.section = section;
        this.player = player;
        this.item = item;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public void setItemMeta(@Nullable ItemMeta itemMeta) {
        javaItem.setItemMeta(itemMeta);
    }

    public ButtonComponent parseToBedrockButton() {
        if (section == null) {
            return null;
        }
        String icon = section.getString("bedrock.icon", section.getString("bedrock-icon"));
        if (ItemUtil.getItemNameWithoutVanilla(javaItem).trim().isEmpty() ||
                section.getBoolean("bedrock.hide", false)) {
            return null;
        }
        String tempVal3 = TextUtil.parse(ItemUtil.getItemName(javaItem), player);
        String tempVal4 = section.getString("bedrock.extra-line");

        if (item == null) {
            if (tempVal4 != null && !tempVal4.isEmpty()) {
                tempVal3 = tempVal3 + "\n" + TextUtil.parse(player, tempVal4);
            }
        } else {
            if (tempVal4 == null) {
                if (item.getBuyPrice().empty) {
                    if (!item.getSellPrice().empty) {
                        tempVal4 = ConfigManager.configManager.getString("menu.bedrock.price-extra-line.only-sell", "");
                    }
                } else if (item.getSellPrice().empty) {
                    tempVal4 = ConfigManager.configManager.getString("menu.bedrock.price-extra-line.only-buy", "");
                } else {
                    tempVal4 = ConfigManager.configManager.getString("menu.bedrock.price-extra-line.default", "");
                }
            }
            if (tempVal4 != null && !tempVal4.isEmpty()) {
                ObjectUseTimesCache tempVal9 = CacheManager.cacheManager.getPlayerCache(player).getUseTimesCache().get(item);
                tempVal3 = tempVal3 + "\n" + TextUtil.parse(player, CommonUtil.modifyString(tempVal4,
                        "buy-price",
                        ObjectPrices.getDisplayNameInLine(player,
                                1,
                                item.getBuyPrice().takeSingleThing(player.getInventory(), player, tempVal9.getBuyUseTimes(), 1, true).getResultMap(),
                                item.getBuyPrice().getMode(),
                                false),
                        "sell-price",
                        ObjectPrices.getDisplayNameInLine(player,
                                1,
                                item.getSellPrice().giveSingleThing(player, tempVal9.getBuyUseTimes(), 1).getResultMap(),
                                item.getSellPrice().getMode(),
                                false)));
            }
        }
        ButtonComponent tempVal1 = null;
        if (icon != null && icon.split(";;").length == 2) {
            String type = icon.split(";;")[0].toLowerCase();
            if (type.equals("url")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
            } else if (type.equals("path")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
            }
        } else {
            tempVal1 = ButtonComponent.of(tempVal3);
        }
        return tempVal1;
    }

    public ItemStack getItemStack() {
        if (javaItem == null) {
            return new ItemStack(Material.AIR);
        }
        return javaItem;
    }
}
