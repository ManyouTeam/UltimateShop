package cn.superiormc.ultimateshop.commands;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SubSellAllHand extends AbstractCommand {

    public SubSellAllHand() {
        this.id = "sellallhand";
        this.requiredPermission =  "ultimateshop." + id;
        this.onlyInGame = true;
        this.requiredArgLength = new Integer[]{1, 2};
    }

    @Override
    public void executeCommandInGame(String[] args, Player player) {
        ItemStack[] items = new ItemStack[]{player.getInventory().getItemInMainHand()};
        ObjectItem item = ShopHelper.getTargetItem(items, player);
        if (item == null || item.getSellPrice().empty) {
            LanguageManager.languageManager.sendStringText(player, "error.result-empty");
            return;
        }
        SellProductMethod.startSell(item, player, false, false, true, 1);
    }
}
