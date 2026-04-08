package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LocateManager {

    public static LocateManager locateManager;

    private final Map<String, String> locateMap;

    private final String languageFileName;

    private volatile JSONObject fileContent;

    private volatile boolean enabled = false;

    private volatile boolean downloading = false;

    public LocateManager() {
        locateManager = this;
        this.locateMap = new HashMap<>();
        this.languageFileName = ConfigManager.configManager.getString("config-files.minecraft-locate-file.file");
        if (languageFileName == null) {
            return;
        }
        File file = new File(UltimateShop.instance.getDataFolder(), languageFileName);
        if (!file.exists()) {
            downloadLocateFile();
        }
        this.enabled = file.exists();
        if (enabled) {
            loadLocateFile();
        }
    }

    public void downloadLocateFile() {
        if (!InitManager.initManager.isFirstLoad()
                && !ConfigManager.configManager.getBoolean("config-files.minecraft-locate-file.generate-new-one")) {
            return;
        }
        if (downloading) {
            return;
        }
        downloading = true;
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " 搂fDownloading Minecraft locate file in background...");
        CompletableFuture.runAsync(() -> {
            try {
                downloadLocateFileNow();
                File file = new File(UltimateShop.instance.getDataFolder(), languageFileName);
                enabled = file.exists();
                if (enabled) {
                    loadLocateFile();
                    if (fileContent != null) {
                        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " 搂aMinecraft locate file downloaded successfully.");
                    }
                }
            } catch (SocketTimeoutException | ConnectException exception) {
                enabled = false;
                ErrorManager.errorManager.sendErrorMessage("搂cError: Failed to download Minecraft locate file. Reason: Connection timed out!");
            } catch (Throwable throwable) {
                enabled = false;
                ErrorManager.errorManager.sendErrorMessage("搂cError: Failed to download Minecraft locate file. Reason: " + throwable.getMessage() + "!");
                throwable.printStackTrace();
            } finally {
                downloading = false;
            }
        });
    }

    private void downloadLocateFileNow() throws Exception {
        String minecraftVersion = UltimateShop.yearVersion + "." + UltimateShop.majorVersion + "." + UltimateShop.minorVersion;
        if (minecraftVersion.endsWith(".0")) {
            minecraftVersion = minecraftVersion.substring(0, minecraftVersion.length() - 2);
        }
        if (languageFileName == null) {
            return;
        }

        JSONObject versionManifest = CommonUtil.fetchJson("https://launchermeta.mojang.com/mc/game/version_manifest.json");
        JSONArray versions = versionManifest.getJSONArray("versions");
        JSONObject targetVersion = null;
        for (int i = 0; i < versions.length(); i++) {
            JSONObject version = versions.getJSONObject(i);
            if (version.getString("id").equals(minecraftVersion)) {
                targetVersion = version;
                break;
            }
        }

        if (targetVersion == null) {
            throw new IllegalStateException("Can not get your Minecraft version");
        }

        JSONObject versionInfo = CommonUtil.fetchJson(targetVersion.getString("url"));
        JSONObject assetIndex = CommonUtil.fetchJson(versionInfo.getJSONObject("assetIndex").getString("url"));
        JSONObject objects = assetIndex.getJSONObject("objects");
        if (!objects.has("minecraft/lang/" + languageFileName)) {
            throw new FileNotFoundException("Can not find locate file: " + languageFileName);
        }

        String languageFileHash = objects.getJSONObject("minecraft/lang/" + languageFileName).getString("hash");
        String downloadUrl = "https://resources.download.minecraft.net/" + languageFileHash.substring(0, 2) + "/" + languageFileHash;

        File targetFile = new File(UltimateShop.instance.getDataFolder(), languageFileName);
        HttpURLConnection connection = openConnection(downloadUrl);
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            connection.disconnect();
        }
    }

    public void loadLocateFile() {
        try (FileInputStream fis = new FileInputStream(new File(UltimateShop.instance.getDataFolder(), languageFileName))) {
            this.fileContent = new JSONObject(new JSONTokener(fis));
            this.enabled = true;
        } catch (FileNotFoundException e) {
            this.enabled = false;
            ErrorManager.errorManager.sendErrorMessage("搂cError: Failed to load Minecraft locate file. Reason: Can not find locate file: " + languageFileName + "!");
        } catch (Throwable throwable) {
            this.enabled = false;
            ErrorManager.errorManager.sendErrorMessage("搂cError: Failed to load Minecraft locate file. Reason: " + throwable.getMessage() + "!");
            throwable.printStackTrace();
        }
    }

    public String getLocateName(ItemStack item) {
        if (!enabled || fileContent == null) {
            return ItemUtil.getItemNameWithoutVanilla(item);
        }

        String translationKey = item.translationKey();
        if (!locateMap.containsKey(translationKey)) {
            Object value = getValueFromJson(fileContent, translationKey);
            if (value != null) {
                locateMap.put(translationKey, String.valueOf(value));
            } else {
                locateMap.put(translationKey, ItemUtil.getItemNameWithoutVanilla(item));
            }
        }
        return locateMap.get(translationKey);
    }

    private Object getValueFromJson(JSONObject jsonObject, String path) {
        if (jsonObject == null) {
            return null;
        }
        if (jsonObject.has(path)) {
            return jsonObject.get(path);
        }
        return null;
    }

    public static boolean enableThis() {
        return CommonUtil.getMajorVersion(16)
                && !UltimateShop.freeVersion
                && ConfigManager.configManager.getBoolean("config-files.minecraft-locate-file.enabled");
    }

    private HttpURLConnection openConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        connection.setRequestProperty("User-Agent", "UltimateShop Locate Downloader");
        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            connection.disconnect();
            throw new IOException("HTTP " + responseCode);
        }
        return connection;
    }
}
