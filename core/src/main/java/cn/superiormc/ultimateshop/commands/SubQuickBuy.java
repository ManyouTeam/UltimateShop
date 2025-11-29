package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubQuickBuy extends AbstractCommand {

    public SubQuickBuy() {
        this.id = "quickbuy";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{3, 4};
        this.requiredConsoleArgLength = new Integer[]{4, 5};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.shop-not-found",
                    "shop",
                    args[1]);
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProductNotHidden(player, args[2]);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    args[2]);
            return;
        }
        switch (args.length) {
            case 3:
                BuyProductMethod.startBuy(tempVal2, player, true);
                break;
            case 4:
                BuyProductMethod.startBuy(tempVal2,
                        player,
                        true,
                        false,
                        Integer.parseInt(args[3]));
                break;
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player player = Bukkit.getPlayer(args[args.length - 1]);
        if (player == null) {
            LanguageManager.languageManager.sendStringText
                    ("error.player-not-found", "player",
                            args[args.length - 1]);
            return;
        }
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.shop-not-found",
                    "shop",
                    args[1]);
            return;
        }
        ObjectItem tempVal2 = tempVal1.getProductNotHidden(player, args[2]);
        if (tempVal2 == null) {
            LanguageManager.languageManager.sendStringText(player,
                    "error.product-not-found",
                    "product",
                    args[2]);
            return;
        }
        switch (args.length) {
            case 4:
                BuyProductMethod.startBuy(tempVal2, player, true);
                break;
            case 5:
                BuyProductMethod.startBuy(tempVal2,
                        player,
                        true,
                        false,
                        Integer.parseInt(args[3]));
                break;
        }
    }

    @Override
    public List<String> getTabResult(String[] args, Player player) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectShop tempVal2: ConfigManager.configManager.getShops()) {
                    tempVal1.add(tempVal2.getShopName());
                }
                break;
            case 3:
                ObjectShop tempVal3 = ConfigManager.configManager.getShop(args[1]);
                if (tempVal3 == null) {
                    tempVal1.add(LanguageManager.languageManager.getStringText("command-tab.unknown-shop"));
                    break;
                }
                for (ObjectItem tempVal4 : tempVal3.getProductListNotHidden(player)) {
                    tempVal1.add(tempVal4.getItemConfig().getName());
                }
                break;
            case 4:
                tempVal1.add("1");
                tempVal1.add("5");
                break;
        }
        return tempVal1;
    }
}
