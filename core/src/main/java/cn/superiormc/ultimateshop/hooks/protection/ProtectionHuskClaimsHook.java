package cn.superiormc.ultimateshop.hooks.protection;

import net.william278.huskclaims.api.BukkitHuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionHuskClaimsHook extends AbstractProtectionHook {

    public BukkitHuskClaimsAPI api = BukkitHuskClaimsAPI.getInstance();

    public ProtectionHuskClaimsHook() {
        super("HuskClaims");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        return api.isOperationAllowed(api.getOnlineUser(player.getUniqueId()), OperationType.CONTAINER_OPEN, api.getPosition(location));
    }
}
