package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.methods.Product.BuyProductMethod;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.entity.Player;

public class ActionBuy extends AbstractRunAction {

    public ActionBuy() {
        super("buy");
        setRequiredArgs("shop", "item");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        singleAction.setLastTradeStatus(BuyProductMethod.startBuy(singleAction.getString("shop", player, amount),
                singleAction.getString("item", player, amount),
                player,
                true,
                false,
                singleAction.getInt("amount", 1)));
    }
}
