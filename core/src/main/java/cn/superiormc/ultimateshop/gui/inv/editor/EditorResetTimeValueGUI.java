package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EditorResetTimeValueGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    private final String backPath;

    public EditorResetTimeValueGUI(Player owner, EditorTarget target, String path, String backPath) {
        super(owner);
        this.target = target;
        this.path = path;
        this.backPath = backPath;
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.reset-time.title", "Reset Time: {path}",
                "path", EditorLang.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 54, title);
        }
        inv.clear();

        String mode = getMode();
        inv.setItem(46, EditorUtil.createItem(Material.BOOK,
                EditorLang.text(player, "editor.reset-time.mode.name", "&eCurrent Mode"),
                modeLore(mode)));

        if (isListMode(mode)) {
            constructListMode();
        } else if (isRandomPlaceholderMode(mode)) {
            constructRandomPlaceholderMode();
        } else if (isManualStringMode(mode)) {
            constructManualStringMode();
        } else {
            constructNeverMode();
        }

        inv.setItem(45, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        String mode = getMode();
        if (slot == 45) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
            return true;
        }
        if (isListMode(mode)) {
            return handleListMode(slot, type);
        }
        if (isRandomPlaceholderMode(mode)) {
            return handleRandomPlaceholderMode(slot, type);
        }
        if (isManualStringMode(mode)) {
            return handleManualStringMode(slot);
        }
        if (slot == 49) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
        }
        return true;
    }

    private void constructListMode() {
        List<String> values = getListValues();
        for (int i = 0; i < Math.min(45, values.size()); i++) {
            String value = values.get(i);
            inv.setItem(i, EditorUtil.createItem(Material.CLOCK,
                    "&e#" + (i + 1),
                    List.of(
                            EditorLang.text(player, "editor.summary.value", "&7Value: &f{value}", "value", value),
                            EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"),
                            EditorLang.text(player, "editor.action.delete", "&cRight click to delete")
                    )));
        }
        inv.setItem(49, EditorUtil.createItem(Material.ANVIL,
                EditorLang.text(player, "editor.reset-time.add.name", "&eAdd Time"),
                List.of(EditorLang.text(player, "editor.reset-time.add.desc", "&7Add a new reset time entry"))));
        inv.setItem(50, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.common.clear.name", "&cClear"),
                List.of(EditorLang.text(player, "editor.common.clear.desc", "&7Remove this value"))));
    }

    private boolean handleListMode(int slot, ClickType type) {
        List<String> values = getListValues();
        if (slot >= 0 && slot < Math.min(45, values.size())) {
            if (type.isRightClick()) {
                values.remove(slot);
                saveListValues(values);
                new EditorResetTimeValueGUI(player, target, path, backPath).openGUI(true);
                return true;
            }
            promptEdit(slot, values.get(slot));
            return true;
        }
        if (slot == 49) {
            promptAdd();
            return true;
        }
        if (slot == 50) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
            return true;
        }
        return true;
    }

    private void constructRandomPlaceholderMode() {
        String current = target.getConfig().getString(path);
        inv.setItem(13, EditorUtil.createItem(Material.CLOCK,
                EditorLang.text(player, "editor.reset-time.random.name", "&eRandom Placeholder"),
                List.of(
                        EditorLang.text(player, "editor.reset-time.current", "&7Current: &f{value}",
                                "value", current == null || current.isEmpty()
                                        ? EditorLang.text(player, "editor.sub-button.not-set", "<not set>")
                                        : current),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        inv.setItem(15, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.common.clear.name", "&cClear"),
                List.of(EditorLang.text(player, "editor.common.clear.desc", "&7Remove this value"))));
    }

    private boolean handleRandomPlaceholderMode(int slot, ClickType type) {
        if (slot == 13) {
            cycleRandomPlaceholder(type.isShiftClick());
            return true;
        }
        if (slot == 15) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
            return true;
        }
        return true;
    }

    private void constructManualStringMode() {
        String current = target.getConfig().getString(path);
        inv.setItem(13, EditorUtil.createItem(Material.NAME_TAG,
                EditorLang.text(player, "editor.reset-time.manual.name", "&eEdit Value"),
                List.of(
                        EditorLang.text(player, "editor.reset-time.current", "&7Current: &f{value}",
                                "value", current == null || current.isEmpty()
                                        ? EditorLang.text(player, "editor.sub-button.not-set", "<not set>")
                                        : EditorUtil.trim(current, 42)),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit")
                )));
        inv.setItem(15, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.common.clear.name", "&cClear"),
                List.of(EditorLang.text(player, "editor.common.clear.desc", "&7Remove this value"))));
    }

    private boolean handleManualStringMode(int slot) {
        if (slot == 13) {
            MenuStatusManager.menuStatusManager.promptString(player, target, path,
                    () -> MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0));
            return true;
        }
        if (slot == 15) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
            return true;
        }
        return true;
    }

    private void constructNeverMode() {
        String current = target.getConfig().getString(path);
        inv.setItem(13, EditorUtil.createItem(Material.PAPER,
                EditorLang.text(player, "editor.reset-time.never.name", "&eNo Reset Time Needed"),
                List.of(
                        EditorLang.text(player, "editor.reset-time.never.desc", "&7This reset mode does not use a time value."),
                        EditorLang.text(player, "editor.reset-time.current", "&7Current: &f{value}",
                                "value", current == null || current.isEmpty()
                                        ? EditorLang.text(player, "editor.sub-button.not-set", "<not set>")
                                        : current)
                )));
        inv.setItem(49, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.common.clear.name", "&cClear"),
                List.of(EditorLang.text(player, "editor.common.clear.desc", "&7Remove this value"))));
    }

    private List<String> getListValues() {
        String raw = target.getConfig().getString(path, "");
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(CommonUtil.translateString(raw));
    }

    private void saveListValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            return;
        }
        MenuStatusManager.menuStatusManager.setValue(player, target, path, CommonUtil.translateStringList(values));
    }

    private void promptAdd() {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.reset-time.prompt-add", "Input the new reset time for &f{path}",
                        "path", path),
                (p, input) -> {
                    List<String> values = getListValues();
                    values.add(input.trim());
                    saveListValues(values);
                    new EditorResetTimeValueGUI(player, target, path, backPath).openGUI(true);
                },
                p -> new EditorResetTimeValueGUI(player, target, path, backPath).openGUI(true)
        ));
    }

    private void promptEdit(int index, String currentValue) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                EditorLang.text(player, "editor.reset-time.prompt-edit", "Input the new reset time for entry &f#{index}",
                        "index", String.valueOf(index + 1))
                        + " "
                        + EditorLang.text(player, "editor.reset-time.current", "&7Current: &f{value}",
                        "value", currentValue),
                (p, input) -> {
                    List<String> values = getListValues();
                    values.set(index, input.trim());
                    saveListValues(values);
                    new EditorResetTimeValueGUI(player, target, path, backPath).openGUI(true);
                },
                p -> new EditorResetTimeValueGUI(player, target, path, backPath).openGUI(true)
        ));
    }

    private void cycleRandomPlaceholder(boolean reverse) {
        List<String> ids = ConfigManager.configManager.getRandomPlaceholders().stream()
                .map(ObjectRandomPlaceholder::getID)
                .sorted(Comparator.naturalOrder())
                .toList();
        if (ids.isEmpty()) {
            return;
        }
        String current = target.getConfig().getString(path);
        int index = ids.indexOf(current);
        if (index < 0) {
            index = 0;
        } else if (reverse) {
            index = (index - 1 + ids.size()) % ids.size();
        } else {
            index = (index + 1) % ids.size();
        }
        MenuStatusManager.menuStatusManager.setValue(player, target, path, ids.get(index));
        new EditorResetTimeValueGUI(player, target, path, backPath).openGUI(true);
    }

    private String getMode() {
        return target.getConfig().getString(getModePath(), "NEVER").toUpperCase();
    }

    private String getModePath() {
        if (!path.endsWith("-time")) {
            return path;
        }
        return path.substring(0, path.length() - 5) + "-mode";
    }

    private boolean isListMode(String mode) {
        return mode.equals("TIMER")
                || mode.equals("TIMED")
                || mode.equals("COOLDOWN_TIMER")
                || mode.equals("COOLDOWN_TIMED");
    }

    private boolean isRandomPlaceholderMode(String mode) {
        return mode.equals("RANDOM_PLACEHOLDER");
    }

    private boolean isManualStringMode(String mode) {
        return mode.equals("CUSTOM") || mode.equals("COOLDOWN_CUSTOM");
    }

    private List<String> modeLore(String mode) {
        List<String> lore = new ArrayList<>();
        lore.add(EditorLang.text(player, "editor.reset-time.mode.value", "&7Mode: &f{value}", "value", mode));
        lore.add(EditorLang.text(player, "editor.reset-time.mode.desc", "&7This editor changes behavior based on the selected reset mode."));
        switch (mode) {
            case "TIMER", "COOLDOWN_TIMER" -> {
                lore.add("&7Format: &fss:mm:hh");
                lore.add("&7Also supports day/month at the front.");
                lore.add("&7Example: &f15:00:00");
            }
            case "TIMED", "COOLDOWN_TIMED" -> {
                lore.add("&7Format: &fss:mm:hh");
                lore.add("&7Also supports day/month at the front.");
                lore.add("&7Supports multiple entries with &f;;");
                lore.add("&7Example: &f20:00:00;;19:00:00");
            }
            case "CUSTOM", "COOLDOWN_CUSTOM" -> {
                lore.add("&7Input a full PlaceholderAPI placeholder.");
                lore.add("&7Set the matching time format in");
                lore.add("&7the reset-time-format option.");
            }
            case "RANDOM_PLACEHOLDER" -> lore.add("&7Select a valid random placeholder id.");
            case "NEVER" -> lore.add("&7No reset time value is required.");
            default -> {
            }
        }
        return lore;
    }
}
