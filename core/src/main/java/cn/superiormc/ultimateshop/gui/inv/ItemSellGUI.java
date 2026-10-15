package cn.superiormc.ultimateshop.gui.inv;

import cn.superiormc.ultimateshop.UltimateShop;
import cn.superiormc.ultimateshop.gui.InvGUI;
import cn.superiormc.ultimateshop.managers.LanguageManager;
import cn.superiormc.ultimateshop.methods.Product.SellProductMethod;
import cn.superiormc.ultimateshop.methods.ProductTradeStatus;
import cn.superiormc.ultimateshop.objects.buttons.AbstractButton;
import cn.superiormc.ultimateshop.objects.items.AbstractSingleThing;
import cn.superiormc.ultimateshop.objects.items.ItemSellQuote;
import cn.superiormc.ultimateshop.objects.items.ItemStorage;
import cn.superiormc.ultimateshop.objects.items.ThingMode;
import cn.superiormc.ultimateshop.objects.items.prices.ObjectPrices;
import cn.superiormc.ultimateshop.objects.menus.MenuSender;
import cn.superiormc.ultimateshop.objects.menus.ObjectItemSellMenu;
import cn.superiormc.ultimateshop.objects.menus.ObjectMenu;
import cn.superiormc.ultimateshop.utils.CommonUtil;
import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.SchedulerUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemSellGUI extends InvGUI {

    private final ObjectItemSellMenu menu;

    private boolean selling;

    private ItemSellGUI(Player owner, ObjectItemSellMenu menu, boolean bypass) {
        super(owner, bypass);
        this.menu = menu;
    }

    @Override
    public void constructGUI() {
        if (menu == null || menu.getConfig() == null) {
            return;
        }
        if (!canDisplayMenu()) {
            LanguageManager.languageManager.sendStringText(player, "menu-condition-not-meet", "menu", menu.getName());
            return;
        }
        title = TextUtil.withPAPI(CommonUtil.parseLang(player, menu.getString("title", "Item Sell")), player);
        if (Objects.isNull(inv)) {
            inv = UltimateShop.methodUtil.createNewInv(player, menu.getInt("size", 54), title, this);
        }

        Map<Integer, ItemStack> inputItems = collectInputItems();
        inv.clear();
        menuButtons = menu.getMenu(MenuSender.of(player));
        menuItems = getMenuItems(player);
        for (Map.Entry<Integer, ItemStack> entry : menuItems.entrySet()) {
            if (entry.getKey() >= 0 && entry.getKey() < inv.getSize()) {
                inv.setItem(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<Integer, ItemStack> entry : inputItems.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue());
        }
        renderConfirmButton(inputItems);
    }

    private void renderConfirmButton(Map<Integer, ItemStack> inputItems) {
        QuoteSummary summary = summarize(inputItems);
        String price = summary.finalPrices.isEmpty()
                ? LanguageManager.languageManager.getStringText(player, "plugin.item-sell-no-price", "&cNothing sellable")
                : ObjectPrices.getDisplayNameInLine(player, 1, summary.finalPrices, ThingMode.ALL, true);
        String originalPrice = summary.originalPrices.isEmpty()
                ? LanguageManager.languageManager.getStringText(player, "plugin.item-sell-no-price", "&cNothing sellable")
                : ObjectPrices.getDisplayNameInLine(player, 1, summary.originalPrices, ThingMode.ALL, true);
        String multiplier = summary.originalTotal.compareTo(BigDecimal.ZERO) <= 0
                ? "1"
                : MathUtil.toDisplayString(summary.finalTotal.divide(
                        summary.originalTotal, 12, java.math.RoundingMode.HALF_UP));
        ItemStack displayItem = menu.getConfirmButton().buildDisplayItem(player,
                price, originalPrice, multiplier, summary.sellableAmount, summary.unsellableAmount);
        for (int slot : menu.getConfirmSlots()) {
            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, displayItem);
            }
        }
    }

    private QuoteSummary summarize(Map<Integer, ItemStack> inputItems) {
        QuoteSummary summary = new QuoteSummary();
        ItemSellQuote.QuoteContext quoteContext = new ItemSellQuote.QuoteContext(menu.getMaxSellAmount());
        for (ItemStack itemStack : inputItems.values()) {
            ItemSellQuote quote = ItemSellQuote.create(menu, player, itemStack, quoteContext);
            if (quote == null) {
                summary.unsellableAmount += itemStack.getAmount();
                continue;
            }
            summary.sellableAmount += quote.getItemAmount();
            summary.unsellableAmount += Math.max(0, itemStack.getAmount() - quote.getItemAmount());
            mergePrices(summary.originalPrices, quote.getOriginalPrices());
            mergePrices(summary.finalPrices, quote.getFinalPrices());
        }
        summary.originalTotal = sumPrices(summary.originalPrices);
        summary.finalTotal = sumPrices(summary.finalPrices);
        return summary;
    }

    @Override
    public boolean clickEventHandle(Inventory inventory, ClickType type, int slot) {
        if (menu.getInputSlots().contains(slot)) {
            queueRefresh();
            return false;
        }
        if (menu.getConfirmSlots().contains(slot)) {
            sellInputItems();
            return true;
        }
        AbstractButton normalButton = menuButtons.get(slot);
        if (normalButton != null) {
            normalButton.clickEvent(type, player);
        }
        return true;
    }

    private void sellInputItems() {
        if (selling) {
            return;
        }
        selling = true;
        int soldAmount = 0;
        boolean limited = menu.getMaxSellAmount() >= 0;
        int remainingAmount = limited ? menu.getMaxSellAmount() : -1;
        Map<AbstractSingleThing, BigDecimal> rewards = new LinkedHashMap<>();
        try {
            for (int slot : menu.getInputSlots()) {
                if (limited && remainingAmount <= 0) {
                    break;
                }
                ItemStack itemStack = inv.getItem(slot);
                ItemSellQuote quote = ItemSellQuote.create(menu, player, itemStack, remainingAmount);
                if (quote == null) {
                    continue;
                }
                ProductTradeStatus status = SellProductMethod.startPriceModifierSell(
                        new SingleSlotItemStorage(inv, slot),
                        quote.getItem(),
                        player,
                        false,
                        false,
                        true,
                        quote.getTradeAmount(),
                        1);
                if (status.getStatus() != ProductTradeStatus.Status.DONE || status.getGiveResult() == null) {
                    continue;
                }
                if (limited) {
                    remainingAmount = Math.max(0, remainingAmount - status.getAmount());
                }
                soldAmount += status.getAmount() * quote.getItem().getDisplayItemObject().getAmountPlaceholder(player);
                mergePrices(rewards, status.getGiveResult().getResultMap());
            }
        } finally {
            selling = false;
        }
        if (soldAmount > 0) {
            LanguageManager.languageManager.sendStringText(player, "start-sell-all",
                    "amount", String.valueOf(soldAmount),
                    "reward", ObjectPrices.getDisplayNameInLine(player, 1, rewards, ThingMode.ALL, true));
        } else {
            LanguageManager.languageManager.sendStringText(player, "plugin.item-sell-nothing");
        }
        constructGUI();
    }

    @Override
    public boolean dragEventHandle(Map<Integer, ItemStack> newItems) {
        for (int slot : newItems.keySet()) {
            if (!menu.getInputSlots().contains(slot)) {
                return true;
            }
        }
        queueRefresh();
        return false;
    }

    @Override
    public boolean closeEventHandle(Inventory inventory) {
        returnInputItems();
        return super.closeEventHandle(inventory);
    }

    @Override
    public ObjectMenu getMenu() {
        return menu;
    }

    private Map<Integer, ItemStack> collectInputItems() {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();
        if (inv == null) {
            return items;
        }
        for (int slot : menu.getInputSlots()) {
            ItemStack itemStack = inv.getItem(slot);
            if (itemStack != null && !itemStack.getType().isAir()) {
                items.put(slot, itemStack.clone());
            }
        }
        return items;
    }

    private void returnInputItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int slot : menu.getInputSlots()) {
            ItemStack itemStack = inv == null ? null : inv.getItem(slot);
            if (itemStack != null && !itemStack.getType().isAir()) {
                items.add(itemStack.clone());
                inv.setItem(slot, null);
            }
        }
        if (!items.isEmpty()) {
            CommonUtil.giveOrDrop(player, items.toArray(new ItemStack[0]));
        }
    }

    private void queueRefresh() {
        SchedulerUtil.runTaskLater(() -> {
            if (inv != null && player.getOpenInventory().getTopInventory().equals(inv)) {
                constructGUI();
            }
        }, 1L);
    }

    private static void mergePrices(Map<AbstractSingleThing, BigDecimal> target,
                                    Map<AbstractSingleThing, BigDecimal> source) {
        for (Map.Entry<AbstractSingleThing, BigDecimal> entry : source.entrySet()) {
            target.merge(entry.getKey(), entry.getValue(), BigDecimal::add);
        }
    }

    private static BigDecimal sumPrices(Map<AbstractSingleThing, BigDecimal> prices) {
        return prices.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static void openGUI(Player player, ObjectItemSellMenu menu, boolean bypass, boolean reopen) {
        new ItemSellGUI(player, menu, bypass).openGUI(reopen);
    }

    private static class QuoteSummary {
        private final Map<AbstractSingleThing, BigDecimal> originalPrices = new LinkedHashMap<>();
        private final Map<AbstractSingleThing, BigDecimal> finalPrices = new LinkedHashMap<>();
        private BigDecimal originalTotal = BigDecimal.ZERO;
        private BigDecimal finalTotal = BigDecimal.ZERO;
        private int sellableAmount;
        private int unsellableAmount;
    }

    private static class SingleSlotItemStorage implements ItemStorage {
        private final Inventory inventory;
        private final int slot;

        private SingleSlotItemStorage(Inventory inventory, int slot) {
            this.inventory = inventory;
            this.slot = slot;
        }

        @Override
        public ItemStack[] getStorageContents() {
            ItemStack itemStack = inventory.getItem(slot);
            return new ItemStack[]{itemStack == null ? null : itemStack.clone()};
        }

        @Override
        public void setStorageContents(ItemStack[] contents) {
            inventory.setItem(slot, contents == null || contents.length == 0 ? null : contents[0]);
        }
    }
}
