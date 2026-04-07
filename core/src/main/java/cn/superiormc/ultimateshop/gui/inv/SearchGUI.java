package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.methods.Items.BuildItem;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SearchGUI extends InvGUI {

    private final Map<Integer, ObjectItem> resultButtons = new HashMap<>();

    private final Map<Integer, ItemStack> inputButtons = new HashMap<>();

    private SearchGUI(Player owner) {
        super(owner);
    }

    @Override
    public void constructGUI() {
        title = ConfigManager.configManager.getStringWithLang(player, "menu.search-gui.title");
        int size = ConfigManager.configManager.getInt("menu.search-gui.size", 54);
        List<Integer> inputSlots = ConfigManager.configManager.getIntList("menu.search-gui.input-slots");
        List<Integer> resultSlots = ConfigManager.configManager.getIntList("menu.search-gui.result-slots");
        if (Objects.isNull(inv)) {
            inv = UltimateShop.methodUtil.createNewInv(player, size, title);
        }
        initInputItemMap();
        inv.clear();
        resultButtons.clear();

        if (ConfigManager.configManager.getBoolean("menu.search-gui.filler.enabled")) {
            ItemStack filler = BuildItem.buildItemStack(player,
                    ConfigManager.configManager.getSection("menu.search-gui.filler.display-item"),
                    1);
            for (int slot = 0; slot < size; slot++) {
                if (inputSlots.contains(slot)
                        || resultSlots.contains(slot)
                        || slot == ConfigManager.configManager.getInt("menu.search-gui.guide-item.slot", 11)
                        || slot == ConfigManager.configManager.getInt("menu.search-gui.state-item.slot", 13)
                        || slot == ConfigManager.configManager.getInt("menu.search-gui.clear-search.slot", 15)
                        || slot == ConfigManager.configManager.getInt("menu.search-gui.no-result-item.slot", 31)) {
                    continue;
                }
                inv.setItem(slot, filler);
            }
        }
        for (Map.Entry<Integer, ItemStack> entry : inputButtons.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < inv.getSize()) {
                inv.setItem(entry.getKey(), entry.getValue());
            }
        }
        inv.setItem(ConfigManager.configManager.getInt("menu.search-gui.guide-item.slot", 11),
                BuildItem.buildItemStack(player,
                        ConfigManager.configManager.getSection("menu.search-gui.guide-item.display-item"),
                        ConfigManager.configManager.getInt("menu.search-gui.guide-item.display-item.amount", 1)));
        inv.setItem(ConfigManager.configManager.getInt("menu.search-gui.clear-search.slot", 15),
                BuildItem.buildItemStack(player,
                        ConfigManager.configManager.getSection("menu.search-gui.clear-search.display-item"),
                        ConfigManager.configManager.getInt("menu.search-gui.clear-search.display-item.amount", 1)));

        List<ObjectItem> matchedItems = ShopHelper.getTargetItems(inputButtons.values().toArray(new ItemStack[0]), player);
        if (!inputButtons.isEmpty()) {
            inv.setItem(ConfigManager.configManager.getInt("menu.search-gui.state-item.slot", 13),
                    BuildItem.buildItemStack(player,
                    ConfigManager.configManager.getSection("menu.search-gui.state-item.has-input.display-item"),
                    ConfigManager.configManager.getInt("menu.search-gui.state-item.has-input.display-item.amount", 1),
                    "result-amount", String.valueOf(matchedItems.size()),
                    "showing-amount", String.valueOf(Math.min(matchedItems.size(), resultSlots.size())),
                    "input-amount", String.valueOf(countInputItems())));
        } else {
            inv.setItem(ConfigManager.configManager.getInt("menu.search-gui.state-item.slot", 13),
                    BuildItem.buildItemStack(player,
                    ConfigManager.configManager.getSection("menu.search-gui.state-item.empty-input.display-item"),
                    ConfigManager.configManager.getInt("menu.search-gui.state-item.empty-input.display-item.amount", 1),
                    "result-amount", "0",
                    "showing-amount", "0",
                    "input-amount", "0"));
        }

        if (matchedItems.isEmpty()) {
            if (!inputButtons.isEmpty()) {
                inv.setItem(ConfigManager.configManager.getInt("menu.search-gui.no-result-item.slot", 31),
                        BuildItem.buildItemStack(player,
                                ConfigManager.configManager.getSection("menu.search-gui.no-result-item.display-item"),
                                ConfigManager.configManager.getInt("menu.search-gui.no-result-item.display-item.amount", 1),
                                "input-amount", String.valueOf(countInputItems())));
            }
            return;
        }

        int limit = Math.min(matchedItems.size(), resultSlots.size());
        for (int i = 0; i < limit; i++) {
            int slot = resultSlots.get(i);
            ObjectItem item = matchedItems.get(i);
            resultButtons.put(slot, item);
            inv.setItem(slot, item.getDisplayItem(player, 1).getItemStack());
        }
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (ConfigManager.configManager.getIntList("menu.search-gui.input-slots").contains(slot)) {
            queueRefresh();
            return false;
        }
        if (slot == ConfigManager.configManager.getInt("menu.search-gui.clear-search.slot", 15)) {
            returnInputItems();
            constructGUI();
            return true;
        }
        ObjectItem resultItem = resultButtons.get(slot);
        if (resultItem != null) {
            resultItem.clickEvent(type, player);
            queueRefresh();
            return true;
        }
        return true;
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        boolean changedInput = false;
        for (int slot : newItems.keySet()) {
            if (!ConfigManager.configManager.getIntList("menu.search-gui.input-slots").contains(slot)) {
                return true;
            }
            changedInput = true;
        }
        if (changedInput) {
            queueRefresh();
        }
        return false;
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        returnInputItems();
        return super.closeEventHandle(inventory);
    }

    private void returnInputItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int slot : ConfigManager.configManager.getIntList("menu.search-gui.input-slots")) {
            ItemStack itemStack = inv.getItem(slot);
            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }
            items.add(itemStack.clone());
            inv.setItem(slot, new ItemStack(Material.AIR));
        }
        if (!items.isEmpty()) {
            CommonUtil.giveOrDrop(player, items.toArray(new ItemStack[0]));
        }
    }

    private void initInputItemMap() {
        inputButtons.clear();
        for (int slot : ConfigManager.configManager.getIntList("menu.search-gui.input-slots")) {
            ItemStack itemStack = inv.getItem(slot);
            if (itemStack != null && !itemStack.getType().isAir()) {
                inputButtons.put(slot, itemStack.clone());
            }
        }
    }

    private int countInputItems() {
        int amount = 0;
        for (ItemStack itemStack : inputButtons.values()) {
            amount += itemStack.getAmount();
        }
        return amount;
    }

    private void queueRefresh() {
        SchedulerUtil.runTaskLater(() -> {
            if (inv != null && player.getOpenInventory().getTopInventory().equals(inv)) {
                constructGUI();
            }
        }, 1L);
    }

    public static void openGUI(Player player) {
        SearchGUI gui = new SearchGUI(player);
        gui.openGUI(true);
    }
}
