package cn.superiormc.ultimateshop.gui.inv.editor.subinventory;

import cn.superiormc.ultimateshop.gui.inv.editor.EditProductGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorInvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorMode;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditSingleProduct extends EditorInvGUI {

    public ConfigurationSection section;

    public ItemStack displayItem;

    public EditSingleProduct(Player owner, EditProductGUI gui) {
        super(owner);
        this.previousGUI = gui;
        this.section = gui.section.getConfigurationSection("products");
        if (section == null) {
            section = gui.section.createSection("products");
        }
    }

    @Override
    protected void constructGUI() {
        // display item
        this.displayItem = ItemUtil.buildItemStack(owner, section, 1);
        ItemStack tempDisplayItem = this.displayItem.clone();
        ItemMeta tempVal1 = tempDisplayItem.getItemMeta();
        List<String> tempVal3 = new ArrayList<>();
        if (tempVal1.hasLore()) {
            tempVal3 = tempVal1.getLore();
        }
        tempVal3.addAll(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "edit-display-item-gui.display-item.add-lore")));
        tempDisplayItem.setItemMeta(tempVal1);
        // modify lore
        ItemStack modifyLoreItem = new ItemStack(Material.BOOK);
        ItemMeta tempVal2 = modifyLoreItem.getItemMeta();
        tempVal2.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-display-item-gui.modify-lore.name")));
        tempVal2.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-display-item-gui.modify-lore.lore")),
                "value",
                section.getString("modify-lore", "true")));
        modifyLoreItem.setItemMeta(tempVal2);
        // finish
        ItemStack finishItem = new ItemStack(Material.GREEN_DYE);
        ItemMeta tempVal4 = finishItem.getItemMeta();
        tempVal4.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-display-item-gui.finish.name")));
        tempVal4.setLore(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "edit-display-item-gui.finish.lore")));
        finishItem.setItemMeta(tempVal4);
        if (Objects.isNull(inv)) {
            inv = Bukkit.createInventory(owner, 9,
                    TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                            "edit-display-item-gui.title")));
        }
        inv.setItem(0, tempDisplayItem);
        inv.setItem(1, modifyLoreItem);
        inv.setItem(8, finishItem);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (!Objects.equals(inventory, getInv())) {
            return true;
        }
        if (slot == 1) {
            if (section.getBoolean("modify-lore", true)) {
                section.set("modify-lore", "false");
            } else {
                section.set("modify-lore", "true");
            }
            constructGUI();
        }
        if (slot == 8) {
            Map<String, Object> itemSection = ItemUtil.debuildItem(displayItem);
            for (String key : itemSection.keySet()) {
                section.set(key, itemSection.get(key));
            }
            previousGUI.openGUI();
        }
        return true;
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        if (newItems.containsKey(0)) {
            this.displayItem = newItems.get(0);
            constructGUI();
            return true;
        }
        return true;
    }
}
