package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.cache.ServerCache;
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
import java.util.Random;

public class SubSetRandomPlaceholder extends AbstractCommand {

    public SubSetRandomPlaceholder() {
        this.id = "setrandomplaceholder";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{2, 3, 4, 5};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ObjectRandomPlaceholder placeholder = ConfigManager.configManager.getRandomPlaceholder(args[1]);
        if (placeholder == null) {
            LanguageManager.languageManager.sendStringText(player, "error.random-placeholder-not-found", "placeholder", args[1]);
            return;
        }
        ServerCache tempVal1;
        if (args.length < 3 || args[args.length - 1].equals("global")) {
            tempVal1 = ServerCache.serverCache;
        } else {
            Player changePlayer = Bukkit.getPlayer(args[3]);
            if (changePlayer == null) {
                if (args.length == 4) {
                    changePlayer = player;
                } else {
                    LanguageManager.languageManager.sendStringText(player,
                            "error.player-not-found",
                            "player",
                            args[3]);
                    return;
                }
            }
            tempVal1 = CacheManager.cacheManager.getPlayerCache(changePlayer);
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
        if (args.length > 2) {
            String[] element = args[2].split("~");
            if (element.length == 1) {
                cache.setPlaceholder(CommonUtil.translateString(args[2]), false);
            } else {
                int min = Integer.parseInt(element[0]);
                int max = Integer.parseInt(element[1]);
                Random random = new Random();
                cache.setPlaceholder(CommonUtil.translateString(String.valueOf(random.nextInt(max - min + 1) + min)),
                        false);
            }
        } else {
            cache.setPlaceholder(false);
        }
        LanguageManager.languageManager.sendStringText(player,
                "set-random-placeholder",
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
        ServerCache tempVal1;
        if (args.length < 4 || args[args.length - 1].equals("global")) {
            tempVal1 = ServerCache.serverCache;
        } else {
            Player changePlayer = Bukkit.getPlayer(args[args.length - 1]);
            if (changePlayer == null) {
                LanguageManager.languageManager.sendStringText(
                        "error.player-not-found",
                        "player",
                        args[3]);
                return;
            }
            tempVal1 = CacheManager.cacheManager.getPlayerCache(changePlayer);
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
        if (args.length > 2) {
            String[] element = args[2].split("~");
            if (element.length == 1) {
                cache.setPlaceholder(CommonUtil.translateString(args[2]), false);
            } else {
                int min = Integer.parseInt(element[0]);
                int max = Integer.parseInt(element[1]);
                Random random = new Random();
                cache.setPlaceholder(CommonUtil.translateString(String.valueOf(random.nextInt(max - min + 1) + min)),
                        false);
            }
        } else {
            cache.setPlaceholder(false);
        }
        LanguageManager.languageManager.sendStringText("set-random-placeholder",
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
                ObjectRandomPlaceholder tempVal3 = ConfigManager.configManager.getRandomPlaceholder(args[1]);
                if (tempVal3 == null) {
                    tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.unknown-random-placeholder"));
                } else {
                    tempVal1.addAll(tempVal3.getConfigElements());
                }
                break;
            case 4:
                for (Player tempVal2 : Bukkit.getOnlinePlayers()) {
                    tempVal1.add(tempVal2.getName());
                }
                tempVal1.add("global");
                break;
        }
        return tempVal1;
    }
}
