package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.ObjectSellStick;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SubGiveSellStick extends AbstractCommand {

    public SubGiveSellStick() {
        this.id = "givesellstick";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 3, 4};
        this.requiredConsoleArgLength = new Integer[]{3, 4};
        this.premiumOnly = true;
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        Player givePlayer = player;
        if (args.length > 2) {
            givePlayer = Bukkit.getPlayer(args[2]);
            if (givePlayer == null) {
                LanguageManager.languageManager.sendStringText(player, "error.player-not-found", "player", args[2]);
                return;
            }
        }
        ObjectSellStick sellStick = ConfigManager.configManager.getSellStick(args[1]);
        if (sellStick == null) {
            LanguageManager.languageManager.sendStringText("error-item-not-found",
                    "item",
                    args[1]);
            return;
        }
        switch (args.length) {
            // /shop givestick 物品名称 玩家名称 数量
            case 2: case 3:
                CommonUtil.giveOrDrop(givePlayer, sellStick.getNewItem(givePlayer, 1));
                LanguageManager.languageManager.sendStringText(player,
                        "give-sell-stick",
                        "player",
                        givePlayer.getName(),
                        "item",
                        args[1],
                        "amount",
                        "1");
                break;
            case 4:
                CommonUtil.giveOrDrop(givePlayer, sellStick.getNewItem(givePlayer, Integer.parseInt(args[3])));
                LanguageManager.languageManager.sendStringText(player,
                        "give-sell-stick",
                        "player",
                        givePlayer.getName(),
                        "item",
                        args[1],
                        "amount",
                        args[3]);
                break;
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        Player givePlayer = Bukkit.getPlayer(args[2]);
        if (givePlayer == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[2]);
            return;
        }
        ObjectSellStick sellStick = ConfigManager.configManager.getSellStick(args[1]);
        if (sellStick == null) {
            LanguageManager.languageManager.sendStringText("error-item-not-found",
                    "item",
                    args[1]);
            return;
        }
        switch (args.length) {
            // /shop givestick 物品名称 玩家名称 数量
            case 3:
                CommonUtil.giveOrDrop(givePlayer, sellStick.getNewItem(givePlayer, 1));
                LanguageManager.languageManager.sendStringText("give-sell-stick",
                        "player",
                        givePlayer.getName(),
                        "item",
                        args[1],
                        "amount",
                        "1");
                break;
            case 4:
                CommonUtil.giveOrDrop(givePlayer, sellStick.getNewItem(givePlayer, Integer.parseInt(args[3])));
                LanguageManager.languageManager.sendStringText("give-sell-stick",
                        "player",
                        givePlayer.getName(),
                        "item",
                        args[1],
                        "amount",
                        args[3]);
                break;
        }
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        switch (args.length) {
            case 2:
                for (ObjectSellStick sellStick : ConfigManager.configManager.getSellSticks()) {
                    tempVal1.add(sellStick.getID());
                }
                break;
            case 3:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    tempVal1.add(player.getName());
                }
                break;
            case 4:
                tempVal1.add("1");
                tempVal1.add("10");
                tempVal1.add("64");
        }
        return tempVal1;
    }
}
