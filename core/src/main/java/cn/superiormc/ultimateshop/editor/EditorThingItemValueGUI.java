package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorThingItemValueGUI extends InvGUI {

    private static final int EDIT_SLOT = 13;

    private final EditorTarget target;

    private final String path;

    public EditorThingItemValueGUI(Player owner, EditorTarget target, String path) {
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
        title = EditorLang.text(player, "editor.thing-item.title", "Thing ItemFormat: {path}",
                "path", EditorUtil.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title);
        }
        inv.clear();

        ConfigurationSection section = EditorManager.editorManager.getSection(target, path);
        if (section != null) {
            try {
                ItemStack itemStack = BuildItem.buildItemStack(player, section, Math.max(section.getInt("amount", 1), 1));
                inv.setItem(EDIT_SLOT, itemStack);
            } catch (Throwable ignored) {
                inv.setItem(EDIT_SLOT, new ItemStack(Material.AIR));
            }
        }

        inv.setItem(10, EditorUtil.createItem(Material.HOPPER,
                EditorLang.text(player, "editor.thing-item.slot.name", "&eInline ItemFormat"),
                List.of(
                        EditorLang.text(player, "editor.thing-item.slot.line-1", "&7Put the item in slot 13."),
                        EditorLang.text(player, "editor.thing-item.slot.line-2", "&7This only refreshes item-format fields."),
                        EditorLang.text(player, "editor.thing-item.slot.line-3", "&7Common single thing options are preserved.")
                )));
        inv.setItem(22, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.thing-item.apply.name", "&aApply ItemFormat"),
                List.of(EditorLang.text(player, "editor.thing-item.apply.desc", "&7Update item data inside this single thing"))));
        inv.setItem(24, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.thing-item.clear.name", "&cClear Item Fields"),
                List.of(EditorLang.text(player, "editor.thing-item.clear.desc", "&7Remove material / hook-item / item-format fields only"))));
        inv.setItem(26, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.thing-item.back.desc", "&7Return to the preset editor"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == EDIT_SLOT) {
            return false;
        }
        if (slot == 22) {
            ItemStack itemStack = inv.getItem(EDIT_SLOT);
            if (itemStack == null || itemStack.getType().isAir()) {
                return true;
            }
            EditorManager.editorManager.replaceInlineThingItem(player, target, path, itemStack);
            EditorManager.editorManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 24) {
            EditorManager.editorManager.clearInlineThingItem(player, target, path);
            EditorManager.editorManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 26) {
            EditorManager.editorManager.openTarget(player, target, path, 0);
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
