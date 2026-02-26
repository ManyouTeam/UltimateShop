package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.libs.bstats.Metrics;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public final class LicenseManager {

    public static LicenseManager licenseManager;

    private final String BBB_FLAG = "%%__BUILTBYBIT__%%";
    private final String POLYMART_FLAG = "%%__POLYMART__%%";
    private final String USERNAME = "%%__USERNAME__%%";
    private final String USER_ID = "%%__USER__%%";

    private DownloadSource source = DownloadSource.SELF_BUILD;
    private String username = null;

    public final boolean valid;

    public enum DownloadSource {
        BUILTBYBIT,
        POLYMART,
        SPIGOT,
        SELF_BUILD
    }

    public LicenseManager() {
        licenseManager = this;
        new Metrics(UltimateShop.instance, 20783);
        this.valid = checkIllegal("cn.superiormc.ultimateshop.UltimateShop", "UltimateShop");

        if (!UltimateShop.freeVersion) {
            detectSourceAndUser();
            printStartupInfo();
        }
    }

    private void detectSourceAndUser() {

        // ===== BuiltByBit =====
        if (!BBB_FLAG.contains("%%") && Boolean.parseBoolean(BBB_FLAG)) {
            source = DownloadSource.BUILTBYBIT;
            if (!USERNAME.contains("%%")) {
                username = USERNAME;
            }
            return;
        }

        // ===== Polymart =====
        if (!POLYMART_FLAG.contains("%%")) {
            source = DownloadSource.POLYMART;
            if (!USERNAME.contains("%%")) {
                username = USERNAME;
            } else if (!USER_ID.contains("%%")) {
                username = "UserID#" + USER_ID;
            }
            return;
        }

        // ===== SpigotMC =====
        if (!USER_ID.contains("%%")) {
            source = DownloadSource.SPIGOT;
            username = fetchSpigotUsername(USER_ID);
            return;
        }

        // ===== Self build =====
        source = DownloadSource.SELF_BUILD;
        username = null;
    }

    private String fetchSpigotUsername(String userId) {
        try {
            String url = "https://api.spigotmc.org/simple/0.2/index.php?action=getAuthor&id=" + userId;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                return json.optString("username", "SpigotUser#" + userId);
            }
        } catch (Exception ignored) {
        }
        return "SpigotUser#" + userId;
    }

    public boolean checkIllegal(
            String mainClass,
            String pluginName
    ) {
        try (JarFile jar = new JarFile(new File(
                UltimateShop.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        ), false, ZipFile.OPEN_READ)) {

            boolean foundJarComment = false;
            if (jar.getComment() == null || jar.getComment().isBlank() || jar.getComment().length() <= 10) {
                foundJarComment = true;
            }

            String mainClassPath = mainClass.replace('.', '/') + ".class";
            String mainPackagePath =
                    mainClassPath.substring(0, mainClassPath.lastIndexOf('/') + 1);
            String mainClassSimple =
                    mainClass.substring(mainClass.lastIndexOf('.') + 1) + ".class";

            boolean foundPluginTxt = false;
            boolean foundExtraClass = false;

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!name.startsWith(mainPackagePath)) {
                    continue;
                }

                if (name.endsWith(".txt")
                        && name.substring(name.lastIndexOf('/') + 1)
                        .startsWith(pluginName)) {
                    foundPluginTxt = true;
                }

                if (name.endsWith(".class")
                        && !name.endsWith(mainClassSimple)) {
                    foundExtraClass = true;
                }
            }

            return foundJarComment || foundPluginTxt || foundExtraClass || hasInvokeIoTrace(jar, mainClass);

        } catch (Throwable e) {
            return false;
        }
    }

    private boolean hasInvokeIoTrace(JarFile jar, String mainClassName) {
        String classPath = mainClassName.replace('.', '/') + ".class";

        JarEntry entry = jar.getJarEntry(classPath);
        if (entry == null) {
            return false;
        }

        try (InputStream in = jar.getInputStream(entry)) {
            byte[] bytes = in.readAllBytes();

            String content = new String(bytes, StandardCharsets.ISO_8859_1);

            boolean hasJavaIO = content.contains("java/io");
            boolean hasInvoke = content.contains("java/lang/invoke");

            return hasJavaIO && hasInvoke;

        } catch (Exception e) {
            return false;
        }
    }

    private void printStartupInfo() {
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cPREMIUM version found, checking your license...");
        TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fDownload source: " + source.name());
        if (!valid) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " No license found in your jar file. Please redownload it if you believe this is mistake.");
        } else if (username != null && !username.isEmpty()) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLicensed to: " + username);
        } else if (source != DownloadSource.SELF_BUILD) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " No license found in your jar file. Seems that you are self-building this plugin.");
        }
    }
}