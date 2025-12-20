package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.objects.caches.ObjectCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubResetRandomPlaceholder extends AbstractCommand {

    public SubResetRandomPlaceholder() {
        this.id = "resetrandomplaceholder";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{2, 3};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ObjectRandomPlaceholder placeholder = ConfigManager.configManager.getRandomPlaceholder(args[1]);
        if (placeholder == null) {
            LanguageManager.languageManager.sendStringText(player, "error.random-placeholder-not-found", "placeholder", args[1]);
            return;
        }
        ObjectCache tempVal1;
        if (args.length < 3 || args[2].equals("global")) {
            tempVal1 = CacheManager.cacheManager.serverCache;
        } else {
            Player changePlayer = Bukkit.getPlayer(args[2]);
            if (changePlayer == null) {
                LanguageManager.languageManager.sendStringText(player,
                        "error.player-not-found",
                        "player",
                        args[2]);
                return;
            }
            tempVal1 = CacheManager.cacheManager.getObjectCache(changePlayer);
        }
        ObjectRandomPlaceholderCache cache = tempVal1.getRandomPlaceholderCache().get(placeholder);
        if (cache == null) {
            tempVal1.addRandomPlaceholderCache(placeholder);
            cache = tempVal1.getRandomPlaceholderCache().get(placeholder);
        }
        if (cache == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.random-placeholder-player-arg-required",
                    "placeholder",
                    args[1]);
            return;
        }
        cache.removeRefreshDoneTime();
        cache.setRefreshTime();
        LanguageManager.languageManager.sendStringText(player,
                "reset-random-placeholder",
                "placeholder",
                args[1],
                "value",
                CommonUtil.translateStringList(cache.getNowValue()));
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        ObjectRandomPlaceholder placeholder = ConfigManager.configManager.getRandomPlaceholder(args[1]);
        if (placeholder == null) {
            LanguageManager.languageManager.sendStringText("error.random-placeholder-not-found", "placeholder", args[1]);
            return;
        }
        ObjectCache tempVal1;
        if (args.length < 3 || args[2].equals("global")) {
            tempVal1 = CacheManager.cacheManager.serverCache;
        } else {
            Player changePlayer = Bukkit.getPlayer(args[2]);
            if (changePlayer == null) {
                LanguageManager.languageManager.sendStringText(
                        "error.player-not-found",
                        "player",
                        args[2]);
                return;
            }
            tempVal1 = CacheManager.cacheManager.getObjectCache(changePlayer);
        }
        ObjectRandomPlaceholderCache cache = tempVal1.getRandomPlaceholderCache().get(placeholder);
        if (cache == null) {
            tempVal1.addRandomPlaceholderCache(placeholder);
            cache = tempVal1.getRandomPlaceholderCache().get(placeholder);
        }
        if (cache == null) {
            LanguageManager.languageManager.sendStringText(
                    "error.random-placeholder-player-arg-required",
                    "placeholder",
                    args[1]);
            return;
        }
        cache.removeRefreshDoneTime();
        cache.setRefreshTime();
        LanguageManager.languageManager.sendStringText("reset-random-placeholder",
                "placeholder",
                args[1],
                "value",
                CommonUtil.translateStringList(cache.getNowValue()));
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectRandomPlaceholder tempVal2: ConfigManager.configManager.getRandomPlaceholders()) {
                    tempVal1.add(tempVal2.getID());
                }
                break;
            case 3:
                for (Player tempVal2 : Bukkit.getOnlinePlayers()) {
                    tempVal1.add(tempVal2.getName());
                }
                tempVal1.add("global");
                break;
        }
        return tempVal1;
    }
}
