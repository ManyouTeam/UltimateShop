package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.inv.CommonGUI;
import cn.superiormc.ultimateshop.gui.inv.ShopGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.menus.MenuType;
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
        boolean bypass = args[args.length - 1].equals("-b") && player.hasPermission(requiredPermission + ".admin") && !UltimateShop.freeVersion;
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            CommonGUI.openGUI(player, args[1], bypass, false);
        } else {
            ShopGUI.openGUI(player, tempVal1, bypass, false);
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        boolean bypass = false;
        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            LanguageManager.languageManager.sendStringText
                    ("error.player-not-found",
                            "player",
                            args[2]);
            return;
        }
        if (args[args.length - 1].equals("-b") && !UltimateShop.freeVersion) {
            bypass = true;
        }
        ObjectShop tempVal1 = ConfigManager.configManager.getShop(args[1]);
        if (tempVal1 == null) {
            CommonGUI.openGUI(player, args[1], bypass, false);
        } else {
            ShopGUI.openGUI(player, tempVal1, bypass, false);
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
                for (ObjectMenu tempVal4 : ObjectMenu.commonMenus.values()) {
                    if (!tempVal4.getType().equals(MenuType.Common)) {
                        continue;
                    }
                    tempVal1.add(tempVal4.getName());
                }
                break;
        }
        return tempVal1;
    }
}
