package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.SellStickItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClickListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        int times = SellStickItem.getExtraSlotItemValue(item);
        if (times == 0) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        BlockState state = block.getState();
        if (state instanceof Container) {
            Inventory inventory = ((Container)state).getInventory();
            if (inventory.isEmpty()) {
                return;
            }
            LanguageManager.languageManager.sendStringText(event.getPlayer(), "start-selling");
            for (String shop : ConfigManager.configManager.shopConfigs.keySet()) {
                for (ObjectItem products : ConfigManager.configManager.getShop(shop).getProductList()) {
                    SellProductMethod.startSell(inventory,
                            shop,
                            products.getProduct(),
                            event.getPlayer(),
                            false,
                            false,
                            true,
                            1);
                }
            }
            SellStickItem.removeExtraSlotItemValue(event.getPlayer(), item);
        }
    }

}
