package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.cache.ServerCache;
import cn.superiormc.ultimateshop.managers.CacheManager;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubSetSellTimes extends AbstractCommand {

    public SubSetSellTimes() {
        this.id = "setselltimes";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{4, 5};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText("error.shop-not-found",
                    "shop",
                    args[1]);
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProduct(args[2]);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player, "error.product-not-found",
                    "product",
                    args[2]);
            return;
        }
        ServerCache tempVal3;
        if (args[3].equals("global")) {
            tempVal3 = ServerCache.serverCache;
        }
        else {
            Player changePlayer = Bukkit.getPlayer(args[3]);
            if (changePlayer == null) {
                LanguageManager.languageManager.sendStringText(player,
                                "error.player-not-found",
                                "player",
                                args[3]);
                return;
            }
            tempVal3 = CacheManager.cacheManager.getPlayerCache(changePlayer);
        }
        if (tempVal3 == null) {
            LanguageManager.languageManager.sendStringText(player,
                            "error.player-not-found",
                            "player",
                            args[3]);
            return;
        }
        ObjectUseTimesCache tempVal4 = tempVal3.getUseTimesCache().get(tempVal2);
        switch (args.length) {
            case 4:
                if (tempVal4 == null) {
                    tempVal3.setUseTimesCache(tempVal1.getShopName(),
                            tempVal2.getProduct(),
                            0,
                            0,
                            null,
                            null,
                            null,
                            null);
                } else {
                    tempVal4.setSellUseTimes(0);
                }
                LanguageManager.languageManager.sendStringText(player,
                        "set-times",
                        "player",
                        args[3],
                        "item",
                        args[2],
                        "times",
                        "0");
                break;
            case 5:
                if (tempVal4 == null) {
                    tempVal3.setUseTimesCache(tempVal1.getShopName(),
                            tempVal2.getProduct(),
                            0,
                            Integer.parseInt(args[4]),
                            null,
                            null,
                            null,
                            null);
                } else {
                    tempVal4.setSellUseTimes(Integer.parseInt(args[4]));
                }
                LanguageManager.languageManager.sendStringText(player,
                        "set-times",
                        "player",
                        args[3],
                        "item",
                        args[2],
                        "times",
                        args[4]);
                break;
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText("error.shop-not-found",
                    "shop",
                    args[1]);
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProduct(args[2]);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText("error.product-not-found",
                    "product",
                    args[2]);
            return;
        }
        ServerCache tempVal3;
        if (args[3].equals("global")) {
            tempVal3 = ServerCache.serverCache;
        }
        else {
            Player changePlayer = Bukkit.getPlayer(args[3]);
            if (changePlayer == null) {
                LanguageManager.languageManager.sendStringText("error.player-not-found",
                        "player",
                        args[3]);
                return;
            }
            tempVal3 = CacheManager.cacheManager.getPlayerCache(changePlayer);
        }
        if (tempVal3 == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found",
                    "player",
                    args[3]);
            return;
        }
        ObjectUseTimesCache tempVal4 = tempVal3.getUseTimesCache().get(tempVal2);
        switch (args.length) {
            case 4:
                if (tempVal4 == null) {
                    tempVal3.setUseTimesCache(tempVal1.getShopName(),
                            tempVal2.getProduct(),
                            0,
                            0,
                            null,
                            null,
                            null,
                            null);
                } else {
                    tempVal4.setSellUseTimes(0);
                }
                LanguageManager.languageManager.sendStringText("set-times",
                        "player",
                        args[3],
                        "item",
                        args[2],
                        "times",
                        "0");
                break;
            case 5:
                if (tempVal4 == null) {
                    tempVal3.setUseTimesCache(tempVal1.getShopName(),
                            tempVal2.getProduct(),
                            0,
                            Integer.parseInt(args[4]),
                            null,
                            null,
                            null,
                            null);
                } else {
                    tempVal4.setSellUseTimes(Integer.parseInt(args[4]));
                }
                LanguageManager.languageManager.sendStringText("set-times",
                        "player",
                        args[3],
                        "item",
                        args[2],
                        "times",
                        args[4]);
                break;
        }
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectShop tempVal2: ConfigManager.configManager.getShopList()) {
                    tempVal1.add(tempVal2.getShopName());
                }
                break;
            case 3:
                ObjectShop tempVal3 = ConfigManager.configManager.getShop(args[1]);
                if (tempVal3 == null) {
                    tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.unknown-shop"));
                    break;
                }
                for (ObjectItem tempVal4 : tempVal3.getProductList()) {
                    tempVal1.add(tempVal4.getItemConfig().getName());
                }
                break;
            case 4:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tempVal1.add(player.getName());
                }
                tempVal1.add("global");
                break;
            case 5:
                tempVal1.add("0");
                tempVal1.add("5");
                break;
        }
        return tempVal1;
    }
}
