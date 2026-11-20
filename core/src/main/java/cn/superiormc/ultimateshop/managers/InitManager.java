package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class InitManager extends AbstractManager {

    private static final Set<String> MENU_SUBFOLDERS = Set.of(
        "shop_menu_templates", "buy_more_menus", "favourite_menus", "search_menus", "item_sell_menus");

    public static InitManager initManager;

    private final BiConsumer<String, Throwable> errorLogger = (message, exception) ->
            ErrorManager.errorManager.sendErrorMessage("§cError: " + message + ": " + exception.getMessage());

    private boolean firstLoad = false;

    public InitManager() {
        initManager = this;
        File file = new File(UltimateShop.instance.getDataFolder(), "config.yml");
        if (!file.exists()) {
            UltimateShop.instance.saveDefaultConfig();
            firstLoad = true;
        }
        init();
    }

    public void init() {
        migrateLegacyFolders();
        resourceOutputFolder("ui", true);
        resourceOutputFolder("languages", true);
        resourceOutputFolder("shops", false);
        resourceOutputFolder("menus", false);
        resourceOutputFolder("shared_use_times", false);
        resourceOutputFolder("placeholders", false);
        resourceOutputFolder("sell_sticks", false);
        resourceOutputFolder("sell_chests", false);
    }

    private void migrateLegacyFolders() {
        File dataFolder = UltimateShop.instance.getDataFolder();
        for (String name : MENU_SUBFOLDERS) {
            migrate(new File(dataFolder, name), new File(dataFolder, "menus/" + name));
        }
        File menuFolder = new File(dataFolder, "menus");
        File[] commonMenus = menuFolder.listFiles();
        if (commonMenus != null) {
            for (File file : commonMenus) {
                if (!MENU_SUBFOLDERS.contains(file.getName()) && !file.getName().equals("common_menus")) {
                    migrate(file, new File(menuFolder, "common_menus/" + file.getName()));
                }
            }
        }
        for (String name : List.of("random_placeholders", "custom_placeholders", "conditional_placeholders")) {
            migrate(new File(dataFolder, name), new File(dataFolder, "placeholders/" + name));
        }
    }

    private void migrate(File source, File target) {
        if (!source.exists()) {
            return;
        }
        try {
            if (source.isDirectory()) {
                Files.createDirectories(target.toPath());
                File[] children = source.listFiles();
                if (children == null) {
                    throw new IOException("Cannot list directory " + source);
                }
                for (File child : children) {
                    migrate(child, new File(target, child.getName()));
                }
                File[] remaining = source.listFiles();
                if (remaining != null && remaining.length == 0) {
                    Files.delete(source.toPath());
                }
            } else if (target.exists()) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Cannot migrate " + source + ": " + target
                    + " already exists. The old file was preserved; merge it manually into the new location.");
            } else {
                Files.createDirectories(target.toPath().getParent());
                Files.move(source.toPath(), target.toPath());
            }
        } catch (IOException exception) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Cannot migrate " + source + " to " + target + ": " + exception.getMessage());
        }
    }

    private void resourceOutputFolder(String folderName, boolean regenerate) {
        String normalizedFolder = folderName.endsWith("/") ? folderName : folderName + "/";
        File sourceFile;
        try {
            sourceFile = new File(InitManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            // Maven and IDE runs load classes from a directory rather than a plugin JAR.
            if (sourceFile.isDirectory()) {
                URL resource = InitManager.class.getResource("/" + normalizedFolder);
                if (resource == null) {
                    return;
                }
                Path folder = Path.of(resource.toURI());
                try (Stream<Path> files = Files.walk(folder)) {
                    files.filter(Files::isRegularFile).forEach(file -> resourceOutput(
                            normalizedFolder + folder.relativize(file).toString().replace('\\', '/'),
                            regenerate));
                }
                return;
            }
        } catch (URISyntaxException | IOException exception) {
            errorLogger.accept("Failed to extract resource folder: " + folderName, exception);
            return;
        }

        try (JarFile jarFile = new JarFile(sourceFile)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                String entryName = jarEntry.getName();
                if (!entryName.startsWith(normalizedFolder)) {
                    continue;
                }
                resourceOutput(entryName, regenerate);
            }
        } catch (IOException exception) {
            errorLogger.accept("Failed to extract resource folder: " + folderName, exception);
        }
    }

    private void resourceOutput(String fileName, boolean regenerate) {
        File tempVal1 = new File(UltimateShop.instance.getDataFolder(), fileName);
        if (!tempVal1.exists()) {
            if (!firstLoad && !regenerate) {
                return;
            }
            if (tempVal1.getParentFile() != null) {
                CommonUtil.mkDir(tempVal1.getParentFile());
            }
            UltimateShop.instance.saveResource(fileName, false);
        }
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }
}
