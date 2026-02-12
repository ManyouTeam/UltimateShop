package cn.superiormc.ultimateshop.managers;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.AbstractHologram;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.impl.CMIHook;
import cn.superiormc.ultimateshop.objects.sellchests.holograms.impl.DecentHologramsHook;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SellChestManager {

    public static final NamespacedKey KEY_IS_SELL_CHEST = new NamespacedKey(UltimateShop.instance, "sell_chest");

    public static final NamespacedKey KEY_OWNER = new NamespacedKey(UltimateShop.instance, "owner");

    public static final NamespacedKey SELL_CHEST_TIMES = new NamespacedKey(UltimateShop.instance, "sell_chest_usage");

    public static final NamespacedKey SELL_CHEST_ID = new NamespacedKey(UltimateShop.instance, "sell_chest_id");

    public static final NamespacedKey SELL_CHEST_PRICE = new NamespacedKey(UltimateShop.instance, "sell_chest_price");

    public static final NamespacedKey KEY_CHUNK_CHESTS = new NamespacedKey(UltimateShop.instance, "chunk_chests_list");

    public static SellChestManager sellChestManager;

    private final Map<Chunk, Set<Location>> chestLocations = new ConcurrentHashMap<>();

    private int currentBatchIndex = 0;

    private int activeBatchCount = 0;

    private final List<Location> cycleLocations = new ArrayList<>();

    private AbstractHologram hologram;

    public SellChestManager() {
        sellChestManager = this;
        initHologram();
    }

    private void initHologram() {
        if (ConfigManager.configManager.getBoolean("sell.sell-chest.hologram.enabled")) {
            String plugin = ConfigManager.configManager.getString("sell.sell-chest.hologram.plugin", "DecentHolograms");
            if (plugin.equalsIgnoreCase("DecentHolograms") && CommonUtil.checkPluginLoad("DecentHolograms")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into DecentHolograms...");
                hologram = new DecentHologramsHook();
            } else if (plugin.equalsIgnoreCase("CMI") && CommonUtil.checkPluginLoad("CMI")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fHooking into CMI...");
                hologram = new CMIHook();
            }
        }
    }

    public void setHologram(AbstractHologram hologram) {
        this.hologram = hologram;
    }

    public void tick() {
        int batchCount = ConfigManager.configManager.getInt("sell.sell-chest.batch-count", 5);
        if (batchCount <= 0) {
            batchCount = 1;
        }

        if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSell chest tick, batch=" + currentBatchIndex);
        }

        if (currentBatchIndex == 0) {
            cycleLocations.clear();

            for (Set<Location> locations : chestLocations.values()) {
                if (locations == null || locations.isEmpty()) {
                    continue;
                }
                cycleLocations.addAll(locations);
            }

            if (cycleLocations.isEmpty()) {
                return;
            }

            activeBatchCount = Math.min(batchCount, cycleLocations.size());

            if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
                TextUtil.sendMessage(null,
                        TextUtil.pluginPrefix() +
                                " §fNew cycle started, total=" +
                                cycleLocations.size() +
                                ", activeBatches=" + activeBatchCount);
            }
        }

        if (currentBatchIndex >= activeBatchCount) {
            advanceBatch(batchCount);
            return;
        }

        int total = cycleLocations.size();
        int batchSize = (int) Math.ceil(total / (double) activeBatchCount);

        int fromIndex = currentBatchIndex * batchSize;
        int toIndex = Math.min(fromIndex + batchSize, total);

        for (int i = fromIndex; i < toIndex; i++) {
            Location loc = cycleLocations.get(i);

            if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fSelling chest at location: " + loc);
            }

            BlockState state = loc.getBlock().getState();
            if (!(state instanceof Chest)) {
                unregisterSellChest(loc);
                continue;
            }

            sell(loc);
        }

        advanceBatch(batchCount);
    }

    private void advanceBatch(int batchCount) {
        currentBatchIndex++;
        if (currentBatchIndex >= batchCount) {
            currentBatchIndex = 0;
        }
    }

    private void sell(Location loc) {
        Block block = loc.getBlock();
        if (!(block.getState() instanceof Chest chest)) {
            return;
        }

        Inventory inventory = chest.getBlockInventory();

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

        int usage = sellChest.isInfinite()
                ? Integer.MAX_VALUE
                : pdc.getOrDefault(
                SELL_CHEST_TIMES,
                PersistentDataType.INTEGER,
                -1
        );

        if (!sellChest.isInfinite() && usage <= 0) {
            unregisterSellChest(loc);
            pdc.remove(KEY_IS_SELL_CHEST);
            pdc.remove(SELL_CHEST_ID);
            pdc.remove(SELL_CHEST_TIMES);
            pdc.remove(SELL_CHEST_PRICE);
            chest.update();
            return;
        }

        Map<AbstractSingleThing, BigDecimal> result =
                ShopHelper.sellAll(player, inventory, sellChest.getMultiplier());

        if (result.isEmpty()) {
            return;
        }

        // Refresh
        chest = (Chest) loc.getBlock().getState();
        pdc = chest.getPersistentDataContainer();

        if (!sellChest.isInfinite()) {
            usage--;
            pdc.set(SELL_CHEST_TIMES, PersistentDataType.INTEGER, usage);
            if (usage <= 0) {
                unregisterSellChest(loc);
                pdc.remove(KEY_IS_SELL_CHEST);
                pdc.remove(SELL_CHEST_ID);
                pdc.remove(SELL_CHEST_TIMES);
                pdc.remove(SELL_CHEST_PRICE);
            }
        }

        pdc.set(
                SELL_CHEST_PRICE,
                PersistentDataType.STRING,
                ObjectPrices.getDisplayNameInLine(
                        player, 1, result, ThingMode.ALL, true
                )
        );

        sellChest.getAction().runAllActions(new ObjectThingRun(player));

        chest.update();
        hologram.update(chest);
    }

    public void handleChunkLoad(Chunk chunk) {
        PersistentDataContainer chunkPdc = chunk.getPersistentDataContainer();

        String data = chunkPdc.get(KEY_CHUNK_CHESTS, PersistentDataType.STRING);
        // Debug
        if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fChecking sell chest at chunk: " + chunk.getChunkKey());
        }
        if (data == null || data.isEmpty()) {
            return;
        }

        // Debug
        if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFound sell chest at this chunk PDC: " + data);
        }
        String[] locStrings = data.split(";");

        for (String s : locStrings) {
            Location loc = stringToLoc(chunk.getWorld(), s);
            if (loc == null) {
                continue;
            }

            BlockState state = loc.getBlock().getState();

            if (!(state instanceof Chest chest)) {
                continue;
            }

            PersistentDataContainer pdc = chest.getPersistentDataContainer();
            if (!pdc.has(KEY_IS_SELL_CHEST, PersistentDataType.BYTE)) {
                continue;
            }

            String ownerStr = pdc.get(KEY_OWNER, PersistentDataType.STRING);
            if (ownerStr == null) {
                continue;
            }

            add(chest);
        }
    }

    public void handleChunkUnload(ChunkUnloadEvent event) {
        chestLocations.remove(event.getChunk());
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

        Chunk chunk = chest.getChunk();
        PersistentDataContainer chunkPdc = chunk.getPersistentDataContainer();
        String currentData = chunkPdc.getOrDefault(KEY_CHUNK_CHESTS, PersistentDataType.STRING, "");
        String locStr = locToString(chest.getLocation());

        if (!currentData.contains(locStr)) {
            String newData = currentData.isEmpty() ? locStr : currentData + ";" + locStr;
            chunkPdc.set(KEY_CHUNK_CHESTS, PersistentDataType.STRING, newData);
            // Debug
            if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
                TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fRegister new chest and save in chunk PDC: " + newData + " at chunk " + chunk.getChunkKey());
            }
        }

        add(chest);
    }

    public void unregisterSellChest(Location location) {
        if (hologram != null) {
            hologram.remove(location);
        }

        Chunk chunk = location.getChunk();
        Set<Location> set = chestLocations.get(chunk);
        if (set != null) {
            set.remove(location);
        }

        PersistentDataContainer chunkPdc = chunk.getPersistentDataContainer();
        String currentData = chunkPdc.get(KEY_CHUNK_CHESTS, PersistentDataType.STRING);
        if (currentData != null && !currentData.isEmpty()) {
            String locStr = locToString(location);
            String newData = Arrays.stream(currentData.split(";"))
                    .filter(s -> !s.equals(locStr))
                    .reduce((a, b) -> a + ";" + b)
                    .orElse("");

            if (newData.isEmpty()) {
                chunkPdc.remove(KEY_CHUNK_CHESTS);
            } else {
                chunkPdc.set(KEY_CHUNK_CHESTS, PersistentDataType.STRING, newData);
            }
        }
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
        if (!chestPdc.has(SELL_CHEST_TIMES, PersistentDataType.INTEGER) && !sellChest.isInfinite()) {
            return null;
        }

        Integer times = chestPdc.get(SELL_CHEST_TIMES, PersistentDataType.INTEGER);
        if (!sellChest.isInfinite() && (times == null || times <= 0)) {
            return null;
        }
        if (times == null) {
            times = -1;
        }
        return sellChest.getItemWithUsageTimes(1, times);
    }

    private void add(Chest chest) {
        if (ConfigManager.configManager.getBoolean("sell.sell-chest.debug")) {
            TextUtil.sendMessage(null, TextUtil.pluginPrefix() + " §fFound sell chest at " + chest.getBlock().getLocation() + ".");
        }
        chestLocations.computeIfAbsent(chest.getChunk(), k -> new HashSet<>()).add(chest.getBlock().getLocation());
        if (hologram != null) {
            hologram.create(chest);
        }
    }

    private String locToString(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location stringToLoc(World world, String str) {
        String[] parts = str.split(",");
        if (parts.length != 3) {
            return null;
        }
        return new Location(world, Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }
}
