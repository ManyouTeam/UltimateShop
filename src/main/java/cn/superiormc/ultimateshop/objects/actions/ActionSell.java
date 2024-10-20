package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionSell extends AbstractRunAction {

    public ActionSell() {
        super("sell");
        setRequiredArgs("shop", "item");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        singleAction.setLastTradeStatus(SellProductMethod.startSell(singleAction.getString("shop", player, amount),
                singleAction.getString("item", player, amount),
                player,
                true,
                false,
                singleAction.getBoolean("sell-all", false),
                singleAction.getInt("amount", 1)));
    }
}
