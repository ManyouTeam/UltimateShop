package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EditorSubButtonValueGUI extends cn.superiormc.ultimateshop.gui.InvGUI {

    private final EditorTarget target;

    private final String itemPath;

    private String sameShopTarget;

    private String crossShopId;

    private String crossProductId;

    public EditorSubButtonValueGUI(Player owner, EditorTarget target, String itemPath) {
        this(owner, target, itemPath, null, null, null);
    }

    public EditorSubButtonValueGUI(Player owner,
                                   EditorTarget target,
                                   String itemPath,
                                   String sameShopTarget,
                                   String crossShopId,
                                   String crossProductId) {
        super(owner);
        this.target = target;
        this.itemPath = itemPath;

        String currentValue = target.getConfig().getString(itemPath + ".as-sub-button");
        if (sameShopTarget == null && crossShopId == null && crossProductId == null && currentValue != null && !currentValue.isEmpty()) {
            String[] split = currentValue.split(";;", 2);
            if (split.length == 2) {
                this.crossShopId = split[0];
                this.crossProductId = split[1];
            } else {
                this.sameShopTarget = currentValue;
            }
        } else {
            this.sameShopTarget = sameShopTarget;
            this.crossShopId = crossShopId;
            this.crossProductId = crossProductId;
        }
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.sub-button.title", "Sub Button: {path}",
                "path", EditorLang.displayPath(player, itemPath + ".as-sub-button"));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title);
        }
        inv.clear();

        inv.setItem(10, EditorUtil.createItem(Material.CHAINMAIL_CHESTPLATE,
                EditorLang.text(player, "editor.sub-button.same-shop.name", "&eCurrent Shop Product"),
                List.of(
                        EditorLang.text(player, "editor.sub-button.current", "&7Current: &f{value}",
                                "value", displayValue(sameShopTarget)),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        inv.setItem(12, EditorUtil.createItem(Material.CHEST,
                EditorLang.text(player, "editor.sub-button.cross-shop.name", "&eTarget Shop"),
                List.of(
                        EditorLang.text(player, "editor.sub-button.current", "&7Current: &f{value}",
                                "value", displayValue(crossShopId)),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        inv.setItem(14, EditorUtil.createItem(Material.NAME_TAG,
                EditorLang.text(player, "editor.sub-button.cross-product.name", "&eTarget Product"),
                List.of(
                        EditorLang.text(player, "editor.sub-button.current", "&7Current: &f{value}",
                                "value", displayValue(crossProductId)),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        inv.setItem(16, EditorUtil.createItem(Material.BOOK,
                EditorLang.text(player, "editor.sub-button.preview.name", "&ePreview"),
                List.of(
                        EditorLang.text(player, "editor.sub-button.same-preview", "&7Same shop: &f{value}",
                                "value", displayValue(sameShopTarget)),
                        EditorLang.text(player, "editor.sub-button.cross-preview", "&7Cross shop: &f{value}",
                                "value", buildCrossValue())
                )));
        inv.setItem(20, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.sub-button.apply-same.name", "&aUse Current Shop Target"),
                List.of(EditorLang.text(player, "editor.sub-button.apply-same.desc",
                        "&7Save as &fProductID"))));
        inv.setItem(22, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.sub-button.apply-cross.name", "&aUse Cross Shop Target"),
                List.of(EditorLang.text(player, "editor.sub-button.apply-cross.desc",
                        "&7Save as &fShopID;;ProductID"))));
        inv.setItem(24, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.sub-button.clear.name", "&cClear"),
                List.of(EditorLang.text(player, "editor.sub-button.clear.desc",
                        "&7Remove the as-sub-button value"))));
        inv.setItem(26, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.common.back.desc", "&7Return to the previous screen"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (slot == 10) {
            openWith(cycle(listSameShopTargets(), sameShopTarget, type.isShiftClick()), crossShopId, crossProductId);
            return true;
        }
        if (slot == 12) {
            String newShop = cycle(listCrossShopTargets(), crossShopId, type.isShiftClick());
            String newProduct = first(listCrossProducts(newShop));
            openWith(sameShopTarget, newShop, newProduct);
            return true;
        }
        if (slot == 14) {
            openWith(sameShopTarget, crossShopId, cycle(listCrossProducts(crossShopId), crossProductId, type.isShiftClick()));
            return true;
        }
        if (slot == 20) {
            if (sameShopTarget != null && !sameShopTarget.isEmpty()) {
                EditorManager.editorManager.setValue(player, target, itemPath + ".as-sub-button", sameShopTarget);
            }
            EditorManager.editorManager.openTarget(player, target, itemPath, 0);
            return true;
        }
        if (slot == 22) {
            String value = buildCrossValue();
            if (!value.equals(EditorLang.text(player, "editor.sub-button.not-set", "<not set>"))) {
                EditorManager.editorManager.setValue(player, target, itemPath + ".as-sub-button", value);
            }
            EditorManager.editorManager.openTarget(player, target, itemPath, 0);
            return true;
        }
        if (slot == 24) {
            EditorManager.editorManager.removeValue(player, target, itemPath + ".as-sub-button");
            EditorManager.editorManager.openTarget(player, target, itemPath, 0);
            return true;
        }
        if (slot == 26) {
            EditorManager.editorManager.openTarget(player, target, itemPath, 0);
            return true;
        }
        return true;
    }

    private void openWith(String sameTarget, String shopId, String productId) {
        new EditorSubButtonValueGUI(player, target, itemPath, sameTarget, shopId, productId).openGUI(true);
    }

    private List<String> listSameShopTargets() {
        List<String> result = new ArrayList<>();
        ConfigurationSection items = target.getConfig().getConfigurationSection("items");
        if (items == null) {
            return result;
        }
        String selfId = itemPath.substring(itemPath.lastIndexOf('.') + 1);
        for (String key : items.getKeys(false)) {
            if (!key.equals(selfId)) {
                result.add(key);
            }
        }
        result.sort(String::compareToIgnoreCase);
        return result;
    }

    private List<String> listCrossShopTargets() {
        List<String> result = new ArrayList<>(EditorScope.SHOP.listIds());
        result.sort(String::compareToIgnoreCase);
        return result;
    }

    private List<String> listCrossProducts(String shopId) {
        List<String> result = new ArrayList<>();
        if (shopId == null || shopId.isEmpty()) {
            return result;
        }
        EditorTarget shopTarget = EditorTarget.load(EditorScope.SHOP, shopId);
        if (shopTarget == null) {
            return result;
        }
        ConfigurationSection items = shopTarget.getConfig().getConfigurationSection("items");
        if (items == null) {
            return result;
        }
        result.addAll(items.getKeys(false));
        result.sort(String::compareToIgnoreCase);
        return result;
    }

    private String cycle(List<String> values, String current, boolean reverse) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        int index = values.indexOf(current);
        if (index < 0) {
            return values.get(0);
        }
        if (reverse) {
            return values.get((index - 1 + values.size()) % values.size());
        }
        return values.get((index + 1) % values.size());
    }

    private String first(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    private String buildCrossValue() {
        if (crossShopId == null || crossShopId.isEmpty() || crossProductId == null || crossProductId.isEmpty()) {
            return EditorLang.text(player, "editor.sub-button.not-set", "<not set>");
        }
        return crossShopId + ";;" + crossProductId;
    }

    private String displayValue(String value) {
        if (value == null || value.isEmpty()) {
            return EditorLang.text(player, "editor.sub-button.not-set", "<not set>");
        }
        return value;
    }
}
