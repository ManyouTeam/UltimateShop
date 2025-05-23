package cn.superiormc.ultimateshop.hooks.protection;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionResidenceHook extends AbstractProtectionHook {

    public ProtectionResidenceHook() {
        super("Residence");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        FlagPermissions perms = Residence.getInstance().getPermsByLocForPlayer(location, player);
        return perms.playerHas(player, Flags.container, false);
    }
}
