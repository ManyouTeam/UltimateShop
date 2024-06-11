package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.methods.SellStickItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickListener implements Listener {

    public static List<Player> playerList = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (!EquipmentSlot.HAND.equals(event.getHand())) {
            return;
        }
        if (event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        int times = SellStickItem.getSellStickValue(item);
        if (times <= 0) {
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
            Map<AbstractSingleThing, BigDecimal> result = new HashMap<>();
            boolean firstSell = false;
            int cooldown = ConfigManager.configManager.getInt("sell.sell-stick.cooldown", -1);
            if (cooldown > 0) {
                if (playerList.contains(event.getPlayer())) {
                    return;
                }
                playerList.add(event.getPlayer());
                if (UltimateShop.isFolia) {
                    Bukkit.getGlobalRegionScheduler().runDelayed(UltimateShop.instance, task -> {
                        playerList.remove(event.getPlayer());
                    }, cooldown);
                    return;
                } else {
                    Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> {
                        playerList.remove(event.getPlayer());
                    }, cooldown);
                }
            }
            for (String shop : ConfigManager.configManager.shopConfigs.keySet()) {
                for (ObjectItem products : ConfigManager.configManager.getShop(shop).getProductList()) {
                    if (ConfigManager.configManager.getStringListOrDefault("menu.sell-all.ignore-items",
                            "sell.sell-all.ignore-items").contains(shop + ";;" + products.getProduct())) {
                        continue;
                    }
                    ProductTradeStatus status = SellProductMethod.startSell(inventory,
                            shop,
                            products.getProduct(),
                            event.getPlayer(),
                            false,
                            false,
                            ConfigManager.configManager.getBooleanOrDefault(
                                    "menu.sell-all.hide-message", "sell.sell-stick.hide-message"),
                            true,
                            firstSell,
                            1);
                    if (status.getStatus() == ProductTradeStatus.Status.DONE && status.getGiveResult() != null) {
                        result.putAll(status.getGiveResult().getResultMap());
                    }
                    if (!products.getSellAction().isEmpty()) {
                        firstSell = true;
                    }
                }
            }
            if (!result.isEmpty()) {
                LanguageManager.languageManager.sendStringText(event.getPlayer(), "start-sell-stick",
                        "reward", ObjectPrices.getDisplayNameInLine(event.getPlayer(),
                        result, ThingMode.ALL
                ));
                SellStickItem.removeSellStickValue(event.getPlayer(), item);
            }
        }
    }

}
