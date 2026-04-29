package cn.superiormc.ultimateshop.gui.inv.editor;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.editor.EditorLang;
import cn.superiormc.ultimateshop.editor.EditorTarget;
import cn.superiormc.ultimateshop.editor.EditorUtil;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.gui.Prompt;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EditorThingEconomyValueGUI extends InvGUI {

    private final EditorTarget target;

    private final String path;

    public EditorThingEconomyValueGUI(Player owner, EditorTarget target, String path) {
        super(owner);
        this.target = target;
        this.path = path;
    }

    @Override
    public void constructGUI() {
        title = EditorLang.text(player, "editor.thing-economy.title", "Thing EconomyFormat: {path}",
                "path", EditorUtil.displayPath(player, path));
        if (inv == null) {
            inv = UltimateShop.methodUtil.createNewInv(player, 27, title);
        }
        inv.clear();

        ConfigurationSection section = MenuStatusManager.menuStatusManager.ensureSection(target, path);
        String provider = getProvider(section);
        String currency = getCurrency(section, provider);
        String placeholder = getPlaceholder(section, provider);

        setInvItem(10, EditorUtil.createItem(Material.GOLD_INGOT,
                EditorLang.text(player, "editor.thing-economy.provider.name", "&eProvider"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", provider),
                        EditorLang.text(player, "editor.thing-economy.provider.desc",
                                "&7Switch this single thing to a vanilla or hooked economy format."),
                        EditorLang.text(player, "editor.action.cycle", "&aLeft click to cycle"),
                        EditorLang.text(player, "editor.action.cycle-back", "&aShift-left to cycle backwards")
                )));
        setInvItem(12, EditorUtil.createItem(Material.NAME_TAG,
                EditorLang.text(player, "editor.thing-economy.currency.name", "&eCurrency Type"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}", "value", currency),
                        EditorLang.text(player, "editor.thing-economy.currency.desc",
                                "&7Currency id used by the selected economy provider."),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit")
                )));
        setInvItem(14, EditorUtil.createItem(Material.PAPER,
                EditorLang.text(player, "editor.thing-economy.placeholder.name", "&ePlaceholder"),
                List.of(
                        EditorLang.text(player, "editor.economy.current", "&7Current: &f{value}",
                                "value", EditorUtil.trim(placeholder, 32)),
                        EditorLang.text(player, "editor.thing-economy.placeholder.desc",
                                "&7Display text used for this single thing price or product."),
                        EditorLang.text(player, "editor.action.edit", "&aLeft click to edit"),
                        EditorLang.text(player, "editor.action.delete", "&cRight click to delete")
                )));
        setInvItem(18, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.thing-economy.apply-conditions.name", "&eApply Conditions"),
                List.of(EditorLang.text(player, "editor.thing-economy.apply-conditions.desc",
                        "&7Open apply-conditions for this single thing"))));
        setInvItem(19, EditorUtil.createItem(Material.COMPARATOR,
                EditorLang.text(player, "editor.thing-economy.require-conditions.name", "&eRequire Conditions"),
                List.of(EditorLang.text(player, "editor.thing-economy.require-conditions.desc",
                        "&7Open require-conditions for this single thing"))));
        setInvItem(20, EditorUtil.createItem(Material.BLAZE_POWDER,
                EditorLang.text(player, "editor.thing-economy.give-actions.name", "&eGive Actions"),
                List.of(EditorLang.text(player, "editor.thing-economy.give-actions.desc",
                        "&7Open give-actions for this single thing"))));
        setInvItem(21, EditorUtil.createItem(Material.BLAZE_POWDER,
                EditorLang.text(player, "editor.thing-economy.take-actions.name", "&eTake Actions"),
                List.of(EditorLang.text(player, "editor.thing-economy.take-actions.desc",
                        "&7Open take-actions for this single thing"))));
        setInvItem(22, EditorUtil.createItem(Material.BOOK,
                EditorLang.text(player, "editor.thing-economy.preset.name", "&ePreset Editor"),
                List.of(EditorLang.text(player, "editor.thing-economy.preset.desc",
                        "&7Return to the single thing preset editor"))));
        setInvItem(23, EditorUtil.createItem(Material.EMERALD,
                EditorLang.text(player, "editor.thing-economy.apply.name", "&aApply EconomyFormat"),
                List.of(EditorLang.text(player, "editor.thing-economy.apply.desc",
                        "&7Convert this single thing to economy format and keep common control fields"))));
        setInvItem(24, EditorUtil.createItem(Material.BARRIER,
                EditorLang.text(player, "editor.thing-economy.clear.name", "&cClear Economy Fields"),
                List.of(EditorLang.text(player, "editor.thing-economy.clear.desc",
                        "&7Remove economy-plugin / economy-type fields only"))));
        setInvItem(26, EditorUtil.createItem(Material.ARROW,
                EditorLang.text(player, "editor.common.back.name", "&eBack"),
                List.of(EditorLang.text(player, "editor.thing-economy.back.desc", "&7Return to the preset editor"))));
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        ConfigurationSection section = MenuStatusManager.menuStatusManager.ensureSection(target, path);
        String provider = getProvider(section);
        String currency = getCurrency(section, provider);
        String placeholder = getPlaceholder(section, provider);

        if (slot == 10) {
            List<String> providers = getProviders();
            int index = providers.indexOf(provider);
            if (index < 0) {
                index = 0;
            } else if (type.isShiftClick()) {
                index = (index - 1 + providers.size()) % providers.size();
            } else {
                index = (index + 1) % providers.size();
            }
            String nextProvider = providers.get(index);
            String nextCurrency = isVanillaProvider(nextProvider) ? nextProvider : "default";
            String nextPlaceholder = buildDefaultPlaceholder(nextProvider);
            MenuStatusManager.menuStatusManager.replaceInlineThingEconomy(player, target, path,
                    nextProvider, nextCurrency, nextPlaceholder);
            new EditorThingEconomyValueGUI(player, target, path).openGUI(true);
            return true;
        }
        if (slot == 12) {
            String currentProvider = provider;
            String currentPlaceholder = placeholder;
            MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                    EditorLang.text(player, "editor.prompt.string",
                            "Input the new string for &f{path}", "path", path + ".economy-type"),
                    (p, input) -> {
                        MenuStatusManager.menuStatusManager.replaceInlineThingEconomy(p, target, path,
                                currentProvider, input, currentPlaceholder);
                        new EditorThingEconomyValueGUI(p, target, path).openGUI(true);
                    },
                    p -> new EditorThingEconomyValueGUI(p, target, path).openGUI(true)
            ));
            return true;
        }
        if (slot == 14) {
            if (type.isRightClick()) {
                MenuStatusManager.menuStatusManager.replaceInlineThingEconomy(player, target, path,
                        provider, currency, "");
                MenuStatusManager.menuStatusManager.removeValue(player, target, path + ".placeholder");
                new EditorThingEconomyValueGUI(player, target, path).openGUI(true);
            } else {
                String currentProvider = provider;
                String currentCurrency = currency;
                MenuStatusManager.menuStatusManager.startPrompt(player, new Prompt(
                        EditorLang.text(player, "editor.prompt.string",
                                "Input the new string for &f{path}", "path", path + ".placeholder"),
                        (p, input) -> {
                            MenuStatusManager.menuStatusManager.replaceInlineThingEconomy(p, target, path,
                                    currentProvider, currentCurrency, input);
                            new EditorThingEconomyValueGUI(p, target, path).openGUI(true);
                        },
                        p -> new EditorThingEconomyValueGUI(p, target, path).openGUI(true)
                ));
            }
            return true;
        }
        if (slot == 18) {
            openNestedSection(path + ".apply-conditions");
            return true;
        }
        if (slot == 19) {
            openNestedSection(path + ".require-conditions");
            return true;
        }
        if (slot == 20) {
            openNestedSection(path + ".give-actions");
            return true;
        }
        if (slot == 21) {
            openNestedSection(path + ".take-actions");
            return true;
        }
        if (slot == 22) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 23) {
            MenuStatusManager.menuStatusManager.replaceInlineThingEconomy(player, target, path, provider, currency, placeholder);
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 24) {
            MenuStatusManager.menuStatusManager.clearInlineThingEconomy(player, target, path);
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
            return true;
        }
        if (slot == 26) {
            MenuStatusManager.menuStatusManager.openTarget(player, target, path, 0);
            return true;
        }
        return true;
    }

    private List<String> getProviders() {
        List<String> providers = new ArrayList<>();
        providers.add("exp");
        providers.add("levels");
        providers.addAll(HookManager.hookManager.getEconomyHookNames());
        return providers;
    }

    private String getProvider(ConfigurationSection section) {
        String plugin = section.getString("economy-plugin");
        if (plugin != null && !plugin.isEmpty()) {
            return plugin;
        }
        String type = section.getString("economy-type");
        if (type != null && !type.isEmpty()) {
            return type;
        }
        List<String> providers = getProviders();
        return providers.isEmpty() ? "levels" : providers.get(0);
    }

    private String getCurrency(ConfigurationSection section, String provider) {
        String current = section.getString("economy-type");
        if (current != null && !current.isEmpty()) {
            return current;
        }
        return isVanillaProvider(provider) ? provider : "default";
    }

    private String getPlaceholder(ConfigurationSection section, String provider) {
        String current = section.getString("placeholder");
        if (current != null && !current.isEmpty()) {
            return current;
        }
        return buildDefaultPlaceholder(provider);
    }

    private String buildDefaultPlaceholder(String provider) {
        return "levels".equalsIgnoreCase(provider) || "exp".equalsIgnoreCase(provider)
                ? "{amount} " + provider.toLowerCase()
                : "{amount}";
    }

    private boolean isVanillaProvider(String provider) {
        return provider.equalsIgnoreCase("exp") || provider.equalsIgnoreCase("levels");
    }

    private void openNestedSection(String fullPath) {
        MenuStatusManager.menuStatusManager.ensureSection(target, fullPath);
        MenuStatusManager.menuStatusManager.save(player, target);
        MenuStatusManager.menuStatusManager.openTarget(player, target, fullPath, 0);
    }
}
