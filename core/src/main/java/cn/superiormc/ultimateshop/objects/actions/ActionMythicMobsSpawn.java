package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ActionMythicMobsSpawn extends AbstractRunAction {

    public ActionMythicMobsSpawn() {
        super("mythicmobs_spawn");
        setRequiredArgs("entity");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        String mobName = singleAction.getString("entity");
        String worldName = singleAction.getString("world");
        Location location;
        if (worldName == null) {
            location = player.getLocation();
        } else {
            World world = Bukkit.getWorld(worldName);
            location = new Location(world,
                    singleAction.getDouble("x"),
                    singleAction.getDouble("y"),
                    singleAction.getDouble("z"));

        }
        CommonUtil.summonMythicMobs(location, mobName, singleAction.getInt("level", 1));
    }
}
