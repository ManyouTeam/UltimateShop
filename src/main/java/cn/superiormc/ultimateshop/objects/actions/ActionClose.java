package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionClose extends AbstractRunAction {

    public ActionClose() {
        super("close");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        if (UltimateShop.isFolia) {
            Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> player.closeInventory(), 2L);
        } else {
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> player.closeInventory(), 2L);
        }
    }
}
