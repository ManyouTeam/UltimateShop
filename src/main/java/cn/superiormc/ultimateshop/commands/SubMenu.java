package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubMenu extends AbstractCommand {

    public SubMenu() {
        this.id = "menu";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 3, 4};
        this.requiredConsoleArgLength = new Integer[]{3, 4, 5};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        boolean bypassBedrockCheck = args[args.length - 1].equals("-b");
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            if (ObjectMenu.buyMoreMenuNames.contains(args[1])) {
                LanguageManager.languageManager.sendStringText(player, "error.buy-more-menu-direct-open");
                return;
            }
            OpenGUI.openCommonGUI(player, args[1], bypassBedrockCheck, false);
        }
        else {
            OpenGUI.openShopGUI(player, tempVal1, bypassBedrockCheck, false);

        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        boolean bypassBedrockCheck = false;
        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            LanguageManager.languageManager.sendStringText
                    ("error.player-not-found",
                            "player",
                            args[2]);
            return;
        }
        if (args[args.length - 1].equals("-b")) {
            bypassBedrockCheck = true;
        }
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            if (ObjectMenu.buyMoreMenuNames.contains(args[1])) {
                LanguageManager.languageManager.sendStringText("error.buy-more-menu-direct-open");
                return;
            }
            OpenGUI.openCommonGUI(player, args[1], bypassBedrockCheck, false);
        }
        else {
            OpenGUI.openShopGUI(player, tempVal1, bypassBedrockCheck, false);
        }
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectShop tempVal2: ConfigManager.configManager.getShops()) {
                    tempVal1.add(tempVal2.getShopName());
                }
                for (String tempVal4 : ObjectMenu.commonMenus.keySet()) {
                    if (ObjectMenu.buyMoreMenuNames.contains(tempVal4)) {
                        continue;
                    }
                    tempVal1.add(tempVal4);
                }
                break;
        }
        return tempVal1;
    }
}
