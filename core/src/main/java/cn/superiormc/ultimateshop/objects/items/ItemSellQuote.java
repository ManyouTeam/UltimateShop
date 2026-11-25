package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.api.ShopHelper;
import cn.superiormc.ultimateshop.objects.buttons.ObjectItem;
import cn.superiormc.ultimateshop.objects.caches.ObjectUseTimesCache;
import cn.superiormc.ultimateshop.objects.menus.ObjectItemSellMenu;
import cn.superiormc.ultimateshop.objects.items.prices.PriceMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemSellQuote {

    private final ObjectItem item;

    private final int tradeAmount;

    private final int itemAmount;

    private final BigDecimal finalMultiplier;

    private final Map<AbstractSingleThing, BigDecimal> originalPrices;

    private final Map<AbstractSingleThing, BigDecimal> finalPrices;

    private final PriceMode priceMode;

    private ItemSellQuote(ObjectItem item,
                          int tradeAmount,
                          int itemAmount,
                          BigDecimal finalMultiplier,
                          Map<AbstractSingleThing, BigDecimal> originalPrices,
                          Map<AbstractSingleThing, BigDecimal> finalPrices,
                          PriceMode priceMode) {
        this.item = item;
        this.tradeAmount = tradeAmount;
        this.itemAmount = itemAmount;
        this.finalMultiplier = finalMultiplier;
        this.originalPrices = originalPrices;
        this.finalPrices = finalPrices;
        this.priceMode = priceMode;
    }

    public static ItemSellQuote create(ObjectItemSellMenu menu, Player player, ItemStack itemStack) {
        int maxAmount = menu == null ? -1 : menu.getMaxSellAmount();
        return createInternal(menu, player, itemStack, null, normalizeMaxAmount(maxAmount));
    }

    public static ItemSellQuote create(ObjectItemSellMenu menu,
                                       Player player,
                                       ItemStack itemStack,
                                       QuoteContext context) {
        int maxAmount = context == null
                ? normalizeMaxAmount(menu == null ? -1 : menu.getMaxSellAmount())
                : context.getRemainingAmount();
        return createInternal(menu, player, itemStack, context, maxAmount);
    }

    public static ItemSellQuote create(ObjectItemSellMenu menu,
                                       Player player,
                                       ItemStack itemStack,
                                       int maxAmount) {
        return createInternal(menu, player, itemStack, null, normalizeMaxAmount(maxAmount));
    }

    private static ItemSellQuote createInternal(ObjectItemSellMenu menu,
                                                Player player,
                                                ItemStack itemStack,
                                                QuoteContext context,
                                                int menuRemainingAmount) {
        if (menu == null || player == null || itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        ItemStorage storage = ItemStorage.of(new ItemStack[]{itemStack});
        ObjectItem item = null;
        for (ObjectItem candidate : ShopHelper.getTargetItems(storage, player)) {
            if (!candidate.getRawSellPrice().empty
                    && (candidate.isEnableSellAll() || candidate.isPriceModifierEnabled())) {
                item = candidate;
                break;
            }
        }
        if (item == null) {
            return null;
        }

        ObjectUseTimesCache playerTimes = ShopHelper.getPlayerUseTimesCache(item, player);
        ObjectUseTimesCache serverTimes = ShopHelper.getServerUseTimesCache(item);
        if (playerTimes == null || serverTimes == null) {
            return null;
        }
        playerTimes.refreshTimes();
        serverTimes.refreshTimes();
        int playerUseTimes = context == null ? playerTimes.getSellUseTimes() : context.getUseTimes(playerTimes);
        int serverUseTimes = context == null ? serverTimes.getSellUseTimes() : context.getUseTimes(serverTimes);

        int increment = item.getSellTimesIncrement();
        int maxAmount = menuRemainingAmount;
        if (item.getPlayerSellLimit(player) >= 0) {
            maxAmount = (int) Math.min(maxAmount, Math.max(0L, (long) item.getPlayerSellLimit(player) - playerUseTimes) / increment);
        }
        if (item.getServerSellLimit(player) >= 0) {
            maxAmount = (int) Math.min(maxAmount, Math.max(0L, (long) item.getServerSellLimit(player) - serverUseTimes) / increment);
        }
        if (maxAmount <= 0) {
            return null;
        }

        MaxSellResult maxSellResult = item.getReward().getMaxSellAmount(storage, player, playerUseTimes, maxAmount);
        int tradeAmount = maxSellResult.getMaxAmount();
        if (tradeAmount <= 0 || !item.getSellCondition(player, tradeAmount)) {
            return null;
        }

        GiveResult giveResult = item.getRawSellPrice().give(player, playerUseTimes, tradeAmount);
        if (giveResult == null || giveResult.empty) {
            return null;
        }
        Map<AbstractSingleThing, BigDecimal> originalPrices = new LinkedHashMap<>(giveResult.getResultMap());
        BigDecimal basePrice = originalPrices.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalMultiplier = ShopHelper.getSellMultiplier(
                player, item, itemStack, basePrice, tradeAmount);
        giveResult.setMultiplier(finalMultiplier);
        if (!giveResult.checkSellPriceConditions(playerUseTimes, tradeAmount, player)) {
            return null;
        }
        Map<AbstractSingleThing, BigDecimal> finalPrices = giveResult.getResultMap();
        int itemAmount = tradeAmount * item.getDisplayItemObject().getAmountPlaceholder(player);
        if (context != null) {
            context.consume(tradeAmount);
            long usedTimes = (long) tradeAmount * increment;
            context.addPending(playerTimes, usedTimes);
            if (serverTimes != playerTimes) {
                context.addPending(serverTimes, usedTimes);
            }
        }
        return new ItemSellQuote(item, tradeAmount, itemAmount, finalMultiplier,
                originalPrices, finalPrices, item.getRawSellPrice().getPriceMode());
    }

    private static int normalizeMaxAmount(int maxAmount) {
        return maxAmount < 0 ? Integer.MAX_VALUE : maxAmount;
    }

    public static final class QuoteContext {

        private final Map<ObjectUseTimesCache, Long> pendingUseTimes = new IdentityHashMap<>();

        private int remainingAmount;

        public QuoteContext() {
            this(-1);
        }

        public QuoteContext(int maxAmount) {
            this.remainingAmount = normalizeMaxAmount(maxAmount);
        }

        private int getRemainingAmount() {
            return remainingAmount;
        }

        private void consume(int amount) {
            remainingAmount = Math.max(0, remainingAmount - Math.max(0, amount));
        }

        private int getUseTimes(ObjectUseTimesCache cache) {
            return (int) Math.min(Integer.MAX_VALUE, cache.getSellUseTimes() + pendingUseTimes.getOrDefault(cache, 0L));
        }

        private void addPending(ObjectUseTimesCache cache, long amount) {
            pendingUseTimes.merge(cache, amount, (previous, next) ->
                    Math.min(2L * Integer.MAX_VALUE + 1, previous + next));
        }
    }

    public ObjectItem getItem() {
        return item;
    }

    public int getTradeAmount() {
        return tradeAmount;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public BigDecimal getFinalMultiplier() {
        return finalMultiplier;
    }

    public Map<AbstractSingleThing, BigDecimal> getOriginalPrices() {
        return new LinkedHashMap<>(originalPrices);
    }

    public Map<AbstractSingleThing, BigDecimal> getFinalPrices() {
        return new LinkedHashMap<>(finalPrices);
    }

    public PriceMode getPriceMode() {
        return priceMode;
    }
}
