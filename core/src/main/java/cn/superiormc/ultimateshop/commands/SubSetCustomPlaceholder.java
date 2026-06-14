package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectCustomPlaceholder;

import java.util.ArrayList;
import java.util.List;

import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SubSetCustomPlaceholder extends AbstractCommand {

    public SubSetCustomPlaceholder() {
        this.id = "setcustomplaceholder";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{3, 4};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        setCustomPlaceholder(args, player);
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        setCustomPlaceholder(args, null);
    }

    private void setCustomPlaceholder(String[] args, Player player) {
        ObjectCustomPlaceholder placeholder = ConfigManager.configManager.getCustomPlaceholder(args[1]);
        if (placeholder == null) {
            LanguageManager.languageManager.sendStringText(player, "error.custom-placeholder-not-found", "placeholder", args[1]);
            return;
        }
        String value = args[2];
        if (placeholder.isNumber() && !CommonUtil.isNumberValue(value)) {
            LanguageManager.languageManager.sendStringText(player, "error.custom-placeholder-invalid-number", "value", value);
            return;
        }
        ObjectCache tempVal1;
        if (!placeholder.isPerPlayer()) {
            tempVal1 = CacheManager.cacheManager.serverCache;
        } else {
            Player changePlayer = player;
            if (args.length >= 4) {
                changePlayer = Bukkit.getPlayer(args[3]);
            }
            if (changePlayer == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.custom-placeholder-player-arg-required",
                        "placeholder",
                        args[1]);
                return;
            }
            tempVal1 = CacheManager.cacheManager.getObjectCache(changePlayer);
        }
        if (tempVal1 == null) {
            return;
        }
        tempVal1.setCustomPlaceholderCache(placeholder, value);
        LanguageManager.languageManager.sendStringText(player,
                "set-custom-placeholder",
                "placeholder",
                args[1],
                "value",
                value);
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectCustomPlaceholder tempVal2: ConfigManager.configManager.getCustomPlaceholders()) {
                    tempVal1.add(tempVal2.getID());
                }
                break;
            case 3:
                ObjectCustomPlaceholder tempVal3 = ConfigManager.configManager.getCustomPlaceholder(args[1]);
                if (tempVal3 == null) {
                    tempVal1.add(LanguageManager.languageManager.getStringText(player, "command-tab.unknown-custom-placeholder"));
                } else {
                    tempVal1.add(tempVal3.getDefaultValue());
                }
                break;
            case 4:
                ObjectCustomPlaceholder tempVal4 = ConfigManager.configManager.getCustomPlaceholder(args[1]);
                if (tempVal4 != null && tempVal4.isPerPlayer()) {
                    for (Player tempVal2 : Bukkit.getOnlinePlayers()) {
                        tempVal1.add(tempVal2.getName());
                    }
                }
                break;
        }
        return tempVal1;
    }
}
