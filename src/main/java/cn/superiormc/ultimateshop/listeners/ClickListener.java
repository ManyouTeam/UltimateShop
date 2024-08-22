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
import cn.superiormc.ultimateshop.utils.FoliaUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

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
        int times = SellStickItem.getSellStickValue(event.getItem());
        if (times <= 0) {
            return;
        }
        if (!event.getAction().isRightClick() && ConfigManager.configManager.getString("sell.sell-stick.click-type", "RIGHT").equals("RIGHT")) {
            return;
        } else if (!event.getAction().isLeftClick() && ConfigManager.configManager.getString("sell.sell-stick.click-type", "RIGHT").equals("LEFT")) {
            return;
        }
        if (UltimateShop.isFolia) {
            FoliaUtil.startUseSellStickForFolia(this, event);
        } else {
            Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> startSell(event), 2L);
        }
    }

    public void startSell(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) {
            return;
        }
        BlockState state = block.getState();
        Inventory inventory = null;
        if (state instanceof Container) {
            inventory = ((Container) state).getInventory();
        } else if (state instanceof EnderChest) {
            inventory = event.getPlayer().getEnderChest();
        }
        if (inventory != null) {
            if (inventory.isEmpty()) {
                return;
            }
            Map<AbstractSingleThing, BigDecimal> result = new HashMap<>();
            boolean firstSell = false;
            int cooldown = ConfigManager.configManager.getInt("sell.sell-stick.cooldown", -1);
            if (cooldown < 5) {
                cooldown = 5;
            }
            if (playerList.contains(event.getPlayer())) {
                return;
            }
            playerList.add(event.getPlayer());
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
                        "reward", ObjectPrices.getDisplayNameInLine(event.getPlayer(), 1,
                                result, ThingMode.ALL, false));
                SellStickItem.removeSellStickValue(event.getPlayer(), event.getItem());
            }
            if (UltimateShop.isFolia) {
                FoliaUtil.removeSellStoclCooldownForFolia(event.getPlayer(), cooldown);
            } else {
                Bukkit.getScheduler().runTaskLater(UltimateShop.instance, () -> playerList.remove(event.getPlayer()), cooldown);
            }
        }
    }

}
