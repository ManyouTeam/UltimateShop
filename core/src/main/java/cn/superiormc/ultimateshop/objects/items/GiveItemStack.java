package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.objects.ObjectThingRun;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class GiveItemStack {

    private ItemStack displayItem;

    private ItemStack targetItem;

    private Collection<ItemStack> items;

    private double cost;

    private boolean canGive;

    private final AbstractSingleThing thing;

    public GiveItemStack(Collection<ItemStack> items, ItemStack targetItem, ItemStack displayItem, boolean canGive, AbstractSingleThing thing) {
        this.displayItem = displayItem;
        this.targetItem = targetItem;
        this.items = items;
        this.thing = thing;
        if (!ConfigManager.configManager.getBoolean("give-item.check-full")) {
            this.canGive = true;
        } else {
            this.canGive = canGive;
        }
    }

    public GiveItemStack(double cost, AbstractSingleThing thing) {
        this.cost = cost;
        this.thing = thing;
        this.canGive = true;
    }

    public GiveItemStack(ItemStack targetItem, ItemStack displayItem, AbstractSingleThing thing) {
        this.targetItem = targetItem;
        this.thing = thing;
        this.displayItem = displayItem;
        this.items = new ArrayList<>();
        items.add(displayItem);
        this.canGive = true;
    }

    public GiveItemStack(AbstractSingleThing thing) {
        this.thing = thing;
        this.canGive = true;
    }

    public Collection<ItemStack> getItems() {
        return items;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void giveToPlayer(int times, double multiplier, Player player) {
        if (thing.singleSection == null) {
            return;
        }
        switch (thing.type) {
            case VANILLA_ITEM:
            case HOOK_ITEM:
            case MATCH_ITEM:
                if (items == null) {
                    break;
                }
                for (ItemStack tempVal1 : items) {
                    CommonUtil.giveOrDrop(player, tempVal1);
                }
                break;
            case HOOK_ECONOMY:
                HookManager.hookManager.giveEconomy(thing.singleSection.getString("economy-plugin"),
                        thing.singleSection.getString("economy-type", "Unknown"),
                        player,
                        cost * multiplier);
                break;
            case VANILLA_ECONOMY:
                HookManager.hookManager.giveEconomy(thing.singleSection.getString("economy-type"),
                        player,
                        (int) (cost * multiplier));
                break;
        }
        thing.giveAction.runAllActions(new ObjectThingRun(player, times, cost));
    }

    public void setCanGive(boolean canGive) {
        this.canGive = canGive;
    }

    public boolean isCanGive() {
        return canGive;
    }

    public ItemStack getTargetItem() {
        return targetItem;
    }

    public AbstractSingleThing getThing() {
        return thing;
    }
}
