package cn.superiormc.ultimateshop.objects.sellchests.holograms.impl;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CMIHook extends AbstractHologram {

    @Override
    public boolean isAvailable() {
        return CommonUtil.checkPluginLoad("CMI");
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

        CMIHologram holo = new CMIHologram(id, loc);
        holo.setLines(getLines(chest, sellChest));
        CMI.getInstance().getHologramManager().addHologram(holo);
        holo.update();
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
        CMIHologram holo = CMI.getInstance().getHologramManager().getHolograms().get(id);

        if (holo != null) {
            holo.setLines(getLines(chest, sellChest));
            holo.update();
        }
    }

    @Override
    public void remove(Location location) {
        if (!isAvailable()) {
            return;
        }

        String id = holoId(location);
        CMIHologram holo = CMI.getInstance().getHologramManager().getHolograms().get(id);

        if (holo != null) {
            holo.remove();
        }
    }
}
