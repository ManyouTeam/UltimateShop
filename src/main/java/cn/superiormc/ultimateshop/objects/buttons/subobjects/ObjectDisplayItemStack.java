package cn.superiormc.ultimateshop.objects.buttons.subobjects;

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

    public ObjectDisplayItemStack(ItemStack javaItemOnly) {
        this.javaItem = javaItemOnly;
        this.meta = javaItemOnly.getItemMeta();
    }

    public ObjectDisplayItemStack(Player player, ItemStack javaItem, ConfigurationSection section) {
        this.javaItem = javaItem;
        this.meta = javaItem.getItemMeta();
        this.section = section;
        this.player = player;
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
