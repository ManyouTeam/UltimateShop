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
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LocateManager {

    public static LocateManager locateManager;

    private final Map<String, String> locateMap;

    private final String languageFileName;

    private JSONObject fileContent;

    private boolean enabled = false;

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
        if (!InitManager.initManager.isFirstLoad() && !ConfigManager.configManager.getBoolean("config-files.minecraft-locate-file.generate-new-one")) {
            return;
        }
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDownloading Minecraft locate file, this will cost some time...");
        String minecraftVersion = UltimateShop.yearVersion + "." + UltimateShop.majorVersion + "." + UltimateShop.minorVersion;
        if (minecraftVersion.endsWith(".0")) {
            minecraftVersion = minecraftVersion.substring(0, minecraftVersion.length() - 2);
        }
        if (languageFileName == null) {
            return;
        }
        try {
            String versionManifestUrl = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
            JSONObject versionManifest = CommonUtil.fetchJson(versionManifestUrl);

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
                ErrorManager.errorManager.sendErrorMessage("§cError: Failed to download Minecraft locate file. Reason: Can not get your Minecraft version!");
                return;
            }

            String versionInfoUrl = targetVersion.getString("url");
            JSONObject versionInfo = CommonUtil.fetchJson(versionInfoUrl);

            String assetIndexUrl = versionInfo.getJSONObject("assetIndex").getString("url");
            JSONObject assetIndex = CommonUtil.fetchJson(assetIndexUrl);

            JSONObject objects = assetIndex.getJSONObject("objects");
            if (!objects.has("minecraft/lang/" + languageFileName)) {
                ErrorManager.errorManager.sendErrorMessage("§cError: Failed to download Minecraft locate file. Reason: Can not find locate file: " + languageFileName + "!");
                return;
            }

            String languageFileHash = objects.getJSONObject("minecraft/lang/" + languageFileName).getString("hash");
            String downloadUrl = "https://resources.download.minecraft.net/"
                    + languageFileHash.substring(0, 2) + "/" + languageFileHash;

            URL url = new URL(downloadUrl);
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

            try (var inputStream = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(new File(UltimateShop.instance.getDataFolder(), languageFileName))) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
            } finally {
                connection.disconnect();
            }
        } catch (SocketTimeoutException e) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Failed to download Minecraft locate file. Reason: Connection timed out!");
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§cError: Failed to download Minecraft locate file. Reason: Internet problem!");
            throwable.fillInStackTrace();
        }
    }

    public void loadLocateFile() {
        try {
            FileInputStream fis = new FileInputStream(new File(UltimateShop.instance.getDataFolder(), languageFileName));
            this.fileContent = new JSONObject(new JSONTokener(fis));
            fis.close();
            this.enabled = true;
        } catch (FileNotFoundException e) {
            this.enabled = false;
            ErrorManager.errorManager.sendErrorMessage("§cError: Failed to load Minecraft locate file. Reason: Can not find locate file: " + languageFileName + "!");
        } catch (Throwable throwable) {
            this.enabled = false;
            ErrorManager.errorManager.sendErrorMessage("§cError: Failed to load Minecraft locate file. Reason: " + throwable.getMessage() + "!");
            throwable.fillInStackTrace();
        }
    }

    public String getLocateName(ItemStack item) {
        if (!enabled || fileContent == null) {
            return ItemUtil.getItemNameWithoutVanilla(item);
        }

        if (!locateMap.containsKey(item.getTranslationKey())) {
            Object value = getValueFromJson(fileContent, item.getTranslationKey());
            if (value != null) {
                locateMap.put(item.getTranslationKey(), String.valueOf(value));
            } else {
                locateMap.put(item.getTranslationKey(), ItemUtil.getItemNameWithoutVanilla(item));
            }
        }
        return locateMap.get(item.getTranslationKey());
    }

    private Object getValueFromJson(JSONObject jsonObject, String path) {
        Object value = jsonObject;

        if (value != null) {
            JSONObject json = (JSONObject) value;
            if (json.has(path)) {
                value = json.get(path);
            } else {
                return null;
            }
        } else {
            return null;
        }

        return value;
    }

    public static boolean enableThis() {
        return CommonUtil.getMajorVersion(16) && !UltimateShop.freeVersion && ConfigManager.configManager.getBoolean("config-files.minecraft-locate-file.enabled");
    }
}
