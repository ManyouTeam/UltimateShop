package cn.superiormc.ultimateshop.objects.sellchests.holograms;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class AbstractHologram {

    public abstract void create(Chest chest);

    public abstract void update(Chest chest);

    public abstract void remove(Location location);

    public abstract boolean isAvailable();

    protected Location holoLocation(Chest chest, ObjectSellChest sellChest) {
        return chest.getBlock().getLocation().add(0.5, sellChest.getYOffset(), 0.5);
    }

    protected String holoId(Chest chest) {
        Location l = chest.getBlock().getLocation();
        return "sellchest_" + l.getWorld().getName()
                + "_" + l.getBlockX()
                + "_" + l.getBlockY()
                + "_" + l.getBlockZ();
    }

    protected String holoId(Location location) {
        Location l = location.getBlock().getLocation();
        return "sellchest_" + l.getWorld().getName()
                + "_" + l.getBlockX()
                + "_" + l.getBlockY()
                + "_" + l.getBlockZ();
    }

    protected List<String> getLines(Chest chest, ObjectSellChest sellChest) {
        PersistentDataContainer pdc = chest.getPersistentDataContainer();

        int usage = pdc.getOrDefault(
                SellChestManager.SELL_CHEST_TIMES,
                PersistentDataType.INTEGER,
                -1
        );

        return CommonUtil.modifyList(null, sellChest.getHolograms(),
                "id", sellChest.getID(),
                "multiplier", String.valueOf(sellChest.getMultiplier()),
                "price", getPrice(chest),
                "usage", sellChest.isInfinite() ? ConfigManager.configManager.getStringWithLang(null, "placeholder.sell-stick.infinite") : String.valueOf(usage)
        );
    }

    protected String getPrice(Chest chest) {
        PersistentDataContainer pdc = chest.getPersistentDataContainer();

        if (!pdc.has(SellChestManager.SELL_CHEST_PRICE)) {
            return ConfigManager.configManager.getStringWithLang(null, "sell.sell-chest.price-empty");
        }

        return pdc.get(SellChestManager.SELL_CHEST_PRICE, PersistentDataType.STRING);
    }
}
