package cn.superiormc.ultimateshop.objects.buttons.subobjects;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.dialog.DialogAction;
import cn.superiormc.ultimateshop.gui.dialog.DialogResponse;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.managers.MinecraftResourceManager;
import cn.superiormc.ultimateshop.methods.Dupe;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
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
        if (!UltimateShop.freeVersion) {
            this.sprite = section == null ? null : section.getString("sprite");
        }
    }

    public ObjectDisplayItemStack(Player player, ItemStack javaItem, ObjectItem item) {
        this.javaItem = Dupe.markGuiDisplayItem(javaItem);
        this.meta = this.javaItem.getItemMeta();
        this.player = player;
        this.item = item;
        if (!UltimateShop.freeVersion) {
            this.sprite =
                item == null ? null : item.getDisplayItemObject().getDisplayItem(player).sprite;
        }
    }

    public ObjectDisplayItemStack(
        Player player, ItemStack javaItem, ConfigurationSection section, ObjectItem item) {
        this.javaItem = Dupe.markGuiDisplayItem(javaItem);
        this.meta = this.javaItem.getItemMeta();
        this.section = section;
        this.player = player;
        this.item = item;
        this.sprite = section == null ? null : section.getString("sprite");
    }

    public String getMaterialTextureUrl() {
        return getMaterialTextureUrl(null);
    }

    public String getMaterialTextureUrl(ObjectMenu menu) {
        if (UltimateShop.freeVersion || MinecraftResourceManager.minecraftResourceManager == null) {
            return null;
        }
        String texturePath =
            MinecraftResourceManager.minecraftResourceManager.getTexturePath(javaItem.getType());
        if (texturePath == null || texturePath.isEmpty()) {
            return null;
        }
        int namespaceSeparator = texturePath.indexOf(':');
        String namespace =
            namespaceSeparator < 0 ? "minecraft" : texturePath.substring(0, namespaceSeparator);
        String path =
            namespaceSeparator < 0
        ? texturePath
        : texturePath.substring(namespaceSeparator + 1);
        if (!"minecraft".equals(namespace)) {
            return null;
        }

        String version = CommonUtil.getMinecraftVersion();
        return ConfigManager.configManager.getMenuRendererText(
            menu == null ? null : menu.getConfig(), player, "form", "auto-add-icon.format",
            "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/{version}/assets/minecraft/textures/{path}.png",
            "version",
            version,
            "path",
            path);
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
        if (UltimateShop.freeVersion || MinecraftResourceManager.minecraftResourceManager == null) {
            return null;
        }
        return MinecraftResourceManager.minecraftResourceManager.getMaterialSprite(javaItem.getType());
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

    public String getPriceExtraLine(String override) {
        String tempVal4 =
            override != null
        ? override
        : section == null ? null : section.getString("bedrock.extra-line");

        if (item != null) {
            var menuConfig = item.getShopObject().getShopMenuObject().getConfig();
            if (Boolean.FALSE.equals(ConfigManager.configManager.getProductUISetting(
                menuConfig, item.getButtonConfig(), "price.enabled", true))) {
                return "";
            }
            String kind = item.getBuyPrice().empty
                ? "only-sell" : item.getRawSellPrice().empty ? "only-buy" : "both";
            if (tempVal4 == null) {
                Object format = ConfigManager.configManager.getProductUISetting(
                    menuConfig, item.getButtonConfig(), "price.formats." + kind, "");
                tempVal4 = format instanceof String text ? text : "";
            }
            if (tempVal4.equalsIgnoreCase("{lang}")) {
                String languageKind = kind.equals("both") ? "default" : kind;
                tempVal4 = LanguageManager.languageManager.getStringText(
                    player, "override-lang.menu.bedrock.price-extra-line." + languageKind, "");
            }
            if (tempVal4 != null && !tempVal4.isEmpty()) {
                ObjectUseTimesCache tempVal9 = ShopHelper.getPlayerUseTimesCache(item, player);
                return TextUtil.parse(
                    player,
                    CommonUtil.modifyString(
                    player,
                    tempVal4,
                    "buy-price",
                    ObjectPrices.getDisplayNameInLine(
                    player,
                    1,
                    item.getBuyPrice()
                    .take(
                    player.getInventory(),
                    player,
                    tempVal9.getBuyUseTimes(),
                    1,
                    true)
                    .getResultMap(),
                    item.getBuyPrice().getMode(),
                    false),
                    "sell-price",
                    ObjectPrices.getDisplayNameInLine(
                    player,
                    1,
                    item.getSellPrice()
                    .give(player, tempVal9.getSellUseTimes(), 1)
                    .getResultMapForSellMultiplierDisplay(player),
                    item.getSellPrice().getMode(),
                    false)));
            }
        }
        if (tempVal4 != null && !tempVal4.isEmpty() && !tempVal4.equalsIgnoreCase("{lang}")) {
            return TextUtil.parse(player, CommonUtil.parseLang(player, tempVal4));
        }
        return "";
    }

    public ButtonComponent parseToBedrockButton() {
        return parseToBedrockButton(null);
    }

    public ButtonComponent parseToBedrockButton(ObjectMenu menu) {
        if (section == null) {
            return null;
        }
        String icon = section.getString("bedrock.icon");
        if (ItemUtil.getItemNameWithoutVanilla(javaItem).trim().isEmpty()
            || section.getBoolean("bedrock.hide", false)) {
            return null;
        }
        String tempVal3 = TextUtil.parse(ItemUtil.getItemName(javaItem), player);
        String extra = getPriceExtraLine(null);
        if (!extra.isEmpty()) {
            tempVal3 += "\n" + extra;
        }
        ButtonComponent tempVal1 = null;
        if (icon != null && icon.split(";;").length == 2) {
            String type = icon.split(";;")[0].toLowerCase();
            if (type.equals("url")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.URL, icon.split(";;")[1]);
            } else if (type.equals("path")) {
                tempVal1 = ButtonComponent.of(tempVal3, FormImage.Type.PATH, icon.split(";;")[1]);
            }
        } else if (ConfigManager.configManager.getMenuRendererBoolean(
            menu == null ? null : menu.getConfig(), "form", "auto-add-icon.enabled", true)) {
            String autoUrl = getMaterialTextureUrl(menu);
            if (autoUrl != null) {
                tempVal1 =
                    ButtonComponent.of(tempVal3, FormImage.Type.URL, autoUrl);
            } else {
                tempVal1 = ButtonComponent.of(tempVal3);
            }
        } else {
            tempVal1 = ButtonComponent.of(tempVal3);
        }
        return tempVal1;
    }

    @Nullable
    public DialogAction parseToDialogButton(String id, Consumer<DialogResponse> handler) {
        Boolean showSprite = item == null ? null : item.getShowSprite();
        return parseToDialogButton(id, showSprite, handler);
    }

    @Nullable
    public DialogAction parseToDialogButton(
        String id, Boolean showSprite, Consumer<DialogResponse> handler) {
        String label = ItemUtil.getItemName(javaItem);
        if (label.trim().isEmpty()) {
            return null;
        }
        if (!UltimateShop.freeVersion
            && (Boolean.TRUE.equals(showSprite)
            || showSprite == null
            && ConfigManager.configManager.getBoolean(
            "menu.ui.renderers.dialog.auto-add-sprite.enabled"))) {
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
        return DialogAction.of(id, label, tooltip, handler).withShowSprite(showSprite);
    }

    public ItemStack getItemStack() {
        if (javaItem == null) {
            return new ItemStack(Material.AIR);
        }
        return javaItem;
    }
}
