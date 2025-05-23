package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.gui.inv.BuyMoreGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.objects.ObjectShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.entity.Player;

public class ActionBuyMoreMenu extends AbstractRunAction {

    public ActionBuyMoreMenu() {
        super("buy_more_menu");
        setRequiredArgs("shop", "item");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        ObjectShop shop = ConfigManager.configManager.getShop(singleAction.getString("shop", player, amount));
        if (shop == null) {
            return;
        }
        ObjectItem item = shop.getProduct(singleAction.getString("item", player, amount));
        if (item != null) {
            BuyMoreGUI.openGUI(player, item);
        }
    }
}
