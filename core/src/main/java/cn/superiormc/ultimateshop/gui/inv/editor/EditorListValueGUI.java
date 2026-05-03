package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EditorListValueGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    private final boolean integerMode;

    private final int page;

    public EditorListValueGUI(Player owner, EditorTarget target, String path, boolean integerMode, int page) {
        super(owner);
        this.target = target;
        this.path = path;
        this.integerMode = integerMode;
        this.page = Math.max(page, 0);
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.list.title", "List Editor: {path}",
                "path", EditorLang.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 54, title);
        }
        inv.clear();

        List<Object> values = getValues();
        int start = page * 45;
        for (int i = 0; i < 45; i++) {
            int index = start + i;
            if (index >= values.size()) {
                break;
            }
            Object value = values.get(index);
            inv.setItem(i, EditorUtil.createItem(Material.WRITABLE_BOOK,
                    "&e#" + (index + 1),
                    List.of(
                            EditorLang.text(player, "editor.summary.value", "&7Value: &f{value}",
                                    "value", EditorUtil.trim(String.valueOf(value), 48)),
                            EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"),
                            EditorLang.text(player, "editor.action.delete", "&cRight click to delete"),
                            EditorLang.text(player, "editor.list.move-up", "&eShift-left to move up"),
                            EditorLang.text(player, "editor.list.move-down", "&eShift-right to move down")
                    )));
        }

        inv.setItem(45, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
        inv.setItem(49, EditorUtil.createItem(Material.ANVIL,
                EditorLang.text(player, "editor.list.add.name", "&eAdd Line"),
                List.of(EditorLang.text(player, "editor.list.add.desc", "&7Add a new line to this list"))));
        inv.setItem(50, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.list.clear.name", "&cClear List"),
                List.of(EditorLang.text(player, "editor.list.clear.desc", "&7Remove all entries from this list"))));
        inv.setItem(52, EditorUtil.createItem(Material.SPECTRAL_ARROW,
                EditorLang.text(player, "editor.common.previous-page.name", "&ePrevious Page"),
                List.of(EditorLang.text(player, "editor.common.previous-page.desc", "&7Go to the previous page"))));
        inv.setItem(53, EditorUtil.createItem(Material.TIPPED_ARROW,
                EditorLang.text(player, "editor.common.next-page.name", "&eNext Page"),
                List.of(EditorLang.text(player, "editor.common.next-page.desc", "&7Go to the next page"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        List<Object> values = getValues();

        if (slot >= 0 && slot < 45) {
            int index = page * 45 + slot;
            if (index >= values.size()) {
                return true;
            }

            if (type.isShiftClick() && type.isLeftClick()) {
                move(index, index - 1);
                return true;
            }
            if (type.isShiftClick() && type.isRightClick()) {
                move(index, index + 1);
                return true;
            }
            if (type.isRightClick()) {
                delete(index);
                return true;
            }

            promptEdit(index, values.get(index));
            return true;
        }

        if (slot == 45) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, EditorUtil.parentPath(path), 0);
            return true;
        }
        if (slot == 49) {
            promptAdd();
            return true;
        }
        if (slot == 50) {
            MenuStatusManager.menuStatusManager.setValue(player, target, path, new ArrayList<>());
            new EditorListValueGUI(player, target, path, integerMode, 0).openGUI(true);
            return true;
        }
        if (slot == 52 && page > 0) {
            new EditorListValueGUI(player, target, path, integerMode, page - 1).openGUI(true);
            return true;
        }
        if (slot == 53 && (page + 1) * 45 < values.size()) {
            new EditorListValueGUI(player, target, path, integerMode, page + 1).openGUI(true);
            return true;
        }
        return true;
    }

    private List<Object> getValues() {
        List<?> raw = target.getConfig().getList(path, new ArrayList<>());
        return new ArrayList<>(raw);
    }

    private void delete(int index) {
        List<Object> values = getValues();
        values.remove(index);
        MenuStatusManager.menuStatusManager.setValue(player, target, path, values);
        new EditorListValueGUI(player, target, path, integerMode, normalizePage(values.size())).openGUI(true);
    }

    private void move(int from, int to) {
        List<Object> values = getValues();
        if (to < 0 || to >= values.size()) {
            return;
        }
        Object value = values.remove(from);
        values.add(to, value);
        MenuStatusManager.menuStatusManager.setValue(player, target, path, values);
        new EditorListValueGUI(player, target, path, integerMode, normalizePage(values.size())).openGUI(true);
    }

    private void promptAdd() {
        promptValue(EditorLang.text(player, integerMode ? "editor.prompt.integer-list-entry" : "editor.prompt.string-list-entry",
                integerMode ? "Input the new integer list entry for &f{path}" : "Input the new list entry for &f{path}",
                "path", path), input -> {
            List<Object> values = getValues();
            Object parsed = parseInput(input);
            if (parsed == null) {
                return false;
            }
            values.add(parsed);
            MenuStatusManager.menuStatusManager.setValue(player, target, path, values);
            new EditorListValueGUI(player, target, path, integerMode, normalizePage(values.size())).openGUI(true);
            return true;
        });
    }

    private void promptEdit(int index, Object currentValue) {
        promptValue(EditorLang.text(player, integerMode ? "editor.prompt.integer-list-entry-edit" : "editor.prompt.string-list-entry-edit",
                integerMode ? "Input the new integer for list entry &f#{index}" : "Input the new value for list entry &f#{index}",
                "index", String.valueOf(index + 1)), input -> {
            List<Object> values = getValues();
            Object parsed = parseInput(input);
            if (parsed == null) {
                return false;
            }
            values.set(index, parsed);
            MenuStatusManager.menuStatusManager.setValue(player, target, path, values);
            new EditorListValueGUI(player, target, path, integerMode, page).openGUI(true);
            return true;
        }, currentValue);
    }

    private void promptValue(String description, java.util.function.Function<String, Boolean> handler) {
        promptValue(description, handler, null);
    }

    private void promptValue(String description, java.util.function.Function<String, Boolean> handler, Object currentValue) {
        MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                currentValue == null ? description
                        : description + " " + EditorLang.text(player, "editor.list.current", "&7Current: &f{value}",
                        "value", String.valueOf(currentValue)),
                (p, input) -> {
                    if (!handler.apply(input)) {
                        new EditorListValueGUI(player, target, path, integerMode, page).openGUI(true);
                    }
                },
                p -> new EditorListValueGUI(player, target, path, integerMode, page).openGUI(true)
        ));
    }

    private Object parseInput(String input) {
        if (!integerMode) {
            return input;
        }
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            EditorLang.send(player, "editor.message.invalid-integer", "&cThat is not a valid integer.");
            return null;
        }
    }

    private int normalizePage(int size) {
        int maxPage = size <= 0 ? 0 : (size - 1) / 45;
        return Math.min(page, maxPage);
    }
}
