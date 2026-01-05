package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.SellChestManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.sellchests.ObjectSellChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SellChestListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.CHEST) {
            return;
        }

        Block block = event.getBlockPlaced();
        Block[] adjacent = {
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.WEST)
        };

        for (Block b : adjacent) {
            if (isSellChest(b)) {
                if (block.getState() instanceof Chest chest) {
                    org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
                    chestData.setType(org.bukkit.block.data.type.Chest.Type.SINGLE);
                    chest.setBlockData(chestData);
                    chest.update();
                }
            }
        }

        ItemStack item = event.getItemInHand();
        if (!item.hasItemMeta()) {
            return;
        }

        ObjectSellChest sellChest = ConfigManager.configManager.getSellChestID(item);
        if (sellChest == null) {
            return;
        }

        if (!sellChest.getCondition().getAllBoolean(new ObjectThingRun(event.getPlayer()))) {
            return;
        }

        int times = ObjectSellChest.getSellChestValue(item);
        if (times <= 0 && !sellChest.isInfinite()) {
            return;
        }

        BlockState state = event.getBlockPlaced().getState();
        if (!(state instanceof Chest chest)) {
            return;
        }

        org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();
        chestData.setType(org.bukkit.block.data.type.Chest.Type.SINGLE);
        chest.setBlockData(chestData);
        chest.update();

        chest.setCustomName(null);
        chest.update();

        SellChestManager.sellChestManager.registerSellChest(chest, event.getPlayer(), sellChest, times);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (!(state instanceof Chest chest)) {
            return;
        }

        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        if (!pdc.has(SellChestManager.KEY_IS_SELL_CHEST, PersistentDataType.BYTE)) {
            return;
        }

        event.setDropItems(false);

        Inventory inventory = chest.getBlockInventory();
        Location dropLoc = block.getLocation().add(0.5, 0.5, 0.5);

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            block.getWorld().dropItemNaturally(dropLoc, item);
        }

        inventory.clear();

        handleSellChestDestroy(block, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            handleSellChestDestroy(block, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            handleSellChestDestroy(block, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (isSellChest(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (isSellChest(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onEntityGrief(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        if (isSellChest(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        SellChestManager.sellChestManager.handleChunkLoad(event);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        SellChestManager.sellChestManager.handleChunkUnload(event);
    }

    private void handleSellChestDestroy(Block block, boolean dropItem) {
        BlockState state = block.getState();
        if (!(state instanceof Chest chest)) {
            return;
        }

        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        if (!pdc.has(SellChestManager.KEY_IS_SELL_CHEST, PersistentDataType.BYTE)) {
            return;
        }

        // 生成掉落物
        if (dropItem) {
            ItemStack item = SellChestManager.sellChestManager.createSellChestItem(chest);
            if (item != null) {
                block.getWorld().dropItemNaturally(block.getLocation(), item);
            }
        }

        // 清理
        SellChestManager.sellChestManager.unregisterSellChest(block.getLocation());
    }

    private boolean isSellChest(Block block) {
        BlockState state = block.getState();
        if (!(state instanceof Chest chest)) {
            return false;
        }

        return chest.getPersistentDataContainer()
                .has(SellChestManager.KEY_IS_SELL_CHEST, PersistentDataType.BYTE);
    }
}
