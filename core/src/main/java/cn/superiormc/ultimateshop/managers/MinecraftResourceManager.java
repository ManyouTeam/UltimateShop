package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class MinecraftResourceManager extends AbstractManager {

    private static final URI VERSION_MANIFEST_URI =
        URI.create("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");

    private static final URI ASSET_BASE_URI = URI.create("https://resources.download.minecraft.net/");

    public static MinecraftResourceManager minecraftResourceManager;

    private final Path resourceDirectory;

    private final String minecraftVersion;

    private final ExecutorService resourceExecutor = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "UltimateShop-MinecraftResources");
        thread.setDaemon(true);
        return thread;
    });

    private final Object resourceLock = new Object();

    private final Map<String, CompletableFuture<?>> resourceRequests = new HashMap<>();

    private final Map<String, Long> failureTimes = new HashMap<>();

    private final Map<Path, String> verifiedDownloads = new HashMap<>();

    private JSONObject versionInformation;

    private JSONObject assetIndexObjects;

    private volatile boolean stopped;

    public MinecraftResourceManager() {
        minecraftVersion = CommonUtil.getMinecraftVersion();
        if (!minecraftVersion.matches("[a-zA-Z0-9_.-]+")) {
            throw new IllegalArgumentException("Invalid Minecraft version");
        }
        resourceDirectory = UltimateShop.instance.getDataFolder().toPath().resolve("minecraft-resources")
            .resolve(minecraftVersion).toAbsolutePath().normalize();
        minecraftResourceManager = this;
    }

    @Override
    public synchronized void onPluginDisable() {
        stopped = true;
        resourceRequests.values().forEach(request -> request.cancel(true));
        resourceExecutor.shutdownNow();
    }

    public Path getTextureDirectory() {
        return resourceDirectory.resolve("textures");
    }

    public CompletableFuture<JSONObject> requestLanguage(String locale) {
        String normalized = locale.toLowerCase(Locale.ROOT).replace('-', '_').replaceFirst("\\.json$", "");
        if (!normalized.matches("[a-z0-9_]+")) {
            normalized = "en_us";
        }
        String language = normalized;
        return requestResource("language:" + language, () -> {
            Path resourceFile = resourceDirectory.resolve("languages/" + language + ".json");
            if (Files.isRegularFile(resourceFile)) {
                return new JSONObject(Files.readString(resourceFile));
            }
            String resourcePath = "assets/minecraft/lang/" + language + ".json";
            // Some plugin locales have no vanilla counterpart. Fall back to English.
            if (!language.equals("en_us")
                && !getAssetIndexObjects().has("minecraft/lang/" + language + ".json")) {
                resourcePath = "assets/minecraft/lang/en_us.json";
            }
            JSONObject languageContent = new JSONObject(new String(readAsset(resourcePath), StandardCharsets.UTF_8));
            writeResource(resourceFile, languageContent.toString().getBytes(StandardCharsets.UTF_8));
            return languageContent;
        });
    }

    public CompletableFuture<Map<String, String>> requestMaterialMapping() {
        return requestResource("item-materials", () -> {
            Path resourceFile = resourceDirectory.resolve("item-materials.json");
            Map<String, String> result = new LinkedHashMap<>();
            if (Files.isRegularFile(resourceFile)) {
                JSONObject mappingContent = new JSONObject(Files.readString(resourceFile));
                for (String key : mappingContent.keySet()) {
                    result.put(key, mappingContent.getString(key));
                }
                return Map.copyOf(result);
            }
            Map<String, JSONObject> models = new HashMap<>();
            Map<String, JSONObject> items = new HashMap<>();
            Set<String> textures = new HashSet<>();
            try (ZipFile archive = new ZipFile(getClientArchive().toFile())) {
                Enumeration<? extends ZipEntry> entries = archive.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (entry.isDirectory()) {
                        continue;
                    }
                    if (name.startsWith("assets/minecraft/models/") && name.endsWith(".json")) {
                        try (InputStream inputStream = archive.getInputStream(entry)) {
                            models.put(
                                name.substring(24, name.length() - 5),
                                new JSONObject(new JSONTokener(inputStream)));
                        }
                    } else if (name.startsWith("assets/minecraft/items/") && name.endsWith(".json")) {
                        try (InputStream inputStream = archive.getInputStream(entry)) {
                            items.put(
                                name.substring(23, name.length() - 5),
                                new JSONObject(new JSONTokener(inputStream)));
                        }
                    } else if (name.startsWith("assets/minecraft/textures/") && name.endsWith(".png")) {
                        textures.add(name.substring(26, name.length() - 4));
                    }
                }
            }
            for (Material material : Material.values()) {
                if (!material.isItem() || material.isLegacy()) {
                    continue;
                }
                String resourceIdentifier = material.getKey().getKey();
                String texture = resolveTexture(resourceIdentifier, models, items, textures);
                if (texture != null) {
                    result.put(material.name(), texture);
                }
            }
            writeResource(resourceFile, new JSONObject(result).toString().getBytes(StandardCharsets.UTF_8));
            return Map.copyOf(result);
        });
    }

    public String getLocalizedName(String key, String locale) {
        JSONObject language = SchedulerUtil.getAvailableResult(requestLanguage(locale));
        return language == null ? null : language.optString(key, null);
    }

    public String getTexturePath(Material material) {
        if (material == null) {
            return null;
        }
        Map<String, String> mapping = SchedulerUtil.getAvailableResult(requestMaterialMapping());
        return mapping == null ? null : mapping.get(material.name());
    }

    public String getMaterialSprite(Material material) {
        String texture = getTexturePath(material);
        if (texture == null || texture.isEmpty()) {
            return null;
        }
        int separator = texture.indexOf(':');
        String namespace = separator < 0 ? "minecraft" : texture.substring(0, separator);
        String path = separator < 0 ? texture : texture.substring(separator + 1);
        return ConfigManager.configManager.getString(
            "menu.ui.renderers.dialog.auto-add-sprite.format",
            "<sprite:\"{namespace}:{atlas}\":{path}>",
            "namespace",
            namespace,
            "atlas",
            path.startsWith("block/") ? "blocks" : "items",
            "path",
            path);
    }

    public Set<String> getConfiguredSprites(ConfigurationSection display) {
        Set<String> sprites = new HashSet<>();
        if (display == null) {
            return sprites;
        }
        String sprite = display.getString("sprite");
        if (sprite != null) {
            sprites.add(sprite);
        } else if (display.contains("material") && !UltimateShop.freeVersion) {
            sprite = getMaterialSprite(
                Material.matchMaterial(display.getString("material", "")));
            if (sprite != null) {
                sprites.add(sprite);
            }
        }
        for (String key : display.getKeys(false)) {
            ConfigurationSection child = display.getConfigurationSection(key);
            if (child != null) {
                sprites.addAll(getConfiguredSprites(child));
            }
        }
        return sprites;
    }

    /** Called only by background pack generation. Custom textures remain user supplied. */
    public void cacheTextures(Map<String, Path> requested) throws Exception {
        for (Map.Entry<String, Path> entry : requested.entrySet()) {
            if (Files.isRegularFile(entry.getValue())) {
                continue;
            }
            String resourceIdentifier = entry.getKey();
            if (!resourceIdentifier.matches("minecraft:[a-z0-9_/.-]+")
                || resourceIdentifier.contains("..")) {
                throw new IOException("Supply custom icon PNG at " + entry.getValue());
            }
            writeResource(entry.getValue(),
                readAsset("assets/minecraft/textures/" + resourceIdentifier.substring(10) + ".png"));
        }
    }

    private String resolveTexture(
        String resourceIdentifier,
        Map<String, JSONObject> models,
        Map<String, JSONObject> items,
        Set<String> availableTextures) {
        String resourcePath = findModel(items.get(resourceIdentifier));
        String modelPath = resourcePath == null ? "item/" + resourceIdentifier : resourcePath;
        JSONObject model = models.get(modelPath.substring(modelPath.indexOf(':') + 1));
        Map<String, String> textures = new LinkedHashMap<>();
        for (int depth = 0; model != null && depth < 32; depth++) {
            JSONObject values = model.optJSONObject("textures");
            if (values != null) {
                for (String key : values.keySet()) {
                    textures.putIfAbsent(key, values.optString(key, null));
                }
            }
            String parent = model.optString("parent", null);
            model = parent == null ? null : models.get(parent.substring(parent.indexOf(':') + 1));
        }
        String texture = null;
        for (String key : new String[] {"layer0", "all", "particle", "side", "top", "end"}) {
            if (textures.get(key) != null && !textures.get(key).isEmpty()) {
                texture = textures.get(key);
                break;
            }
        }
        if (texture == null) {
            texture = textures.values().stream()
                .filter(value -> value != null && !value.isEmpty())
                .findFirst()
                .orElse(null);
        }
        for (int count = 0; texture != null && texture.startsWith("#") && count < 16; count++) {
            texture = textures.get(texture.substring(1));
        }
        if (texture != null && availableTextures.contains(texture.substring(texture.indexOf(':') + 1))) {
            return texture.contains(":") ? texture : "minecraft:" + texture;
        }
        for (String fallback : new String[] {"item/" + resourceIdentifier, "block/" + resourceIdentifier}) {
            if (availableTextures.contains(fallback)) {
                return "minecraft:" + fallback;
            }
        }
        return null;
    }

    private String findModel(Object node) {
        if (node instanceof JSONObject object) {
            String type = object.optString("type", "");
            Object model = object.opt("model");
            if (model instanceof String
                && (type.isEmpty() || type.endsWith(":model") || type.equals("model"))) {
                return (String) model;
            }
            for (String key : object.keySet()) {
                String result = findModel(object.opt(key));
                if (result != null) {
                    return result;
                }
            }
        } else if (node instanceof JSONArray array) {
            for (Object value : array) {
                String result = findModel(value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> CompletableFuture<T> requestResource(String key, Callable<T> action) {
        if (stopped) {
            return CompletableFuture.failedFuture(new IllegalStateException("Minecraft resource manager stopped"));
        }
        CompletableFuture<?> existing = resourceRequests.get(key);
        if (existing != null
            && (!existing.isCompletedExceptionally()
            || System.currentTimeMillis() - failureTimes.getOrDefault(key, 0L) < 60000)) {
            return (CompletableFuture<T>) existing;
        }
        CompletableFuture<T> result = CompletableFuture.supplyAsync(() -> {
            try {
                return action.call();
            } catch (Exception exception) {
                synchronized (this) {
                    failureTimes.put(key, System.currentTimeMillis());
                }
                if (!stopped && ErrorManager.errorManager != null) {
                    ErrorManager.errorManager.sendErrorMessage(
                        "§cError: Unable to prepare Minecraft " + key + ": " + exception.getMessage());
                }
                throw new CompletionException(exception);
            }
        }, resourceExecutor);
        resourceRequests.put(key, result);
        return result;
    }

    private void checkRunning() throws InterruptedIOException {
        if (stopped || Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException("Minecraft resource preparation interrupted");
        }
    }

    private JSONObject getVersionInformation() throws Exception {
        synchronized (resourceLock) {
            if (versionInformation != null) {
                return versionInformation;
            }
            Path resourceFile = resourceDirectory.resolve("version.json");
            if (Files.isRegularFile(resourceFile)) {
                versionInformation = new JSONObject(Files.readString(resourceFile));
                return versionInformation;
            }
            JSONObject manifest = new JSONObject(new String(
                fetchResource(VERSION_MANIFEST_URI, "Minecraft version list"), StandardCharsets.UTF_8));
            for (Object value : manifest.getJSONArray("versions")) {
                JSONObject entry = (JSONObject) value;
                if (minecraftVersion.equals(entry.getString("id"))) {
                    JSONObject result = new JSONObject(new String(fetchResource(
                        URI.create(entry.getString("url")), "Minecraft " + minecraftVersion + " version information"),
                        StandardCharsets.UTF_8));
                    result.getJSONObject("downloads").getJSONObject("client");
                    result.getJSONObject("assetIndex");
                    writeResource(resourceFile, result.toString().getBytes(StandardCharsets.UTF_8));
                    versionInformation = result;
                    return result;
                }
            }
            throw new IOException("Unknown Minecraft version " + minecraftVersion);
        }
    }

    private JSONObject getAssetIndexObjects() throws Exception {
        synchronized (resourceLock) {
            if (assetIndexObjects == null) {
                JSONObject record = getVersionInformation().getJSONObject("assetIndex");
                Path resourceFile = resourceDirectory.resolve("asset-index.json");
                downloadResource(record, resourceFile, "Minecraft asset index");
                assetIndexObjects = new JSONObject(Files.readString(resourceFile)).getJSONObject("objects");
            }
            return assetIndexObjects;
        }
    }

    private Path getClientArchive() throws Exception {
        synchronized (resourceLock) {
            Path resourceFile = resourceDirectory.resolve("client.jar");
            downloadResource(
                getVersionInformation().getJSONObject("downloads").getJSONObject("client"),
                resourceFile,
                "Minecraft client JAR");
            return resourceFile;
        }
    }

    private byte[] readAsset(String resourcePath) throws Exception {
        synchronized (resourceLock) {
            checkRunning();
            if (!resourcePath.startsWith("assets/minecraft/") || resourcePath.contains("..")) {
                throw new IOException("Invalid Minecraft asset path " + resourcePath);
            }
            JSONObject record = getAssetIndexObjects().optJSONObject(resourcePath.substring(7));
            if (record != null) {
                String resourceHash = record.getString("hash");
                JSONObject downloadInformation = new JSONObject()
                    .put("sha1", resourceHash)
                    .put("url", ASSET_BASE_URI.resolve(resourceHash.substring(0, 2) + "/" + resourceHash).toString());
                Path resourceFile = resourceDirectory.resolve(
                    "objects/" + resourceHash.substring(0, 2) + "/" + resourceHash);
                downloadResource(downloadInformation, resourceFile, resourcePath);
                return Files.readAllBytes(resourceFile);
            }
            try (ZipFile archive = new ZipFile(getClientArchive().toFile())) {
                ZipEntry entry = archive.getEntry(resourcePath);
                if (entry == null) {
                    throw new IOException("Missing Minecraft asset " + resourcePath);
                }
                try (InputStream inputStream = archive.getInputStream(entry)) {
                    return inputStream.readAllBytes();
                }
            }
        }
    }

    private void downloadResource(JSONObject record, Path resourceFile, String resourceDescription) throws Exception {
        synchronized (resourceLock) {
            checkRunning();
            String expected = record.getString("sha1");
            if (!expected.matches("[a-fA-F0-9]{40}")) {
                throw new IOException("Invalid resource hash");
            }
            if (Files.isRegularFile(resourceFile)
                && (expected.equals(verifiedDownloads.get(resourceFile))
                || expected.equalsIgnoreCase(calculateSha1(resourceFile)))) {
                verifiedDownloads.put(resourceFile, expected);
                return;
            }
            Files.createDirectories(resourceFile.getParent());
            Path temporary = Files.createTempFile(resourceFile.getParent(), "download-", ".tmp");
            reportResourceStatus("Downloading " + resourceDescription + "...");
            try {
                HttpURLConnection connection = openConnection(URI.create(record.getString("url")));
                try (InputStream inputStream = connection.getInputStream();
                    OutputStream outputStream = Files.newOutputStream(temporary)) {
                    long resourceSize = record.optLong("size", -1);
                    byte[] buffer = new byte[65536];
                    long downloadedBytes = 0;
                    long lastReportedTime = System.nanoTime();
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        checkRunning();
                        outputStream.write(buffer, 0, length);
                        downloadedBytes += length;
                        long currentTime = System.nanoTime();
                        if (currentTime - lastReportedTime >= 5_000_000_000L) {
                            String progress = resourceSize > 0
                                ? String.format(Locale.ROOT, "%d%% (%.2f MB / %.2f MB)",
                                    Math.min(100, downloadedBytes * 100 / resourceSize),
                                    downloadedBytes / 1048576.0, resourceSize / 1048576.0)
                                : String.format(Locale.ROOT, "%.2f MB", downloadedBytes / 1048576.0);
                            reportResourceStatus("Downloading " + resourceDescription + ": " + progress);
                            lastReportedTime = currentTime;
                        }
                    }
                } finally {
                    connection.disconnect();
                }
                if (!expected.equalsIgnoreCase(calculateSha1(temporary))) {
                    throw new IOException(
                        "Resource checksum mismatch: " + resourceFile.getFileName());
                }
                checkRunning();
                publishResource(temporary, resourceFile);
                verifiedDownloads.put(resourceFile, expected);
                reportResourceStatus("Downloaded " + resourceDescription + ".");
            } catch (Exception exception) {
                reportResourceFailure(resourceDescription, exception);
                throw exception;
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
    }

    private byte[] fetchResource(URI url, String resourceDescription) throws IOException {
        reportResourceStatus("Downloading " + resourceDescription + "...");
        try {
            HttpURLConnection connection = openConnection(url);
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] resourceData = inputStream.readAllBytes();
                reportResourceStatus("Downloaded " + resourceDescription + ".");
                return resourceData;
            } finally {
                connection.disconnect();
            }
        } catch (IOException exception) {
            reportResourceFailure(resourceDescription, exception);
            throw exception;
        }
    }

    private HttpURLConnection openConnection(URI url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("User-Agent", "UltimateShop Minecraft Resources");
        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            connection.disconnect();
            throw new IOException("HTTP " + status);
        }
        return connection;
    }

    private void reportResourceStatus(String message) {
        if (!stopped && UltimateShop.instance != null) {
            String color = message.startsWith("Downloaded ") ? "§a" : "§f";
            TextUtil.sendMessage(null,
                TextUtil.pluginPrefix() + " " + color + "Minecraft " + minecraftVersion + ": " + message);
        }
    }

    private void reportResourceFailure(String resourceDescription, Exception exception) {
        if (!stopped && ErrorManager.errorManager != null) {
            ErrorManager.errorManager.sendErrorMessage(
                "§cError: Failed to download Minecraft " + minecraftVersion
                    + " resource " + resourceDescription + ": " + exception.getMessage());
        }
    }

    private static String calculateSha1(Path resourceFile) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream inputStream = Files.newInputStream(resourceFile)) {
            byte[] buffer = new byte[65536];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, length);
            }
        }
        StringBuilder result = new StringBuilder();
        for (byte value : digest.digest()) {
            result.append(String.format("%02x", value & 255));
        }
        return result.toString();
    }

    private static void writeResource(Path resourceFile, byte[] resourceData) throws IOException {
        Files.createDirectories(resourceFile.getParent());
        Path temporary = Files.createTempFile(resourceFile.getParent(), "resource-", ".tmp");
        try {
            Files.write(temporary, resourceData);
            publishResource(temporary, resourceFile);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void publishResource(Path source, Path destination) throws IOException {
        try {
            Files.move(
                source,
                destination,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
