package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorFileListGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorPresetGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorRootGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorSectionGUI;
import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.methods.Items.DebuildItem;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EditorManager {

    public static final EditorManager editorManager = new EditorManager();

    private final Map<UUID, EditorContext> contexts = new ConcurrentHashMap<>();

    private EditorManager() {
    }

    public void clear(Player player) {
        contexts.remove(player.getUniqueId());
    }

    public EditorContext getContext(Player player) {
        return contexts.computeIfAbsent(player.getUniqueId(), uuid -> new EditorContext());
    }

    public void openRoot(Player player) {
        openRoot(player, false, false);
    }

    public void openRoot(Player player, boolean settings) {
        openRoot(player, settings, true);
    }

    private void openRoot(Player player, boolean settings, boolean reopen) {
        EditorContext context = getContext(player);
        context.setScope(null);
        context.setTarget(null);
        context.setPath("");
        context.setPage(0);
        new EditorRootGUI(player, settings).openGUI(reopen);
    }

    public void openScope(Player player, EditorScope scope, int page) {
        EditorContext context = getContext(player);
        context.setScope(scope);
        context.setTarget(null);
        context.setPath("");
        context.setPage(page);
        new EditorFileListGUI(player, scope, page).openGUI(true);
    }

    public void openTarget(Player player, EditorTarget target, String path, int page) {
        if (target == null) {
            EditorLang.send(player, "editor.message.target-not-found", "&cTarget file not found.");
            return;
        }
        EditorContext context = getContext(player);
        context.setScope(target.getScope());
        context.setTarget(target);
        context.setPath(path);
        context.setPage(page);
        EditorPreset preset = EditorPresetRegistry.resolve(target, path);
        if (preset != null) {
            new EditorPresetGUI(player, target, path, preset, page).openGUI(true);
        } else {
            new EditorSectionGUI(player, target, path, page, false).openGUI(true);
        }
    }

    public void reopen(Player player) {
        EditorContext context = getContext(player);
        if (context.getTarget() != null) {
            openTarget(player, context.getTarget(), context.getPath(), context.getPage());
            return;
        }
        if (context.getScope() != null) {
            openScope(player, context.getScope(), context.getPage());
            return;
        }
        openRoot(player);
    }

    public ConfigurationSection ensureSection(EditorTarget target, String path) {
        if (path == null || path.isEmpty()) {
            return target.getConfig();
        }
        ConfigurationSection section = target.getConfig().getConfigurationSection(path);
        if (section == null) {
            target.getConfig().set(path, null);
            section = target.getConfig().createSection(path);
        }
        return section;
    }

    public boolean setValue(Player player, EditorTarget target, String path, Object value) {
        if (!EditorFieldRules.validate(player, target, path, value)) {
            return false;
        }
        target.getConfig().set(path, value);
        return save(player, target);
    }

    public void removeValue(Player player, EditorTarget target, String path) {
        target.getConfig().set(path, null);
        save(player, target);
    }

    public void removeLimitConditionGroup(Player player, EditorTarget target, String limitsPath, String groupId) {
        String valuePath = limitsPath + "." + groupId;
        target.getConfig().set(valuePath, null);
        target.getConfig().set(EditorUtil.limitConditionsPath(limitsPath) + "." + groupId, null);
        save(player, target);
    }

    public void replaceItemSection(Player player, EditorTarget target, String path, ItemStack itemStack) {
        target.getConfig().set(path, null);
        ConfigurationSection section = target.getConfig().createSection(path);
        DebuildItem.debuildItem(itemStack, section);
        save(player, target);
    }

    public boolean save(Player player, EditorTarget target) {
        try {
            target.save();
            EditorLang.send(player, "editor.message.saved", "&aSaved &f{value}",
                "value", target.getScope().getFilePath(target.getId()));
            return true;
        } catch (IOException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Could not save editor file "
                    + target.getScope().getFilePath(target.getId()) + ": " + e.getMessage());
            try {
                target.discardUnsavedChanges();
            } catch (InvalidConfigurationException exception) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Could not restore the editor configuration: "
                        + exception.getMessage());
            }
            EditorLang.send(player, "editor.message.save-failed", "&cFailed to save file: {value}",
                "value", e.getMessage());
            return false;
        }
    }

    public void reloadAndReopen(Player player) {
        EditorContext context = getContext(player);
        EditorTarget previous = context.getTarget();
        String path = context.getPath();
        int page = context.getPage();
        ReloadPlugin.reload(player);
        SchedulerUtil.runTaskLater(() -> {
            if (!player.isOnline() || !ConfigManager.configManager.getBoolean("editor.enabled")) {
                return;
            }
            if (previous == null) {
                reopen(player);
                return;
            }
            EditorTarget reloaded = EditorTarget.load(previous.getScope(), previous.getId());
            if (reloaded == null) {
                openScope(player, previous.getScope(), 0);
                return;
            }
            openTarget(player, reloaded, path, page);
        }, 2L);
    }

    public void createDefaultCollectionEntry(Player player, EditorTarget target, String collectionPath) {
        createDefaultCollectionEntry(player, target, collectionPath, false);
    }

    public void createDefaultCollectionEntry(Player player, EditorTarget target, String collectionPath, boolean economyTemplate) {
        ConfigurationSection section = ensureSection(target, collectionPath);
        String nextKey = String.valueOf(EditorUtil.nextNumericKey(section));
        ConfigurationSection child = section.createSection(nextKey);
        if (EditorTypeResolver.isActionCollection(collectionPath)) {
            List<String> types = ActionManager.actionManager.getActionTypes();
            child.set("type", types.isEmpty() ? "message" : types.get(0));
        } else if (EditorTypeResolver.isConditionCollection(collectionPath)) {
            List<String> types = ConditionManager.conditionManager.getConditionTypes();
            child.set("type", types.isEmpty() ? "permission" : types.get(0));
        } else if (EditorTypeResolver.isThingCollection(collectionPath)) {
            child.set("amount", 1);
            if (economyTemplate) {
                applyDefaultEconomyFields(child);
            } else {
                child.set("material", "STONE");
            }
        }
        save(player, target);
    }

    public void replaceInlineThingEconomy(Player player, EditorTarget target, String path, String provider, String economyType, String placeholder) {
        ConfigurationSection section = ensureSection(target, path);
        Map<String, Object> preserved = preserveThingControls(section);
        clearInlineThingItem(player, target, path);
        ConfigurationSection refreshed = ensureSection(target, path);
        for (Map.Entry<String, Object> entry : preserved.entrySet()) {
            refreshed.set(entry.getKey(), entry.getValue());
        }
        applyEconomyFields(refreshed, provider, economyType, placeholder);
        if (!refreshed.contains("amount")) {
            refreshed.set("amount", 1);
        }
        save(player, target);
    }

    public void clearInlineThingEconomy(Player player, EditorTarget target, String path) {
        ConfigurationSection section = ensureSection(target, path);
        section.set("economy-plugin", null);
        section.set("economy-type", null);
        save(player, target);
    }

    public String createLimitConditionGroup(Player player, EditorTarget target, String limitsPath, String groupId) {
        String normalizedId = groupId == null ? "" : groupId.trim();
        if (normalizedId.isEmpty()) {
            EditorLang.send(player, "editor.message.empty-id", "&cEntry id can not be empty.");
            return null;
        }
        ensureSection(target, limitsPath);
        target.getConfig().set(limitsPath + "." + normalizedId, "0");
        ensureSection(target, EditorUtil.limitConditionsPath(limitsPath) + "." + normalizedId);
        save(player, target);
        return normalizedId;
    }

    public String createLimitConditionGroupFromRoot(Player player, EditorTarget target, String conditionsRootPath, String groupId) {
        String normalizedId = groupId == null ? "" : groupId.trim();
        if (normalizedId.isEmpty()) {
            EditorLang.send(player, "editor.message.empty-id", "&cEntry id can not be empty.");
            return null;
        }
        String limitsPath = EditorUtil.limitsPathFromConditionsRoot(conditionsRootPath);
        if (limitsPath == null) {
            EditorLang.send(player, "editor.message.target-not-found", "&cTarget file not found.");
            return null;
        }
        return createLimitConditionGroup(player, target, limitsPath, normalizedId);
    }

    public void promptCreateLimitConditionGroup(Player player, EditorTarget target, String limitsPath, Runnable reopenAction) {
        promptCreateLimitConditionGroup(player, target, limitsPath, groupId -> reopenAction.run(), reopenAction);
    }

    public void promptCreateLimitConditionGroup(Player player,
        EditorTarget target,
        String limitsPath,
        Consumer<String> successAction,
        Runnable cancelAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.new-condition-id", "Input the new condition id"),
            (p, input) -> {
                String groupId = createLimitConditionGroup(p, target, limitsPath, input);
                if (groupId == null) {
                    cancelAction.run();
                    return;
                }
                successAction.accept(groupId);
            },
            p -> cancelAction.run()
            ));
    }

    public void promptCreateLimitConditionRootEntry(Player player, EditorTarget target, String conditionsRootPath, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.new-condition-id", "Input the new condition id"),
            (p, input) -> {
                createLimitConditionGroupFromRoot(p, target, conditionsRootPath, input);
                reopenAction.run();
            },
            p -> reopenAction.run()
            ));
    }

    public void promptCreateNamedSection(Player player, EditorTarget target, String parentPath, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.new-entry-id", "Input the new entry id"),
            (p, input) -> {
                String fullPath = parentPath == null || parentPath.isEmpty() ? input : parentPath + "." + input;
                ensureSection(target, fullPath);
                if ("items".equals(parentPath)) {
                    target.getConfig().set(fullPath + ".display-name", input);
                    target.getConfig().set(fullPath + ".price-mode", "CLASSIC_ALL");
                    target.getConfig().set(fullPath + ".product-mode", "CLASSIC_ALL");
                    target.getConfig().set(fullPath + ".products.1.material", "STONE");
                    target.getConfig().set(fullPath + ".products.1.amount", 1);
                } else if (EditorTypeResolver.lastSegment(parentPath).equals("buttons")) {
                    target.getConfig().set(fullPath + ".display-item.material", "STONE");
                    target.getConfig().set(fullPath + ".actions.1.type", "close");
                }
                save(p, target);
                reopenAction.run();
            },
            p -> reopenAction.run()
            ));
    }

    public void replaceInlineThingItem(Player player, EditorTarget target, String path, ItemStack itemStack) {
        ConfigurationSection section = ensureSection(target, path);
        Map<String, Object> preserved = preserveThingControls(section);
        clearInlineThingItem(player, target, path);
        ConfigurationSection refreshed = ensureSection(target, path);
        DebuildItem.debuildItem(itemStack, refreshed);
        for (Map.Entry<String, Object> entry : preserved.entrySet()) {
            refreshed.set(entry.getKey(), entry.getValue());
        }
        save(player, target);
    }

    public void clearInlineThingItem(Player player, EditorTarget target, String path) {
        ConfigurationSection section = ensureSection(target, path);
        for (String key : new ArrayList<>(section.getKeys(false))) {
            if (isThingControlKey(key)) {
                continue;
            }
            section.set(key, null);
        }
        save(player, target);
    }

    private Map<String, Object> preserveThingControls(ConfigurationSection section) {
        Map<String, Object> preserved = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            if (isThingControlKey(key)) {
                preserved.put(key, section.get(key));
            }
        }
        return preserved;
    }

    private boolean isThingControlKey(String key) {
        return key.equals("amount")
            || key.equals("apply-conditions")
            || key.equals("require-conditions")
            || key.equals("conditions")
            || key.equals("give-actions")
            || key.equals("take-actions")
            || key.equals("give-item")
            || key.equals("take")
            || key.equals("placeholder")
            || key.equals("min-amount")
            || key.equals("max-amount")
            || key.equals("start-apply")
            || key.equals("end-apply")
            || key.equals("apply");
    }

    private void applyDefaultEconomyFields(ConfigurationSection section) {
        List<String> economyHooks = HookManager.hookManager.getEconomyHookNames();
        if (economyHooks.isEmpty()) {
            section.set("economy-plugin", null);
            section.set("economy-type", "levels");
            section.set("placeholder", "{amount} levels");
        } else {
            section.set("economy-plugin", economyHooks.get(0));
            section.set("economy-type", "default");
            section.set("placeholder", "{amount}");
        }
    }

    private void applyEconomyFields(ConfigurationSection section, String provider, String economyType, String placeholder) {
        String normalizedProvider = provider == null ? "" : provider.trim();
        if (normalizedProvider.isEmpty() || normalizedProvider.equalsIgnoreCase("exp") || normalizedProvider.equalsIgnoreCase("levels")) {
            section.set("economy-plugin", null);
            section.set("economy-type", normalizedProvider.isEmpty() ? "levels" : normalizedProvider.toLowerCase(Locale.ROOT));
        } else {
            section.set("economy-plugin", normalizedProvider);
            section.set("economy-type", economyType == null || economyType.isEmpty() ? "default" : economyType);
        }
        if (placeholder != null && !placeholder.isEmpty()) {
            section.set("placeholder", placeholder);
        } else if (!section.contains("placeholder")) {
            section.set("placeholder", normalizedProvider.equalsIgnoreCase("levels") || normalizedProvider.isEmpty()
                ? "{amount} levels" : "{amount}");
        }
    }

    public void createGenericChild(Player player, EditorTarget target, String parentPath, String key, String type) {
        String childPath = parentPath == null || parentPath.isEmpty() ? key : parentPath + "." + key;
        switch (type.toLowerCase(Locale.ROOT)) {
            case "section" -> {
                ensureSection(target, childPath);
                save(player, target);
            }
            case "string" -> {
                setValue(player, target, childPath, "");
            }
            case "int", "integer" -> {
                setValue(player, target, childPath, 0);
            }
            case "double", "number" -> {
                setValue(player, target, childPath, 0.0D);
            }
            case "boolean", "bool" -> {
                setValue(player, target, childPath, false);
            }
            case "list", "string-list", "int-list", "integer-list" -> {
                setValue(player, target, childPath, List.of());
            }
            default -> {
                EditorLang.send(player, "editor.message.unknown-type",
                        "&cUnknown type. Use section/string/int/double/boolean/list/int-list.");
            }
        }
    }

    public void promptString(Player player, EditorTarget target, String path, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.string", "Input the new string for &f{path}",
            "path", path),
            (p, input) -> {
                if (input.equalsIgnoreCase("null")) {
                    removeValue(p, target, path);
                } else {
                    if (!setValue(p, target, path, input)) {
                        promptString(p, target, path, reopenAction);
                        return;
                    }
                }
                reopenAction.run();
            },
            p -> reopenAction.run()
            ));
    }

    public void promptInteger(Player player, EditorTarget target, String path, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.integer", "Input the new integer for &f{path}",
            "path", path),
            (p, input) -> {
                try {
                    if (setValue(p, target, path, Integer.parseInt(input))) {
                        reopenAction.run();
                    } else {
                        promptInteger(p, target, path, reopenAction);
                    }
                } catch (NumberFormatException e) {
                    EditorLang.send(p, "editor.message.invalid-integer", "&cThat is not a valid integer.");
                    promptInteger(p, target, path, reopenAction);
                }
            },
            p -> reopenAction.run()
            ));
    }

    public void promptDouble(Player player, EditorTarget target, String path, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.double", "Input the new number for &f{path}",
            "path", path),
            (p, input) -> {
                try {
                    double number = Double.parseDouble(input);
                    if (!Double.isFinite(number)) {
                        throw new NumberFormatException();
                    }
                    if (setValue(p, target, path, number)) {
                        reopenAction.run();
                    } else {
                        promptDouble(p, target, path, reopenAction);
                    }
                } catch (NumberFormatException e) {
                    EditorLang.send(p, "editor.message.invalid-number", "&cThat is not a valid number.");
                    promptDouble(p, target, path, reopenAction);
                }
            },
            p -> reopenAction.run()
            ));
    }

    public void promptStringList(Player player, EditorTarget target, String path, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.string-list",
            "Input the new list for &f{path} &7(use &f;; &7as separator)",
            "path", path),
            (p, input) -> {
                if (input.equalsIgnoreCase("null")) {
                    removeValue(p, target, path);
                } else {
                    setValue(p, target, path, CommonUtil.translateString(input));
                }
                reopenAction.run();
            },
            p -> reopenAction.run()
            ));
    }

    public void promptIntegerList(Player player, EditorTarget target, String path, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.integer-list",
            "Input the new integer list for &f{path} &7(use &f;; &7as separator)",
            "path", path),
            (p, input) -> {
                try {
                    List<Integer> values = new ArrayList<>();
                    for (String part : input.split(";;")) {
                        values.add(Integer.parseInt(part.trim()));
                    }
                    setValue(p, target, path, values);
                    reopenAction.run();
                } catch (NumberFormatException e) {
                    EditorLang.send(p, "editor.message.invalid-integer-list", "&cOne of the values is not an integer.");
                    promptIntegerList(p, target, path, reopenAction);
                }
            },
            p -> reopenAction.run()
            ));
    }

    public void promptNewChild(Player player, EditorTarget target, String parentPath, Runnable reopenAction) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.new-child",
            "Input the new child key"),
            (p, input) -> {
                String key = input == null ? "" : input.trim();
                if (key.isEmpty()) {
                    EditorLang.send(p, "editor.message.empty-id", "&cEntry id can not be empty.");
                    promptNewChild(p, target, parentPath, reopenAction);
                    return;
                }
                promptNewChildValue(p, target, parentPath, key, reopenAction);
            },
            p -> reopenAction.run()
            ));
    }

    private void promptNewChildValue(Player player, EditorTarget target, String parentPath, String key, Runnable reopenAction) {
        String childPath = parentPath == null || parentPath.isEmpty() ? key : parentPath + "." + key;
        EditorPresetField field = EditorPresetRegistry.findField(target, childPath);
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
            EditorLang.text(player, "editor.prompt.new-child-value",
            "Input the value for &f{path}&7. Use &fsection &7or &f{} &7to create a section. Use &f;; &7for lists.",
            "path", childPath),
            (p, input) -> {
                Object parsed;
                if (field != null && field.getType() == EditorPresetFieldType.STRING_CHOICE && !field.getChoices().isEmpty()) {
                    if (!field.getChoices().contains(input)) {
                        EditorLang.send(p, "editor.message.invalid-choice", "&cChoose one of: &f{values}",
                                "values", String.join(", ", field.getChoices()));
                        promptNewChildValue(p, target, parentPath, key, reopenAction);
                        return;
                    }
                    parsed = input;
                } else {
                    parsed = EditorUtil.parseNewChildValue(input, field);
                }
                if (parsed == null) {
                    EditorLang.send(p, "editor.message.invalid-new-child-value",
                        "&cInvalid value. Use section/{}, boolean, number, list (;;), or plain text.");
                    promptNewChildValue(p, target, parentPath, key, reopenAction);
                    return;
                }
                if (parsed == EditorUtil.SECTION_SENTINEL) {
                    ensureSection(target, childPath);
                } else {
                    if (setValue(p, target, childPath, parsed)) {
                        reopenAction.run();
                    } else {
                        promptNewChildValue(p, target, parentPath, key, reopenAction);
                    }
                    return;
                }
                save(p, target);
                reopenAction.run();
            },
            p -> reopenAction.run()
            ));
    }
}
