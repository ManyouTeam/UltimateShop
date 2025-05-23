package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ActionTeleport extends AbstractRunAction {

    public ActionTeleport() {
        super("teleport");
        setRequiredArgs("world", "x", "y", "z");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        Location loc = new Location(Bukkit.getWorld(singleAction.getString("world")),
                    singleAction.getDouble("x"),
                    singleAction.getDouble("y"),
                    singleAction.getDouble("z"),
                    singleAction.getInt("yaw", (int) player.getLocation().getYaw()),
                    singleAction.getInt("pitch", (int) player.getLocation().getPitch()));
        UltimateShop.methodUtil.playerTeleport(player, loc);
    }
}
