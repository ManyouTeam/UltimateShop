package cn.superiormc.ultimateshop.hooks;

import cn.superiormc.mythicchanger.manager.MatchItemManager;
import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.function.Predicate;

public class ItemPriceUtil {

    public static boolean getPrice(Inventory inventory, String pluginName, String item, int value, boolean take) {
        return getPrice(ItemStorage.of(inventory), pluginName, item, value, take);
    }

    public static boolean getPrice(ItemStorage storage, String pluginName, String item, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storageContents = storage.getStorageContents();
        if (take || getItemAmount(storage, pluginName, item) >= value) {
            if (take) {
                takeItems(storageContents, value, itemStack -> {
                    String tempVal10 = HookManager.hookManager.getHookItemID(pluginName, itemStack);
                    return tempVal10 != null && tempVal10.equals(item);
                });
                storage.setStorageContents(storageContents);
            }
            return true;
        } else {
            return false;
        }
    }

    public static int getItemAmount(Inventory inventory, String pluginName, String item) {
        return getItemAmount(ItemStorage.of(inventory), pluginName, item);
    }

    public static int getItemAmount(ItemStorage storage, String pluginName, String item) {
        if (item == null) {
            return 0;
        }
        return countItems(storage.getStorageContents(), tempVal1 -> {
            String tempVal10 = HookManager.hookManager.getHookItemID(pluginName, tempVal1);
            return tempVal10 != null && tempVal10.equals(item);
        });
    }

    public static boolean getPrice(Inventory inventory, ItemStack item, int value, boolean take) {
        return getPrice(ItemStorage.of(inventory), item, value, take);
    }

    public static boolean getPrice(ItemStorage storage, ItemStack item, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storageContents = storage.getStorageContents();
        if (take || getItemAmount(storage, item) >= value) {
            if (take) {
                takeItems(storageContents, value, itemStack -> ItemUtil.isSameItem(itemStack, item));
                storage.setStorageContents(storageContents);
            }
            return true;
        }
        else {
            return false;
        }
    }

    public static int getItemAmount(Inventory inventory, Player player, ConfigurationSection section) {
        return getItemAmount(ItemStorage.of(inventory), player, section);
    }

    public static int getItemAmount(ItemStorage storage, Player player, ConfigurationSection section) {
        if (section == null) {
            return 0;
        }
        if (!CommonUtil.checkPluginLoad("MythicChanger")) {
            return 0;
        }
        return countItems(storage.getStorageContents(), tempVal1 -> MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), player, tempVal1));
    }

    public static boolean getPrice(Inventory inventory, Player player, ConfigurationSection section, int value, boolean take) {
        return getPrice(ItemStorage.of(inventory), player, section, value, take);
    }

    public static boolean getPrice(ItemStorage storage, Player player, ConfigurationSection section, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        if (!CommonUtil.checkPluginLoad("MythicChanger")) {
            return false;
        }
        ItemStack[] storageContents = storage.getStorageContents();
        if (take || getItemAmount(storage, player, section) >= value) {
            if (take) {
                takeItems(storageContents, value, itemStack -> MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), player, itemStack));
                storage.setStorageContents(storageContents);
            }
            return true;
        }
        else {
            return false;
        }
    }

    public static int getItemAmount(Inventory inventory, ItemStack item) {
        return getItemAmount(ItemStorage.of(inventory), item);
    }

    public static int getItemAmount(ItemStorage storage, ItemStack item) {
        if (item == null) {
            return 0;
        }
        return countItems(storage.getStorageContents(), tempVal1 -> ItemUtil.isSameItem(tempVal1, item));
    }

    private static int countItems(ItemStack[] contents, Predicate<ItemStack> matcher) {
        if (contents == null || contents.length == 0) {
            return 0;
        }
        int amount = 0;
        for (ItemStack itemStack : contents) {
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }
            if (matcher.test(itemStack)) {
                amount += itemStack.getAmount();
            }
            amount += countItems(getShulkerContents(itemStack), matcher);
        }
        return amount;
    }

    private static int takeItems(ItemStack[] contents, int amount, Predicate<ItemStack> matcher) {
        if (contents == null || contents.length == 0 || amount <= 0) {
            return amount;
        }
        for (int i = 0; i < contents.length && amount > 0; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }
            if (matcher.test(itemStack)) {
                if (itemStack.getAmount() > amount) {
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    return 0;
                }
                amount -= itemStack.getAmount();
                itemStack.setAmount(0);
                if (amount <= 0) {
                    return 0;
                }
            }
            if (itemStack.getAmount() <= 0) {
                continue;
            }
            BlockStateMeta blockStateMeta = getShulkerMeta(itemStack);
            if (blockStateMeta == null) {
                continue;
            }
            ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
            ItemStack[] shulkerContents = shulkerBox.getInventory().getStorageContents();
            amount = takeItems(shulkerContents, amount, matcher);
            shulkerBox.getInventory().setStorageContents(shulkerContents);
            blockStateMeta.setBlockState(shulkerBox);
            itemStack.setItemMeta(blockStateMeta);
        }
        return amount;
    }

    private static ItemStack[] getShulkerContents(ItemStack itemStack) {
        BlockStateMeta blockStateMeta = getShulkerMeta(itemStack);
        if (blockStateMeta == null) {
            return new ItemStack[0];
        }
        return ((ShulkerBox) blockStateMeta.getBlockState()).getInventory().getStorageContents();
    }

    private static BlockStateMeta getShulkerMeta(ItemStack itemStack) {
        if (UltimateShop.freeVersion || !ConfigManager.configManager.getBoolean("sell.shulker-box-sell")) {
            return null;
        }
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta)) {
            return null;
        }
        if (!(blockStateMeta.getBlockState() instanceof ShulkerBox)) {
            return null;
        }
        return blockStateMeta;
    }

}
