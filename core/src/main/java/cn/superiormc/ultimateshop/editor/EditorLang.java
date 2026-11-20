package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class EditorLang {

    public static String text(Player player, String key, String fallback, String... args) {
        String[] values = new String[args.length + 1];
        values[0] = fallback;
        System.arraycopy(args, 0, values, 1, args.length);
        return LanguageManager.languageManager.getStringText(player, key, values);
    }

    public static void send(Player player, String key, String fallback, String... args) {
        TextUtil.sendMessage(player, text(player, key, fallback, args));
    }

    public static String scope(Player player, EditorScope scope) {
        return text(player, "editor.scope." + scope.getCommandName(), scope.getDisplayName());
    }

    public static String valueKind(Player player, EditorValueKind kind) {
        return text(player, "editor.value-kind." + kind.name().toLowerCase(Locale.ROOT), kind.name());
    }

    public static String displayPath(Player player, String path) {
        if (path == null || path.isEmpty()) {
            return text(player, "editor.common.root", "root");
        }
        return path;
    }

    public static String presetTitle(Player player, EditorPreset preset) {
        return text(player, "editor.presets." + preset.getKind().name().toLowerCase(Locale.ROOT) + ".title",
                fallbackPresetTitle(preset.getKind()));
    }

    public static String presetFieldName(Player player, EditorPreset preset, EditorPresetField field) {
        return text(player, fieldPath(preset, field) + ".name", fallbackFieldName(field));
    }

    public static String presetFieldDescription(Player player, EditorPreset preset, EditorPresetField field) {
        String description = field.getDescription();
        if (preset.getKind() == EditorPresetKind.LIMITS_SECTION && field.getKey().equals(field.getTitle())) {
            description = text(player, "editor.preset.custom-limit-description",
                    "Limit used when the player matches this condition group.");
        } else if (preset.getKind() == EditorPresetKind.CLICK_EVENT_SECTION && field.getKey().equals(field.getTitle())) {
            description = text(player, "editor.preset.custom-click-description",
                    "Choose the clicks that run this custom action.");
        }
        return text(player, fieldPath(preset, field) + ".desc", description == null ? "" : description);
    }

    private static String fieldPath(EditorPreset preset, EditorPresetField field) {
        return "editor.presets." + preset.getKind().name().toLowerCase(Locale.ROOT) + ".fields." + normalizeFieldKey(field);
    }

    private static String fallbackPresetTitle(EditorPresetKind kind) {
        return humanize(kind.name().toLowerCase(Locale.ROOT));
    }

    private static String fallbackFieldName(EditorPresetField field) {
        if (field.getTitle() != null && !field.getTitle().isEmpty()) {
            return field.getTitle();
        }
        return humanize(normalizeFieldKey(field));
    }

    private static String normalizeFieldKey(EditorPresetField field) {
        String key = field.getKey();
        if (key == null || key.isEmpty()) {
            return "display";
        }
        if (key.startsWith("__") && key.endsWith("__") && key.length() > 4) {
            return key.substring(2, key.length() - 2);
        }
        return key;
    }

    private static String humanize(String raw) {
        String normalized = raw.replace('-', ' ').replace('_', ' ').replace('.', ' ').trim();
        if (normalized.isEmpty()) {
            return "Value";
        }
        String[] parts = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
