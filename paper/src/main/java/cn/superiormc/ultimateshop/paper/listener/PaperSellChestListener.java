package cn.superiormc.ultimateshop.paper.listener;

import cn.superiormc.ultimateshop.listeners.SellChestListener;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

/** Adds Paper-only destruction sources while reusing the common Bukkit listener. */
public class PaperSellChestListener extends SellChestListener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakBlock(BlockBreakBlockEvent event) {
        Block block = event.getBlock();
        if (!isSellChest(block)) {
            return;
        }
        event.getDrops().clear();
        handlePaperDestroy(block);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        if (!isSellChest(block)) {
            return;
        }
        event.setWillDrop(false);
        handlePaperDestroy(block);
    }

    private void handlePaperDestroy(Block block) {
        if (block.getState() instanceof Chest chest) {
            for (ItemStack item : chest.getBlockInventory().getContents()) {
                if (item != null && !item.getType().isAir()) {
                    block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
                }
            }
            chest.getBlockInventory().clear();
        }
        handleSellChestDestroy(block, true);
    }
}
