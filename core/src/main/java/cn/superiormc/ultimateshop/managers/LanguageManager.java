package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class LanguageManager {

    public static LanguageManager languageManager;

    private String serverLanguage = null;

    private final Map<String, YamlConfiguration> languageFiles = new HashMap<>();

    private YamlConfiguration tempMessageFile;

    public LanguageManager() {
        languageManager = this;
        initLanguages();
    }

    protected String getPlayerLanguage(Player player) {
        if (player == null) {
            return serverLanguage.toLowerCase();
        }
        try {
            if (ConfigManager.configManager.getBoolean("config-files.per-player-language") && !UltimateShop.freeVersion) {
                return player.getLocale().toLowerCase();
            } else {
                return serverLanguage.toLowerCase();
            }
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            return serverLanguage.toLowerCase();
        }
    }

    private void initLanguages() {
        File langFolder = new File(UltimateShop.instance.getDataFolder(), "languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        // 加载默认 en_US.yml 作为临时文件
        InputStream is = UltimateShop.instance.getResource("languages/en_US.yml");
        if (is != null) {
            try {
                File tempFile = new File(UltimateShop.instance.getDataFolder(), "tempMessage.yml");
                Files.copy(is, tempFile.toPath());
                tempMessageFile = YamlConfiguration.loadConfiguration(tempFile);
                tempFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tempMessageFile = new YamlConfiguration();
        }

        // 加载 languages 下所有 yml 文件
        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String lang = file.getName().replace(".yml", "");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                languageFiles.put(lang.toLowerCase(), config);
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fLoaded language: " + lang + ".yml!");
            }
        }

        if (!languageFiles.containsKey("en_US")) {
            languageFiles.put("en_US".toLowerCase(), tempMessageFile);
        }
        serverLanguage = ConfigManager.configManager.getString("config-files.language", "en-US");
        if (!languageFiles.containsKey(serverLanguage)) {
            serverLanguage = "en_US";
        }
    }

    private String getMessage(Player player, String key, String... args) {
        String lang = getPlayerLanguage(player);

        YamlConfiguration config = languageFiles.getOrDefault(lang, tempMessageFile);
        String text = config.getString(key);


        if (text == null) {
            if (tempMessageFile.getString(key) != null) {
                text = tempMessageFile.getString(key);
                config.set(key, text);
                saveLanguageFile(lang, config);
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cAdded new language key: " + key + " for " + lang);
            } else {
                if (args.length == 0) {
                    text = "§cLanguage key not found: " + key;
                } else {
                    text = args[0];
                }
            }
            if (text == null) {
                text = "§cLanguage key not found: " + key;
            }
        }

        for (int i = 1 ; i < args.length ; i += 2) {
            String var = "{" + args[i] + "}";
            if (args[i + 1] == null) {
                text = text.replace(var, "");
            } else {
                text = text.replace(var, args[i + 1]);
            }
        }
        text = text.replace("{plugin_folder}", String.valueOf(UltimateShop.instance.getDataFolder()));
        return text;
    }

    private void saveLanguageFile(String lang, YamlConfiguration config) {
        File file = new File(UltimateShop.instance.getDataFolder(), "languages/" + lang + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStringText(CommandSender sender, String... args) {
        if (sender instanceof Player player) {
            sendStringText(player, args);
        } else {
            sendStringText(null, args);
        }
    }

    public void sendStringText(String... args) {
        sendStringText(null, args);
    }

    public void sendStringText(Player player, String... args) {
        if (args.length == 0) {
            return;
        }
        String text = getMessage(player, args[0]);

        // 替换变量 {key}
        for (int i = 1; i < args.length; i += 2) {
            String var = "{" + args[i] + "}";
            text = text.replace(var, i + 1 < args.length ? (args[i + 1] == null ? "" : args[i + 1]) : "");
        }

        if (!text.isEmpty()) {
            TextUtil.sendMessage(player, text);
        }
    }

    public String getStringText(Player player, String path, String... args) {
        return getMessage(player, path, args);
    }

    public List<String> getStringListText(Player player, String path) {
        String lang = getPlayerLanguage(player);
        YamlConfiguration config = languageFiles.getOrDefault(lang, tempMessageFile);

        List<String> list = config.getStringList(path);
        if (list.isEmpty()) {
            List<String> temp = tempMessageFile.getStringList(path);
            if (!temp.isEmpty()) {
                config.set(path, temp);
                saveLanguageFile(lang, config);
                return temp;
            } else {
                temp.add("§cLanguage key not found: " + path);
                return temp;
            }
        }
        return list;
    }
}