package cn.superiormc.ultimateshop.gui.inv.editor.subinventory;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditProductGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EditDisplayItem extends InvGUI {

    public ConfigurationSection section;

    public ItemStack displayItem;

    public ItemStack tempDisplayItem;

    public EditDisplayItem(Player owner, EditProductGUI gui) {
        super(owner);
        this.previousGUI = gui;
        this.section = gui.section.getConfigurationSection("display-item");
        if (section == null) {
            section = gui.section.createSection("display-item");
            this.displayItem = new ItemStack(Material.BARRIER);
            this.tempDisplayItem = new ItemStack(Material.BARRIER);
        }
        else {
            // display item
            this.displayItem = ItemUtil.buildItemStack(owner, section,
                    MathUtil.doCalculate(TextUtil.withPAPI(section.getString("amount", "1"), owner)).intValue());
            this.tempDisplayItem = ItemUtil.buildItemStack(owner, section,
                    MathUtil.doCalculate(TextUtil.withPAPI(section.getString("amount", "1"), owner)).intValue());
        }
    }

    @Override
    protected void constructGUI() {
        ItemMeta tempVal1 = tempDisplayItem.getItemMeta();
        if (tempVal1 == null) {
            return;
        }
        List<String> tempVal3 = new ArrayList<>();
        if (tempVal1.hasLore()) {
            tempVal3 = tempVal1.getLore();
        }
        tempVal3.addAll(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                "edit-display-item-gui.display-item.add-lore")));
        tempVal1.setLore(tempVal3);
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
        if (slot == 0) {
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
            if (displayItem.getType() == Material.BARRIER && previousGUI instanceof EditProductGUI) {
                previousGUI.getSection().set("display-item", null);
            }
            previousGUI.openGUI();
        }
        return true;
    }

    @Override
    public void afterClickEventHandle(ItemStack item, ItemStack currentItem, int slot) {
        if (slot == 0) {
            if (item != null && !item.getType().isAir()) {
                this.displayItem = new ItemStack(item);
                this.tempDisplayItem = new ItemStack(item);
                for (String key : section.getKeys(true)) {
                    if (!key.equals("modify-lore")) {
                        section.set(key, null);
                    }
                }
                Map<String, Object> itemSection = ItemUtil.debuildItem(displayItem);
                for (String key : itemSection.keySet()) {
                    section.set(key, itemSection.get(key));
                }
                constructGUI();
            }
        }
    }
}
