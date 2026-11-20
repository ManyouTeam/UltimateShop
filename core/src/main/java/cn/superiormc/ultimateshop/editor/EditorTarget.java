package cn.superiormc.ultimateshop.editor;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class EditorTarget {

    private final EditorScope scope;

    private final String id;

    private final File file;

    private final YamlConfiguration config;

    private String savedContents;

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

    public void save() throws IOException {
        Path destination = file.toPath();
        synchronized (EditorTarget.class) {
            if (savedContents != null && !savedContents.equals(Files.readString(destination))) {
                throw new IOException("The file changed after it was opened. Reopen it before editing.");
            }
            String contents = config.saveToString();
            Path temporary = Files.createTempFile(destination.toAbsolutePath().getParent(), ".ultimateshop-editor-", ".tmp");
            try {
                Files.writeString(temporary, contents);
                try {
                    Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException exception) {
                    Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
                }
                savedContents = contents;
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
    }

    public void discardUnsavedChanges() throws InvalidConfigurationException {
        if (savedContents != null) {
            config.loadFromString(savedContents);
        }
    }

    public static EditorTarget load(EditorScope scope, String id) {
        if (scope == null || !scope.acceptsId(id)) {
            return null;
        }
        File file = scope.getFile(id);
        if (!file.isFile()) {
            return null;
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            if (!file.getCanonicalFile().getParentFile().equals(scope.getFolder().getCanonicalFile())) {
                return null;
            }
            String contents = Files.readString(file.toPath());
            config.loadFromString(contents);
            EditorTarget target = new EditorTarget(scope, id, file, config);
            target.savedContents = contents;
            return target;
        } catch (IOException | InvalidConfigurationException exception) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Could not load editor file " + scope.getFilePath(id)
                    + ": " + exception.getMessage());
            return null;
        }
    }
}
