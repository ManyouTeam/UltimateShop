package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.*;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EditorSectionGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    private final int page;

    private final boolean rawMode;

    private final List<String> keys = new ArrayList<>();

    public EditorSectionGUI(Player owner, EditorTarget target, String path, int page, boolean rawMode) {
        super(owner);
        this.target = target;
        this.path = path == null ? "" : path;
        this.page = page;
        this.rawMode = rawMode;
    }

    @Override
    public void constructGUI() {
        ConfigurationSection section = MenuStatusManager.menuStatusManager.getSection(target, path);
        title = target.getId() + ": " + EditorUtil.displayPath(player, path);
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 54, title);
        }
        inv.clear();
        keys.clear();
        if (section != null) {
            keys.addAll(section.getKeys(false));
            keys.sort(Comparator.naturalOrder());
        }

        int start = page * 45;
        for (int i = 0; i < 45; i++) {
            int index = start + i;
            if (index >= keys.size()) {
                break;
            }

            String key = keys.get(index);
            EditorValueKind kind = EditorTypeResolver.resolve(section, key);
            Object value = section.get(key);
            inv.setItem(i, createEntryItem(key, kind, value));
        }

        inv.setItem(45, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
        inv.setItem(46, EditorUtil.createItem(Material.OAK_SIGN,
                EditorLang.text(player, "editor.section.path.name", "&eCurrent Path"),
                List.of(EditorLang.text(player, "editor.section.path.desc", "&f{value}",
                        "value", EditorUtil.displayPath(player, path)))));
        inv.setItem(49, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.common.reload.name", "&aReload Plugin"),
                List.of(
                        EditorLang.text(player, "editor.section.reload.line-1", "&7Changes are already written to disk."),
                        EditorLang.text(player, "editor.section.reload.line-2", "&7Use this to rebuild live shop/menu objects.")
                )));
        inv.setItem(50, EditorUtil.createItem(Material.ANVIL,
                EditorLang.text(player, "editor.section.add-child.name", "&eAdd Child"),
                List.of(
                        EditorTypeResolver.isActionCollection(path)
                                || (EditorTypeResolver.isConditionCollection(path) && !EditorTypeResolver.isLimitConditionRoot(path))
                                || EditorTypeResolver.isThingCollection(path)
                                ? EditorLang.text(player, "editor.section.add-child.numbered", "&7Add a new numbered entry")
                                : ("items".equals(path) || EditorTypeResolver.lastSegment(path).equals("buttons")
                                || EditorTypeResolver.isLimitConditionRoot(path))
                                ? EditorLang.text(player, "editor.section.add-child.named", "&7Create a new named section entry")
                                : EditorLang.text(player, "editor.section.add-child.generic", "&7Add a new child node under this section"),
                        !EditorTypeResolver.isActionCollection(path)
                                && !EditorTypeResolver.isThingCollection(path)
                                && !("items".equals(path) || EditorTypeResolver.lastSegment(path).equals("buttons")
                                || EditorTypeResolver.isLimitConditionRoot(path))
                                ? EditorLang.text(player, "editor.section.add-child.value-mode",
                                "&7After the key, input the value directly. Use &fsection &7or &f{} &7to create a section.")
                                : "",
                        EditorTypeResolver.isThingCollection(path)
                                ? EditorLang.text(player, "editor.section.add-child.thing-item", "&aLeft click: add item template")
                                : "",
                        EditorTypeResolver.isThingCollection(path)
                                ? EditorLang.text(player, "editor.section.add-child.thing-economy", "&eRight click: add economy template")
                                : ""
                )));
        inv.setItem(52, EditorUtil.createItem(Material.SPECTRAL_ARROW,
                EditorLang.text(player, "editor.common.previous-page.name", "&ePrevious Page"),
                List.of(EditorLang.text(player, "editor.common.previous-page.desc", "&7Go to the previous page"))));
        inv.setItem(53, EditorUtil.createItem(Material.TIPPED_ARROW,
                EditorLang.text(player, "editor.common.next-page.name", "&eNext Page"),
                List.of(EditorLang.text(player, "editor.common.next-page.desc", "&7Go to the next page"))));
    }

    private ItemStack createEntryItem(String key, EditorValueKind kind, Object value) {
        Material material = switch (kind) {
            case ITEM_SECTION -> Material.ITEM_FRAME;
            case ECONOMY_SECTION -> Material.GOLD_INGOT;
            case ACTION_COLLECTION, ACTION_TYPE -> Material.BLAZE_POWDER;
            case CONDITION_COLLECTION, CONDITION_TYPE -> Material.COMPARATOR;
            case BOOLEAN -> Boolean.TRUE.equals(value) ? Material.LIME_DYE : Material.GRAY_DYE;
            case INTEGER, DOUBLE -> Material.SLIME_BALL;
            case STRING -> Material.NAME_TAG;
            case STRING_LIST, INTEGER_LIST -> Material.WRITABLE_BOOK;
            case SECTION -> Material.CHEST;
            default -> Material.PAPER;
        };

        List<String> lore = new ArrayList<>();
        lore.add(EditorLang.text(player, "editor.lore.path", "&7Path: &f{value}",
                "value", EditorTypeResolver.buildPath(path, key)));
        lore.add(EditorLang.text(player, "editor.lore.kind", "&7Kind: &f{value}",
                "value", EditorLang.valueKind(player, kind)));
        lore.addAll(EditorUtil.summarize(player, value));
        switch (kind) {
            case SECTION, ITEM_SECTION, ECONOMY_SECTION, ACTION_COLLECTION, CONDITION_COLLECTION, STRING_LIST, INTEGER_LIST:
                lore.add(EditorLang.text(player, "editor.action.open", "&aLeft click to open"));
                if (kind == EditorValueKind.STRING_LIST || kind == EditorValueKind.INTEGER_LIST) {
                    lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                }
                break;
            case BOOLEAN:
                lore.add(EditorLang.text(player, "editor.action.toggle", "&aLeft click to toggle"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
            case INTEGER, DOUBLE:
                lore.add(EditorLang.text(player, "editor.action.increase-ten", "&aLeft click to add 10"));
                lore.add(EditorLang.text(player, "editor.action.decrease-ten", "&cRight click to subtract 10"));
                lore.add(EditorLang.text(player, "editor.action.increase-one", "&aShift-left to add 1"));
                lore.add(EditorLang.text(player, "editor.action.decrease-one", "&cShift-right to subtract 1"));
                lore.add(EditorLang.text(player, "editor.action.middle-clear", "&eMiddle click to clear"));
                break;
            case ACTION_TYPE:
                lore.add(EditorLang.text(player, "editor.action.cycle-action", "&aLeft click to cycle action type"));
                lore.add(EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
            case CONDITION_TYPE:
                lore.add(EditorLang.text(player, "editor.action.cycle-condition", "&aLeft click to cycle condition type"));
                lore.add(EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
            default:
                lore.add(EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
        }

        return EditorUtil.createItem(material, "&e" + key, lore);
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        ConfigurationSection section = MenuStatusManager.menuStatusManager.getSection(target, path);

        if (slot >= 0 && slot < 45) {
            int index = page * 45 + slot;
            if (index >= keys.size() || section == null) {
                return true;
            }

            String key = keys.get(index);
            String childPath = EditorTypeResolver.buildPath(path, key);
            EditorValueKind kind = EditorTypeResolver.resolve(section, key);

            if (type.isRightClick() && kind != EditorValueKind.INTEGER && kind != EditorValueKind.DOUBLE) {
                if (EditorTypeResolver.isLimitConditionRoot(path)) {
                    String limitsPath = path.replace("-conditions", "");
                    MenuStatusManager.menuStatusManager.removeLimitConditionGroup(player, target, limitsPath, key);
                } else {
                    MenuStatusManager.menuStatusManager.removeValue(player, target, childPath);
                }
                MenuStatusManager.menuStatusManager.openTarget(player, target, path, page);
                return true;
            }

            switch (kind) {
                case SECTION:
                case ACTION_COLLECTION:
                case CONDITION_COLLECTION:
                    MenuStatusManager.menuStatusManager.openTarget(player, target, childPath, 0);
                    return true;
                case ITEM_SECTION:
                    new EditorItemValueGUI(player, target, childPath).openGUI(true);
                    return true;
                case ECONOMY_SECTION:
                    new EditorEconomyValueGUI(player, target, childPath).openGUI(true);
                    return true;
                case BOOLEAN:
                    boolean current = section.getBoolean(key);
                    MenuStatusManager.menuStatusManager.setValue(player, target, childPath, !current);
                    MenuStatusManager.menuStatusManager.openTarget(player, target, path, page);
                    return true;
                case INTEGER:
                    if (type == ClickType.MIDDLE) {
                        MenuStatusManager.menuStatusManager.removeValue(player, target, childPath);
                    } else {
                        int delta = resolveIntegerDelta(type);
                        if (delta != 0) {
                            int currentValue = section.getInt(key, 0);
                            MenuStatusManager.menuStatusManager.setValue(player, target, childPath, currentValue + delta);
                        }
                    }
                    MenuStatusManager.menuStatusManager.openTarget(player, target, path, page);
                    return true;
                case DOUBLE:
                    if (type == ClickType.MIDDLE) {
                        MenuStatusManager.menuStatusManager.removeValue(player, target, childPath);
                    } else {
                        double delta = resolveDoubleDelta(type);
                        if (delta != 0) {
                            double currentValue = section.getDouble(key, 0D);
                            MenuStatusManager.menuStatusManager.setValue(player, target, childPath, currentValue + delta);
                        }
                    }
                    MenuStatusManager.menuStatusManager.openTarget(player, target, path, page);
                    return true;
                case STRING:
                    if ("economy-plugin".equalsIgnoreCase(key)) {
                        cycleValue(player, childPath, HookManager.hookManager.getEconomyHookNames(), type.isShiftClick());
                    } else if ("hook-plugin".equalsIgnoreCase(key)) {
                        cycleValue(player, childPath, HookManager.hookManager.getItemHookNames(), type.isShiftClick());
                    } else if ("as-sub-button".equalsIgnoreCase(key)) {
                        new EditorSubButtonValueGUI(player, target, path).openGUI(true);
                    } else if (EditorTypeResolver.isClickEventSection(path)) {
                        new EditorClickTypeBindingGUI(player, target, childPath, path).openGUI(true);
                    } else if (EditorTypeResolver.isResetTimeKey(key)) {
                        new EditorResetTimeValueGUI(player, target, childPath, path).openGUI(true);
                    } else {
                        MenuStatusManager.menuStatusManager.promptString(player, target, childPath,
                                () -> MenuStatusManager.menuStatusManager.openTarget(player, target, path, page));
                    }
                    return true;
                case STRING_LIST:
                    new EditorListValueGUI(player, target, childPath, false, 0).openGUI(true);
                    return true;
                case INTEGER_LIST:
                    new EditorListValueGUI(player, target, childPath, true, 0).openGUI(true);
                    return true;
                case ACTION_TYPE:
                    cycleValue(player, childPath, ActionManager.actionManager.getActionTypes(), type.isShiftClick());
                    return true;
                case CONDITION_TYPE:
                    cycleValue(player, childPath, ConditionManager.conditionManager.getConditionTypes(), type.isShiftClick());
                    return true;
                default:
                    return true;
            }
        }

        if (slot == 45) {
            if (path.isEmpty()) {
                MenuStatusManager.menuStatusManager.openScope(player, target.getScope(), 0);
            } else {
                MenuStatusManager.menuStatusManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            }
            return true;
        }
        if (slot == 49) {
            MenuStatusManager.menuStatusManager.reloadAndReopen(player);
            return true;
        }
        if (slot == 50) {
            if (EditorTypeResolver.isActionCollection(path)
                    || (EditorTypeResolver.isConditionCollection(path) && !EditorTypeResolver.isLimitConditionRoot(path))
                    || EditorTypeResolver.isThingCollection(path)) {
                MenuStatusManager.menuStatusManager.createDefaultCollectionEntry(player, target, path,
                        EditorTypeResolver.isThingCollection(path) && type.isRightClick());
                MenuStatusManager.menuStatusManager.openTarget(player, target, path, page);
            } else if ("items".equals(path) || EditorTypeResolver.lastSegment(path).equals("buttons")) {
                MenuStatusManager.menuStatusManager.promptCreateNamedSection(player, target, path,
                        () -> MenuStatusManager.menuStatusManager.openTarget(player, target, path, page));
            } else if (EditorTypeResolver.isLimitConditionRoot(path)) {
                MenuStatusManager.menuStatusManager.promptCreateLimitConditionRootEntry(player, target, path,
                        () -> MenuStatusManager.menuStatusManager.openTarget(player, target, path, page));
            } else {
                MenuStatusManager.menuStatusManager.promptNewChild(player, target, path,
                        () -> MenuStatusManager.menuStatusManager.openTarget(player, target, path, page));
            }
            return true;
        }
        if (slot == 52 && page > 0) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, page - 1);
            return true;
        }
        if (slot == 53 && (page + 1) * 45 < keys.size()) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, page + 1);
            return true;
        }
        return true;
    }

    private void cycleValue(Player player, String childPath, List<String> values, boolean reverse) {
        if (values == null || values.isEmpty()) {
            return;
        }
        String current = target.getConfig().getString(childPath);
        int index = values.indexOf(current);
        if (index < 0) {
            index = 0;
        } else if (reverse) {
            index = (index - 1 + values.size()) % values.size();
        } else {
            index = (index + 1) % values.size();
        }
        MenuStatusManager.menuStatusManager.setValue(player, target, childPath, values.get(index));
        MenuStatusManager.menuStatusManager.openTarget(player, target, path, page);
    }

    private int resolveIntegerDelta(ClickType type) {
        if (type.isShiftClick() && type.isLeftClick()) {
            return 1;
        }
        if (type.isShiftClick() && type.isRightClick()) {
            return -1;
        }
        if (type.isLeftClick()) {
            return 10;
        }
        if (type.isRightClick()) {
            return -10;
        }
        return 0;
    }

    private double resolveDoubleDelta(ClickType type) {
        return resolveIntegerDelta(type);
    }
}
