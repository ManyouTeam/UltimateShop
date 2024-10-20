package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.managers.ItemManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SubGiveSaveItem extends AbstractCommand {


    public SubGiveSaveItem() {
        this.id = "givesaveitem";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = false;
        this.requiredArgLength = new Integer[]{2, 3, 4};
    }

    /* Usage:

    /shop giveesaveitem <itemID> <amount>

     */
    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ItemStack item = ItemManager.itemManager.getItemByKey(args[1]);
        if (item == null) {
            LanguageManager.languageManager.sendStringText(player, "error.save-item-not-found", "item", args[1]);
            return;
        }
        Player target = null;
        if (args.length > 2) {
            target = Bukkit.getPlayer(args[2]);
        }
        if (target == null) {
            int amount = 1;
            if (args.length == 3) {
                amount = Integer.parseInt(args[2]);
            }
            item.setAmount(item.getAmount() * amount);
            CommonUtil.giveOrDrop(player, item);
            LanguageManager.languageManager.sendStringText(player, "give-save-item", "player", player.getName(),
                    "item", args[1], "amount", String.valueOf(amount));
        } else {
            int amount = 1;
            if (args.length == 4) {
                amount = Integer.parseInt(args[3]);
            }
            item.setAmount(item.getAmount() * amount);
            CommonUtil.giveOrDrop(target, item);
            LanguageManager.languageManager.sendStringText(player, "give-save-item", "player", target.getName(),
                    "item", args[1], "amount", String.valueOf(amount));
        }
    }

    @Override
    public void executeCommandInConsole(String[] args) {
        ItemStack item = ItemManager.itemManager.getItemByKey(args[1]);
        if (item == null) {
            LanguageManager.languageManager.sendStringText("error.save-item-not-found", "item", args[1]);
            return;
        }
        if (args.length < 3) {
            LanguageManager.languageManager.sendStringText("error.args");
            return;
        }
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            LanguageManager.languageManager.sendStringText("error.player-not-found", "player", args[2]);
        } else {
            int amount = 1;
            if (args.length == 4) {
                amount = Integer.parseInt(args[3]);
            }
            item.setAmount(item.getAmount() * amount);
            CommonUtil.giveOrDrop(target, item);
            LanguageManager.languageManager.sendStringText("give-save-item", "player", target.getName(),
                    "item", args[1], "amount", String.valueOf(amount));
        }
    }

    @Override
    public List<String> getTabResult(String[] args) {
        List<String> tempVal1 = new ArrayList<>();
        int length = args.length;
        if (length == 2) {
            tempVal1.addAll(ItemManager.itemManager.getSavedItemMap().keySet());
        } else if (length == 3) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                tempVal1.add(player.getName());
            }
        } else if (length == 4) {
            tempVal1.add("[amount]");
        }
        return tempVal1;
    }
}
