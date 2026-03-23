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
        inv.setItem(18, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.thing-item.apply-conditions.name", "&eApply Conditions"),
                List.of(EditorLang.text(player, "editor.thing-item.apply-conditions.desc",
                        "&7Open apply-conditions for this single thing"))));
        inv.setItem(19, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.thing-item.require-conditions.name", "&eRequire Conditions"),
                List.of(EditorLang.text(player, "editor.thing-item.require-conditions.desc",
                        "&7Open require-conditions for this single thing"))));
        inv.setItem(20, EditorUtil.createItem(Material.BLAZE_POWDER,
                EditorLang.text(player, "editor.thing-item.give-actions.name", "&eGive Actions"),
                List.of(EditorLang.text(player, "editor.thing-item.give-actions.desc",
                        "&7Open give-actions for this single thing"))));
        inv.setItem(21, EditorUtil.createItem(Material.BLAZE_POWDER,
                EditorLang.text(player, "editor.thing-item.take-actions.name", "&eTake Actions"),
                List.of(EditorLang.text(player, "editor.thing-item.take-actions.desc",
                        "&7Open take-actions for this single thing"))));
        inv.setItem(22, EditorUtil.createItem(Material.BOOK,
                EditorLang.text(player, "editor.thing-item.preset.name", "&ePreset Editor"),
                List.of(EditorLang.text(player, "editor.thing-item.preset.desc",
                        "&7Return to the single thing preset editor"))));
        inv.setItem(23, EditorUtil.createItem(Material.EMERALD,
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
        if (slot == 18) {
            openNestedSection(path + ".apply-conditions");
            return true;
        }
        if (slot == 19) {
            openNestedSection(path + ".require-conditions");
            return true;
        }
        if (slot == 20) {
            openNestedSection(path + ".give-actions");
            return true;
        }
        if (slot == 21) {
            openNestedSection(path + ".take-actions");
            return true;
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

    private void openNestedSection(String fullPath) {
        EditorManager.editorManager.ensureSection(target, fullPath);
        EditorManager.editorManager.save(player, target);
        EditorManager.editorManager.openTarget(player, target, fullPath, 0);
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
