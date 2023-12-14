package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EditShopGUI extends InvGUI {

    public static Map<Player, EditShopGUI> guiCache = new HashMap<>();

    public EditorMode editMode = EditorMode.NOT_EDITING;

    public YamlConfiguration config = null;

    public EditShopGUI(Player owner, ObjectShop shop) {
        super(owner);
        config = shop.getShopConfig();
        guiCache.put(owner, this);
    }

    @Override
    public void openGUI() {
        editMode = EditorMode.NOT_EDITING;
        constructGUI();
        if (inv != null) {
            owner.getPlayer().openInventory(inv);
        }
    }

    @Override
    protected void constructGUI() {
        // shop name
        ItemStack shopNameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta tempVal1 = shopNameItem.getItemMeta();
        tempVal1.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-shop-gui.shop-name.name")));
        tempVal1.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "create-shop-gui.shop-name.lore")),
                "value",
                config.getString("settings.shop-name", "Shop")));
        shopNameItem.setItemMeta(tempVal1);
        // buy more
        ItemStack buyMoreItem = new ItemStack(Material.BEACON);
        ItemMeta tempVal3 = shopNameItem.getItemMeta();
        tempVal3.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-shop-gui.buy-more.name")));
        tempVal3.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "create-shop-gui.buy-more.lore")),
                "value",
                config.getString("settings.buy-more", "true")));
        buyMoreItem.setItemMeta(tempVal3);
        // menu
        ItemStack menuItem = new ItemStack(Material.CHEST);
        ItemMeta tempVal4 = shopNameItem.getItemMeta();
        tempVal4.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-shop-gui.menu-id.name")));
        tempVal4.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "create-shop-gui.menu-id.lore")),
                "value",
                config.getString("settings.menu", "Unset")));
        menuItem.setItemMeta(tempVal4);
        // send message
        ItemStack sendMessageAfterBuyItem = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        ItemMeta tempVal5 = shopNameItem.getItemMeta();
        tempVal5.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-shop-gui.send-message-after-buy.name").replace("{shop}", config.getName())));
        tempVal5.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "create-shop-gui.send-message-after-buy.lore")),
                "value",
                config.getString("settings.send-message-after-buy", "true")));
        sendMessageAfterBuyItem.setItemMeta(tempVal5);
        // finish
        ItemStack finishItem = new ItemStack(Material.GREEN_DYE);
        ItemMeta tempVal6 = shopNameItem.getItemMeta();
        tempVal6.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-shop-gui.finish.name")));
        tempVal6.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "create-shop-gui.finish.lore")));
        finishItem.setItemMeta(tempVal6);
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, 9,
                    TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                            "edit-shop-gui.title")));
        }
        inv.setItem(0, shopNameItem);
        inv.setItem(1, buyMoreItem);
        inv.setItem(2, menuItem);
        inv.setItem(3, sendMessageAfterBuyItem);
        inv.setItem(8, finishItem);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (!Objects.equals(inventory, getInv())) {
            return true;
        }
        if (slot == 0) {
            editMode = EditorMode.EDIT_SHOP_NAME;
            LanguageManager.languageManager.sendStringText(owner, "editor.enter-shop-name");
            owner.closeInventory();
        }
        if (slot == 1) {
            if (config.getBoolean("settings.buy-more")) {
                config.set("settings.buy-more", "false");
            } else {
                config.set("settings.buy-more", "true");
            }
            constructGUI();
        }
        if (slot == 2) {
            editMode = EditorMode.EDIT_MENU_ID;
            LanguageManager.languageManager.sendStringText(owner, "editor.enter-menu-id");
            owner.closeInventory();
        }
        if (slot == 3) {
            if (config.getBoolean("settings.send-message-after-buy")) {
                config.set("settings.send-message-after-buy", "false");
            } else {
                config.set("settings.send-message-after-buy", "true");
            }
            constructGUI();
        }
        if (slot == 8) {
            Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance, () -> {
                File dir = new File(UltimateShop.instance.getDataFolder() + "/shops");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, config.getName() + ".yml");
                file.delete();
                try {
                    config.save(file);
                    ReloadPlugin.reload(owner);
                    LanguageManager.languageManager.sendStringText(owner,
                            "editor.shop-edited",
                            "shop",
                            config.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            owner.closeInventory();
        }
        return true;
    }

    @Override
    public boolean closeEventHandle() {
        if (editMode == EditorMode.NOT_EDITING) {
            guiCache.remove(owner);
            return true;
        }
        return false;
    }

    @Override
    public boolean dragEventHandle(Set<Integer> slots) {
        return true;
    }
}
