package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.listeners.GUIListener;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Objects;

public class CreateOrEditShopGUI extends EditorInvGUI {

    public CreateOrEditShopGUI(Player owner) {
        super(owner);
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        ItemStack createItem = new ItemStack(Material.FEATHER);
        ItemMeta tempVal1 = createItem.getItemMeta();
        tempVal1.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-or-edit-shop-gui.create-shop.name")));
        tempVal1.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "create-or-edit-shop-gui.create-shop.lore")));
        createItem.setItemMeta(tempVal1);
        ItemStack editItem = new ItemStack(Material.PAPER);
        ItemMeta tempVal2 = editItem.getItemMeta();
        tempVal2.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "create-or-edit-shop-gui.edit-shop.name")));
        tempVal2.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "create-or-edit-shop-gui.edit-shop.lore")));
        editItem.setItemMeta(tempVal2);
        inv = Bukkit.createInventory(owner, 9,
                TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                        "create-or-edit-shop-gui.title")));
        inv.setItem(0, createItem);
        inv.setItem(1, editItem);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == 0) {
            CreateShopGUI gui = new CreateShopGUI(owner);
            gui.openGUI();
            Listener guiListener = new GUIListener(gui);
            Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
        }
        if (slot == 1) {
            ChooseShopGUI gui = new ChooseShopGUI(owner);
            gui.openGUI();
            Listener guiListener = new GUIListener(gui);
            Bukkit.getPluginManager().registerEvents(guiListener, UltimateShop.instance);
        }
        return true;
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        return true;
    }
}
