package cn.superiormc.ultimateshop.hooks.protection;

import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ProtectionTownyHook extends AbstractProtectionHook {

    public ProtectionTownyHook() {
        super("Towny");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        Block block = location.getBlock();
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.SWITCH);
    }
}
