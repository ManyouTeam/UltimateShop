package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §fDownloading Minecraft locate file, this will cost some time...");
        String MINECRAFT_VERSION = "1." + UltimateShop.majorVersion + "." + UltimateShop.minorVersion;
        if (MINECRAFT_VERSION.endsWith(".0")) {
            MINECRAFT_VERSION = MINECRAFT_VERSION.substring(0, MINECRAFT_VERSION.length() -2);
        }
        if (languageFileName == null) {
            return;
        }
        try {
            String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
            JSONObject versionManifest = CommonUtil.fetchJson(VERSION_MANIFEST_URL);

            JSONArray versions = versionManifest.getJSONArray("versions");
            JSONObject targetVersion = null;
            for (int i = 0; i < versions.length(); i++) {
                JSONObject version = versions.getJSONObject(i);
                if (version.getString("id").equals(MINECRAFT_VERSION)) {
                    targetVersion = version;
                    break;
                }
            }

            if (targetVersion == null) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Failed to download Minecraft locate file. Reason: Can not get your Minecraft version!");
                return;
            }

            String versionInfoUrl = targetVersion.getString("url");
            JSONObject versionInfo = CommonUtil.fetchJson(versionInfoUrl);

            String assetIndexUrl = versionInfo.getJSONObject("assetIndex").getString("url");
            JSONObject assetIndex = CommonUtil.fetchJson(assetIndexUrl);

            JSONObject objects = assetIndex.getJSONObject("objects");
            if (!objects.has("minecraft/lang/" + languageFileName)) {
                ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Failed to download Minecraft locate file. Reason: Can not find locate file: " + languageFileName + "!");
                return;
            }

            String languageFileHash = objects.getJSONObject("minecraft/lang/" + languageFileName).getString("hash");

            String downloadUrl = "https://resources.download.minecraft.net/"
                    + languageFileHash.substring(0, 2) + "/" + languageFileHash;

            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                 FileOutputStream fos = new FileOutputStream(new File(UltimateShop.instance.getDataFolder(), languageFileName))) {

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    fos.write(inputLine.getBytes());
                }
            }
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Failed to download Minecraft locate file. Reason: Internet problem!");
            throwable.fillInStackTrace();
        }
    }

    public void loadLocateFile() {

        try {
            FileInputStream fis = new FileInputStream(new File(UltimateShop.instance.getDataFolder(), languageFileName));
            this.fileContent = new JSONObject(new JSONTokener(fis));
            fis.close();
        } catch (FileNotFoundException e) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Failed to load Minecraft locate file. Reason: Can not find locate file: " + languageFileName + "!");
        } catch (Throwable throwable) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Failed to load Minecraft locate file. Reason: " + throwable.getMessage() + "!");
            throwable.fillInStackTrace();
        }

    }

    public String getLocateName(ItemStack item) {
        if (!enabled) {
            return ItemUtil.getItemNameWithoutVanilla(item);
        }
        
        if (!locateMap.containsKey(item.getTranslationKey())) {
            // 根据用户输入的路径查找值
            Object value = getValueFromJson(fileContent, item.getTranslationKey());

            // 输出结果
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
