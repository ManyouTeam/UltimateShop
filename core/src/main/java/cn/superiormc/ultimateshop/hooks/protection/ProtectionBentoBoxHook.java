package cn.superiormc.ultimateshop.hooks.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

public class ProtectionBentoBoxHook extends AbstractProtectionHook {

    public ProtectionBentoBoxHook() {
        super("Towny");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        Island island = BentoBox.getInstance().getIslandsManager().getIslandAt(location).orElse(null);
        if (island != null) {
            return island.isAllowed(User.getInstance(player), Flags.CONTAINER);
        }
        return true;
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        Island island = BentoBox.getInstance().getIslandsManager().getIslandAt(location).orElse(null);
        if (island != null) {
            return island.isAllowed(User.getInstance(player), Flags.BREAK_BLOCKS);
        }
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        Island island = BentoBox.getInstance().getIslandsManager().getIslandAt(location).orElse(null);
        if (island != null) {
            return island.isAllowed(User.getInstance(player), Flags.PLACE_BLOCKS);
        }
        return true;
    }

}
