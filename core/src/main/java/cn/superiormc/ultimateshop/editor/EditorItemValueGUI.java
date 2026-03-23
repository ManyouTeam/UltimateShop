package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.EditorManager;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class EditorItemValueGUI extends InvGUI {

    private static final int EDIT_SLOT = 13;

    private final EditorTarget target;

    private final String path;

    public EditorItemValueGUI(Player owner, EditorTarget target, String path) {
        super(owner);
        this.target = target;
        this.path = path;
    }

    @Override
    public boolean getChangeable() {
        return true;
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.item.title", "Item Editor: {path}",
                "path", EditorUtil.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title);
        }
        inv.clear();

        ConfigurationSection section = EditorManager.editorManager.getSection(target, path);
        if (section != null && (inv.getItem(EDIT_SLOT) == null || inv.getItem(EDIT_SLOT).getType().isAir())) {
            try {
                ItemStack itemStack = BuildItem.buildItemStack(player, section, section.getInt("amount", 1));
                inv.setItem(EDIT_SLOT, itemStack);
            } catch (Throwable ignored) {
                inv.setItem(EDIT_SLOT, new ItemStack(Material.AIR));
            }
        }

        inv.setItem(10, EditorUtil.createItem(Material.HOPPER,
                EditorLang.text(player, "editor.item.slot.name", "&eItemFormat Slot"),
                List.of(
                        EditorLang.text(player, "editor.item.slot.line-1", "&7Put the target item into slot 13."),
                        EditorLang.text(player, "editor.item.slot.line-2", "&7Then click save to DebuildItem into yaml.")
                )));
        inv.setItem(22, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.item.raw.name", "&eRaw Section"),
                List.of(EditorLang.text(player, "editor.item.raw.desc", "&7Open all keys inside this item section"))));
        inv.setItem(23, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.item.apply.name", "&aApply Item"),
                List.of(EditorLang.text(player, "editor.item.apply.desc", "&7Serialize slot 13 into the current section"))));
        inv.setItem(24, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.item.clear.name", "&cClear Section"),
                List.of(EditorLang.text(player, "editor.item.clear.desc", "&7Delete this entire item section"))));
        inv.setItem(26, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.item.back.desc", "&7Return to the parent section"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == EDIT_SLOT) {
            return false;
        }
        if (slot == 22) {
            EditorManager.editorManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 23) {
            ItemStack itemStack = inv.getItem(EDIT_SLOT);
            if (itemStack == null || itemStack.getType().isAir()) {
                return true;
            }
            EditorManager.editorManager.replaceItemSection(player, target, path, itemStack);
            EditorManager.editorManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            return true;
        }
        if (slot == 24) {
            EditorManager.editorManager.removeValue(player, target, path);
            EditorManager.editorManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            return true;
        }
        if (slot == 26) {
            EditorManager.editorManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            return true;
        }
        return true;
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        for (int slot : newItems.keySet()) {
            if (slot != EDIT_SLOT) {
                return true;
            }
        }
        return false;
    }
}
