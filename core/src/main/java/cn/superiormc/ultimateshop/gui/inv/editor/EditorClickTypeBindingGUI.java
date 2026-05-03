package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EditorClickTypeBindingGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    private final String backPath;

    private final List<String> clickTypes = Arrays.stream(ClickType.values())
            .map(Enum::name)
            .toList();

    public EditorClickTypeBindingGUI(Player owner, EditorTarget target, String path, String backPath) {
        super(owner);
        this.target = target;
        this.path = path;
        this.backPath = backPath;
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.click-binding.title", "Click Types: {path}",
                "path", EditorLang.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 54, title);
        }
        inv.clear();

        Set<String> selected = getSelected();
        for (int i = 0; i < Math.min(45, clickTypes.size()); i++) {
            String clickType = clickTypes.get(i);
            boolean enabled = selected.contains(clickType);
            inv.setItem(i, EditorUtil.createItem(enabled ? Material.LIME_DYE : Material.GRAY_DYE,
                    "&e" + clickType,
                    List.of(
                            EditorLang.text(player, "editor.click-binding.state",
                                    "&7Selected: &f{value}",
                                    "value", enabled
                                            ? EditorLang.text(player, "editor.common.enabled", "enabled")
                                            : EditorLang.text(player, "editor.common.disabled", "disabled")),
                            EditorLang.text(player, "editor.action.toggle", "&aLeft click to toggle")
                    )));
        }

        inv.setItem(45, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
        inv.setItem(49, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.common.clear.name", "&cClear"),
                List.of(EditorLang.text(player, "editor.common.clear.desc", "&7Remove this value"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot >= 0 && slot < Math.min(45, clickTypes.size())) {
            toggle(clickTypes.get(slot));
            new EditorClickTypeBindingGUI(player, target, path, backPath).openGUI(true);
            return true;
        }
        if (slot == 45) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
            return true;
        }
        if (slot == 49) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, backPath, 0);
            return true;
        }
        return true;
    }

    private Set<String> getSelected() {
        String raw = target.getConfig().getString(path, "");
        if (raw == null || raw.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(CommonUtil.translateString(raw));
    }

    private void toggle(String clickType) {
        Set<String> selected = getSelected();
        if (selected.contains(clickType)) {
            selected.remove(clickType);
        } else {
            selected.add(clickType);
        }
        if (selected.isEmpty()) {
            MenuStatusManager.menuStatusManager.removeValue(player, target, path);
            return;
        }
        MenuStatusManager.menuStatusManager.setValue(player, target, path, CommonUtil.translateStringList(new ArrayList<>(selected)));
    }
}
