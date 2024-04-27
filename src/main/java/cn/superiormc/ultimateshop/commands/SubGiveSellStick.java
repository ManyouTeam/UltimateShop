package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.SellStickItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        switch (args.length) {
            // /shop givestick 物品名称 玩家名称 数量
            case 2: case 3:
                CommonUtil.giveOrDrop(givePlayer, SellStickItem.getExtraSlotItem(givePlayer, args[1], 1));
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
                CommonUtil.giveOrDrop(givePlayer, SellStickItem.getExtraSlotItem(givePlayer, args[1], Integer.parseInt(args[3])));
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
        switch (args.length) {
            // /shop givestick 物品名称 玩家名称 数量
            case 3:
                CommonUtil.giveOrDrop(givePlayer, SellStickItem.getExtraSlotItem(givePlayer, args[1], 1));
                LanguageManager.languageManager.sendStringText("give-sell-stick",
                        "player",
                        givePlayer.getName(),
                        "item",
                        args[1],
                        "amount",
                        "1");
                break;
            case 4:
                CommonUtil.giveOrDrop(givePlayer, SellStickItem.getExtraSlotItem(givePlayer, args[1], Integer.parseInt(args[3])));
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
}
