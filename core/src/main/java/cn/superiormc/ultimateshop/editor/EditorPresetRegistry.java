package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.MenuStatusManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class EditorPresetRegistry {

    private static final String[] RESET_MODES = {
            "NEVER",
            "TIMER",
            "TIMED",
            "RANDOM_PLACEHOLDER",
            "CUSTOM",
            "COOLDOWN_TIMER",
            "COOLDOWN_TIMED",
            "COOLDOWN_CUSTOM"
    };

    private static final String[] CLICK_TYPES = Arrays.stream(ClickType.values())
            .map(Enum::name)
            .toArray(String[]::new);

    private static final String[] FAIL_TYPES = Arrays.stream(ProductTradeStatus.Status.values())
            .map(Enum::name)
            .toArray(String[]::new);

    public static EditorPreset resolve(EditorTarget target, String path) {
        EditorPresetKind kind = resolveKind(target, path);
        if (kind == null) {
            return null;
        }

        return switch (kind) {
            case ROOT_SHOP -> new EditorPreset(kind, rootShopFields());
            case ROOT_MENU -> new EditorPreset(kind, rootMenuFields(target));
            case UI_SECTION -> new EditorPreset(kind, uiFields(target, path));
            case VISIBILITY_SECTION -> new EditorPreset(kind, visibilityFields(target));
            case SHOP_ITEM -> new EditorPreset(kind, shopItemFields());
            case BUTTON_ENTRY -> new EditorPreset(kind, buttonFields(target));
            case LIMITS_SECTION -> new EditorPreset(kind, limitFields(target, path));
            case CLICK_EVENT_SECTION -> new EditorPreset(kind, clickEventFields());
            case SINGLE_THING -> new EditorPreset(kind, singleThingFields(path));
            case ACTION_ENTRY -> new EditorPreset(kind, actionFields(target, path));
            case CONDITION_ENTRY -> new EditorPreset(kind, conditionFields(target, path));
        };
    }

    public static EditorPresetKind resolveKind(EditorTarget target, String path) {
        String normalizedPath = path == null ? "" : path;
        String[] split = normalizedPath.isEmpty() ? new String[0] : normalizedPath.split("\\.");

        if (normalizedPath.isEmpty()) {
            return target.getScope() == EditorScope.SHOP ? EditorPresetKind.ROOT_SHOP : EditorPresetKind.ROOT_MENU;
        }
        if (normalizedPath.equals("menu-visibility") || normalizedPath.endsWith(".menu-visibility")) {
            return EditorPresetKind.VISIBILITY_SECTION;
        }
        if (normalizedPath.equals("ui") || normalizedPath.endsWith(".ui")
                || normalizedPath.equals("ui.products") || normalizedPath.endsWith(".ui.products")) {
            EditorScope scope = menuScope(target);
            if (scope == EditorScope.BUY_MORE_MENU || scope == EditorScope.ITEM_SELL_MENU) {
                return null;
            }
            return EditorPresetKind.UI_SECTION;
        }
        if (target.getScope() == EditorScope.SHOP && normalizedPath.equals("settings.menu-settings")) {
            return EditorPresetKind.ROOT_MENU;
        }
        if (target.getScope() == EditorScope.SHOP && normalizedPath.equals("general-configs")) {
            return EditorPresetKind.SHOP_ITEM;
        }
        if (target.getScope() == EditorScope.SHOP && split.length == 2 && split[0].equals("items")) {
            return EditorPresetKind.SHOP_ITEM;
        }
        if (target.getScope() == EditorScope.SHOP && split.length == 3 && split[0].equals("items")
                && split[2].equals("click-event")) {
            return EditorPresetKind.CLICK_EVENT_SECTION;
        }
        if (split.length >= 2 && split[split.length - 2].equals("buttons")) {
            return EditorPresetKind.BUTTON_ENTRY;
        }
        if (target.getScope() == EditorScope.SHOP && split.length == 3 && split[0].equals("items")
                && EditorTypeResolver.isLimitSection(normalizedPath)) {
            return EditorPresetKind.LIMITS_SECTION;
        }
        if (target.getScope() == EditorScope.SHOP && split.length == 4 && split[0].equals("items")
                && (split[2].equals("products") || split[2].equals("buy-prices") || split[2].equals("sell-prices"))) {
            return EditorPresetKind.SINGLE_THING;
        }
        if (EditorTypeResolver.isActionEntry(normalizedPath)) {
            return EditorPresetKind.ACTION_ENTRY;
        }
        if (EditorTypeResolver.isConditionEntry(normalizedPath)) {
            return EditorPresetKind.CONDITION_ENTRY;
        }
        return null;
    }

    private static List<EditorPresetField> rootShopFields() {
        return List.of(
                choice("settings.menu", Material.BOOK, EditorScope.SHOP_MENU_TEMPLATE.listIds().toArray(new String[0])),
                field("settings.menu-settings", Material.WRITABLE_BOOK, EditorPresetFieldType.SECTION),
                field("settings.buy-more", Material.CHEST, EditorPresetFieldType.BOOLEAN),
                new EditorPresetField("settings.allow-favourite", "Allow Favourite",
                        "Allow products from this shop to be added into favourite menus.", Material.NAME_TAG, EditorPresetFieldType.BOOLEAN),
                field("settings.shop-name", Material.NAME_TAG, EditorPresetFieldType.STRING),
                field("settings.hide-message", Material.PAPER, EditorPresetFieldType.BOOLEAN),
                field("settings.secret-shop-items", Material.ENDER_EYE, EditorPresetFieldType.BOOLEAN),
                field("settings.custom-command.name", Material.COMMAND_BLOCK, EditorPresetFieldType.STRING),
                field("settings.custom-command.description", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING),
                field("general-configs", Material.CHEST, EditorPresetFieldType.SECTION),
                field("items", Material.CHEST, EditorPresetFieldType.SECTION),
                field("buttons", Material.STONE_BUTTON, EditorPresetFieldType.SECTION)
        );
    }

    private static EditorScope menuScope(EditorTarget target) {
        if (target.getScope() != EditorScope.MENU) {
            return target.getScope();
        }
        String type = target.getConfig().getString("menu-type", "");
        ConfigManager config = ConfigManager.configManager;
        if (type.equalsIgnoreCase("search")
                || config != null && config.getStringList("menu.search-gui.menu").contains(target.getId())) {
            return EditorScope.SEARCH_MENU;
        }
        if (type.equalsIgnoreCase("favourite") || type.equalsIgnoreCase("favorite")
                || config != null && config.getStringList("menu.favourite-gui.menu").contains(target.getId())) {
            return EditorScope.FAVOURITE_MENU;
        }
        if (type.equalsIgnoreCase("item-sell") || type.equalsIgnoreCase("item_sell")) {
            return EditorScope.ITEM_SELL_MENU;
        }
        return target.getScope();
    }

    private static List<EditorPresetField> rootMenuFields(EditorTarget target) {
        EditorScope scope = menuScope(target);
        List<EditorPresetField> fields = new ArrayList<>(List.of(
                field("title", Material.NAME_TAG, EditorPresetFieldType.STRING),
                field("content", Material.PAPER, EditorPresetFieldType.STRING),
                field("dialog.enabled", Material.LEVER, EditorPresetFieldType.BOOLEAN),
                choice("dialog.layout", Material.BOOK,
                        scope == EditorScope.SEARCH_MENU || scope == EditorScope.FAVOURITE_MENU
                                ? new String[] {"multi-action", "item-action-list"}
                                : new String[] {"multi-action", "item-action-list", "ore"}),
                field("dialog.content", Material.PAPER, EditorPresetFieldType.STRING),
                field("dialog.button-width", Material.STONE_BUTTON, EditorPresetFieldType.INTEGER),
                field("dialog.columns", Material.BOOKSHELF, EditorPresetFieldType.INTEGER),
                field("bedrock.enabled", Material.LEVER, EditorPresetFieldType.BOOLEAN),
                field("bedrock.content", Material.PAPER, EditorPresetFieldType.STRING),
                field("arcmenu.enabled", Material.LEVER, EditorPresetFieldType.BOOLEAN),
                field("arcmenu.content", Material.PAPER, EditorPresetFieldType.STRING),
                field("ui", Material.PAINTING, EditorPresetFieldType.SECTION),
                field("size", Material.CHEST, EditorPresetFieldType.INTEGER),
                field("dynamic-layout", Material.REDSTONE, EditorPresetFieldType.BOOLEAN),
                field("layout", Material.PAPER, EditorPresetFieldType.STRING_LIST),
                field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("open-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("close-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("buttons", Material.STONE_BUTTON, EditorPresetFieldType.SECTION),
                field("custom-command.name", Material.COMMAND_BLOCK, EditorPresetFieldType.STRING),
                field("custom-command.description", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING)
        ));
        if (scope == EditorScope.BUY_MORE_MENU || scope == EditorScope.ITEM_SELL_MENU) {
            fields.removeIf(field -> field.getKey().equals("ui") || field.getKey().equals("content")
                    || field.getKey().startsWith("bedrock.") || field.getKey().startsWith("arcmenu.")
                    || field.getKey().startsWith("dialog.")
                    && !(scope == EditorScope.BUY_MORE_MENU && field.getKey().equals("dialog.enabled")));
        }
        if (scope == EditorScope.BUY_MORE_MENU) {
            fields.removeIf(field -> field.getKey().equals("dynamic-layout") || field.getKey().equals("conditions")
                    || field.getKey().startsWith("custom-command."));
        }
        switch (scope) {
            case SEARCH_MENU -> {
                fields.add(field("input-item", Material.HOPPER, EditorPresetFieldType.STRING));
                fields.add(field("result-item", Material.CHEST, EditorPresetFieldType.STRING));
                fields.add(field("action-items", Material.STONE_BUTTON, EditorPresetFieldType.SECTION));
                fields.add(field("state-items", Material.PAPER, EditorPresetFieldType.SECTION));
                fields.add(field("no-result-item", Material.BARRIER, EditorPresetFieldType.SECTION));
                fields.add(field("result-lore", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING_LIST));
            }
            case FAVOURITE_MENU -> {
                fields.add(field("result-item", Material.CHEST, EditorPresetFieldType.STRING));
                fields.add(field("edit-mode-item", Material.NAME_TAG, EditorPresetFieldType.SECTION));
                fields.add(field("empty-item", Material.BARRIER, EditorPresetFieldType.SECTION));
                fields.add(field("result-lore", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING_LIST));
                fields.add(field("editing-result-lore", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING_LIST));
            }
            case BUY_MORE_MENU -> {
                fields.add(field("amount-items", Material.SLIME_BALL, EditorPresetFieldType.SECTION));
                fields.add(field("display-items", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING_LIST));
                fields.add(field("confirm-items", Material.STONE_BUTTON, EditorPresetFieldType.SECTION));
            }
            case ITEM_SELL_MENU -> {
                fields.add(field("input-item", Material.HOPPER, EditorPresetFieldType.STRING));
                fields.add(field("confirm-item", Material.STONE_BUTTON, EditorPresetFieldType.STRING));
                fields.add(field("confirm-button", Material.STONE_BUTTON, EditorPresetFieldType.SECTION));
                fields.add(field("max-sell-amount", Material.SLIME_BALL, EditorPresetFieldType.INTEGER));
            }
            default -> {
                // Common and shop template menus use the shared fields.
            }
        }
        return fields;
    }

    private static String[] clickActionIds() {
        LinkedHashSet<String> actions = new LinkedHashSet<>(
                List.of("select-amount", "buy", "sell", "buy-or-sell", "sell-all", "add-favourite", "none"));
        if (ConfigManager.configManager != null) {
            ConfigurationSection custom = ConfigManager.configManager.getSection("menu.click-event-actions");
            if (custom != null) {
                actions.addAll(custom.getKeys(false));
            }
        }
        return actions.toArray(new String[0]);
    }

    private static List<EditorPresetField> visibilityFields(EditorTarget target) {
        EditorScope scope = menuScope(target);
        if (scope == EditorScope.BUY_MORE_MENU || scope == EditorScope.ITEM_SELL_MENU) {
            return List.of(field("inventory", Material.CHEST, EditorPresetFieldType.BOOLEAN));
        }
        List<EditorPresetField> fields = new ArrayList<>(List.of(
                field("inventory", Material.CHEST, EditorPresetFieldType.BOOLEAN),
                field("form", Material.MAP, EditorPresetFieldType.BOOLEAN),
                field("dialog", Material.BOOK, EditorPresetFieldType.BOOLEAN),
                field("item-action-list", Material.ITEM_FRAME, EditorPresetFieldType.BOOLEAN),
                field("ore", Material.PAINTING, EditorPresetFieldType.BOOLEAN),
                field("arcmenu", Material.SPYGLASS, EditorPresetFieldType.BOOLEAN)
        ));
        if (scope == EditorScope.SEARCH_MENU || scope == EditorScope.FAVOURITE_MENU) {
            fields.removeIf(field -> field.getKey().equals("ore"));
        }
        return fields;
    }

    private static List<EditorPresetField> uiFields(EditorTarget target, String path) {
        if (path.endsWith("ui.products")) {
            return List.of(
                    field("placement", Material.PAINTING, EditorPresetFieldType.SECTION),
                    field("card", Material.PAINTING, EditorPresetFieldType.SECTION),
                    field("price.enabled", Material.GOLD_INGOT, EditorPresetFieldType.BOOLEAN),
                    field("price.formats", Material.GOLD_INGOT, EditorPresetFieldType.SECTION),
                    field("actions.primary", Material.STONE_BUTTON, EditorPresetFieldType.STRING_LIST),
                    field("actions", Material.STONE_BUTTON, EditorPresetFieldType.SECTION)
            );
        }
        boolean menu = path.equals("settings.menu-settings.ui")
                || path.equals("ui") && target.getScope() != EditorScope.SHOP;
        if (!menu) {
            return List.of(
                    choice("appearance", Material.PAINTING, "card", "button"),
                    field("placement.enabled", Material.LEVER, EditorPresetFieldType.BOOLEAN),
                    field("placement.area", Material.NAME_TAG, EditorPresetFieldType.STRING),
                    field("placement.order", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("placement.row", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("placement.column", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("placement.x", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("placement.y", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("placement.width", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("placement.height", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                    field("font-size", Material.WRITABLE_BOOK, EditorPresetFieldType.DOUBLE),
                    choice("text-align", Material.WRITABLE_BOOK, "left", "center", "right"),
                    field("price.enabled", Material.GOLD_INGOT, EditorPresetFieldType.BOOLEAN),
                    field("price.formats", Material.GOLD_INGOT, EditorPresetFieldType.SECTION),
                    field("actions.primary", Material.STONE_BUTTON, EditorPresetFieldType.STRING_LIST),
                    field("actions", Material.STONE_BUTTON, EditorPresetFieldType.SECTION)
            );
        }
        List<EditorPresetField> fields = new ArrayList<>(List.of(
                field("layout.canvas.width", Material.PAPER, EditorPresetFieldType.INTEGER),
                field("layout.canvas.height", Material.PAPER, EditorPresetFieldType.INTEGER),
                field("layout.header", Material.NAME_TAG, EditorPresetFieldType.SECTION),
                field("layout.areas", Material.PAINTING, EditorPresetFieldType.SECTION),
                field("layout.pagination", Material.ARROW, EditorPresetFieldType.SECTION),
                field("products", Material.CHEST, EditorPresetFieldType.SECTION),
                field("theme", Material.PAINTING, EditorPresetFieldType.SECTION),
                field("text", Material.WRITABLE_BOOK, EditorPresetFieldType.SECTION),
                field("renderers.ore", Material.PAINTING, EditorPresetFieldType.SECTION),
                EditorPresetField.choice("renderers.arcmenu.right-click-action", "ArcMenu Right-click Action",
                        "Product right-click action. Default select-amount opens product details.",
                        Material.STONE_BUTTON, clickActionIds()),
                field("renderers.arcmenu.screen-scale", Material.SPYGLASS, EditorPresetFieldType.DOUBLE),
                field("renderers.arcmenu.update-interval", Material.CLOCK, EditorPresetFieldType.INTEGER),
                field("renderers.arcmenu.animation.enabled", Material.LEVER, EditorPresetFieldType.BOOLEAN),
                field("renderers.arcmenu.animation.duration", Material.CLOCK, EditorPresetFieldType.INTEGER),
                field("renderers.arcmenu.more-menu.columns", Material.BOOKSHELF, EditorPresetFieldType.INTEGER),
                field("renderers.arcmenu.tooltip.enabled", Material.LEVER, EditorPresetFieldType.BOOLEAN),
                field("renderers.arcmenu.tooltip.width", Material.PAPER, EditorPresetFieldType.INTEGER),
                field("renderers.arcmenu.tooltip.font-size", Material.PAPER, EditorPresetFieldType.DOUBLE),
                field("renderers.arcmenu.tooltip.padding", Material.PAPER, EditorPresetFieldType.DOUBLE)
        ));
        EditorScope scope = menuScope(target);
        if (scope == EditorScope.SEARCH_MENU || scope == EditorScope.FAVOURITE_MENU) {
            fields.removeIf(field -> field.getKey().equals("renderers.ore"));
        }
        return fields;
    }

    private static List<EditorPresetField> shopItemFields() {
        return List.of(
                field("display-name", Material.NAME_TAG, EditorPresetFieldType.STRING),
                field("menu-visibility", Material.ENDER_EYE, EditorPresetFieldType.SECTION),
                field("dialog.show-sprite", Material.ITEM_FRAME, EditorPresetFieldType.BOOLEAN),
                field("ui", Material.PAINTING, EditorPresetFieldType.SECTION),
                field("display-item", Material.ITEM_FRAME, EditorPresetFieldType.SECTION),
                field("add-lore", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING_LIST),
                choice("price-mode", Material.CLOCK, "ANY", "ALL", "CLASSIC_ANY", "CLASSIC_ALL"),
                choice("product-mode", Material.CLOCK, "ANY", "ALL", "CLASSIC_ANY", "CLASSIC_ALL"),
                field("products", Material.CHEST, EditorPresetFieldType.SECTION),
                field("buy-prices", Material.GOLD_INGOT, EditorPresetFieldType.SECTION),
                field("sell-prices", Material.EMERALD, EditorPresetFieldType.SECTION),
                field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("buy-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("sell-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("display-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("buy-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("sell-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("fail-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("click-event", Material.STONE_BUTTON, EditorPresetFieldType.SECTION),
                choice("bedrock.click-event", Material.STONE_BUTTON, clickActionIds()),
                field("buy-more", Material.CHEST, EditorPresetFieldType.BOOLEAN),
                new EditorPresetField("default-buy-amount", "Default Buy Amount",
                        "Direct buy trade count (1-9999). Sub buttons inherit the target when unset.",
                        Material.GOLD_INGOT, EditorPresetFieldType.INTEGER),
                new EditorPresetField("default-sell-amount", "Default Sell Amount",
                        "Direct sell trade count (1-9999). Sub buttons inherit the target when unset.",
                        Material.EMERALD, EditorPresetFieldType.INTEGER),
                EditorPresetField.choice("buy-more-menu.menu", "Buy More Menu File",
                        "Quantity menu file; ArcMenu select-amount opens product details instead.", Material.CHEST,
                        EditorScope.BUY_MORE_MENU.listIds().toArray(new String[0])),
                new EditorPresetField("buy-more-menu.max-amount", "Buy More Max Amount",
                        "Maximum selectable quantity, including ArcMenu product details.", Material.SLIME_BALL, EditorPresetFieldType.INTEGER),
                field("hide-message", Material.PAPER, EditorPresetFieldType.BOOLEAN),
                new EditorPresetField("allow-favourite", "Allow Favourite",
                        "Allow this product to be added into favourite menus.", Material.NAME_TAG, EditorPresetFieldType.BOOLEAN),
                field("sell-all", Material.HOPPER, EditorPresetFieldType.BOOLEAN),
                field("as-sub-button", Material.CHAINMAIL_HELMET, EditorPresetFieldType.STRING),
                field("bedrock.icon", Material.ITEM_FRAME, EditorPresetFieldType.STRING),
                field("limits", Material.BARRIER, EditorPresetFieldType.SECTION),
                field("limits-conditions", Material.COMPARATOR, EditorPresetFieldType.SECTION),
                field("buy-limits", Material.BARRIER, EditorPresetFieldType.SECTION),
                field("buy-limits-conditions", Material.COMPARATOR, EditorPresetFieldType.SECTION),
                field("sell-limits", Material.BARRIER, EditorPresetFieldType.SECTION),
                field("sell-limits-conditions", Material.COMPARATOR, EditorPresetFieldType.SECTION),
                choice("buy-times-reset-mode", Material.REPEATER, RESET_MODES),
                EditorPresetField.resetTime("buy-times-reset-time", "Buy Reset Time",
                        "Reset time value editor that adapts to the selected buy reset mode.", Material.REPEATER),
                field("buy-times-reset-time-format", Material.CLOCK, EditorPresetFieldType.STRING),
                field("buy-times-reset-value", Material.SLIME_BALL, EditorPresetFieldType.STRING),
                field("buy-times-max-value", Material.BARRIER, EditorPresetFieldType.STRING),
                field("shared-use-times", Material.CHAINMAIL_HELMET, EditorPresetFieldType.STRING),
                choice("sell-times-reset-mode", Material.REPEATER, RESET_MODES),
                EditorPresetField.resetTime("sell-times-reset-time", "Sell Reset Time",
                        "Reset time value editor that adapts to the selected sell reset mode.", Material.REPEATER),
                field("sell-times-reset-time-format", Material.CLOCK, EditorPresetFieldType.STRING),
                field("sell-times-reset-value", Material.SLIME_BALL, EditorPresetFieldType.STRING),
                field("sell-times-max-value", Material.BARRIER, EditorPresetFieldType.STRING)
        );
    }

    private static List<EditorPresetField> buttonFields(EditorTarget target) {
        List<EditorPresetField> fields = new ArrayList<>(List.of(
                field("display-item", Material.ITEM_FRAME, EditorPresetFieldType.SECTION),
                choice("dialog.layout", Material.ITEM_FRAME, "multi-action", "item-action-list"),
                field("dialog.show-sprite", Material.ITEM_FRAME, EditorPresetFieldType.BOOLEAN),
                field("menu-visibility", Material.ENDER_EYE, EditorPresetFieldType.SECTION),
                field("ui", Material.PAINTING, EditorPresetFieldType.SECTION),
                field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("fail-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("display-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS)
        ));
        EditorScope scope = menuScope(target);
        if (scope == EditorScope.BUY_MORE_MENU || scope == EditorScope.ITEM_SELL_MENU) {
            fields.removeIf(field -> field.getKey().equals("ui") || field.getKey().startsWith("dialog."));
        }
        return fields;
    }

    private static List<EditorPresetField> limitFields(EditorTarget target, String path) {
        List<EditorPresetField> result = new ArrayList<>();
        result.add(new EditorPresetField("default", "Default Limit",
                "Limit used when the player does not match any custom limit condition group.", Material.PAPER, EditorPresetFieldType.STRING));
        result.add(new EditorPresetField("global", "Global Limit",
                "Global shared limit applied to the whole server for this product.", Material.GLOBE_BANNER_PATTERN, EditorPresetFieldType.STRING));

        ConfigurationSection section = MenuStatusManager.menuStatusManager.getSection(target, path);
        if (section == null) {
            return result;
        }

        List<String> customKeys = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            if (key.equals("default") || key.equals("global")) {
                continue;
            }
            customKeys.add(key);
        }
        customKeys.sort(Comparator.naturalOrder());
        for (String key : customKeys) {
            result.add(field(key, Material.NAME_TAG, EditorPresetFieldType.STRING));
        }
        return result;
    }

    private static List<EditorPresetField> clickEventFields() {
        List<EditorPresetField> result = new ArrayList<>();
        result.add(EditorPresetField.clickBinding("buy", "Buy",
                "Click types that trigger the buy action for this product.", Material.EMERALD));
        result.add(EditorPresetField.clickBinding("sell", "Sell",
                "Click types that trigger the sell action for this product.", Material.GOLD_INGOT));
        result.add(EditorPresetField.clickBinding("buy-or-sell", "Buy Or Sell",
                "Click types that trigger the shared buy-or-sell action.", Material.CLOCK));
        result.add(EditorPresetField.clickBinding("sell-all", "Sell All",
                "Click types that trigger sell-all for this product.", Material.HOPPER));
        result.add(EditorPresetField.clickBinding("select-amount", "Select Amount",
                "Opens quantity selection; ArcMenu opens product details. ArcMenu right-click has its own UI setting.", Material.CHEST));

        ConfigurationSection customActions = ConfigManager.configManager.getSection("menu.click-event-actions");
        if (customActions != null) {
            List<String> keys = new ArrayList<>(new LinkedHashSet<>(customActions.getKeys(false)));
            keys.sort(String::compareToIgnoreCase);
            for (String key : keys) {
                result.add(EditorPresetField.clickBinding(key, key,
                        "Click types that trigger the custom click-event action '" + key + "'.", Material.STONE_BUTTON));
            }
        }
        return result;
    }

    private static List<EditorPresetField> singleThingFields(String path) {
        List<EditorPresetField> fields = new ArrayList<>();
        boolean priceThing = isPriceThing(path);

        fields.add(new EditorPresetField("", Material.BOOK, EditorPresetFieldType.DISPLAY));
        fields.add(new EditorPresetField("__inline-item__", Material.ITEM_FRAME, EditorPresetFieldType.ITEM_INLINE));
        fields.add(new EditorPresetField("__inline-economy__", Material.GOLD_INGOT, EditorPresetFieldType.ECONOMY_INLINE));
        fields.add(field("amount", Material.SLIME_BALL, EditorPresetFieldType.STRING));
        fields.add(field("material", Material.IRON_INGOT, EditorPresetFieldType.STRING));
        fields.add(field("sprite", Material.PAINTING, EditorPresetFieldType.STRING));
        fields.add(field("hook-plugin", Material.CHEST_MINECART, EditorPresetFieldType.STRING));
        fields.add(field("hook-item", Material.CHEST_MINECART, EditorPresetFieldType.STRING));
        fields.add(field("match-item", Material.HOPPER, EditorPresetFieldType.SECTION));
        fields.add(field("match-placeholder", Material.PAPER, EditorPresetFieldType.STRING));
        fields.add(field("economy-plugin", Material.GOLD_INGOT, EditorPresetFieldType.STRING));
        fields.add(field("economy-type", Material.GOLD_NUGGET, EditorPresetFieldType.STRING));
        fields.add(field("placeholder", Material.NAME_TAG, EditorPresetFieldType.STRING));
        fields.add(field("min-amount", Material.REDSTONE, EditorPresetFieldType.DOUBLE));
        fields.add(field("max-amount", Material.REDSTONE, EditorPresetFieldType.DOUBLE));
        if (priceThing) {
            fields.add(field("start-apply", Material.CLOCK, EditorPresetFieldType.INTEGER));
            fields.add(field("end-apply", Material.CLOCK, EditorPresetFieldType.INTEGER));
            fields.add(field("apply", Material.CLOCK, EditorPresetFieldType.INTEGER_LIST));
        }
        fields.add(field("apply-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS));
        fields.add(field("require-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS));
        fields.add(field("give-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
        fields.add(field("take-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
        fields.add(field("give-item", Material.CHEST, EditorPresetFieldType.BOOLEAN));
        fields.add(field("take", Material.CHEST, EditorPresetFieldType.BOOLEAN));
        return fields;
    }

    private static List<EditorPresetField> actionFields(EditorTarget target, String path) {
        List<EditorPresetField> result = new ArrayList<>();
        result.add(choice("type", Material.BLAZE_POWDER,
                ActionManager.actionManager.getActionTypes().toArray(new String[0])));
        result.add(field("start-apply", Material.CLOCK, EditorPresetFieldType.INTEGER));
        result.add(field("end-apply", Material.CLOCK, EditorPresetFieldType.INTEGER));
        result.add(field("apply", Material.CLOCK, EditorPresetFieldType.INTEGER_LIST));
        result.add(field("multi-once", Material.CHEST, EditorPresetFieldType.BOOLEAN));
        result.add(field("sell-all-once", Material.CHEST, EditorPresetFieldType.BOOLEAN));
        result.add(field("open-once", Material.CHEST, EditorPresetFieldType.BOOLEAN));
        result.add(choice("click-type", Material.STONE_BUTTON, CLICK_TYPES));
        result.add(field("bedrock-only", Material.MAP, EditorPresetFieldType.BOOLEAN));
        result.add(field("java-only", Material.MAP, EditorPresetFieldType.BOOLEAN));
        result.add(choice("fail-type", Material.BARRIER, FAIL_TYPES));

        ConfigurationSection section = MenuStatusManager.menuStatusManager.getSection(target, path);
        String type = section == null ? "message" : section.getString("type", "message");
        switch (type) {
            case "message", "action_bar", "announcement" ->
                    result.add(field("message", Material.PAPER, EditorPresetFieldType.STRING));
            case "title" -> {
                result.add(field("main-title", Material.PAPER, EditorPresetFieldType.STRING));
                result.add(field("sub-title", Material.PAPER, EditorPresetFieldType.STRING));
                result.add(field("fade-in", Material.CLOCK, EditorPresetFieldType.INTEGER));
                result.add(field("stay", Material.CLOCK, EditorPresetFieldType.INTEGER));
                result.add(field("fade-out", Material.CLOCK, EditorPresetFieldType.INTEGER));
            }
            case "sound" -> {
                result.add(field("sound", Material.NOTE_BLOCK, EditorPresetFieldType.STRING));
                result.add(field("volume", Material.NOTE_BLOCK, EditorPresetFieldType.STRING));
                result.add(field("pitch", Material.NOTE_BLOCK, EditorPresetFieldType.STRING));
            }
            case "particle" -> {
                result.add(field("particle", Material.FIREWORK_STAR, EditorPresetFieldType.STRING));
                result.add(field("count", Material.FIREWORK_STAR, EditorPresetFieldType.INTEGER));
                result.add(field("offset-x", Material.FIREWORK_STAR, EditorPresetFieldType.DOUBLE));
                result.add(field("offset-y", Material.FIREWORK_STAR, EditorPresetFieldType.DOUBLE));
                result.add(field("offset-z", Material.FIREWORK_STAR, EditorPresetFieldType.DOUBLE));
                result.add(field("speed", Material.FIREWORK_STAR, EditorPresetFieldType.DOUBLE));
            }
            case "console_command", "op_command", "player_command" ->
                    result.add(field("command", Material.COMMAND_BLOCK, EditorPresetFieldType.STRING));
            case "shop_menu" ->
                    result.add(field("shop", Material.CHEST, EditorPresetFieldType.STRING));
            case "open_menu" ->
                    result.add(field("menu", Material.BOOK, EditorPresetFieldType.STRING));
            case "back" -> {
                result.add(field("menu", Material.BOOK, EditorPresetFieldType.STRING));
                result.add(field("shop", Material.CHEST, EditorPresetFieldType.STRING));
            }
            case "buy_more_menu" -> {
                result.add(field("shop", Material.CHEST, EditorPresetFieldType.STRING));
                result.add(field("item", Material.NAME_TAG, EditorPresetFieldType.STRING));
            }
            case "buy" -> {
                result.add(field("shop", Material.CHEST, EditorPresetFieldType.STRING));
                result.add(field("item", Material.NAME_TAG, EditorPresetFieldType.STRING));
                result.add(field("amount", Material.SLIME_BALL, EditorPresetFieldType.INTEGER));
            }
            case "sell" -> {
                result.add(field("shop", Material.CHEST, EditorPresetFieldType.STRING));
                result.add(field("item", Material.NAME_TAG, EditorPresetFieldType.STRING));
                result.add(field("amount", Material.SLIME_BALL, EditorPresetFieldType.INTEGER));
                result.add(field("sell-all", Material.HOPPER, EditorPresetFieldType.BOOLEAN));
            }
            case "chance" -> {
                result.add(field("rate", Material.CLOCK, EditorPresetFieldType.DOUBLE));
                result.add(field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
            }
            case "delay" -> {
                result.add(field("time", Material.CLOCK, EditorPresetFieldType.INTEGER));
                result.add(field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
            }
            case "any" -> {
                result.add(field("amount", Material.CLOCK, EditorPresetFieldType.INTEGER));
                result.add(field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
            }
            case "conditional" -> {
                result.add(field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS));
                result.add(field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
            }
            case "prompt" -> {
                result.add(field("description", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING));
                result.add(field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
                result.add(field("cancel-actions", Material.BARRIER, EditorPresetFieldType.ACTIONS));
                result.add(field("reopen-on-submit", Material.CHEST, EditorPresetFieldType.BOOLEAN));
                result.add(field("reopen-on-cancel", Material.CHEST, EditorPresetFieldType.BOOLEAN));
            }
            case "connect" ->
                    result.add(field("server", Material.ENDER_PEARL, EditorPresetFieldType.STRING));
            case "effect" -> {
                result.add(field("potion", Material.POTION, EditorPresetFieldType.STRING));
                result.add(field("duration", Material.POTION, EditorPresetFieldType.INTEGER));
                result.add(field("level", Material.POTION, EditorPresetFieldType.INTEGER));
                result.add(field("ambient", Material.POTION, EditorPresetFieldType.BOOLEAN));
                result.add(field("particles", Material.POTION, EditorPresetFieldType.BOOLEAN));
                result.add(field("icon", Material.POTION, EditorPresetFieldType.BOOLEAN));
            }
            case "entity_spawn", "mythicmobs_spawn" -> {
                result.add(field("entity", Material.ZOMBIE_HEAD, EditorPresetFieldType.STRING));
                result.add(field("world", Material.GRASS_BLOCK, EditorPresetFieldType.STRING));
                result.add(field("x", Material.COMPASS, EditorPresetFieldType.DOUBLE));
                result.add(field("y", Material.COMPASS, EditorPresetFieldType.DOUBLE));
                result.add(field("z", Material.COMPASS, EditorPresetFieldType.DOUBLE));
                if (type.equals("mythicmobs_spawn")) {
                    result.add(field("level", Material.EXPERIENCE_BOTTLE, EditorPresetFieldType.INTEGER));
                }
            }
            case "teleport" -> {
                result.add(field("world", Material.GRASS_BLOCK, EditorPresetFieldType.STRING));
                result.add(field("x", Material.COMPASS, EditorPresetFieldType.DOUBLE));
                result.add(field("y", Material.COMPASS, EditorPresetFieldType.DOUBLE));
                result.add(field("z", Material.COMPASS, EditorPresetFieldType.DOUBLE));
                result.add(field("yaw", Material.COMPASS, EditorPresetFieldType.INTEGER));
                result.add(field("pitch", Material.COMPASS, EditorPresetFieldType.INTEGER));
            }
            default -> {
            }
        }
        return result;
    }

    private static List<EditorPresetField> conditionFields(EditorTarget target, String path) {
        List<EditorPresetField> result = new ArrayList<>();
        result.add(choice("type", Material.COMPARATOR,
                ConditionManager.conditionManager.getConditionTypes().toArray(new String[0])));
        result.add(field("start-apply", Material.CLOCK, EditorPresetFieldType.INTEGER));
        result.add(field("end-apply", Material.CLOCK, EditorPresetFieldType.INTEGER));
        result.add(field("apply", Material.CLOCK, EditorPresetFieldType.INTEGER_LIST));
        result.add(choice("click-type", Material.STONE_BUTTON, CLICK_TYPES));
        result.add(field("bedrock-only", Material.MAP, EditorPresetFieldType.BOOLEAN));
        result.add(field("java-only", Material.MAP, EditorPresetFieldType.BOOLEAN));
        result.add(field("meet-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));
        result.add(field("not-meet-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS));

        ConfigurationSection section = MenuStatusManager.menuStatusManager.getSection(target, path);
        String type = section == null ? "permission" : section.getString("type", "permission");
        switch (type) {
            case "menu" ->
                    result.add(field("menu", Material.BOOK, EditorPresetFieldType.STRING));
            case "menu-type" ->
                    result.add(field("menu-type", Material.BOOKSHELF, EditorPresetFieldType.STRING));
            case "permission" ->
                    result.add(field("permission", Material.TRIPWIRE_HOOK, EditorPresetFieldType.STRING));
            case "placeholder" -> {
                result.add(field("placeholder", Material.PAPER, EditorPresetFieldType.STRING));
                result.add(field("rule", Material.PAPER, EditorPresetFieldType.STRING));
                result.add(field("value", Material.PAPER, EditorPresetFieldType.STRING));
            }
            case "biome" ->
                    result.add(field("biome", Material.GRASS_BLOCK, EditorPresetFieldType.STRING));
            case "world" ->
                    result.add(field("world", Material.GRASS_BLOCK, EditorPresetFieldType.STRING));
            case "any", "not" ->
                    result.add(field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS));
            default -> {
            }
        }
        return result;
    }

    private static boolean isPriceThing(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String[] split = path.split("\\.");
        return split.length >= 3 && (split[2].equals("buy-prices") || split[2].equals("sell-prices"));
    }

    private static EditorPresetField field(String key, Material material, EditorPresetFieldType type) {
        return new EditorPresetField(key, material, type);
    }

    private static EditorPresetField choice(String key, Material material, String... choices) {
        return EditorPresetField.choice(key, material, choices);
    }
}
