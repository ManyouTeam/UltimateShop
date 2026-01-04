package cn.superiormc.ultimateshop.objects.sellchests.holograms;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class AbstractHologram {

    public static final double Y_OFFSET = 5.5;

    public abstract void create(Player player, Chest chest);

    public abstract void update(Player player, Chest chest);

    public abstract void remove(Location location);

    public abstract boolean isAvailable();

    protected Location holoLocation(Chest chest) {
        return chest.getBlock().getLocation()
                .add(0.5, Y_OFFSET, 0.5);
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

    protected List<String> getLines(Player player, Chest chest) {
        PersistentDataContainer pdc = chest.getPersistentDataContainer();

        String sellChestID =
                pdc.get(SellChestManager.SELL_CHEST_ID, PersistentDataType.STRING);

        ObjectSellChest sellChest = ConfigManager.configManager.getSellChest(sellChestID);

        int usage = pdc.getOrDefault(
                SellChestManager.SELL_CHEST_TIMES,
                PersistentDataType.INTEGER,
                -1
        );

        return CommonUtil.modifyList(player, sellChest.getHolograms(),
                "id", sellChestID,
                "multiplier", String.valueOf(sellChest.getMultiplier()),
                "usage", sellChest.isInfinite() ? ConfigManager.configManager.getString(player, "placeholder.sell-stick.infinite") : String.valueOf(usage)
        );
    }
}
