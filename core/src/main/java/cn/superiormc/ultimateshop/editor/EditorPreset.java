package cn.superiormc.ultimateshop.editor;

import java.util.List;

public class EditorPreset {

    private final EditorPresetKind kind;

    private final String title;

    private final List<EditorPresetField> fields;

    public EditorPreset(EditorPresetKind kind, List<EditorPresetField> fields) {
        this(kind, null, fields);
    }

    public EditorPreset(EditorPresetKind kind, String title, List<EditorPresetField> fields) {
        this.kind = kind;
        this.title = title;
        this.fields = fields;
    }

    public EditorPresetKind getKind() {
        return kind;
    }

    public String getTitle() {
        return title;
    }

    public List<EditorPresetField> getFields() {
        return fields;
    }
}
