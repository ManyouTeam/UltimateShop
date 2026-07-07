package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.dialog.DialogAction;
import cn.superiormc.ultimateshop.gui.dialog.DialogResponse;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.ItemMaterialManager;
import cn.superiormc.ultimateshop.methods.Dupe;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ObjectDisplayItemStack {

    public static ObjectDisplayItemStack getAir() {
        return new ObjectDisplayItemStack(new ItemStack(Material.AIR));
    }

    private final ItemStack javaItem;

    private final ItemMeta meta;

    private ConfigurationSection section;

    private Player player;

    private ObjectItem item;

    private String sprite;

    public ObjectDisplayItemStack(ItemStack javaItemOnly) {
        this.javaItem = Dupe.markGuiDisplayItem(javaItemOnly);
        this.meta = this.javaItem.getItemMeta();
    }

    public ObjectDisplayItemStack(ItemStack javaItemOnly, ConfigurationSection section) {
        this.javaItem = Dupe.markGuiDisplayItem(javaItemOnly);
        this.meta = this.javaItem.getItemMeta();
        this.section = section;
        if (ItemMaterialManager.enableThis()) {
            this.sprite = section == null ? null : section.getString("sprite");
        }
    }

    public ObjectDisplayItemStack(Player player, ItemStack javaItem, ObjectItem item) {
        this.javaItem = Dupe.markGuiDisplayItem(javaItem);
        this.meta = this.javaItem.getItemMeta();
        this.player = player;
        this.item = item;
        if (ItemMaterialManager.enableThis()) {
            this.sprite = item == null ? null : item.getDisplayItemObject().getDisplayItem(player).getSprite();
        }
    }

    public ObjectDisplayItemStack(Player player, ItemStack javaItem, ConfigurationSection section, ObjectItem item) {
        this.javaItem = Dupe.markGuiDisplayItem(javaItem);
        this.meta = this.javaItem.getItemMeta();
        this.section = section;
        this.player = player;
        this.item = item;
        this.sprite = section == null ? null : section.getString("sprite");
    }

    public String getMaterialTextureUrl() {
        if (!ItemMaterialManager.enableThis()) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not use auto-add-icon feature when item material mapping not enabled, please set config-files." +
                    "minecraft-item-material-file.enabled to true in config.yml and generate the mapping file.");
            return null;
        }
        String texturePath = ItemMaterialManager.itemMaterialManager.getMaterialTexturePath(javaItem.getType());
        if (texturePath == null || texturePath.isEmpty()) {
            return null;
        }
        int namespaceSeparator = texturePath.indexOf(':');
        String namespace = namespaceSeparator < 0 ? "minecraft" : texturePath.substring(0, namespaceSeparator);
        String path = namespaceSeparator < 0 ? texturePath : texturePath.substring(namespaceSeparator + 1);
        if (!"minecraft".equals(namespace)) {
            return null;
        }

        String version = UltimateShop.yearVersion + "." + UltimateShop.majorVersion + "." + UltimateShop.minorVersion;
        if (version.endsWith(".0")) {
            version = version.substring(0, version.length() - 2);
        }
        return ConfigManager.configManager.getString("bedrock.auto-add-icon.format",
                "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/{version}/assets/minecraft/textures/{path}.png",
                "version", version,
                "path", path);
    }

    public String getSprite() {
        if (!CommonUtil.getMinorVersion(21, 9)) {
            return null;
        }
        if (sprite != null) {
            return sprite;
        }
        if (javaItem.getType() == Material.AIR) {
            return null;
        }
        if (!ItemMaterialManager.enableThis()) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Can not use auto-add-sprite feature when item material mapping not enabled, please set config-files." +
                    "minecraft-item-material-file.enabled to true in config.yml and generate the mapping file.");
            return null;
        }
        String texturePath = ItemMaterialManager.itemMaterialManager.getMaterialTexturePath(javaItem.getType());
        if (texturePath == null || texturePath.isEmpty()) {
            return null;
        }
        int namespaceSeparator = texturePath.indexOf(':');
        String namespace = namespaceSeparator < 0 ? "minecraft" : texturePath.substring(0, namespaceSeparator);
        String path = namespaceSeparator < 0 ? texturePath : texturePath.substring(namespaceSeparator + 1);
        String atlas = path.startsWith("block/") ? "blocks" : "items";
        return ConfigManager.configManager.getString("menu.dialog.auto-add-sprite.format", "<sprite:\"{namespace}:{atlas}\":{path}>",
                "namespace", namespace,
                "atlas", atlas,
                "path", path);
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public Player getPlayer() {
        return player;
    }

    public ObjectItem getItem() {
        return item;
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
                        tempVal4 = ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.price-extra-line.only-sell", "");
                    }
                } else if (item.getSellPrice().empty) {
                    tempVal4 = ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.price-extra-line.only-buy", "");
                } else {
                    tempVal4 = ConfigManager.configManager.getStringWithLang(player, "menu.bedrock.price-extra-line.default", "");
                }
            }
            if (tempVal4 != null && !tempVal4.isEmpty()) {
                ObjectUseTimesCache tempVal9 = ShopHelper.getPlayerUseTimesCache(item, player);
                tempVal3 = tempVal3 + "\n" + TextUtil.parse(player, CommonUtil.modifyString(player, tempVal4,
                        "buy-price",
                        ObjectPrices.getDisplayNameInLine(player,
                                1,
                                item.getBuyPrice().take(player.getInventory(), player, tempVal9.getBuyUseTimes(), 1, true).getResultMap(),
                                item.getBuyPrice().getMode(),
                                false),
                        "sell-price",
                        ObjectPrices.getDisplayNameInLine(player,
                                1,
                                item.getSellPrice().give(player, tempVal9.getBuyUseTimes(), 1).getResultMapForSellMultiplierDisplay(player),
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
        } else if (ConfigManager.configManager.getBoolean("menu.bedrock.auto-add-icon.enabled") && ItemMaterialManager.enableThis()) {
            tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.URL, getMaterialTextureUrl());
        } else {
            tempVal1 = ButtonComponent.of(tempVal3);
        }
        return tempVal1;
    }

    @Nullable
    public DialogAction parseToDialogButton(String id, Consumer<DialogResponse> handler) {
        String label = ItemUtil.getItemName(javaItem);
        if (label.trim().isEmpty()) {
            return null;
        }
        if (ItemMaterialManager.enableThis() && ConfigManager.configManager.getBoolean("menu.dialog.auto-add-sprite.enabled")) {
            String sprite = getSprite();
            if (sprite != null) {
                label = sprite + " " + label;
            }
        }
        String tooltip = null;
        if (meta != null) {
            List<String> lore = UltimateShop.methodUtil.getItemLore(meta);
            if (lore != null && !lore.isEmpty()) {
                if (lore.get(0).isEmpty()) {
                    lore.removeFirst();
                }
                if (lore.get(lore.size() - 1).isEmpty()) {
                    lore.removeLast();
                }
                tooltip = String.join("\n", lore);
            }
        }
        return DialogAction.of(id, label, tooltip, handler);
    }

    public ItemStack getItemStack() {
        if (javaItem == null) {
            return new ItemStack(Material.AIR);
        }
        return javaItem;
    }
}
