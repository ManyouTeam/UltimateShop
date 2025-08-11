package cn.superiormc.ultimateshop.hooks.protection;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionSuperiorSkyblock2Hook extends AbstractProtectionHook {

    public ProtectionSuperiorSkyblock2Hook() {
        super("SuperiorSkyblock2");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        Island island = SuperiorSkyblockAPI.getGrid().getIslandAt(location);
        if (island == null) {
            return true;
        }
        return island.hasPermission(player, IslandPrivilege.getByName("CHEST_ACCESS"));
    }
}
