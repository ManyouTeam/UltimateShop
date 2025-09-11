package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LanguageManager {

    public static LanguageManager languageManager;

    private YamlConfiguration messageFile;

    private YamlConfiguration tempMessageFile;

    private File file;

    private File tempFile;

    public LanguageManager() {
        languageManager = this;
        initLanguage();
    }

    private void initLanguage() {
        this.file = new File(UltimateShop.instance.getDataFolder() + "/languages/" + ConfigManager.configManager.getStringOrDefault("language", "config-files.language", "en_US") + ".yml");
        if (!file.exists()){
            this.file = new File(UltimateShop.instance.getDataFolder(), "message.yml");
            if (!file.exists()) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cWe can not found your message file, " +
                        "please try restart your server!");
            }
        }
        else {
            this.messageFile = YamlConfiguration.loadConfiguration(file);
        }
        InputStream is = UltimateShop.instance.getResource("languages/en_US.yml");
        if (is == null) {
            return;
        }
        this.tempFile = new File(UltimateShop.instance.getDataFolder(), "tempMessage.yml");
        try {
            Files.copy(is, tempFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tempMessageFile = YamlConfiguration.loadConfiguration(tempFile);
        if (messageFile == null) {
            messageFile = tempMessageFile;
        }
        this.tempFile.delete();
    }

    public void sendStringText(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            sendStringText((Player) sender, args);
        } else {
            sendStringText(args);
        }
    }

    public void sendStringText(String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cCan not found language key: " + args[0] + "!");
                return;
            } else {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        if (!text.isEmpty()) {
            UltimateShop.methodUtil.sendMessage(null, text);
        }
    }

    public void sendStringText(Player player, String... args) {
        String text = this.messageFile.getString(args[0]);
        if (text == null) {
            if (this.tempMessageFile.getString(args[0]) == null) {
                player.sendMessage(TextUtil.pluginPrefix() + " §cCan not found language key: " + args[0] + "!");
                return;
            } else {
                UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                        "new language key and it's default value: " + args[0] + "!");
                text = this.tempMessageFile.getString(args[0]);
                messageFile.set(args[0], text);
                try {
                    messageFile.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        if (!text.isEmpty()) {
            UltimateShop.methodUtil.sendMessage(player, text);
        }
    }

    public String getStringText(String path) {
        if (this.messageFile.getString(path) == null) {
            if (this.tempMessageFile.getString(path) == null) {
                return "§cCan not found language key: " + path + "!";
            }
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                    "new language key and it's default value: " + path + "!");
            messageFile.set(path, this.tempMessageFile.getString(path));
            try {
                messageFile.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this.tempMessageFile.getString(path);
        }
        return this.messageFile.getString(path);
    }

    public List<String> getStringListText(String path) {
        if (this.messageFile.getStringList(path).isEmpty()) {
            List<String> tempVal1 = new ArrayList<>();
            if (this.tempMessageFile.getString(path) == null) {
                tempVal1.add("§cCan not found language key: " + path + "!");
                return tempVal1;
            }
            UltimateShop.methodUtil.sendMessage(null, TextUtil.pluginPrefix() + " §cUpdated your language file, added " +
                    "new language key and it's default value: " + path + "!");
            messageFile.set(path, this.tempMessageFile.getStringList(path));
            try {
                messageFile.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this.tempMessageFile.getStringList(path);
        }
        return this.messageFile.getStringList(path);
    }

}
