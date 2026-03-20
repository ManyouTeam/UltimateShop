package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.managers.ActionManager;
import cn.superiormc.ultimateshop.managers.ConditionManager;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Comparator;
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

    private static final String[] CLICK_TYPES = java.util.Arrays.stream(ClickType.values())
            .map(Enum::name)
            .toArray(String[]::new);

    private static final String[] FAIL_TYPES = java.util.Arrays.stream(ProductTradeStatus.Status.values())
            .map(Enum::name)
            .toArray(String[]::new);

    public static EditorPreset resolve(EditorTarget target, String path) {
        EditorPresetKind kind = resolveKind(target, path);
        if (kind == null) {
            return null;
        }

        return switch (kind) {
            case ROOT_SHOP -> new EditorPreset(kind, rootShopFields());
            case ROOT_MENU -> new EditorPreset(kind, rootMenuFields());
            case SHOP_ITEM -> new EditorPreset(kind, shopItemFields());
            case BUTTON_ENTRY -> new EditorPreset(kind, buttonFields());
            case LIMITS_SECTION -> new EditorPreset(kind, limitFields(target, path));
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
        if (target.getScope() == EditorScope.SHOP && normalizedPath.equals("settings.menu-settings")) {
            return EditorPresetKind.ROOT_MENU;
        }
        if (target.getScope() == EditorScope.SHOP && normalizedPath.equals("general-configs")) {
            return EditorPresetKind.SHOP_ITEM;
        }
        if (target.getScope() == EditorScope.SHOP && split.length == 2 && split[0].equals("items")) {
            return EditorPresetKind.SHOP_ITEM;
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
        List<String> commonMenus = new ArrayList<>(ObjectMenu.commonMenus.keySet());
        commonMenus.sort(String::compareToIgnoreCase);
        return List.of(
                choice("settings.menu", Material.BOOK, commonMenus.toArray(new String[0])),
                field("settings.menu-settings", Material.WRITABLE_BOOK, EditorPresetFieldType.SECTION),
                field("settings.buy-more", Material.CHEST, EditorPresetFieldType.BOOLEAN),
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

    private static List<EditorPresetField> rootMenuFields() {
        return List.of(
                field("title", Material.NAME_TAG, EditorPresetFieldType.STRING),
                field("size", Material.CHEST, EditorPresetFieldType.INTEGER),
                field("dynamic-layout", Material.REDSTONE, EditorPresetFieldType.BOOLEAN),
                field("bedrock.enabled", Material.MAP, EditorPresetFieldType.BOOLEAN),
                field("bedrock.content", Material.PAPER, EditorPresetFieldType.STRING),
                field("layout", Material.PAPER, EditorPresetFieldType.STRING_LIST),
                field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("open-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("close-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("buttons", Material.STONE_BUTTON, EditorPresetFieldType.SECTION),
                field("custom-command.name", Material.COMMAND_BLOCK, EditorPresetFieldType.STRING),
                field("custom-command.description", Material.WRITABLE_BOOK, EditorPresetFieldType.STRING)
        );
    }

    private static List<EditorPresetField> shopItemFields() {
        return List.of(
                field("display-name", Material.NAME_TAG, EditorPresetFieldType.STRING),
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
                field("buy-more", Material.CHEST, EditorPresetFieldType.BOOLEAN),
                field("buy-more-menu", Material.CHEST, EditorPresetFieldType.SECTION),
                field("hide-message", Material.PAPER, EditorPresetFieldType.BOOLEAN),
                field("sell-all", Material.HOPPER, EditorPresetFieldType.BOOLEAN),
                field("as-sub-button", Material.CHAINMAIL_HELMET, EditorPresetFieldType.STRING),
                field("bedrock.hide", Material.MAP, EditorPresetFieldType.BOOLEAN),
                field("bedrock.icon", Material.ITEM_FRAME, EditorPresetFieldType.STRING),
                field("limits", Material.BARRIER, EditorPresetFieldType.SECTION),
                field("limits-conditions", Material.COMPARATOR, EditorPresetFieldType.SECTION),
                field("buy-limits", Material.BARRIER, EditorPresetFieldType.SECTION),
                field("buy-limits-conditions", Material.COMPARATOR, EditorPresetFieldType.SECTION),
                field("sell-limits", Material.BARRIER, EditorPresetFieldType.SECTION),
                field("sell-limits-conditions", Material.COMPARATOR, EditorPresetFieldType.SECTION),
                choice("buy-times-reset-mode", Material.REPEATER, RESET_MODES),
                field("buy-times-reset-time", Material.REPEATER, EditorPresetFieldType.STRING),
                field("buy-times-reset-time-format", Material.CLOCK, EditorPresetFieldType.STRING),
                field("buy-times-reset-value", Material.SLIME_BALL, EditorPresetFieldType.STRING),
                field("buy-times-max-value", Material.BARRIER, EditorPresetFieldType.STRING),
                choice("sell-times-reset-mode", Material.REPEATER, RESET_MODES),
                field("sell-times-reset-time", Material.REPEATER, EditorPresetFieldType.STRING),
                field("sell-times-reset-time-format", Material.CLOCK, EditorPresetFieldType.STRING),
                field("sell-times-reset-value", Material.SLIME_BALL, EditorPresetFieldType.STRING),
                field("sell-times-max-value", Material.BARRIER, EditorPresetFieldType.STRING)
        );
    }

    private static List<EditorPresetField> buttonFields() {
        return List.of(
                field("display-item", Material.ITEM_FRAME, EditorPresetFieldType.SECTION),
                field("actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("fail-actions", Material.BLAZE_POWDER, EditorPresetFieldType.ACTIONS),
                field("conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS),
                field("display-conditions", Material.COMPARATOR, EditorPresetFieldType.CONDITIONS)
        );
    }

    private static List<EditorPresetField> limitFields(EditorTarget target, String path) {
        List<EditorPresetField> result = new ArrayList<>();
        result.add(field("default", Material.PAPER, EditorPresetFieldType.STRING));
        result.add(field("global", Material.GLOBE_BANNER_PATTERN, EditorPresetFieldType.STRING));

        ConfigurationSection section = EditorManager.editorManager.getSection(target, path);
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

    private static List<EditorPresetField> singleThingFields(String path) {
        List<EditorPresetField> fields = new ArrayList<>();
        boolean priceThing = isPriceThing(path);

        fields.add(new EditorPresetField("", Material.BOOK, EditorPresetFieldType.DISPLAY));
        fields.add(new EditorPresetField("__inline-item__", Material.ITEM_FRAME, EditorPresetFieldType.ITEM_INLINE));
        fields.add(field("amount", Material.SLIME_BALL, EditorPresetFieldType.STRING));
        fields.add(field("material", Material.IRON_INGOT, EditorPresetFieldType.STRING));
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

        ConfigurationSection section = EditorManager.editorManager.getSection(target, path);
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

        ConfigurationSection section = EditorManager.editorManager.getSection(target, path);
        String type = section == null ? "permission" : section.getString("type", "permission");
        switch (type) {
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
