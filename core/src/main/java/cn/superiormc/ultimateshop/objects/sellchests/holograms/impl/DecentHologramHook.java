package cn.superiormc.ultimateshop.objects.sellchests.holograms.impl;

import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class DecentHologramHook extends AbstractHologram {

    @Override
    public boolean isAvailable() {
        return CommonUtil.checkPluginLoad("DecentHolograms");
    }

    @Override
    public void create(Player player, Chest chest) {
        if (!isAvailable()) {
            return;
        }

        Location loc = holoLocation(chest);
        String id = holoId(chest);

        if (DHAPI.getHologram(id) != null) {
            return;
        }

        DHAPI.createHologram(id, loc, getLines(player, chest));
    }

    @Override
    public void update(Player player, Chest chest) {
        if (!isAvailable()) return;

        String id = holoId(chest);
        if (DHAPI.getHologram(id) == null) {
            create(player, chest);
            return;
        }

        DHAPI.setHologramLines(DHAPI.getHologram(id), getLines(player, chest));
    }

    @Override
    public void remove(Location location) {
        if (!isAvailable()) {
            return;
        }

        String id = holoId(location);
        DHAPI.removeHologram(id);
    }
}
