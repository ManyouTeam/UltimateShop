package cn.superiormc.ultimateshop.gui.inv.editor.subinventory;

import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.GUIMode;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class EditEconomyItem extends InvGUI {

    public ConfigurationSection section;

    private String id;

    public EditEconomyItem(Player owner, String id, ChooseSingleProductGUI gui) {
        super(owner);
        this.previousGUI = gui;
        this.id = id;
        this.section = gui.section.createSection(id);
    }

    @Override
    protected void constructGUI() {
        // hook plugin
        ItemStack hookPluginItem = new ItemStack(Material.MAP);
        ItemMeta tempVal2 = hookPluginItem.getItemMeta();
        tempVal2.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-economy-item-gui.economy-plugin.name")));
        tempVal2.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-economy-item-gui.economy-plugin.lore")),
                "value",
                section.getString("economy-plugin", "Not Set")));
        hookPluginItem.setItemMeta(tempVal2);
        // economy type
        ItemStack economyTypeItem = new ItemStack(Material.ANVIL);
        ItemMeta tempVal3 = hookPluginItem.getItemMeta();
        tempVal3.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-economy-item-gui.economy-type.name")));
        tempVal3.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-economy-item-gui.economy-type.lore")),
                "value",
                section.getString("economy-type", "Not Set")));
        economyTypeItem.setItemMeta(tempVal3);
        // amount
        ItemStack amountItem = new ItemStack(Material.BUCKET);
        ItemMeta tempVal1 = hookPluginItem.getItemMeta();
        tempVal1.setDisplayName(TextUtil.parse(LanguageManager.languageManager.getStringText("editor." +
                "edit-economy-item-gui.amount.name")));
        tempVal1.setLore(CommonUtil.modifyList(TextUtil.getListWithColor(LanguageManager.languageManager.getStringListText("editor." +
                        "edit-economy-item-gui.amount.lore")),
                "value",
                section.getString("amount", "1")));
        amountItem.setItemMeta(tempVal1);
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
                            "edit-economy-item-gui.title")));
        }
        inv.setItem(0, hookPluginItem);
        inv.setItem(1, economyTypeItem);
        inv.setItem(2, amountItem);
        inv.setItem(8, finishItem);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == 0) {
            switch (section.getString("economy-plugin", "Not Set")) {
                case "Not Set":
                case "RoyaleEconomy":
                    section.set("economy-plugin", "VANILLA");
                    section.set("economy-type", "exp");
                    constructGUI();
                    break;
                case "VANILLA":
                    section.set("economy-plugin", "Vault");
                    section.set("economy-type", null);
                    constructGUI();
                    break;
                case "Vault":
                    section.set("economy-plugin", "PlayerPoints");
                    section.set("economy-type", null);
                    constructGUI();
                    break;
                case "PlayerPoints":
                    section.set("economy-plugin", "CoinsEngine");
                    section.set("economy-type", "default");
                    constructGUI();
                    break;
                case "CoinsEngine":
                    section.set("economy-plugin", "UltraEconomy");
                    section.set("economy-type", "default");
                    constructGUI();
                    break;
                case "UltraEconomy":
                    section.set("economy-plugin", "EcoBits");
                    section.set("economy-type", "default");
                    constructGUI();
                    break;
                case "EcoBits":
                    section.set("economy-plugin", "PEconomy");
                    section.set("economy-type", "default");
                    constructGUI();
                    break;
                case "PEconomy":
                    section.set("economy-plugin", "RedisEconomy");
                    section.set("economy-type", "default");
                    constructGUI();
                    break;
                case "RedisEconomy":
                    section.set("economy-plugin", "RoyaleEconomy");
                    section.set("economy-type", "default");
                    constructGUI();
                    break;
            }
            constructGUI();
        }
        if (slot == 1) {
            if (section.getString("economy-plugin", "VANILLA").equals("VANILLA")) {
                if (section.getString("economy-type", "Not Set").equals("exp")) {
                    section.set("economy-type", "levels");
                } else {
                    section.set("economy-type", "exp");
                }
                constructGUI();
            } else {
                guiCache.put(owner, this);
                guiMode = GUIMode.EDIT_ECONOMY_TYPE;
                LanguageManager.languageManager.sendStringText(owner, "editor.enter-economy-type");
                owner.closeInventory();
            }
        }
        if (slot == 2) {
            guiCache.put(owner, this);
            guiMode = GUIMode.EDIT_ECONOMY_AMOUNT;
            LanguageManager.languageManager.sendStringText(owner, "editor.enter-economy-amount");
            owner.closeInventory();
        }
        if (slot == 8) {
            if (section.getString("economy-plugin") == null && section.getString("economy-type") == null) {
                previousGUI.getSection().set(id, null);
            }
            else if (section.getString("economy-plugin") != null && section.getString("economy-plugin").equals("VANILLA")) {
                section.set("economy-plugin", null);
            }
            guiMode = GUIMode.OPEN_NEW_GUI;
            previousGUI.openGUI();
        }
        return true;
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        if (section.get("economy-plugin") == null && section.get("economy-type") == null) {
            previousGUI.getSection().set(id, null);
        }
        return super.closeEventHandle(inventory);
    }
}
