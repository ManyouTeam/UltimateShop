package cn.superiormc.ultimateshop.objects.sellchests.holograms.impl;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DecentHologramsHook extends AbstractHologram {

    @Override
    public boolean isAvailable() {
        return CommonUtil.checkPluginLoad("DecentHolograms");
    }

    @Override
    public void create(Player player, Chest chest) {
        if (!isAvailable()) {
            return;
        }

        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        String sellChestID = pdc.get(SellChestManager.SELL_CHEST_ID, PersistentDataType.STRING);

        ObjectSellChest sellChest = ConfigManager.configManager.getSellChest(sellChestID);

        Location loc = holoLocation(chest, sellChest);
        String id = holoId(chest);

        if (DHAPI.getHologram(id) != null) {
            return;
        }

        DHAPI.createHologram(id, loc, getLines(player, chest, sellChest));
    }

    @Override
    public void update(Player player, Chest chest) {
        if (!isAvailable()) {
            return;
        }

        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        String sellChestID = pdc.get(SellChestManager.SELL_CHEST_ID, PersistentDataType.STRING);

        ObjectSellChest sellChest = ConfigManager.configManager.getSellChest(sellChestID);
        if (sellChest == null) {
            return;
        }

        String id = holoId(chest);
        if (DHAPI.getHologram(id) == null) {
            create(player, chest);
            return;
        }

        DHAPI.setHologramLines(DHAPI.getHologram(id), getLines(player, chest, sellChest));
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
