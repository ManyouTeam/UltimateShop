package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.GUI.OpenGUI;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionShopMenu extends AbstractRunAction {

    public ActionShopMenu() {
        super("shop_menu");
        setRequiredArgs("shop");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        OpenGUI.openShopGUI(player, ConfigManager.configManager.getShop(singleAction.getString("shop", player, amount)), false, true);
    }
}
