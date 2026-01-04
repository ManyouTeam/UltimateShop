package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.impl.DecentHologramHook;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.util.*;

public class SellChestManager {

    public static final NamespacedKey KEY_IS_SELL_CHEST = new NamespacedKey(UltimateShop.instance, "sell_chest");

    public static final NamespacedKey KEY_OWNER = new NamespacedKey(UltimateShop.instance, "owner");

    public static final NamespacedKey SELL_CHEST_TIMES = new NamespacedKey(UltimateShop.instance, "sell_chest_usage");

    public static final NamespacedKey SELL_CHEST_ID = new NamespacedKey(UltimateShop.instance, "sell_chest_item");

    public static SellChestManager sellChestManager;

    private final Map<Long, Set<Location>> CACHE = new HashMap<>();

    private AbstractHologram hologram;

    public SellChestManager() {
        sellChestManager = this;
        initHologram();
    }

    private void initHologram() {
        if (ConfigManager.configManager.getBoolean("sell.sell-chest.hologram.enabled")) {
            if (CommonUtil.checkPluginLoad("DecentHolograms")) {
                hologram = new DecentHologramHook();
            }
        }
    }

    public void setHologram(AbstractHologram hologram) {
        this.hologram = hologram;
    }

    public void tick() {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {

                Set<Location> locations = CACHE.get(chunkKey(chunk));
                if (locations == null || locations.isEmpty()) {
                    continue;
                }

                Iterator<Location> it = locations.iterator();
                while (it.hasNext()) {
                    Location loc = it.next();

                    BlockState state = loc.getBlock().getState();
                    if (!(state instanceof Chest chest)) {
                        unregisterSellChest(loc);
                        it.remove();
                        continue;
                    }

                    sell(chest);
                }
            }
        }
    }

    private void sell(Chest chest) {
        Inventory inventory = chest.getInventory();
        if (inventory.isEmpty()) {
            return;
        }

        PersistentDataContainer pdc = chest.getPersistentDataContainer();

        String ownerStr = pdc.get(KEY_OWNER, PersistentDataType.STRING);
        String sellChestID = pdc.get(SELL_CHEST_ID, PersistentDataType.STRING);

        if (ownerStr == null || sellChestID == null) {
            return;
        }

        ObjectSellChest sellChest = ConfigManager.configManager.getSellChest(sellChestID);
        if (sellChest == null) {
            return;
        }

        Player player = Bukkit.getPlayer(UUID.fromString(ownerStr));
        if (player == null) {
            return;
        }

        if (!sellChest.getCondition().getAllBoolean(new ObjectThingRun(player))) {
            return;
        }

        int usage = 0;
        if (!sellChest.isInfinite()) {
            usage = pdc.getOrDefault(
                    SELL_CHEST_TIMES,
                    PersistentDataType.INTEGER,
                    sellChest.getUsageTimes()
            );

            if (usage <= 0) {
                unregisterSellChest(chest.getBlock().getLocation());
                pdc.remove(KEY_IS_SELL_CHEST);
                pdc.remove(SELL_CHEST_ID);
                pdc.remove(SELL_CHEST_TIMES);
                chest.update();
                return;
            }

            pdc.set(SELL_CHEST_TIMES, PersistentDataType.INTEGER, usage - 1);
        }

        Map<AbstractSingleThing, BigDecimal> result = ShopHelper.sellAll(
                player,
                inventory,
                sellChest.getMultiplier()
        );

        if (!result.isEmpty()) {
            if (!sellChest.isInfinite()) {
                if (usage - 1 <= 0) {
                    unregisterSellChest(chest.getBlock().getLocation());
                    pdc.remove(KEY_IS_SELL_CHEST);
                    pdc.remove(SELL_CHEST_ID);
                    pdc.remove(SELL_CHEST_TIMES);
                }
            } else {
                pdc.set(SELL_CHEST_TIMES, PersistentDataType.INTEGER, usage);
            }
            sellChest.getAction().runAllActions(new ObjectThingRun(player));
            chest.update();
        }

        if (hologram != null) {
            hologram.update(player, chest);
        }
    }

    public void handleChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        for (BlockState state : chunk.getTileEntities()) {
            if (!(state instanceof Chest chest)) {
                continue;
            }

            PersistentDataContainer pdc = chest.getPersistentDataContainer();
            if (!pdc.has(KEY_IS_SELL_CHEST, PersistentDataType.BYTE)) {
                continue;
            }

            String ownerStr = pdc.get(KEY_OWNER, PersistentDataType.STRING);
            if (ownerStr == null) {
                return;
            }

            Player player = Bukkit.getPlayer(UUID.fromString(ownerStr));
            if (player == null) {
                return;
            }

            add(player, chest);
        }
    }

    public void handleChunkUnload(ChunkUnloadEvent event) {
        CACHE.remove(chunkKey(event.getChunk()));
    }

    public void registerSellChest(Chest chest, Player owner, ObjectSellChest sellChest, int times) {
        PersistentDataContainer pdc = chest.getPersistentDataContainer();

        pdc.set(KEY_IS_SELL_CHEST, PersistentDataType.BYTE, (byte) 1);
        pdc.set(KEY_OWNER, PersistentDataType.STRING, owner.getUniqueId().toString());
        pdc.set(SELL_CHEST_ID, PersistentDataType.STRING, sellChest.getID());

        if (!sellChest.isInfinite()) {
            pdc.set(SELL_CHEST_TIMES, PersistentDataType.INTEGER, times);
        }

        chest.update();
        add(owner, chest);
    }

    public void unregisterSellChest(Location location) {
        Set<Location> set = CACHE.get(chunkKey(location));
        if (set != null) {
            set.remove(location);
            if (hologram != null) {
                hologram.remove(location);
            }
        }
    }

    private void add(Player player, Chest chest) {
        CACHE.computeIfAbsent(chunkKey(chest.getBlock().getLocation()), k -> new HashSet<>()).add(chest.getBlock().getLocation());
        if (hologram != null) {
            hologram.create(player, chest);
        }
    }

    private long chunkKey(Location loc) {
        return chunkKey(loc.getChunk());
    }

    private long chunkKey(Chunk chunk) {
        return (((long) chunk.getX()) << 32) ^ (chunk.getZ() & 0xffffffffL);
    }

    public ItemStack createSellChestItem(Chest chest) {

        PersistentDataContainer chestPdc = chest.getPersistentDataContainer();

        String sellChestID = chestPdc.get(SELL_CHEST_ID, PersistentDataType.STRING);
        if (sellChestID == null) {
            return null;
        }

        ObjectSellChest sellChest = ConfigManager.configManager.getSellChest(sellChestID);
        if (sellChest == null) {
            return null;
        }

        if (chestPdc.has(SELL_CHEST_TIMES, PersistentDataType.INTEGER)) {
            return null;
        }

        Integer times = chestPdc.get(SELL_CHEST_TIMES, PersistentDataType.INTEGER);
        if (times == null || times < 0) {
            return null;
        }

        return sellChest.getItemWithUsageTimes(1, times);
    }
}
