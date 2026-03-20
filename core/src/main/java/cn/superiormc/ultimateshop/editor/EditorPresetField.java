package cn.superiormc.ultimateshop.editor;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorPresetField {

    private final String key;

    private final String title;

    private final String description;

    private final Material material;

    private final EditorPresetFieldType type;

    private final List<String> choices;

    public EditorPresetField(String key, Material material, EditorPresetFieldType type) {
        this(key, null, null, material, type, new ArrayList<>());
    }

    public EditorPresetField(String key, String title, String description, Material material, EditorPresetFieldType type) {
        this(key, title, description, material, type, new ArrayList<>());
    }

    public EditorPresetField(String key, String title, String description, Material material, EditorPresetFieldType type, List<String> choices) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.material = material;
        this.type = type;
        this.choices = choices;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Material getMaterial() {
        return material;
    }

    public EditorPresetFieldType getType() {
        return type;
    }

    public List<String> getChoices() {
        return choices;
    }

    public static EditorPresetField choice(String key, Material material, String... choices) {
        return new EditorPresetField(key, null, null, material, EditorPresetFieldType.STRING_CHOICE, Arrays.asList(choices));
    }

    public static EditorPresetField choice(String key, String title, String description, Material material, String... choices) {
        return new EditorPresetField(key, title, description, material, EditorPresetFieldType.STRING_CHOICE, Arrays.asList(choices));
    }
}
