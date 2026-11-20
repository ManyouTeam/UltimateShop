package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorManager;
import cn.superiormc.ultimateshop.editor.EditorPreset;
import cn.superiormc.ultimateshop.editor.EditorPresetField;
import cn.superiormc.ultimateshop.editor.EditorPresetFieldType;
import cn.superiormc.ultimateshop.editor.EditorPresetKind;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.editor.EditorTypeResolver;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
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

    private static final int FIELDS_PER_PAGE = 45;

    private final EditorTarget target;

    private final String path;

    private final EditorPreset preset;

    private final int page;

    public EditorPresetGUI(Player owner, EditorTarget target, String path, EditorPreset preset, int page) {
        super(owner);
        this.target = target;
        this.path = path == null ? "" : path;
        this.preset = preset;
        this.page = Math.max(0, Math.min(page, (preset.getFields().size() - 1) / FIELDS_PER_PAGE));
    }

    @Override
    public void constructGUI() {
        title = target.getId() + ": " + EditorLang.presetTitle(player, preset);
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 54, title, this);
        }
        inv.clear();

        List<EditorPresetField> fields = preset.getFields();
        int first = page * FIELDS_PER_PAGE;
        for (int i = first; i < Math.min(fields.size(), first + FIELDS_PER_PAGE); i++) {
            inv.setItem(i - first, createItem(fields.get(i)));
        }

        inv.setItem(45, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
        if (preset.getKind() == EditorPresetKind.LIMITS_SECTION) {
            inv.setItem(50, EditorUtil.createItem(Material.ANVIL,
                    EditorLang.text(player, "editor.preset.add-limit-group.name", "&eAdd Condition Group"),
                    List.of(EditorLang.text(player, "editor.preset.add-limit-group.desc",
                            "&7Create a new named limit group and matching conditions section"))));
        }
        inv.setItem(49, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.preset.raw.name", "&eRaw Section"),
                List.of(EditorLang.text(player, "editor.preset.raw.desc", "&7Open the raw section fallback editor"))));
        if (page > 0) {
            inv.setItem(51, EditorUtil.createItem(Material.SPECTRAL_ARROW,
                    EditorLang.text(player, "editor.common.previous-page.name", "&ePrevious Page"),
                    List.of(EditorLang.text(player, "editor.common.previous-page.desc", "&7Go to the previous page"))));
        }
        if ((page + 1) * FIELDS_PER_PAGE < fields.size()) {
            inv.setItem(52, EditorUtil.createItem(Material.TIPPED_ARROW,
                    EditorLang.text(player, "editor.common.next-page.name", "&eNext Page"),
                    List.of(EditorLang.text(player, "editor.common.next-page.desc", "&7Go to the next page"))));
        }
        inv.setItem(53, EditorUtil.createItem(Material.EMERALD,
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
        if (!fieldDescription.isBlank()) {
            lore.add(EditorLang.text(player, "editor.lore.description", "&7Description: &f{value}",
                    "value", fieldDescription));
        }
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
                if (field.getChoices().isEmpty()) {
                    lore.add(EditorLang.text(player, "editor.preset.no-choices", "&7No values are currently available."));
                } else {
                    lore.add(EditorLang.text(player, "editor.preset.choices", "&7Options: &f{values}",
                            "values", EditorUtil.trim(String.join(", ", field.getChoices()), 54)));
                }
                lore.add(EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"));
                lore.add(EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
            case BOOLEAN:
                lore.add(EditorLang.text(player, "editor.action.toggle", "&aLeft click to toggle"));
                lore.add(EditorLang.text(player, "editor.action.delete", "&cRight click to delete"));
                break;
            case INTEGER, DOUBLE:
                lore.add(EditorLang.text(player, "editor.action.exact-number", "&aPress Q to enter an exact value"));
                if (isInventorySize(field)) {
                    lore.add(EditorLang.text(player, "editor.action.increase-row", "&aLeft click to add a row"));
                    lore.add(EditorLang.text(player, "editor.action.decrease-row", "&cRight click to remove a row"));
                } else {
                    lore.add(EditorLang.text(player, "editor.action.increase-ten", "&aLeft click to add 10"));
                    lore.add(EditorLang.text(player, "editor.action.decrease-ten", "&cRight click to subtract 10"));
                    lore.add(EditorLang.text(player, "editor.action.increase-one", "&aShift-left to add 1"));
                    lore.add(EditorLang.text(player, "editor.action.decrease-one", "&cShift-right to subtract 1"));
                }
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
            ConfigurationSection section = EditorUtil.getSection(target, path);
            if (section == null) {
                return EditorLang.text(player, "editor.summary.thing.unset", "Nothing selected");
            }
            if (section.contains("hook-plugin") && section.contains("hook-item")) {
                return EditorLang.text(player, "editor.summary.thing.hook-item", "Item from another plugin");
            }
            if (section.contains("match-item")) {
                return EditorLang.text(player, "editor.summary.thing.match-item", "Item matching rules");
            }
            if (section.contains("match-placeholder")) {
                return EditorLang.text(player, "editor.summary.thing.match-placeholder", "Match a placeholder");
            }
            if (section.contains("economy-plugin")) {
                return EditorLang.text(player, "editor.summary.thing.hook-economy", "Currency from another plugin");
            }
            if (section.contains("economy-type") && !section.contains("economy-plugin")) {
                return EditorLang.text(player, "editor.summary.thing.vanilla-economy", "Minecraft experience");
            }
            if (section.contains("material") || section.contains("item")) {
                return EditorLang.text(player, "editor.summary.thing.vanilla-item", "Minecraft item");
            }
        }
        return EditorLang.text(player, "editor.summary.thing.unset", "Nothing selected");
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        List<EditorPresetField> fields = preset.getFields();
        int index = page * FIELDS_PER_PAGE + slot;
        if (slot >= 0 && slot < FIELDS_PER_PAGE && index < fields.size()) {
            EditorPresetField field = fields.get(index);
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
                    EditorManager.editorManager.removeLimitConditionGroup(player, target, path, field.getKey());
                } else {
                    EditorManager.editorManager.removeValue(player, target, fullPath);
                }
                EditorManager.editorManager.openTarget(player, target, path, page);
                return true;
            }

            switch (field.getType()) {
                case SECTION -> {
                    ConfigurationSection section = EditorManager.editorManager.ensureSection(target, fullPath);
                    EditorManager.editorManager.save(player, target);
                    if (EditorTypeResolver.isItemSection(fullPath, section)) {
                        new EditorItemValueGUI(player, target, fullPath).openGUI(true);
                    } else if (EditorTypeResolver.isEconomySection(fullPath, section)) {
                        new EditorEconomyValueGUI(player, target, fullPath).openGUI(true);
                    } else {
                        EditorManager.editorManager.openTarget(player, target, fullPath, 0);
                    }
                    return true;
                }
                case ACTIONS, CONDITIONS -> {
                    EditorManager.editorManager.ensureSection(target, fullPath);
                    EditorManager.editorManager.save(player, target);
                    EditorManager.editorManager.openTarget(player, target, fullPath, 0);
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
                    boolean boundaryHide = preset.getKind() == EditorPresetKind.SHOP_PAGE_BUTTON;
                    boolean current = target.getConfig().getBoolean(fullPath, boundaryHide);
                    EditorManager.editorManager.setValue(player, target, fullPath, !current);
                    EditorManager.editorManager.openTarget(player, target, path, page);
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
                        EditorManager.editorManager.promptString(player, target, fullPath,
                                () -> EditorManager.editorManager.openTarget(player, target, path, page));
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
                    new EditorResetTimeValueGUI(player, target, fullPath, path, page).openGUI(true);
                    return true;
                }
                case INTEGER -> {
                    if (type == ClickType.DROP || type == ClickType.CONTROL_DROP) {
                        EditorManager.editorManager.promptInteger(player, target, fullPath,
                                () -> EditorManager.editorManager.openTarget(player, target, path, page));
                        return true;
                    }
                    if (type == ClickType.MIDDLE) {
                        EditorManager.editorManager.removeValue(player, target, fullPath);
                    } else {
                        int delta = resolveIntegerDelta(type);
                        if (delta != 0) {
                            int current = target.getConfig().getInt(fullPath, 0);
                            int step = isInventorySize(field) ? Integer.signum(delta) * 9 : delta;
                            EditorManager.editorManager.setValue(player, target, fullPath, current + step);
                        }
                    }
                    EditorManager.editorManager.openTarget(player, target, path, page);
                    return true;
                }
                case DOUBLE -> {
                    if (type == ClickType.DROP || type == ClickType.CONTROL_DROP) {
                        EditorManager.editorManager.promptDouble(player, target, fullPath,
                                () -> EditorManager.editorManager.openTarget(player, target, path, page));
                        return true;
                    }
                    if (type == ClickType.MIDDLE) {
                        EditorManager.editorManager.removeValue(player, target, fullPath);
                    } else {
                        double delta = resolveDoubleDelta(type);
                        if (delta != 0) {
                            double current = target.getConfig().getDouble(fullPath, 0D);
                            EditorManager.editorManager.setValue(player, target, fullPath, current + delta);
                        }
                    }
                    EditorManager.editorManager.openTarget(player, target, path, page);
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
                EditorManager.editorManager.openTarget(player, target, "", 0);
            } else if (path.isEmpty()) {
                EditorManager.editorManager.openScope(player, target.getScope(), 0);
            } else {
                EditorManager.editorManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            }
            return true;
        }
        if (slot == 49) {
            new EditorSectionGUI(player, target, path, 0, true).openGUI(true);
            return true;
        }
        if (slot == 50 && preset.getKind() == EditorPresetKind.LIMITS_SECTION) {
            EditorManager.editorManager.promptCreateLimitConditionGroup(player, target, path,
                    groupId -> EditorManager.editorManager.openTarget(player, target,
                            EditorUtil.limitConditionsPath(path) + "." + groupId, 0),
                    () -> EditorManager.editorManager.openTarget(player, target, path, page));
            return true;
        }
        if (slot == 51 && page > 0) {
            EditorManager.editorManager.openTarget(player, target, path, page - 1);
            return true;
        }
        if (slot == 52 && (page + 1) * FIELDS_PER_PAGE < fields.size()) {
            EditorManager.editorManager.openTarget(player, target, path, page + 1);
            return true;
        }
        if (slot == 53) {
            EditorManager.editorManager.reloadAndReopen(player);
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
        EditorManager.editorManager.setValue(player, target, fullPath, values.get(index));
        EditorManager.editorManager.openTarget(player, target, path, page);
    }

    private String fullPath(EditorPresetField field) {
        if (field.getKey() == null || field.getKey().isEmpty() || field.getKey().startsWith("__")) {
            return "";
        }
        return path.isEmpty() ? field.getKey() : path + "." + field.getKey();
    }

    private boolean isInventorySize(EditorPresetField field) {
        return field.getKey().equals("size") && preset.getKind() == EditorPresetKind.ROOT_MENU;
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
