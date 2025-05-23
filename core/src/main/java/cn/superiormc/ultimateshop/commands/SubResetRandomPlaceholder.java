package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.caches.ObjectRandomPlaceholderCache;
import cn.superiormc.ultimateshop.objects.items.subobjects.ObjectRandomPlaceholder;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubResetRandomPlaceholder extends AbstractCommand {

    public SubResetRandomPlaceholder() {
        this.id = "resetrandomplaceholder";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.premiumOnly = true;
        this.requiredArgLength = new Integer[]{2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ObjectRandomPlaceholder placeholder = ConfigManager.configManager.getRandomPlaceholder(args[1]);
        if (placeholder == null) {
            LanguageManager.languageManager.sendStringText(player, "error.random-placeholder-not-found", "placeholder", args[1]);
            return;
        }
        ObjectRandomPlaceholderCache cache = ServerCache.serverCache.getRandomPlaceholderCache().get(placeholder);
        if (cache == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(placeholder);
            cache = ServerCache.serverCache.getRandomPlaceholderCache().get(placeholder);
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
        ObjectRandomPlaceholderCache cache = ServerCache.serverCache.getRandomPlaceholderCache().get(placeholder);
        if (cache == null) {
            CacheManager.cacheManager.serverCache.addRandomPlaceholderCache(placeholder);
            cache = ServerCache.serverCache.getRandomPlaceholderCache().get(placeholder);
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
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectRandomPlaceholder tempVal2: ConfigManager.configManager.getRandomPlaceholders()) {
                    tempVal1.add(tempVal2.getID());
                }
                break;
        }
        return tempVal1;
    }
}
