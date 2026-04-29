package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.*;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EditorPresetGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    private final EditorPreset preset;

    public EditorPresetGUI(Player owner, EditorTarget target, String path, EditorPreset preset) {
        super(owner);
        this.target = target;
        this.path = path == null ? "" : path;
        this.preset = preset;
    }

    @Override
    public void constructGUI() {
        title = target.getId() + ": " + EditorLang.presetTitle(player, preset);
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 54, title);
        }
        inv.clear();

        List<EditorPresetField> fields = preset.getFields();
        for (int i = 0; i < Math.min(fields.size(), 45); i++) {
            EditorPresetField field = fields.get(i);
            setInvItem(i, createItem(field));
        }

        setInvItem(45, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
        if (preset.getKind() == EditorPresetKind.LIMITS_SECTION) {
            setInvItem(50, EditorUtil.createItem(Material.ANVIL,
                    EditorLang.text(player, "editor.preset.add-limit-group.name", "&eAdd Condition Group"),
                    List.of(EditorLang.text(player, "editor.preset.add-limit-group.desc",
                            "&7Create a new named limit group and matching conditions section"))));
        }
        setInvItem(49, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.preset.raw.name", "&eRaw Section"),
                List.of(EditorLang.text(player, "editor.preset.raw.desc", "&7Open the raw section fallback editor"))));
        setInvItem(53, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.common.reload.name", "&aReload Plugin"),
                List.of(EditorLang.text(player, "editor.preset.reload.desc", "&7Apply changes to live shop/menu objects"))));
    }

    private ItemStack createItem(EditorPresetField field) {
        List<String> lore = new ArrayList<>();
        String fullPath = fullPath(field);
        String fieldName = EditorLang.presetFieldName(player, preset, field);
        String fieldDescription = EditorLang.presetFieldDescription(player, preset, field);
        Object value;
        if (field.getType() == EditorPresetFieldType.DISPLAY) {
            value = getDisplayValue();
        } else if (fullPath.isEmpty()) {
            value = null;
        } else {
            value = target.getConfig().get(fullPath);
        }
        lore.add(EditorLang.text(player, "editor.lore.path", "&7Path: &f{value}",
                "value", fullPath.isEmpty() ? EditorLang.displayPath(player, path) : fullPath));
        lore.add(EditorLang.text(player, "editor.lore.description", "&7Description: &f{value}",
                "value", fieldDescription));
        if (value != null) {
            lore.addAll(EditorUtil.summarize(player, value));
        } else {
            lore.add(EditorLang.text(player, "editor.lore.not-set", "&7Current: &f<not set>"));
        }
        switch (field.getType()) {
            case SECTION, ACTIONS, CONDITIONS, ITEM_INLINE, ECONOMY_INLINE, STRING_LIST, INTEGER_LIST, CLICK_TYPE_BINDING, RESET_TIME:
                lore.add(EditorLang.text(player, "editor.action.open", "&aLeft click to open"));
                if (field.getType() == EditorPresetFieldType.STRING_LIST || field.getType() == EditorPresetFieldType.INTEGER_LIST) {
                    lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                }
                break;
            case STRING_CHOICE:
                lore.add(EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"));
                lore.add(EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
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
            case DISPLAY:
                lore.add(EditorLang.text(player, "editor.action.read-only", "&7Read-only summary"));
                break;
            default:
                lore.add(EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
        }
        return EditorUtil.createItem(field.getMaterial(), "&e" + fieldName, lore);
    }

    private String getDisplayValue() {
        if (preset.getKind() == EditorPresetKind.SINGLE_THING) {
            ConfigurationSection section = MenuStatusManager.menuStatusManager.getSection(target, path);
            if (section == null) {
                return "UNKNOWN";
            }
            if (section.contains("hook-plugin") && section.contains("hook-item")) {
                return "HOOK_ITEM";
            }
            if (section.contains("match-item")) {
                return "MATCH_ITEM";
            }
            if (section.contains("match-placeholder")) {
                return "CUSTOM";
            }
            if (section.contains("economy-plugin")) {
                return "HOOK_ECONOMY";
            }
            if (section.contains("economy-type") && !section.contains("economy-plugin")) {
                return "VANILLA_ECONOMY";
            }
            if (section.contains("material") || section.contains("item")) {
                return "VANILLA_ITEM";
            }
        }
        return "INFO";
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        List<EditorPresetField> fields = preset.getFields();
        if (slot >= 0 && slot < Math.min(fields.size(), 45)) {
            EditorPresetField field = fields.get(slot);
            if (field.getType() == EditorPresetFieldType.DISPLAY) {
                return true;
            }

            String fullPath = fullPath(field);
            if (type.isRightClick() && !fullPath.isEmpty()
                    && field.getType() != EditorPresetFieldType.SECTION
                    && field.getType() != EditorPresetFieldType.ACTIONS
                    && field.getType() != EditorPresetFieldType.CONDITIONS
                    && field.getType() != EditorPresetFieldType.ITEM_INLINE
                    && field.getType() != EditorPresetFieldType.ECONOMY_INLINE
                    && field.getType() != EditorPresetFieldType.INTEGER
                    && field.getType() != EditorPresetFieldType.DOUBLE
                    && field.getType() != EditorPresetFieldType.CLICK_TYPE_BINDING
                    && field.getType() != EditorPresetFieldType.RESET_TIME) {
                if (preset.getKind() == EditorPresetKind.LIMITS_SECTION
                        && !field.getKey().equals("default")
                        && !field.getKey().equals("global")) {
                    MenuStatusManager.menuStatusManager.removeLimitConditionGroup(player, target, path, field.getKey());
                } else {
                    MenuStatusManager.menuStatusManager.removeValue(player, target, fullPath);
                }
                MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
                return true;
            }

            switch (field.getType()) {
                case SECTION -> {
                    ConfigurationSection section = MenuStatusManager.menuStatusManager.ensureSection(target, fullPath);
                    MenuStatusManager.menuStatusManager.save(player, target);
                    if (EditorTypeResolver.isItemSection(fullPath, section)) {
                        new EditorItemValueGUI(player, target, fullPath).openGUI(true);
                    } else if (EditorTypeResolver.isEconomySection(fullPath, section)) {
                        new EditorEconomyValueGUI(player, target, fullPath).openGUI(true);
                    } else {
                        MenuStatusManager.menuStatusManager.openTarget(player, target, fullPath, 0);
                    }
                    return true;
                }
                case ACTIONS, CONDITIONS -> {
                    MenuStatusManager.menuStatusManager.ensureSection(target, fullPath);
                    MenuStatusManager.menuStatusManager.save(player, target);
                    MenuStatusManager.menuStatusManager.openTarget(player, target, fullPath, 0);
                    return true;
                }
                case ITEM_INLINE -> {
                    new EditorThingItemValueGUI(player, target, path).openGUI(true);
                    return true;
                }
                case ECONOMY_INLINE -> {
                    new EditorThingEconomyValueGUI(player, target, path).openGUI(true);
                    return true;
                }
                case BOOLEAN -> {
                    boolean current = target.getConfig().getBoolean(fullPath);
                    MenuStatusManager.menuStatusManager.setValue(player, target, fullPath, !current);
                    MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
                    return true;
                }
                case STRING -> {
                    if (field.getKey().equals("economy-plugin")) {
                        List<String> choices = new ArrayList<>();
                        choices.add("exp");
                        choices.add("levels");
                        choices.addAll(HookManager.hookManager.getEconomyHookNames());
                        cycle(fullPath, choices, type.isShiftClick());
                    } else if (field.getKey().equals("hook-plugin")) {
                        cycle(fullPath, HookManager.hookManager.getItemHookNames(), type.isShiftClick());
                    } else if (field.getKey().equals("as-sub-button")) {
                        new EditorSubButtonValueGUI(player, target, path).openGUI(true);
                    } else {
                        MenuStatusManager.menuStatusManager.promptString(player, target, fullPath,
                                () -> MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0));
                    }
                    return true;
                }
                case STRING_CHOICE -> {
                        cycle(fullPath, field.getChoices(), type.isShiftClick());
                    return true;
                }
                case CLICK_TYPE_BINDING -> {
                    new EditorClickTypeBindingGUI(player, target, fullPath, path).openGUI(true);
                    return true;
                }
                case RESET_TIME -> {
                    new EditorResetTimeValueGUI(player, target, fullPath, path).openGUI(true);
                    return true;
                }
                case INTEGER -> {
                    if (type == ClickType.MIDDLE) {
                        MenuStatusManager.menuStatusManager.removeValue(player, target, fullPath);
                    } else {
                        int delta = resolveIntegerDelta(type);
                        if (delta != 0) {
                            int current = target.getConfig().getInt(fullPath, 0);
                            MenuStatusManager.menuStatusManager.setValue(player, target, fullPath, current + delta);
                        }
                    }
                    MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
                    return true;
                }
                case DOUBLE -> {
                    if (type == ClickType.MIDDLE) {
                        MenuStatusManager.menuStatusManager.removeValue(player, target, fullPath);
                    } else {
                        double delta = resolveDoubleDelta(type);
                        if (delta != 0) {
                            double current = target.getConfig().getDouble(fullPath, 0D);
                            MenuStatusManager.menuStatusManager.setValue(player, target, fullPath, current + delta);
                        }
                    }
                    MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
                    return true;
                }
                case STRING_LIST -> {
                    new EditorListValueGUI(player, target, fullPath, false, 0).openGUI(true);
                    return true;
                }
                case INTEGER_LIST -> {
                    new EditorListValueGUI(player, target, fullPath, true, 0).openGUI(true);
                    return true;
                }
                default -> {
                    return true;
                }
            }
        }

        if (slot == 45) {
            if (path.equals("settings.menu-settings")) {
                MenuStatusManager.menuStatusManager.openTarget(player, target, "", 0);
            } else if (path.isEmpty()) {
                MenuStatusManager.menuStatusManager.openScope(player, target.getScope(), 0);
            } else {
                MenuStatusManager.menuStatusManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            }
            return true;
        }
        if (slot == 49) {
            new EditorSectionGUI(player, target, path, 0, true).openGUI(true);
            return true;
        }
        if (slot == 50 && preset.getKind() == EditorPresetKind.LIMITS_SECTION) {
            MenuStatusManager.menuStatusManager.promptCreateLimitConditionGroup(player, target, path,
                    groupId -> MenuStatusManager.menuStatusManager.openTarget(player, target,
                            MenuStatusManager.menuStatusManager.getLimitConditionsPathFor(path) + "." + groupId, 0),
                    () -> MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0));
            return true;
        }
        if (slot == 53) {
            MenuStatusManager.menuStatusManager.reloadAndReopen(player);
            return true;
        }
        return true;
    }

    private void cycle(String fullPath, List<String> values, boolean reverse) {
        if (values == null || values.isEmpty()) {
            return;
        }
        String current = target.getConfig().getString(fullPath);
        int index = values.indexOf(current);
        if (index < 0) {
            index = 0;
        } else if (reverse) {
            index = (index - 1 + values.size()) % values.size();
        } else {
            index = (index + 1) % values.size();
        }
        MenuStatusManager.menuStatusManager.setValue(player, target, fullPath, values.get(index));
        MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
    }

    private String fullPath(EditorPresetField field) {
        if (field.getKey() == null || field.getKey().isEmpty() || field.getKey().startsWith("__")) {
            return "";
        }
        return path.isEmpty() ? field.getKey() : path + "." + field.getKey();
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
