package cn.superiormc.ultimateshop.objects.sellchests.holograms.impl;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FancyHologramsHook extends AbstractHologram {

    @Override
    public boolean isAvailable() {
        return CommonUtil.checkPluginLoad("FancyHolograms");
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

        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        if (manager.getHologram(id).isPresent()) {
            update(chest);
            return;
        }

        TextHologramData data = new TextHologramData(id, loc);
        data.setText(getLines(chest, sellChest));
        data.setPersistent(false);

        Hologram hologram = manager.create(data);
        manager.addHologram(hologram);
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
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        Hologram hologram = manager.getHologram(id).orElse(null);

        if (hologram == null) {
            create(chest);
            return;
        }

        if (hologram.getData() instanceof TextHologramData data) {
            data.setLocation(holoLocation(chest, sellChest));
            data.setText(getLines(chest, sellChest));
            hologram.forceUpdate();
        }
    }

    @Override
    public void remove(Location location) {
        if (!isAvailable()) {
            return;
        }

        String id = holoId(location);
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        Hologram hologram = manager.getHologram(id).orElse(null);

        if (hologram != null) {
            manager.removeHologram(hologram);
        }
    }
}
