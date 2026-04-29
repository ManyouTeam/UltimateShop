package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.editor.*;
import cn.superiormc.ultimateshop.gui.AbstractGUI;
import cn.superiormc.ultimateshop.gui.GUIStatus;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorFileListGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorPresetGUI;
import cn.superiormc.ultimateshop.editor.EditorContext;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.gui.PromptUtil;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorRootGUI;
import cn.superiormc.ultimateshop.gui.inv.editor.EditorSectionGUI;
import cn.superiormc.ultimateshop.methods.Items.DebuildItem;
import cn.superiormc.ultimateshop.methods.ReloadPlugin;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MenuStatusManager {

    public static MenuStatusManager menuStatusManager;

    private static final Object SECTION_SENTINEL = new Object();

    private final Map<UUID, EditorContext> contexts = new ConcurrentHashMap<>();

    private final Map<UUID, Prompt> prompts = new ConcurrentHashMap<>();

    private final Map<UUID, GUIStatus> openGuis = new ConcurrentHashMap<>();

    private final Map<UUID, InvGUI> activeInvGuis = new ConcurrentHashMap<>();

    public MenuStatusManager() {
        menuStatusManager = this;
    }

    public GUIStatus getGUIStatus(Player player) {
        if (player == null) {
            return null;
        }
        return openGuis.get(player.getUniqueId());
    }

    public AbstractGUI getOpeningGUI(Player player) {
        GUIStatus guiStatus = getGUIStatus(player);
        if (guiStatus == null) {
            return null;
        }
        return guiStatus.getGUI();
    }

    public ObjectMenu getOpeningMenu(Player player) {
        AbstractGUI gui = getOpeningGUI(player);
        if (gui == null) {
            return null;
        }
        return gui.getMenu();
    }

    public InvGUI getActiveInvGUI(Player player) {
        if (player == null) {
            return null;
        }
        return activeInvGuis.get(player.getUniqueId());
    }

    public void setActiveInvGUI(Player player, InvGUI gui) {
        if (player == null || gui == null) {
            return;
        }
        activeInvGuis.put(player.getUniqueId(), gui);
    }

    public void removeActiveInvGUI(Player player, InvGUI gui) {
        if (player == null || gui == null) {
            return;
        }
        activeInvGuis.remove(player.getUniqueId(), gui);
    }

    public void removeActiveInvGUI(Player player) {
        if (player == null) {
            return;
        }
        activeInvGuis.remove(player.getUniqueId());
    }

    public boolean hasOpeningGUI(Player player) {
        return getGUIStatus(player) != null;
    }

    public void setGUIStatus(Player player, GUIStatus guiStatus) {
        if (player == null) {
            return;
        }
        if (guiStatus == null) {
            openGuis.remove(player.getUniqueId());
            return;
        }
        openGuis.put(player.getUniqueId(), guiStatus);
    }

    public void removeGUIStatus(Player player) {
        if (player == null) {
            return;
        }
        openGuis.remove(player.getUniqueId());
        activeInvGuis.remove(player.getUniqueId());
    }

    public boolean canOpenGUI(Player player, AbstractGUI gui, boolean reopen) {
        if (player == null || gui == null) {
            return false;
        }
        GUIStatus guiStatus = getGUIStatus(player);
        if (!reopen && guiStatus != null && ConfigManager.configManager.getLong("menu.cooldown.reopen", -1L) > 0L) {
            return false;
        }
        setGUIStatus(player, GUIStatus.of(gui, reopen ? GUIStatus.Status.ACTION_OPEN_MENU : GUIStatus.Status.CAN_REOPEN));
        return true;
    }

    public void removeOpenGUIStatus(Player player, AbstractGUI gui) {
        if (player == null || gui == null) {
            return;
        }
        long time = ConfigManager.configManager.getLong("menu.cooldown.reopen", 3L);
        GUIStatus guiStatus = getGUIStatus(player);
        if (guiStatus == null || guiStatus.getGUI() != gui) {
            return;
        }
        if (time > 0L && guiStatus.getStatus() != GUIStatus.Status.ALREADY_IN_COOLDOWN) {
            setGUIStatus(player, GUIStatus.of(gui, GUIStatus.Status.ALREADY_IN_COOLDOWN));
            SchedulerUtil.runTaskLater(() -> removeGUIStatus(player), time);
        }
    }

    public EditorContext getContext(Player player) {
        return contexts.computeIfAbsent(player.getUniqueId(), uuid -> new EditorContext());
    }

    public void clear(Player player) {
        contexts.remove(player.getUniqueId());
        prompts.remove(player.getUniqueId());
        openGuis.remove(player.getUniqueId());
        activeInvGuis.remove(player.getUniqueId());
    }

    public boolean hasPrompt(Player player) {
        return prompts.containsKey(player.getUniqueId());
    }

    public void startPrompt(Player player, Prompt prompt) {
        prompts.put(player.getUniqueId(), prompt);
        player.closeInventory();
        EditorLang.send(player, "editor.prompt.description", "&f{value}", "value", prompt.getDescription());
        EditorLang.send(player, "editor.prompt.cancel-tip", "&7Type &f{value} &7to abort this edit.",
                "value", PromptUtil.getCancelKeyword(player));
    }

    public void cancelPrompt(Player player) {
        Prompt prompt = prompts.remove(player.getUniqueId());
        if (prompt != null) {
            prompt.cancel(player);
        }
    }

    public boolean cancelPromptAndShouldReopen(Player player) {
        Prompt prompt = prompts.remove(player.getUniqueId());
        if (prompt == null) {
            return false;
        }
        prompt.cancel(player);
        return prompt.shouldReopenOnCancel();
    }

    public void submitPrompt(Player player, String input) {
        Prompt prompt = prompts.remove(player.getUniqueId());
        if (prompt != null) {
            prompt.handle(player, input);
        }
    }

    public void openRoot(Player player) {
        EditorContext context = getContext(player);
        context.setScope(null);
        context.setTarget(null);
        context.setPath("");
        context.setPage(0);
        new EditorRootGUI(player).openGUI(false);
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
            new EditorPresetGUI(player, target, path, preset).openGUI(true);
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

    public ConfigurationSection getSection(EditorTarget target, String path) {
        if (path == null || path.isEmpty()) {
            return target.getConfig();
        }
        return target.getConfig().getConfigurationSection(path);
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

    public void setValue(Player player, EditorTarget target, String path, Object value) {
        target.getConfig().set(path, value);
        save(player, target);
    }

    public void removeValue(Player player, EditorTarget target, String path) {
        target.getConfig().set(path, null);
        save(player, target);
    }

    public void removeLimitConditionGroup(Player player, EditorTarget target, String limitsPath, String groupId) {
        String valuePath = limitsPath + "." + groupId;
        target.getConfig().set(valuePath, null);
        target.getConfig().set(getLimitConditionsPath(limitsPath) + "." + groupId, null);
        save(player, target);
    }

    public void replaceItemSection(Player player, EditorTarget target, String path, ItemStack itemStack) {
        target.getConfig().set(path, null);
        ConfigurationSection section = target.getConfig().createSection(path);
        DebuildItem.debuildItem(itemStack, section);
        save(player, target);
    }

    public void save(Player player, EditorTarget target) {
        try {
            target.getConfig().save(target.getFile());
            EditorLang.send(player, "editor.message.saved", "&aSaved &f{value}",
                    "value", target.getScope().getFolderName() + "/" + target.getId() + ".yml");
        } catch (IOException e) {
            EditorLang.send(player, "editor.message.save-failed", "&cFailed to save file: {value}",
                    "value", e.getMessage());
        }
    }

    public void reloadAndReopen(Player player) {
        ReloadPlugin.reload(player);
        SchedulerUtil.runTaskLater(() -> reopen(player), 2L);
    }

    public int nextNumericKey(ConfigurationSection section) {
        int max = 0;
        for (String key : section.getKeys(false)) {
            if (key.matches("\\d+")) {
                max = Math.max(max, Integer.parseInt(key));
            }
        }
        return max + 1;
    }

    public void createDefaultCollectionEntry(Player player, EditorTarget target, String collectionPath) {
        createDefaultCollectionEntry(player, target, collectionPath, false);
    }

    public void createDefaultCollectionEntry(Player player, EditorTarget target, String collectionPath, boolean economyTemplate) {
        ConfigurationSection section = ensureSection(target, collectionPath);
        String nextKey = String.valueOf(nextNumericKey(section));
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
        ensureSection(target, getLimitConditionsPath(limitsPath) + "." + normalizedId);
        save(player, target);
        return normalizedId;
    }

    public String createLimitConditionGroupFromRoot(Player player, EditorTarget target, String conditionsRootPath, String groupId) {
        String normalizedId = groupId == null ? "" : groupId.trim();
        if (normalizedId.isEmpty()) {
            EditorLang.send(player, "editor.message.empty-id", "&cEntry id can not be empty.");
            return null;
        }
        String limitsPath = getLimitsPathFromConditionsRoot(conditionsRootPath);
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
        startPrompt(player, new Prompt(
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
        startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.prompt.new-condition-id", "Input the new condition id"),
                (p, input) -> {
                    createLimitConditionGroupFromRoot(p, target, conditionsRootPath, input);
                    reopenAction.run();
                },
                p -> reopenAction.run()
        ));
    }

    public void promptCreateNamedSection(Player player, EditorTarget target, String parentPath, Runnable reopenAction) {
        startPrompt(player, new Prompt(
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
        Map<String, Object> preserved = new java.util.LinkedHashMap<>();
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
        List<String> economyHooks = cn.superiormc.ultimateshop.managers.HookManager.hookManager.getEconomyHookNames();
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
            section.set("economy-type", normalizedProvider.isEmpty() ? "levels" : normalizedProvider.toLowerCase());
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
        switch (type.toLowerCase()) {
            case "section":
                ensureSection(target, childPath);
                break;
            case "string":
                setValue(player, target, childPath, "");
                return;
            case "int":
            case "integer":
                setValue(player, target, childPath, 0);
                return;
            case "double":
            case "number":
                setValue(player, target, childPath, 0.0D);
                return;
            case "boolean":
            case "bool":
                setValue(player, target, childPath, false);
                return;
            case "list":
            case "string-list":
                setValue(player, target, childPath, new ArrayList<String>());
                return;
            case "int-list":
            case "integer-list":
                setValue(player, target, childPath, new ArrayList<Integer>());
                return;
            default:
                EditorLang.send(player, "editor.message.unknown-type",
                        "&cUnknown type. Use section/string/int/double/boolean/list/int-list.");
                return;
        }
        save(player, target);
    }

    public void promptString(Player player, EditorTarget target, String path, Runnable reopenAction) {
        startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.prompt.string", "Input the new string for &f{path}",
                        "path", path),
                (p, input) -> {
                    if (input.equalsIgnoreCase("null")) {
                        removeValue(p, target, path);
                    } else {
                        setValue(p, target, path, input);
                    }
                    reopenAction.run();
                },
                p -> reopenAction.run()
        ));
    }

    public void promptInteger(Player player, EditorTarget target, String path, Runnable reopenAction) {
        startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.prompt.integer", "Input the new integer for &f{path}",
                        "path", path),
                (p, input) -> {
                    try {
                        setValue(p, target, path, Integer.parseInt(input));
                        reopenAction.run();
                    } catch (NumberFormatException e) {
                        EditorLang.send(p, "editor.message.invalid-integer", "&cThat is not a valid integer.");
                        promptInteger(p, target, path, reopenAction);
                    }
                },
                p -> reopenAction.run()
        ));
    }

    public void promptDouble(Player player, EditorTarget target, String path, Runnable reopenAction) {
        startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.prompt.double", "Input the new number for &f{path}",
                        "path", path),
                (p, input) -> {
                    try {
                        setValue(p, target, path, Double.parseDouble(input));
                        reopenAction.run();
                    } catch (NumberFormatException e) {
                        EditorLang.send(p, "editor.message.invalid-number", "&cThat is not a valid number.");
                        promptDouble(p, target, path, reopenAction);
                    }
                },
                p -> reopenAction.run()
        ));
    }

    public void promptStringList(Player player, EditorTarget target, String path, Runnable reopenAction) {
        startPrompt(player, new Prompt(
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
        startPrompt(player, new Prompt(
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
        startPrompt(player, new Prompt(
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
        startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.prompt.new-child-value",
                        "Input the value for &f{path}&7. Use &fsection &7or &f{} &7to create a section. Use &f;; &7for lists.",
                        "path", childPath),
                (p, input) -> {
                    Object parsed = parseNewChildValue(input);
                    if (parsed == null) {
                        EditorLang.send(p, "editor.message.invalid-new-child-value",
                                "&cInvalid value. Use section/{}, boolean, number, list (;;), or plain text.");
                        promptNewChildValue(p, target, parentPath, key, reopenAction);
                        return;
                    }
                    if (parsed == SECTION_SENTINEL) {
                        ensureSection(target, childPath);
                    } else {
                        setValue(p, target, childPath, parsed);
                        reopenAction.run();
                        return;
                    }
                    save(p, target);
                    reopenAction.run();
                },
                p -> reopenAction.run()
        ));
    }

    private Object parseNewChildValue(String input) {
        if (input == null) {
            return "";
        }
        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("section") || trimmed.equals("{}")) {
            return SECTION_SENTINEL;
        }
        if (trimmed.equalsIgnoreCase("true") || trimmed.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(trimmed);
        }
        if (trimmed.contains(";;")) {
            String[] split = trimmed.split(";;");
            List<Integer> integerValues = new ArrayList<>();
            List<String> stringValues = new ArrayList<>();
            boolean integerList = true;
            for (String part : split) {
                String element = part.trim();
                if (element.isEmpty()) {
                    stringValues.add("");
                    integerList = false;
                    continue;
                }
                stringValues.add(element);
                if (integerList) {
                    try {
                        integerValues.add(Integer.parseInt(element));
                    } catch (NumberFormatException exception) {
                        integerList = false;
                    }
                }
            }
            return integerList ? integerValues : stringValues;
        }
        if (trimmed.matches("-?\\d+")) {
            try {
                return Integer.parseInt(trimmed);
            } catch (NumberFormatException ignored) {
            }
        }
        if (trimmed.matches("-?\\d+\\.\\d+")) {
            try {
                return Double.parseDouble(trimmed);
            } catch (NumberFormatException ignored) {
            }
        }
        return input;
    }

    private String getLimitConditionsPath(String limitsPath) {
        if (limitsPath == null || limitsPath.isEmpty()) {
            return "limits-conditions";
        }
        if (limitsPath.endsWith("buy-limits")) {
            return limitsPath.substring(0, limitsPath.length() - "buy-limits".length()) + "buy-limits-conditions";
        }
        if (limitsPath.endsWith("sell-limits")) {
            return limitsPath.substring(0, limitsPath.length() - "sell-limits".length()) + "sell-limits-conditions";
        }
        if (limitsPath.endsWith("limits")) {
            return limitsPath.substring(0, limitsPath.length() - "limits".length()) + "limits-conditions";
        }
        return limitsPath + "-conditions";
    }

    public String getLimitConditionsPathFor(String limitsPath) {
        return getLimitConditionsPath(limitsPath);
    }

    private String getLimitsPathFromConditionsRoot(String conditionsRootPath) {
        if (conditionsRootPath == null || conditionsRootPath.isEmpty()) {
            return null;
        }
        if (conditionsRootPath.endsWith("buy-limits-conditions")) {
            return conditionsRootPath.substring(0, conditionsRootPath.length() - "buy-limits-conditions".length()) + "buy-limits";
        }
        if (conditionsRootPath.endsWith("sell-limits-conditions")) {
            return conditionsRootPath.substring(0, conditionsRootPath.length() - "sell-limits-conditions".length()) + "sell-limits";
        }
        if (conditionsRootPath.endsWith("limits-conditions")) {
            return conditionsRootPath.substring(0, conditionsRootPath.length() - "limits-conditions".length()) + "limits";
        }
        return null;
    }
}
