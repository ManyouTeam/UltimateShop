package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
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

        ConfigurationSection section = MenuStatusManager.menuStatusManager.ensureSection(target, path);
        String provider = getProvider(section);
        String currency = section.getString("economy-type", "default");
        String amount = String.valueOf(section.get("amount"));
        String placeholder = section.getString("placeholder", "");

        setInvItem(10, EditorUtil.createItem(Material.GOLD_INGOT,
                EditorLang.text(player, "editor.economy.provider.name", "&eProvider"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", provider),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        setInvItem(12, EditorUtil.createItem(Material.NAME_TAG,
                EditorLang.text(player, "editor.economy.currency.name", "&eCurrency Type"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", currency),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit")
                )));
        setInvItem(14, EditorUtil.createItem(Material.SLIME_BALL,
                EditorLang.text(player, "editor.economy.amount.name", "&eAmount"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", amount),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit")
                )));
        setInvItem(16, EditorUtil.createItem(Material.PAPER,
                EditorLang.text(player, "editor.economy.placeholder.name", "&ePlaceholder"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}",
                                "value", EditorUtil.trim(placeholder, 32)),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"),
                        EditorLang.text(player, "editor.action.delete", "&cRight click to delete")
                )));
        setInvItem(20, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.economy.conditions.name", "&eConditions"),
                List.of(EditorLang.text(player, "editor.economy.conditions.desc", "&7Open the nested conditions section"))));
        setInvItem(22, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.economy.raw.name", "&eRaw Section"),
                List.of(EditorLang.text(player, "editor.economy.raw.desc", "&7Open all keys inside this economy section"))));
        setInvItem(24, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.economy.delete.name", "&cDelete Section"),
                List.of(EditorLang.text(player, "editor.economy.delete.desc", "&7Delete this economy format section"))));
        setInvItem(26, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.economy.back.desc", "&7Return to the parent section"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        ConfigurationSection section = MenuStatusManager.menuStatusManager.ensureSection(target, path);

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
            MenuStatusManager.menuStatusManager.save(player, target);
            new EditorEconomyValueGUI(player, target, path).openGUI(true);
            return true;
        }
        if (slot == 12) {
            MenuStatusManager.menuStatusManager.promptString(player, target, path + ".economy-type",
                    () -> new EditorEconomyValueGUI(player, target, path).openGUI(true));
            return true;
        }
        if (slot == 14) {
            MenuStatusManager.menuStatusManager.promptDouble(player, target, path + ".amount",
                    () -> new EditorEconomyValueGUI(player, target, path).openGUI(true));
            return true;
        }
        if (slot == 16) {
            if (type.isRightClick()) {
                MenuStatusManager.menuStatusManager.removeValue(player, target, path + ".placeholder");
                new EditorEconomyValueGUI(player, target, path).openGUI(true);
            } else {
                MenuStatusManager.menuStatusManager.promptString(player, target, path + ".placeholder",
                        () -> new EditorEconomyValueGUI(player, target, path).openGUI(true));
            }
            return true;
        }
        if (slot == 20) {
            MenuStatusManager.menuStatusManager.ensureSection(target, path + ".conditions");
            MenuStatusManager.menuStatusManager.save(player, target);
            MenuStatusManager.menuStatusManager.openTarget(player, target, path + ".conditions", 0);
            return true;
        }
        if (slot == 22) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 24) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            return true;
        }
        if (slot == 26) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
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
