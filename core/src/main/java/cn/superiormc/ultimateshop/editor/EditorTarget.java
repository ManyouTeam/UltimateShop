package cn.superiormc.ultimateshop.editor;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class EditorTarget {

    private final EditorScope scope;

    private final String id;

    private final File file;

    private final YamlConfiguration config;

    public EditorTarget(EditorScope scope, String id, File file, YamlConfiguration config) {
        this.scope = scope;
        this.id = id;
        this.file = file;
        this.config = config;
    }

    public EditorScope getScope() {
        return scope;
    }

    public String getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public static EditorTarget load(EditorScope scope, String id) {
        if (scope == null || id == null || id.isEmpty()) {
            return null;
        }
        File file = scope.getFile(id);
        if (!file.exists()) {
            return null;
        }
        return new EditorTarget(scope, id, file, YamlConfiguration.loadConfiguration(file));
    }
}
