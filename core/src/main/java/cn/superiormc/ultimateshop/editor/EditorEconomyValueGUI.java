package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.HookManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EditorEconomyValueGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    public EditorEconomyValueGUI(Player owner, EditorTarget target, String path) {
        super(owner);
        this.target = target;
        this.path = path;
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.economy.title", "Economy Editor: {path}",
                "path", EditorUtil.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title);
        }
        inv.clear();

        ConfigurationSection section = EditorManager.editorManager.ensureSection(target, path);
        String provider = getProvider(section);
        String currency = section.getString("economy-type", "default");
        String amount = String.valueOf(section.get("amount"));
        String placeholder = section.getString("placeholder", "");

        inv.setItem(10, EditorUtil.createItem(Material.GOLD_INGOT,
                EditorLang.text(player, "editor.economy.provider.name", "&eProvider"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", provider),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        inv.setItem(12, EditorUtil.createItem(Material.NAME_TAG,
                EditorLang.text(player, "editor.economy.currency.name", "&eCurrency Type"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", currency),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit")
                )));
        inv.setItem(14, EditorUtil.createItem(Material.SLIME_BALL,
                EditorLang.text(player, "editor.economy.amount.name", "&eAmount"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", amount),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit")
                )));
        inv.setItem(16, EditorUtil.createItem(Material.PAPER,
                EditorLang.text(player, "editor.economy.placeholder.name", "&ePlaceholder"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}",
                                "value", EditorUtil.trim(placeholder, 32)),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"),
                        EditorLang.text(player, "editor.action.delete", "&cRight click to delete")
                )));
        inv.setItem(20, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.economy.conditions.name", "&eConditions"),
                List.of(EditorLang.text(player, "editor.economy.conditions.desc", "&7Open the nested conditions section"))));
        inv.setItem(22, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.economy.raw.name", "&eRaw Section"),
                List.of(EditorLang.text(player, "editor.economy.raw.desc", "&7Open all keys inside this economy section"))));
        inv.setItem(24, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.economy.delete.name", "&cDelete Section"),
                List.of(EditorLang.text(player, "editor.economy.delete.desc", "&7Delete this economy format section"))));
        inv.setItem(26, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.economy.back.desc", "&7Return to the parent section"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        ConfigurationSection section = EditorManager.editorManager.ensureSection(target, path);

        if (slot == 10) {
            List<String> providers = new ArrayList<>();
            providers.add("exp");
            providers.add("levels");
            providers.addAll(HookManager.hookManager.getEconomyHookNames());

            String current = getProvider(section);
            int index = providers.indexOf(current);
            if (index < 0) {
                index = 0;
            } else if (type.isShiftClick()) {
                index = (index - 1 + providers.size()) % providers.size();
            } else {
                index = (index + 1) % providers.size();
            }

            String next = providers.get(index);
            if (next.equals("exp") || next.equals("levels")) {
                section.set("economy-plugin", null);
                section.set("economy-type", next);
            } else {
                section.set("economy-plugin", next);
                if (section.getString("economy-type") == null) {
                    section.set("economy-type", "default");
                }
            }
            EditorManager.editorManager.save(player, target);
            new EditorEconomyValueGUI(player, target, path).openGUI(true);
            return true;
        }
        if (slot == 12) {
            EditorManager.editorManager.promptString(player, target, path + ".economy-type",
                    () -> new EditorEconomyValueGUI(player, target, path).openGUI(true));
            return true;
        }
        if (slot == 14) {
            EditorManager.editorManager.promptDouble(player, target, path + ".amount",
                    () -> new EditorEconomyValueGUI(player, target, path).openGUI(true));
            return true;
        }
        if (slot == 16) {
            if (type.isRightClick()) {
                EditorManager.editorManager.removeValue(player, target, path + ".placeholder");
                new EditorEconomyValueGUI(player, target, path).openGUI(true);
            } else {
                EditorManager.editorManager.promptString(player, target, path + ".placeholder",
                        () -> new EditorEconomyValueGUI(player, target, path).openGUI(true));
            }
            return true;
        }
        if (slot == 20) {
            EditorManager.editorManager.ensureSection(target, path + ".conditions");
            EditorManager.editorManager.save(player, target);
            EditorManager.editorManager.openTarget(player, target, path + ".conditions", 0);
            return true;
        }
        if (slot == 22) {
            EditorManager.editorManager.openTarget(player, target, path, 0);
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

    private String getProvider(ConfigurationSection section) {
        String plugin = section.getString("economy-plugin");
        if (plugin != null && !plugin.isEmpty()) {
            return plugin;
        }
        return section.getString("economy-type", "exp");
    }
}
