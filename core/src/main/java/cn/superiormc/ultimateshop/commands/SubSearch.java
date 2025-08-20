package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SubSearch extends AbstractCommand {

    public SubSearch() {
        this.id = "search";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
        ObjectItem item = ShopHelper.getTargetItem(items, player);
        if (item == null) {
            LanguageManager.languageManager.sendStringText(player, "error.result-empty");
            return;
        }
        LanguageManager.languageManager.sendStringText(player, "plugin.search",
                "item-name", item.getDisplayName(player),
                "item", item.getProduct(),
                "shop", item.getShop(),
                "buy-price", ShopHelper.getBuyPricesDisplay(items, player, 1),
                "sell-price", ShopHelper.getSellPricesDisplay(items, player, 1));
    }
}
