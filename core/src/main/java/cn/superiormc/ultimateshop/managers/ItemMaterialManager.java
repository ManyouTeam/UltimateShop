package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Material;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ItemMaterialManager {

    public static ItemMaterialManager itemMaterialManager;

    private final Map<Material, String> materialMap = new ConcurrentHashMap<>();

    private final String mappingFileName;

    public ItemMaterialManager() {
        itemMaterialManager = this;
        mappingFileName = ConfigManager.configManager.getString("config-files.minecraft-item-material-file.file");
        if (mappingFileName == null || mappingFileName.trim().isEmpty()) {
            return;
        }

        File mappingFile = new File(UltimateShop.instance.getDataFolder(), mappingFileName);
        if (!mappingFile.exists()) {
            generateMapping(mappingFile);
        }
        if (mappingFile.exists()) {
            loadMapping(mappingFile);
        }
    }

    public String getTexturePath(Material material) {
        return material == null ? null : materialMap.get(material);
    }

    public String getMaterialTexturePath(Material material) {
        if (material == null) {
            return null;
        }
        return itemMaterialManager.getTexturePath(material);
    }

    public Map<Material, String> getMaterialMap() {
        return new HashMap<>(materialMap);
    }

    private void loadMapping(File file) {
        try (InputStream input = new FileInputStream(file)) {
            JSONObject json = new JSONObject(new JSONTokener(input));
            for (String key : json.keySet()) {
                Material material = Material.matchMaterial(key);
                if (material != null && json.opt(key) instanceof String) {
                    materialMap.put(material, json.getString(key));
                }
            }
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Failed to load Minecraft item material file. Reason: "
                    + throwable.getMessage() + "!");
        }
    }

    private void generateMapping(File outputFile) {
        if (!InitManager.initManager.isFirstLoad()
                && !ConfigManager.configManager.getBoolean("config-files.minecraft-item-material-file.generate-new-one")) {
            return;
        }

        TextUtil.sendMessage(null, TextUtil.pluginPrefix()
                + " §fDownloading Minecraft client assets and generating item material mapping, this will cost some time...");

        File clientJar = null;
        try {
            String version = UltimateShop.yearVersion + "." + UltimateShop.majorVersion + "." + UltimateShop.minorVersion;
            if (version.endsWith(".0")) {
                version = version.substring(0, version.length() - 2);
            }

            JSONObject manifest = CommonUtil.fetchJson("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            JSONObject selected = null;
            for (Object value : manifest.getJSONArray("versions")) {
                JSONObject entry = (JSONObject) value;
                if (version.equals(entry.getString("id"))) {
                    selected = entry;
                    break;
                }
            }
            if (selected == null) {
                throw new IOException("Can not get Minecraft version " + version);
            }

            JSONObject versionInfo = CommonUtil.fetchJson(selected.getString("url"));
            String clientUrl = versionInfo.getJSONObject("downloads").getJSONObject("client").getString("url");
            clientJar = File.createTempFile("ultimateshop-client-", ".jar");
            download(clientUrl, clientJar);

            ClientAssets assets = readAssets(clientJar);
            Map<String, String> result = new LinkedHashMap<>();
            for (Material material : Material.values()) {
                if (!material.isItem() || material.isLegacy()) {
                    continue;
                }
                String id = material.getKey().getKey();
                String texture = resolveItemTexture(id, assets);
                if (texture != null) {
                    result.put(material.name(), texture);
                }
            }

            File parent = outputFile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            Files.writeString(outputFile.toPath(), new JSONObject(result).toString(2), StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Failed to generate Minecraft item material file. Reason: "
                    + throwable.getMessage() + "!");
        } finally {
            if (clientJar != null && clientJar.exists()) {
                clientJar.delete();
            }
        }
    }

    private ClientAssets readAssets(File clientJar) throws IOException {
        Map<String, JSONObject> models = new HashMap<>();
        Map<String, JSONObject> items = new HashMap<>();
        Set<String> textures = new HashSet<>();
        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(clientJar)))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (!entry.isDirectory() && name.startsWith("assets/minecraft/models/") && name.endsWith(".json")) {
                    String key = name.substring("assets/minecraft/models/".length(), name.length() - 5);
                    models.put(key, new JSONObject(new JSONTokener(zip)));
                } else if (!entry.isDirectory() && name.startsWith("assets/minecraft/items/") && name.endsWith(".json")) {
                    String key = name.substring("assets/minecraft/items/".length(), name.length() - 5);
                    items.put(key, new JSONObject(new JSONTokener(zip)));
                } else if (!entry.isDirectory() && name.startsWith("assets/minecraft/textures/") && name.endsWith(".png")) {
                    textures.add(name.substring("assets/minecraft/textures/".length(), name.length() - 4));
                }
            }
        }
        return new ClientAssets(models, items, textures);
    }

    private String resolveItemTexture(String id, ClientAssets assets) {
        String modelPath = findModelPath(assets.items.get(id));
        if (modelPath == null) {
            modelPath = "item/" + id;
        }
        JSONObject model = assets.models.get(stripNamespace(modelPath));
        if (model == null) {
            return fallbackTexture(id, assets.textures);
        }

        Map<String, String> textures = new LinkedHashMap<>();
        JSONObject current = model;
        int depth = 0;
        while (current != null && depth++ < 32) {
            JSONObject values = current.optJSONObject("textures");
            if (values != null) {
                Iterator<String> keys = values.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    textures.putIfAbsent(key, values.optString(key, null));
                }
            }
            String parent = current.optString("parent", null);
            if (parent == null) {
                break;
            }
            current = assets.models.get(stripNamespace(parent));
        }

        String texture = firstTexture(textures, "layer0", "all", "particle", "side", "top", "end");
        int references = 0;
        while (texture != null && texture.startsWith("#") && references++ < 16) {
            texture = textures.get(texture.substring(1));
        }
        if (texture != null) {
            String normalized = stripNamespace(texture);
            if (assets.textures.contains(normalized)) {
                return texture.contains(":") ? texture : "minecraft:" + texture;
            }
        }
        return fallbackTexture(id, assets.textures);
    }

    /** Finds the first concrete vanilla model in a modern item-definition tree. */
    private String findModelPath(Object node) {
        if (node instanceof JSONObject object) {
            String type = object.optString("type", "");
            Object model = object.opt("model");
            if (model instanceof String && (type.isEmpty() || type.endsWith(":model") || "model".equals(type))) {
                return (String) model;
            }

            // Keep insertion order: base/fallback models precede conditional alternatives in Mojang definitions.
            for (String key : object.keySet()) {
                String result = findModelPath(object.opt(key));
                if (result != null) {
                    return result;
                }
            }
        } else if (node instanceof JSONArray array) {
            for (int i = 0; i < array.length(); i++) {
                String result = findModelPath(array.opt(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private String fallbackTexture(String id, Set<String> textures) {
        String item = "item/" + id;
        if (textures.contains(item)) {
            return "minecraft:" + item;
        }
        String block = "block/" + id;
        if (textures.contains(block)) {
            return "minecraft:" + block;
        }
        return null;
    }

    private String firstTexture(Map<String, String> textures, String... preferredKeys) {
        for (String key : preferredKeys) {
            String value = textures.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        for (String value : textures.values()) {
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    private String stripNamespace(String value) {
        int separator = value.indexOf(':');
        return separator < 0 ? value : value.substring(separator + 1);
    }

    private void download(String source, File target) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(source).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("User-Agent", "UltimateShop Item Material Generator");
        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IOException("HTTP " + status);
            }
            try (InputStream input = connection.getInputStream();
                 FileOutputStream output = new FileOutputStream(target)) {
                input.transferTo(output);
            }
        } finally {
            connection.disconnect();
        }
    }

    public static boolean enableThis() {
        return !UltimateShop.freeVersion
                && (ConfigManager.configManager.getBoolean("config-files.minecraft-item-material-file.enabled"));
    }

    private static final class ClientAssets {

        private final Map<String, JSONObject> models;
        private final Map<String, JSONObject> items;
        private final Set<String> textures;

        private ClientAssets(Map<String, JSONObject> models, Map<String, JSONObject> items, Set<String> textures) {
            this.models = models;
            this.items = items;
            this.textures = textures;
        }
    }
}
