package cn.superiormc.ultimateshop.hooks.protection;

import com.plotsquared.bukkit.player.BukkitPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionPlotSquaredHook extends AbstractProtectionHook {

    public ProtectionPlotSquaredHook() {
        super("PlotSquared");
    }

    @Override
    public boolean canUse(Player player, Location location) {
        BukkitPlayer bukkitPlayer = BukkitUtil.adapt(player);
        Plot plot = bukkitPlayer.getCurrentPlot();
        if (plot != null) {
            return plot.isAdded(player.getUniqueId());
        }
        return true;
    }
}
