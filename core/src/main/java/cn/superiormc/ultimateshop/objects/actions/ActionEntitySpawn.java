package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ActionEntitySpawn extends AbstractRunAction {

    public ActionEntitySpawn() {
        super("entity_spawn");
        setRequiredArgs("entity");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        EntityType entity = EntityType.valueOf(singleAction.getString("entity").toUpperCase());
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
        UltimateShop.methodUtil.spawnEntity(location, entity);
    }
}
