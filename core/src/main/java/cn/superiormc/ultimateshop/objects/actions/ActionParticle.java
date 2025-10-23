package cn.superiormc.ultimateshop.objects.actions;

import cn.superiormc.ultimateshop.managers.ErrorManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ActionParticle extends AbstractRunAction {

    public ActionParticle() {
        super("particle");
        setRequiredArgs("particle", "count", "offset-x", "offset-y", "offset-z", "speed");
    }

    @Override
    protected void onDoAction(ObjectSingleAction singleAction, ObjectThingRun thingRun) {
        Player player = thingRun.getPlayer();
        double amount = thingRun.getAmount();
        Location loc = player.getLocation().add(0, 1, 0); // 在玩家头顶播放

        // 读取参数
        String particleName = singleAction.getString("particle", player, amount);
        int count = singleAction.getInt("count");
        double offsetX = singleAction.getDouble("offset-x", player, amount);
        double offsetY = singleAction.getDouble("offset-y", player, amount);
        double offsetZ = singleAction.getDouble("offset-z", player, amount);
        double speed = singleAction.getDouble("speed", player, amount);

        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            player.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
        } catch (IllegalArgumentException e) {
            ErrorManager.errorManager.sendErrorMessage("§cInvalid particle name: " + particleName);
        }
    }
}