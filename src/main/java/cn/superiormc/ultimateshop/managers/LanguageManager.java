package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LanguageManager {

    public static LanguageManager languageManager;

    private Configuration messageFile;

    public LanguageManager() {
        languageManager = this;
        initLanguage();
    }

    private void initLanguage() {
        File file = new File(UltimateShop.instance.getDataFolder(), "message.yml");
        if (!file.exists()){
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cWe can not found your message file, please try restart your server!");
        }
        else {
            this.messageFile = YamlConfiguration.loadConfiguration(file);
        }
    }

    public void sendStringText(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            sendStringText((Player) sender, args);
        }
        else {
            sendStringText(args);
        }
    }

    public void sendStringText(String... args) {
        if (this.messageFile.getString(args[0]) == null) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found language key: " + args[0] + "!");
        }
        else {
            String text = this.messageFile.getString(args[0]);
            for (int i = 1 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    text = text.replace(var, "");
                }
                else {
                    text = text.replace(var, args[i + 1]);
                }
            }
            if (text.length() != 0) {
                Bukkit.getConsoleSender().sendMessage(TextUtil.parse(text));
            }
        }
    }

    public void sendStringText(Player player, String... args) {
        if (this.messageFile.getString(args[0]) == null) {
            player.sendMessage("§x§9§8§F§B§9§8[UltimateShop] §cCan not found language key: " + args[0] + "!");
        }
        else {
            String text = this.messageFile.getString(args[0]);
            for (int i = 1 ; i < args.length ; i += 2) {
                String var = "{" + args[i] + "}";
                if (args[i + 1] == null) {
                    text = text.replace(var, "");
                }
                else {
                    text = text.replace(var, args[i + 1]);
                }
            }
            if (text.length() != 0) {
                player.sendMessage(TextUtil.parse(text, player));
            }
        }
    }

    public String getStringText(String path) {
        if (this.messageFile.getString(path) == null) {
            return "§cCan not found language key: " + path + "!";
        }
        return this.messageFile.getString(path);
    }

    public List<String> getStringListText(String path) {
        if (this.messageFile.getStringList(path).isEmpty()) {
            List<String> tempVal1 = new ArrayList<>();
            tempVal1.add("§cCan not found language key: " + path + "!");
            return tempVal1;
        }
        return this.messageFile.getStringList(path);
    }

}
