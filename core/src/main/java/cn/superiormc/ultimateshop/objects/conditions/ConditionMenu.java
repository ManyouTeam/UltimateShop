package cn.superiormc.ultimateshop.objects.conditions;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import org.bukkit.entity.Player;

public class ConditionMenu extends AbstractCheckCondition {

    public ConditionMenu() {
        super("menu");
        setRequiredArgs("menu");
    }

    @Override
    protected boolean onCheckCondition(ObjectSingleCondition singleCondition, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        ObjectMenu menu = ShopHelper.getOpeningMenu(player);
        if (menu == null) {
            return false;
        }
        return menu.getName().equalsIgnoreCase(singleCondition.getString("menu", player, amount));
    }
}
