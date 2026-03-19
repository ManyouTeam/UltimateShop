package cn.superiormc.ultimateshop.hooks;

import cn.superiormc.mythicchanger.manager.MatchItemManager;
import cn.superiormc.ultimateshop.managers.HookManager;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.utils.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemPriceUtil {

    public static boolean getPrice(Inventory inventory, Player player, String pluginName, String item, int value, boolean take) {
        return getPrice(ItemStorage.of(inventory), player, pluginName, item, value, take);
    }

    public static boolean getPrice(ItemStorage storage, Player player, String pluginName, String item, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storageContents = storage.getStorageContents();
        if (take || getItemAmount(storage, pluginName, item) >= value) {
            if (take) {
                for (ItemStack itemStack : storageContents) {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    String tempVal10 = HookManager.hookManager.getHookItemID(pluginName, itemStack);
                    if (tempVal10 != null && tempVal10.equals(item)) {
                        if (itemStack.getAmount() >= value) {
                            itemStack.setAmount(itemStack.getAmount() - value);
                            break;
                        } else {
                            value -= itemStack.getAmount();
                            itemStack.setAmount(0);
                        }
                    }
                }
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
        int amount = 0;
        ItemStack[] storageContents = storage.getStorageContents();
        for (ItemStack tempVal1 : storageContents) {
            if (tempVal1 == null || tempVal1.getType().isAir()) {
                continue;
            }
            String tempVal10 = HookManager.hookManager.getHookItemID(pluginName, tempVal1);
            if (tempVal10 != null && tempVal10.equals(item)) {
                amount += tempVal1.getAmount();
            }
        }
        return amount;
    }

    public static boolean getPrice(Inventory inventory, Player player, ItemStack item, int value, boolean take) {
        return getPrice(ItemStorage.of(inventory), player, item, value, take);
    }

    public static boolean getPrice(ItemStorage storage, Player player, ItemStack item, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storageContents = storage.getStorageContents();
        if (take || getItemAmount(storage, item) >= value) {
            if (take) {
                for (ItemStack itemStack : storageContents) {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    if (ItemUtil.isSameItem(itemStack, item)) {
                        if (itemStack.getAmount() >= value) {
                            itemStack.setAmount(itemStack.getAmount() - value);
                            break;
                        } else {
                            value -= itemStack.getAmount();
                            itemStack.setAmount(0);
                        }
                    }
                }
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
        ItemStack[] storageContents = storage.getStorageContents();
        int amount = 0;
        for (ItemStack tempVal1 : storageContents) {
            if (tempVal1 == null || tempVal1.getType().isAir()) {
                continue;
            }
            if (MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), player, tempVal1)) {
                amount += tempVal1.getAmount();
            }
        }
        return amount;
    }

    public static boolean getPrice(Inventory inventory, Player player, ConfigurationSection section, int value, boolean take) {
        return getPrice(ItemStorage.of(inventory), player, section, value, take);
    }

    public static boolean getPrice(ItemStorage storage, Player player, ConfigurationSection section, int value, boolean take) {
        if (value < 0) {
            return false;
        }
        ItemStack[] storageContents = storage.getStorageContents();
        if (take || getItemAmount(storage, player, section) >= value) {
            if (take) {
                for (ItemStack itemStack : storageContents) {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        continue;
                    }
                    if (MatchItemManager.matchItemManager.getMatch(section.getConfigurationSection("match-item"), player, itemStack)) {
                        if (itemStack.getAmount() >= value) {
                            itemStack.setAmount(itemStack.getAmount() - value);
                            break;
                        } else {
                            value -= itemStack.getAmount();
                            itemStack.setAmount(0);
                        }
                    }
                }
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
        ItemStack[] storageContents = storage.getStorageContents();
        int amount = 0;
        for (ItemStack tempVal1 : storageContents) {
            if (tempVal1 == null || tempVal1.getType().isAir()) {
                continue;
            }
            if (ItemUtil.isSameItem(tempVal1, item)) {
                amount += tempVal1.getAmount();
            }
        }
        return amount;
    }

}
