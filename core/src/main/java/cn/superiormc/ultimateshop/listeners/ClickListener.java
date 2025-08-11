package cn.superiormc.ultimateshop.listeners;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.ObjectSellStick;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
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
        if (!CommonUtil.actionIsRightClick(event.getAction()) && ConfigManager.configManager.getString("sell.sell-stick.click-type", "RIGHT").equals("RIGHT")) {
            return;
        } else if (!CommonUtil.actionIsLeftClick(event.getAction()) && ConfigManager.configManager.getString("sell.sell-stick.click-type", "RIGHT").equals("LEFT")) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ObjectSellStick sellStick = ConfigManager.configManager.getSellStickID(item);
        if (sellStick == null) {
            return;
        }
        if (!sellStick.getCondition().getAllBoolean(new ObjectThingRun(event.getPlayer()))) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) {
            return;
        }
        if (!HookManager.hookManager.getProtectionCanUse(event.getPlayer(), block.getLocation())) {
            return;
        }
        BlockState state = block.getState();
        SchedulerUtil.runTaskLater(() -> {
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
                                ConfigManager.configManager.getBoolean("sell.sell-stick.hide-message"),
                                true,
                                firstSell,
                                1,
                                sellStick.getMultiplier());
                        if (status.getStatus() == ProductTradeStatus.Status.DONE && status.getGiveResult() != null) {
                            result.putAll(status.getGiveResult().getResultMap());
                        }
                        if (!products.getSellAction().isEmpty()) {
                            firstSell = true;
                        }
                    }
                }
                if (!result.isEmpty()) {
                    if (ConfigManager.configManager.getBoolean("sell.sell-stick.display-calculate-multiplier")) {
                        for (AbstractSingleThing singleThing : result.keySet()) {
                            BigDecimal newValue = result.get(singleThing).multiply(BigDecimal.valueOf(sellStick.getMultiplier()));
                            result.put(singleThing, newValue);
                        }
                    }
                    LanguageManager.languageManager.sendStringText(event.getPlayer(), "start-sell-stick",
                            "reward", ObjectPrices.getDisplayNameInLine(event.getPlayer(), 1,
                                    result, ThingMode.ALL, true),
                            "multiplier", String.valueOf(sellStick.getMultiplier()));
                    sellStick.takeUsageTimes(event.getPlayer(), event.getItem());
                    sellStick.getAction().runAllActions(new ObjectThingRun(event.getPlayer()));
                }
                SchedulerUtil.runTaskLater(() -> playerList.remove(event.getPlayer()), cooldown);
            }
        }, 2L);
    }


}
