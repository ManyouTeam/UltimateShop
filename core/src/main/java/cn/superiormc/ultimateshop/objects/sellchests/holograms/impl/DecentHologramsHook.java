package cn.superiormc.ultimateshop.objects.sellchests.holograms.impl;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DecentHologramsHook extends AbstractHologram {

    @Override
    public boolean isAvailable() {
        return CommonUtil.checkPluginLoad("DecentHolograms");
    }

    @Override
    public void create(Chest chest) {
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

        DHAPI.createHologram(id, loc, getLines(chest, sellChest));
    }

    @Override
    public void update(Chest chest) {
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
            create(chest);
            return;
        }

        DHAPI.setHologramLines(DHAPI.getHologram(id), getLines(chest, sellChest));
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
