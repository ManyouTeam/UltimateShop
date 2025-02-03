package cn.superiormc.ultimateshop.hooks.protection;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionGriefPreventionHook extends AbstractProtectionHook {

    public ProtectionGriefPreventionHook() {
        super("GriefPrevention");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        if (claim == null || playerData.ignoreClaims) {
            return true;
        }
        return claim.checkPermission(player, ClaimPermission.Inventory, null) == null;
    }
}
