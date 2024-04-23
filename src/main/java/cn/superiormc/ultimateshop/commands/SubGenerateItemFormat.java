package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubGenerateItemFormat extends AbstractCommand {

    public SubGenerateItemFormat() {
        this.id = "generateitemformat";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        YamlConfiguration itemConfig = new YamlConfiguration();
        Map<String, Object> tempVal2 = ItemUtil.debuildItem(player.getInventory().getItemInMainHand());
        for (String key : tempVal2.keySet()) {
            itemConfig.set(key, tempVal2.get(key));
        }
        String yaml = itemConfig.saveToString();
        Bukkit.getScheduler().runTaskAsynchronously(UltimateShop.instance,() -> {
            Path path = new File(UltimateShop.instance.getDataFolder(), "generated-item-format.yml").toPath();
            try {
                Files.write(path, yaml.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        LanguageManager.languageManager.sendStringText(player, "plugin.generated");
    }
}
